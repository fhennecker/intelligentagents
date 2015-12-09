/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.tact;

import com.botbox.util.ArrayQueue;
import com.botbox.util.ThreadPool;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TACTConnection {
    private static final boolean DEBUG = false;
    private static final boolean VERBOSE_DEBUG = false;
    private static final Logger log = Logger.getLogger(TACTConnection.class.getName());
    private static final byte[] TACT_HEADER;
    private static final int MAX_BUFFER_SIZE = 2097152;
    private ThreadPool threadPool;
    private String name;
    private String fullName;
    private String userName;
    private long connectTime;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String remoteHost;
    private int remotePort;
    private boolean isServerConnection;
    private int maxBuffer = 2097152;
    private long sentBytes;
    private long requestedSentBytes;
    private ArrayQueue outBuffer;
    private boolean writerRunning = false;
    private boolean isOpen = false;
    private boolean isClosed = false;
    private TACTWriter tactWriter;
    private TACTReader tactReader;

    static {
        byte[] arrby = new byte[8];
        arrby[0] = 84;
        arrby[1] = 65;
        arrby[2] = 67;
        arrby[3] = 84;
        TACT_HEADER = arrby;
    }

    public TACTConnection(String name, String host, int port) {
        this.name = name;
        this.fullName = name;
        this.remoteHost = host;
        this.remotePort = port;
        this.isServerConnection = false;
        this.connectTime = System.currentTimeMillis();
    }

    public TACTConnection(String name, Socket socket) {
        this.name = name;
        this.fullName = name;
        this.socket = socket;
        this.isServerConnection = true;
        this.connectTime = System.currentTimeMillis();
    }

    public String getName() {
        return this.fullName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        if (userName == null) {
            throw new NullPointerException();
        }
        this.fullName = String.valueOf(userName) + '@' + this.name;
        this.userName = userName;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public long getConnectTime() {
        return this.connectTime;
    }

    public int getMaxBuffer() {
        return this.maxBuffer;
    }

    public void setMaxBuffer(int maxBuffer) {
        this.maxBuffer = maxBuffer;
    }

    public ThreadPool getThreadPool() {
        ThreadPool pool = this.threadPool;
        if (pool == null) {
            pool = this.threadPool = ThreadPool.getDefaultThreadPool();
        }
        return pool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public final void start() throws IOException {
        if (this.input != null) {
            return;
        }
        if (this.isServerConnection) {
            InetAddress remoteAddress = this.socket.getInetAddress();
            this.remoteHost = remoteAddress.getHostAddress();
            this.remotePort = this.socket.getPort();
            log.finest(String.valueOf(this.fullName) + ": new connection from " + this.remoteHost + ':' + this.remotePort);
        } else {
            this.socket = new Socket(this.remoteHost, this.remotePort);
        }
        this.input = new DataInputStream(this.socket.getInputStream());
        this.output = new DataOutputStream(this.socket.getOutputStream());
        if (!this.isServerConnection) {
            this.output.write(TACT_HEADER);
        }
        this.isOpen = true;
        this.outBuffer = new ArrayQueue();
        this.tactReader = new TACTReader(this);
        this.tactReader.start();
        this.connectionOpened();
    }

    public void write(byte[] data) {
        if (this.isOpen && data != null) {
            this.requestedSentBytes += (long)data.length;
            if (this.requestedSentBytes - this.sentBytes > (long)this.maxBuffer) {
                log.log(Level.SEVERE, String.valueOf(this.fullName) + ": could not send data", new IOException("out buffer overflow: " + (this.requestedSentBytes - this.sentBytes)));
                this.closeImmediately();
            } else {
                this.addOutBuffer(data);
            }
        }
    }

    public boolean isClosed() {
        return !this.isOpen;
    }

    public void close() {
        if (this.isOpen) {
            this.isOpen = false;
            this.addOutBuffer(null);
        }
    }

    public void closeImmediately() {
        this.closeImmediately(true);
    }

    private void closeImmediately(boolean useThread) {
        if (!this.isClosed) {
            this.isOpen = false;
            this.isClosed = true;
            if (useThread) {
                this.getThreadPool().invokeLater(new ConnectionCloser(this));
            } else {
                this.doClose();
            }
        }
    }

    private void doClose() {
        log.finest(String.valueOf(this.fullName) + ": connection closed from " + this.remoteHost);
        try {
            this.connectionClosed();
        }
        catch (Exception e) {
            log.log(Level.WARNING, String.valueOf(this.fullName) + ": failed to close connection", e);
        }
        try {
            this.tactReader.interrupt();
            this.output.close();
            this.input.close();
            this.socket.close();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, String.valueOf(this.fullName) + ": could not close connection", e);
        }
    }

    protected abstract void connectionOpened();

    protected abstract void connectionClosed();

    protected abstract void dataRead(byte[] var1, int var2, int var3);

    private void addOutBuffer(byte[] data) {
        ArrayQueue arrayQueue = this.outBuffer;
        synchronized (arrayQueue) {
            this.outBuffer.add(data);
            if (!this.writerRunning) {
                if (this.tactWriter == null) {
                    this.tactWriter = new TACTWriter(this);
                }
                this.writerRunning = true;
                this.getThreadPool().invokeLater(this.tactWriter);
            } else {
                this.outBuffer.notify();
            }
        }
    }

    static /* synthetic */ boolean access$1(TACTConnection tACTConnection) {
        return tACTConnection.isClosed;
    }

    static /* synthetic */ void access$4(TACTConnection tACTConnection, boolean bl) {
        tACTConnection.writerRunning = bl;
    }

    static /* synthetic */ long access$6(TACTConnection tACTConnection) {
        return tACTConnection.sentBytes;
    }

    static /* synthetic */ void access$7(TACTConnection tACTConnection, long l) {
        tACTConnection.sentBytes = l;
    }

    static /* synthetic */ DataOutputStream access$8(TACTConnection tACTConnection) {
        return tACTConnection.output;
    }

    private static class ConnectionCloser
    implements Runnable {
        private final TACTConnection connection;

        public ConnectionCloser(TACTConnection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            this.connection.doClose();
        }

        public String toString() {
            return "ConnectionCloser[" + this.connection.fullName + ',' + this.connection.remoteHost + ']';
        }
    }

    private static class TACTReader
    extends Thread {
        private TACTConnection connection;

        TACTReader(TACTConnection connection) {
            super(connection.name);
            this.connection = connection;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[8192];
            try {
                try {
                    this.connection.input.readFully(buffer, 0, TACT_HEADER.length);
                    int i = 0;
                    while (i < 4) {
                        if (buffer[i] != TACT_HEADER[i]) {
                            throw new IOException("illegal protocol header: " + new String(buffer, 0, 8));
                        }
                        ++i;
                    }
                    while (this.connection.isOpen) {
                        int size = this.connection.input.readInt();
                        if (size > buffer.length) {
                            if (size > this.connection.getMaxBuffer()) {
                                throw new IOException("in buffer overflow: " + size);
                            }
                            buffer = new byte[size + 8192];
                        }
                        this.connection.input.readFully(buffer, 0, size);
                        try {
                            this.connection.dataRead(buffer, 0, size);
                            continue;
                        }
                        catch (Throwable e) {
                            log.log(Level.SEVERE, String.valueOf(this.connection.fullName) + ": could not deliver data: " + size, e);
                        }
                    }
                }
                catch (EOFException e) {
                    log.severe(String.valueOf(this.connection.fullName) + ": closed from other side");
                    if (this.connection.isOpen) {
                        this.connection.closeImmediately(false);
                    }
                }
                catch (Throwable e) {
                    block18 : {
                        if (!this.connection.isOpen) break block18;
                        log.log(Level.SEVERE, String.valueOf(this.connection.fullName) + ": reading error ", e);
                    }
                    if (this.connection.isOpen) {
                        this.connection.closeImmediately(false);
                    }
                }
            }
            finally {
                if (this.connection.isOpen) {
                    this.connection.closeImmediately(false);
                }
            }
        }
    }

    private static class TACTWriter
    implements Runnable {
        private TACTConnection connection;

        TACTWriter(TACTConnection connection) {
            this.connection = connection;
        }

        /*
         * Exception decompiling
         */
        @Override
        public void run() {
            // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
            // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:371)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:449)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2859)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:805)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:220)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:165)
            // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
            // org.benf.cfr.reader.entities.Method.analyse(Method.java:354)
            // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:751)
            // org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:664)
            // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:747)
            // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:683)
            // org.benf.cfr.reader.Main.doJar(Main.java:128)
            // org.benf.cfr.reader.Main.main(Main.java:178)
            throw new IllegalStateException("Decompilation failed");
        }

        public String toString() {
            return "TACTWriter[" + this.connection.fullName + ',' + this.connection.outBuffer.size() + ',' + this.connection.remoteHost + ']';
        }
    }

}

