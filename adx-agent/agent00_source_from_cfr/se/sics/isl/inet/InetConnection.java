/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.inet;

import com.botbox.util.ArrayQueue;
import com.botbox.util.ThreadPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class InetConnection {
    private static final Logger log = Logger.getLogger(InetConnection.class.getName());
    private static final Object CLOSE_MESSAGE = new Object();
    private String name;
    private String fullName;
    private String userName;
    private long connectTime;
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private String remoteHost;
    private int remotePort;
    private final boolean isServerConnection;
    private boolean isDeliveryBuffered = false;
    private boolean delivererRunning = false;
    private boolean isWriteBuffered = false;
    private boolean writerRunning = false;
    private boolean isOpen = false;
    private boolean isClosed = true;
    private ArrayQueue inBuffer;
    private ArrayQueue outBuffer;
    private ThreadPool threadPool;
    private MessageWriter messageWriter;
    private MessageDeliverer messageDeliverer;
    private MessageReader messageReader;

    public InetConnection(String name, Socket socket) {
        this.isServerConnection = true;
        this.name = name;
        this.fullName = name;
        this.socket = socket;
        this.connectTime = System.currentTimeMillis();
    }

    public InetConnection(String name, String host, int port) {
        this.isServerConnection = false;
        this.name = name;
        this.fullName = name;
        this.remoteHost = host;
        this.remotePort = port;
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

    public InputStream getInputStream() {
        return this.input;
    }

    public OutputStream getOutputStream() {
        return this.output;
    }

    public boolean isServerConnection() {
        return this.isServerConnection;
    }

    public boolean isDeliveryBuffered() {
        return this.isDeliveryBuffered;
    }

    public void setDeliveryBuffered(boolean isDeliveryBuffered) {
        if (isDeliveryBuffered && this.inBuffer == null) {
            this.inBuffer = new ArrayQueue();
        }
        this.isDeliveryBuffered = isDeliveryBuffered;
    }

    public boolean isWriteBuffered() {
        return this.isWriteBuffered;
    }

    public void setWriteBuffered(boolean isWriteBuffered) {
        if (isWriteBuffered && this.outBuffer == null) {
            this.outBuffer = new ArrayQueue();
        }
        this.isWriteBuffered = isWriteBuffered;
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
        if (this.socket != null) {
            InetAddress remoteAddress = this.socket.getInetAddress();
            this.remoteHost = remoteAddress.getHostAddress();
            this.remotePort = this.socket.getPort();
        } else {
            this.socket = new Socket(this.remoteHost, this.remotePort);
        }
        this.input = this.socket.getInputStream();
        this.output = this.socket.getOutputStream();
        this.isClosed = false;
        this.isOpen = true;
        this.messageReader = new MessageReader(this.name, this);
        this.connectionOpened();
        this.messageReader.start();
    }

    public boolean isClosed() {
        return !this.isOpen;
    }

    public void close() {
        if (this.isOpen) {
            this.isOpen = false;
            this.sendMessage(CLOSE_MESSAGE);
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
        this.messageReader.interrupt();
        try {
            this.connectionClosed();
        }
        catch (Exception e) {
            log.log(Level.WARNING, String.valueOf(this.fullName) + ": failed to close connection", e);
        }
        try {
            this.input.close();
            this.output.close();
            this.socket.close();
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    public void sendMessage(Object message) {
        block11 : {
            if (this.isWriteBuffered) {
                ArrayQueue arrayQueue = this.outBuffer;
                synchronized (arrayQueue) {
                    this.outBuffer.add(message);
                    if (!this.writerRunning) {
                        if (this.messageWriter == null) {
                            this.messageWriter = new MessageWriter(this);
                        }
                        this.writerRunning = true;
                        this.getThreadPool().invokeLater(this.messageWriter);
                    } else {
                        this.outBuffer.notify();
                    }
                }
            }
            if (message == CLOSE_MESSAGE) {
                this.closeImmediately();
            } else {
                try {
                    this.doSendMessage(message);
                }
                catch (Throwable e) {
                    log.log(Level.SEVERE, String.valueOf(this.fullName) + ": could not send " + message, e);
                    this.closeImmediately();
                    if (!(e instanceof ThreadDeath)) break block11;
                    throw (ThreadDeath)e;
                }
            }
        }
    }

    protected void deliverMessage(Object message) {
        if (this.isDeliveryBuffered) {
            ArrayQueue arrayQueue = this.inBuffer;
            synchronized (arrayQueue) {
                this.inBuffer.add(message);
                if (!this.delivererRunning) {
                    if (this.messageDeliverer == null) {
                        this.messageDeliverer = new MessageDeliverer(this);
                    }
                    this.delivererRunning = true;
                    this.getThreadPool().invokeLater(this.messageDeliverer);
                } else {
                    this.inBuffer.notify();
                }
            }
        } else {
            this.doDeliverMessage(message);
        }
    }

    protected abstract void connectionOpened() throws IOException;

    protected abstract void connectionClosed() throws IOException;

    protected abstract void doReadMessages() throws IOException;

    protected abstract void doDeliverMessage(Object var1);

    protected abstract void doSendMessage(Object var1) throws IOException;

    static /* synthetic */ boolean access$1(InetConnection inetConnection) {
        return inetConnection.isClosed;
    }

    static /* synthetic */ void access$4(InetConnection inetConnection, boolean bl) {
        inetConnection.writerRunning = bl;
    }

    static /* synthetic */ Object access$5() {
        return CLOSE_MESSAGE;
    }

    static /* synthetic */ void access$10(InetConnection inetConnection, boolean bl) {
        inetConnection.delivererRunning = bl;
    }

    private static class ConnectionCloser
    implements Runnable {
        private final InetConnection connection;

        public ConnectionCloser(InetConnection connection) {
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

    private static class MessageDeliverer
    implements Runnable {
        private final InetConnection connection;

        public MessageDeliverer(InetConnection connection) {
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
            return "MessageDeliverer[" + this.connection.fullName + ',' + this.connection.inBuffer.size() + ',' + this.connection.remoteHost + ']';
        }
    }

    private static class MessageReader
    extends Thread {
        private final InetConnection connection;

        MessageReader(String name, InetConnection connection) {
            super(name);
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                try {
                    this.connection.doReadMessages();
                }
                catch (Throwable e) {
                    if (this.connection.isOpen) {
                        log.log(Level.SEVERE, String.valueOf(this.connection.fullName) + ": message reader error", e);
                    }
                    if (this.connection.isOpen) {
                        log.warning(String.valueOf(this.connection.fullName) + ": connection closed");
                        this.connection.closeImmediately(false);
                    }
                }
            }
            finally {
                if (this.connection.isOpen) {
                    log.warning(String.valueOf(this.connection.fullName) + ": connection closed");
                    this.connection.closeImmediately(false);
                }
            }
        }
    }

    private static class MessageWriter
    implements Runnable {
        private final InetConnection connection;

        MessageWriter(InetConnection connection) {
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
            return "MessageWriter[" + this.connection.fullName + ',' + this.connection.outBuffer.size() + ',' + this.connection.remoteHost + ']';
        }
    }

}

