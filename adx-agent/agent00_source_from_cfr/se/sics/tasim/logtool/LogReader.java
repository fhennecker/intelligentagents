/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.logtool;

import com.botbox.util.ArrayUtils;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.TransportReader;
import se.sics.tasim.logtool.ParticipantInfo;

public class LogReader {
    private static final String COORDINATOR = "coordinator";
    private static final int DEFAULT_MAX_BUFFER_SIZE = 5242880;
    private DataInputStream input;
    private BinaryTransportReader reader;
    private int maxBufferSize = 5242880;
    private boolean dataRead = false;
    private byte[] buffer = new byte[2048];
    private int simID;
    private int uniqueID;
    private String simType;
    private String simParams;
    private long startTime;
    private int simLength;
    private String serverName;
    private String serverVersion;
    private int majorVersion;
    private int minorVersion;
    private boolean isComplete = false;
    private ParticipantInfo[] participants;
    private boolean isCancelled = false;

    public LogReader(InputStream in) throws IOException, ParseException {
        this(in, true);
    }

    private LogReader(InputStream in, boolean readHeader) throws IOException, ParseException {
        this.input = new DataInputStream(in);
        this.reader = new BinaryTransportReader();
        this.readTACTHeader();
        if (readHeader) {
            this.readHeader();
        }
    }

    private void readTACTHeader() throws IOException {
        this.input.readFully(this.buffer, 0, 8);
        if ((this.buffer[0] & 255) != 84 || (this.buffer[1] & 255) != 65 || (this.buffer[2] & 255) != 67 || (this.buffer[3] & 255) != 84) {
            throw new IOException("not a simulation log file: '" + new String(this.buffer, 0, 4) + '\'');
        }
        this.majorVersion = this.buffer[4] & 255;
        this.minorVersion = this.buffer[5] & 255;
    }

    private void readHeader() throws IOException, ParseException {
        TransportReader reader = this.nextChunk();
        reader.nextNode("simulation", true);
        this.simID = reader.getAttributeAsInt("simID", -1);
        this.uniqueID = reader.getAttributeAsInt("id");
        this.simType = reader.getAttribute("type");
        this.simParams = reader.getAttribute("params", null);
        this.startTime = reader.getAttributeAsLong("startTime");
        this.simLength = reader.getAttributeAsInt("length") * 1000;
        this.serverName = reader.getAttribute("serverName", null);
        this.serverVersion = reader.getAttribute("version", null);
        reader.enterNode();
        int participantCount = 1;
        this.participants = new ParticipantInfo[15];
        this.participants[0] = new ParticipantInfo(0, "coordinator", -1, null, -1);
        while (reader.nextNode("participant", false)) {
            int index = reader.getAttributeAsInt("index");
            ParticipantInfo p = new ParticipantInfo(index, reader.getAttribute("address"), reader.getAttributeAsInt("id", -1), reader.getAttribute("name", null), reader.getAttributeAsInt("role"));
            if (index >= this.participants.length) {
                this.participants = (ParticipantInfo[])ArrayUtils.setSize(this.participants, index + 10);
            }
            if (this.participants[index] != null) {
                throw new ParseException("participant " + index + " already set", 0);
            }
            this.participants[index] = p;
            if (participantCount > index) continue;
            participantCount = index + 1;
        }
        if (participantCount < this.participants.length) {
            this.participants = (ParticipantInfo[])ArrayUtils.setSize(this.participants, participantCount);
        }
        reader.exitNode();
    }

    public int getSimulationID() {
        return this.simID;
    }

    public int getUniqueID() {
        return this.uniqueID;
    }

    public String getSimulationType() {
        return this.simType;
    }

    public String getSimulationParams() {
        return this.simParams;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.startTime + (long)this.simLength;
    }

    public int getSimulationLength() {
        return this.simLength;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public ParticipantInfo[] getParticipants() {
        return this.participants;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public int getMaxBufferSize() {
        return this.maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    public void setContext(Context context) {
        if (this.reader != null) {
            this.reader.setContext(context);
        }
    }

    public synchronized boolean hasMoreChunks() throws IOException {
        if (!this.dataRead) {
            this.read();
        }
        return this.dataRead;
    }

    public synchronized TransportReader nextChunk() throws IOException {
        if (!this.dataRead) {
            this.read();
        }
        if (this.dataRead) {
            this.dataRead = false;
            return this.reader;
        }
        throw new EOFException();
    }

    public boolean isClosed() {
        if (this.reader != null) {
            return true;
        }
        return false;
    }

    public synchronized void close() {
        if (this.reader != null) {
            this.dataRead = false;
            this.reader = null;
            try {
                try {
                    this.input.close();
                }
                catch (Exception var1_1) {
                    this.input = null;
                }
            }
            finally {
                this.input = null;
            }
        }
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void cancel() {
        if (!this.isCancelled) {
            this.isCancelled = true;
            if (!this.isClosed()) {
                this.close();
            }
        }
    }

    private void read() throws IOException {
        int p2;
        int p4;
        int p3;
        if (this.reader == null) {
            this.dataRead = false;
            return;
        }
        int p1 = this.input.read();
        int flag = p1 | (p2 = this.input.read()) | (p3 = this.input.read()) | (p4 = this.input.read());
        if (flag < 0) {
            throw new EOFException();
        }
        if (flag == 0) {
            this.isComplete = true;
            if (this.input.read() >= 0) {
                System.err.println("LogReader: unexpected data after log complete data");
            }
            this.close();
        } else {
            int len = (p1 << 24) + (p2 << 16) + (p3 << 8) + p4;
            if (len > this.maxBufferSize) {
                throw new IOException("too large data block: " + len);
            }
            if (this.buffer.length <= len) {
                this.buffer = new byte[len + 1024];
            }
            this.input.readFully(this.buffer, 0, len);
            this.reader.setMessage(this.buffer, 0, len);
            this.dataRead = true;
        }
    }

    static void generateXML(InputStream input) throws IOException, ParseException {
        LogReader.generateXML(input, true);
    }

    static void generateXML(InputStream input, boolean showChunkSeparator) throws IOException, ParseException {
        LogReader reader = new LogReader(input, false);
        try {
            System.out.println("<simulationLog>");
            while (reader.hasMoreChunks()) {
                BinaryTransportReader r = (BinaryTransportReader)reader.nextChunk();
                if (showChunkSeparator) {
                    System.out.println("<!-- - - - - - - - - - - - - - - - - - - - -->");
                }
                r.printMessage();
            }
            System.out.println("</simulationLog>");
        }
        finally {
            reader.close();
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length != 1) {
            System.out.println("Usage: LogReader file");
            System.exit(1);
        }
        InputStream in = new FileInputStream(args[0]);
        if (args[0].endsWith(".gz")) {
            in = new GZIPInputStream(in);
        }
        LogReader.generateXML(in);
    }
}

