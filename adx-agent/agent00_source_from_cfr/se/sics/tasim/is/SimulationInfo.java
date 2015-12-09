/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

import com.botbox.util.ArrayUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class SimulationInfo
implements Transportable {
    private static final int IS_FULL = 1;
    private static final int IS_RESERVED = 2;
    public static final String RESERVED = "reserved";
    private static final Logger log = Logger.getLogger(SimulationInfo.class.getName());
    private int id;
    private String type;
    private String params;
    private int simulationLength;
    private String[] paramCache;
    private int paramCacheSize = -1;
    private int simulationID = -1;
    private int participantCount;
    private long startTime;
    private int[] participants;
    private int[] roles;
    private int flags;

    public SimulationInfo(int id, String type, String params, int simulationLength) {
        this.id = id;
        this.setType(type);
        this.params = params;
        this.simulationLength = simulationLength;
    }

    public SimulationInfo() {
    }

    public int getID() {
        return this.id;
    }

    public boolean isReservation() {
        if ((this.flags & 2) != 0) {
            return true;
        }
        return false;
    }

    public String getType() {
        return this.type;
    }

    private void setType(String type) {
        if (type == null) {
            throw new NullPointerException();
        }
        if ("reserved".equals(type)) {
            this.flags |= 3;
            this.type = "reserved";
        } else {
            this.type = type.intern();
        }
    }

    public String getParams() {
        return this.params;
    }

    public String getParameter(String name) {
        int index;
        if (this.params == null) {
            return null;
        }
        if (this.paramCacheSize < 0) {
            int start = 0;
            int index2 = this.params.indexOf(38, start);
            this.paramCache = new String[8];
            this.paramCacheSize = 0;
            try {
                while (index2 >= 0) {
                    this.addParam(start, index2);
                    start = index2 + 1;
                    index2 = this.params.indexOf(38, start);
                }
                if (start < this.params.length()) {
                    this.addParam(start, this.params.length());
                }
                if (this.paramCache.length > this.paramCacheSize) {
                    this.paramCache = (String[])ArrayUtils.setSize(this.paramCache, this.paramCacheSize);
                }
            }
            catch (UnsupportedEncodingException e) {
                log.log(Level.WARNING, "could not parse params '" + this.params + '\'', e);
                return null;
            }
        }
        return (index = ArrayUtils.keyValuesIndexOf(this.paramCache, 2, 0, this.paramCacheSize, name)) >= 0 ? this.paramCache[index + 1] : null;
    }

    private void addParam(int start, int end) throws UnsupportedEncodingException {
        int separator = this.params.indexOf(61, start);
        if (separator > start && separator < end) {
            if (this.paramCacheSize >= this.paramCache.length) {
                this.paramCache = (String[])ArrayUtils.setSize(this.paramCache, this.paramCacheSize + 16);
            }
            this.paramCache[this.paramCacheSize] = URLDecoder.decode(this.params.substring(start, separator), "UTF-8");
            this.paramCache[this.paramCacheSize + 1] = URLDecoder.decode(this.params.substring(separator + 1, end), "UTF-8");
            this.paramCacheSize += 2;
        } else {
            log.warning("malformed parameters '" + this.params + "' after " + start);
        }
    }

    public int getParameter(String name, int defaultValue) {
        String value = this.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                log.log(Level.WARNING, "could not parse param " + name + "='" + value + '\'', e);
            }
        }
        return defaultValue;
    }

    public boolean hasSimulationID() {
        if (this.simulationID >= 0) {
            return true;
        }
        return false;
    }

    public int getSimulationID() {
        return this.simulationID;
    }

    public void setSimulationID(int simulationID) {
        if (this.simulationID != simulationID) {
            if (this.simulationID >= 0) {
                throw new IllegalStateException("simulationID already set");
            }
            this.simulationID = simulationID;
        }
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        if (this.startTime > 0) {
            throw new IllegalStateException("start time already set");
        }
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.startTime + (long)this.simulationLength;
    }

    public int getSimulationLength() {
        return this.simulationLength;
    }

    public boolean isEmpty() {
        if (this.participantCount == 0) {
            return true;
        }
        return false;
    }

    public boolean isFull() {
        if ((this.flags & 1) != 0) {
            return true;
        }
        return false;
    }

    public void setFull() {
        this.flags |= 1;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getParticipantCount() {
        return this.participantCount;
    }

    public int getParticipantID(int index) {
        if (index >= this.participantCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.participantCount);
        }
        return this.participants[index];
    }

    public boolean isBuiltinParticipant(int index) {
        if (index >= this.participantCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.participantCount);
        }
        if (this.participants[index] < 0) {
            return true;
        }
        return false;
    }

    public int getParticipantRole(int index) {
        if (index >= this.participantCount) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.participantCount);
        }
        return this.roles[index];
    }

    public int indexOfParticipant(int agentID) {
        int i = this.participantCount - 1;
        while (i >= 0) {
            if (this.participants[i] == agentID) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public boolean isParticipant(int agentID) {
        if (this.indexOfParticipant(agentID) >= 0) {
            return true;
        }
        return false;
    }

    public synchronized boolean addParticipant(int agentID, int role) {
        if (this.isFull() || this.isParticipant(agentID)) {
            return false;
        }
        if (this.participants == null) {
            this.participants = new int[8];
            this.roles = new int[8];
        } else if (this.participants.length == this.participantCount) {
            this.participants = ArrayUtils.setSize(this.participants, this.participantCount + 8);
            this.roles = ArrayUtils.setSize(this.roles, this.participantCount + 8);
        }
        this.participants[this.participantCount] = agentID;
        this.roles[this.participantCount++] = role;
        return true;
    }

    public synchronized void copyParticipants(SimulationInfo info) {
        int i = 0;
        int n = info.participantCount;
        while (i < n) {
            this.addParticipant(info.getParticipantID(i), info.getParticipantRole(i));
            ++i;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getTransportName()).append('[').append(this.id).append(',').append(this.simulationID).append(',').append(this.startTime).append(',').append(this.simulationLength / 1000).append(',').append(this.type);
        if (this.params != null) {
            sb.append('[').append(this.params).append(']');
        }
        sb.append(",[");
        int i = 0;
        int n = this.participantCount;
        while (i < n) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(this.participants[i]).append('=').append(this.roles[i]);
            ++i;
        }
        return sb.append(']').toString();
    }

    @Override
    public String getTransportName() {
        return "simulationInfo";
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.type != null) {
            throw new IllegalStateException("already initialized");
        }
        this.id = reader.getAttributeAsInt("id");
        this.setType(reader.getAttribute("type"));
        this.params = reader.getAttribute("params", null);
        this.simulationLength = reader.getAttributeAsInt("length") * 1000;
        this.simulationID = reader.getAttributeAsInt("simID", -1);
        this.startTime = reader.getAttributeAsLong("startTime", 0);
        this.flags = reader.getAttributeAsInt("flags", 0);
        while (reader.nextNode("agent", false)) {
            this.addParticipant(reader.getAttributeAsInt("agentID"), reader.getAttributeAsInt("role"));
        }
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("id", this.id).attr("type", this.type);
        if (this.params != null) {
            writer.attr("params", this.params);
        }
        writer.attr("length", this.simulationLength / 1000);
        if (this.simulationID >= 0) {
            writer.attr("simID", this.simulationID);
        }
        if (this.startTime > 0) {
            writer.attr("startTime", this.startTime);
        }
        if (this.flags != 0) {
            writer.attr("flags", this.flags);
        }
        int i = 0;
        int n = this.participantCount;
        while (i < n) {
            writer.node("agent").attr("agentID", this.participants[i]).attr("role", this.roles[i]).endNode("agent");
            ++i;
        }
    }
}

