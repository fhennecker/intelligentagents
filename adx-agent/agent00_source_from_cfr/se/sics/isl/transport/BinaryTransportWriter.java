/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.transport;

import com.botbox.util.ArrayUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Hashtable;
import se.sics.isl.transport.BinaryTransport;
import se.sics.isl.transport.TransportWriter;

public class BinaryTransportWriter
extends TransportWriter
implements BinaryTransport {
    public static final String SUPPORT_CONSTANTS = "constants";
    public static final String SUPPORT_TABLES = "tables";
    private static final int DEF_SIZE = 10;
    private static final int ALIAS_SIZE = 256;
    private static final int DATA_SIZE = 1024;
    private static final int TYPE_POS = 0;
    private static final int NAME_POS = 1;
    private Hashtable constantLookup = new Hashtable();
    private boolean inNode = false;
    private int nodeLevel = 0;
    private boolean isTablesSupported = false;
    private boolean isConstantsSupported = false;
    private int[][] currentRow = new int[2][20];
    private long[][] currentIValues = new long[2][10];
    private float[][] currentFValues = new float[2][10];
    private double[][] currentDValues = new double[2][10];
    private Object[][] currentOValues = new Object[2][10];
    private int[] attrCount = new int[2];
    private int[] nodeName = new int[2];
    private boolean[] nodeWritten = new boolean[2];
    private int currentPos = 0;
    private int nextID = 0;
    private byte[] aliasData = new byte[256];
    private int aliasSize = 0;
    private byte[] byteData = new byte[1024];
    private int nrOfBytes;

    public BinaryTransportWriter() {
        this.clear();
    }

    public boolean isSupported(String name) {
        if ("constants".equals(name)) {
            return this.isConstantsSupported;
        }
        if ("tables".equals(name)) {
            return this.isTablesSupported;
        }
        return false;
    }

    public void setSupported(String name, boolean isSupported) {
        if ("constants".equals(name)) {
            this.isConstantsSupported = isSupported;
        } else if ("tables".equals(name)) {
            this.isTablesSupported = isSupported;
        }
    }

    private void writeInit() {
        Enumeration enumb = this.constantLookup.keys();
        while (enumb.hasMoreElements()) {
            String name = (String)enumb.nextElement();
            int id = (Integer)this.constantLookup.get(name);
            this.writeByte(65);
            this.writeShort(id);
            this.writeString(name);
        }
    }

    public int getInitSize() {
        int oldPos = this.nrOfBytes;
        this.writeInit();
        int len = this.nrOfBytes - oldPos;
        this.nrOfBytes = oldPos;
        return len;
    }

    public byte[] getInitBytes() {
        int oldPos = this.nrOfBytes;
        this.writeInit();
        if (oldPos != this.nrOfBytes) {
            int size = this.nrOfBytes - oldPos;
            byte[] buffer = new byte[size];
            System.arraycopy(this.byteData, oldPos, buffer, 0, size);
            this.nrOfBytes = oldPos;
            return buffer;
        }
        return null;
    }

    public void writeInit(ByteBuffer buffer) {
        int oldPos = this.nrOfBytes;
        this.writeInit();
        if (oldPos != this.nrOfBytes) {
            buffer.put(this.byteData, oldPos, this.nrOfBytes - oldPos);
            this.nrOfBytes = oldPos;
        }
    }

    public void writeInit(OutputStream stream) throws IOException {
        int oldPos = this.nrOfBytes;
        this.writeInit();
        if (oldPos != this.nrOfBytes) {
            stream.write(this.byteData, oldPos, this.nrOfBytes - oldPos);
            this.nrOfBytes = oldPos;
        }
    }

    public int size() {
        return this.aliasSize + this.nrOfBytes;
    }

    public void write(ByteBuffer buffer) {
        if (this.aliasSize > 0) {
            buffer.put(this.aliasData, 0, this.aliasSize);
        }
        if (this.nrOfBytes > 0) {
            buffer.put(this.byteData, 0, this.nrOfBytes);
        }
    }

    public void write(OutputStream stream) throws IOException {
        if (this.aliasSize > 0) {
            stream.write(this.aliasData, 0, this.aliasSize);
        }
        if (this.nrOfBytes > 0) {
            stream.write(this.byteData, 0, this.nrOfBytes);
        }
    }

    public void write(byte[] buffer) {
        if (buffer.length < this.aliasSize + this.nrOfBytes) {
            throw new IndexOutOfBoundsException("Too many bytes to fit array, requires " + (this.aliasSize + this.nrOfBytes) + ", got " + buffer.length);
        }
        if (this.aliasSize > 0) {
            System.arraycopy(this.aliasData, 0, buffer, 0, this.aliasSize);
        }
        if (this.nrOfBytes > 0) {
            System.arraycopy(this.byteData, 0, buffer, this.aliasSize, this.nrOfBytes);
        }
    }

    public byte[] getBytes() {
        byte[] buffer = new byte[this.aliasSize + this.nrOfBytes];
        if (this.aliasSize > 0) {
            System.arraycopy(this.aliasData, 0, buffer, 0, this.aliasSize);
        }
        if (this.nrOfBytes > 0) {
            System.arraycopy(this.byteData, 0, buffer, this.aliasSize, this.nrOfBytes);
        }
        return buffer;
    }

    @Override
    public void addConstant(String constant) {
        if (this.isConstantsSupported) {
            this.createConstantID(constant);
        }
    }

    @Override
    public TransportWriter attr(String name, int value) {
        if (!this.inNode) {
            throw new IllegalArgumentException("Can not output attributes outside of nodes");
        }
        int nid = this.createConstantID(name);
        int index = this.setType(105, nid);
        this.currentIValues[this.currentPos][index] = value;
        return this;
    }

    @Override
    public TransportWriter attr(String name, long value) {
        if (!this.inNode) {
            throw new IllegalArgumentException("Can not output attributes outside of nodes");
        }
        int nid = this.createConstantID(name);
        int index = this.setType(108, nid);
        this.currentIValues[this.currentPos][index] = value;
        return this;
    }

    @Override
    public TransportWriter attr(String name, float value) {
        if (!this.inNode) {
            throw new IllegalArgumentException("Can not output attributes outside of nodes");
        }
        int nid = this.createConstantID(name);
        int index = this.setType(102, nid);
        this.currentFValues[this.currentPos][index] = value;
        return this;
    }

    @Override
    public TransportWriter attr(String name, double value) {
        if (!this.inNode) {
            throw new IllegalArgumentException("Can not output attributes outside of nodes");
        }
        int nid = this.createConstantID(name);
        int index = this.setType(100, nid);
        this.currentDValues[this.currentPos][index] = value;
        return this;
    }

    @Override
    public TransportWriter attr(String name, String value) {
        int cid;
        if (!this.inNode) {
            throw new IllegalArgumentException("Can not output attributes outside of nodes");
        }
        int nid = this.createConstantID(name);
        if (this.isConstantsSupported && (cid = this.getConstantID(value)) >= 0) {
            int index = this.setType(83, nid);
            this.currentIValues[this.currentPos][index] = cid;
        } else {
            int index = this.setType(115, nid);
            this.currentOValues[this.currentPos][index] = value;
        }
        return this;
    }

    @Override
    public TransportWriter attr(String name, int[] value) {
        if (!this.inNode) {
            throw new IllegalArgumentException("Can not output attributes outside of nodes");
        }
        int nid = this.createConstantID(name);
        int index = this.setType(73, nid);
        this.currentOValues[this.currentPos][index] = value;
        return this;
    }

    @Override
    public int getNodeLevel() {
        return this.nodeLevel;
    }

    @Override
    public TransportWriter node(String name) {
        if (this.inNode) {
            this.writeCurrentNode(110, this.currentPos);
        }
        this.currentPos = 1 - this.currentPos;
        this.nodeName[this.currentPos] = this.createConstantID(name);
        this.nodeWritten[this.currentPos] = false;
        this.attrCount[this.currentPos] = 0;
        ++this.nodeLevel;
        this.inNode = true;
        return this;
    }

    @Override
    public TransportWriter endNode(String name) {
        return this.endNode();
    }

    private TransportWriter endNode() {
        if (this.nodeLevel > 0) {
            this.writeCurrentNode(10, this.currentPos);
            --this.nodeLevel;
        }
        this.inNode = false;
        return this;
    }

    private void writeCurrentNode(byte nodeType, int pos) {
        int name = this.nodeName[pos];
        if (name == -1 || this.nodeWritten[pos]) {
            if (nodeType == 10) {
                this.writeByte(10);
            }
        } else {
            int type;
            int n;
            int i;
            boolean writeTypes = true;
            if (nodeType == 10) {
                int other = 1 - pos;
                if (this.isTablesSupported && this.nodeName[other] == name && this.nodeWritten[other] && this.attrCount[pos] == this.attrCount[other]) {
                    writeTypes = false;
                    int i2 = 0;
                    int n2 = this.attrCount[pos] * 2;
                    while (i2 < n2) {
                        if (this.currentRow[pos][i2] != this.currentRow[other][i2]) {
                            writeTypes = true;
                            break;
                        }
                        ++i2;
                    }
                }
                this.writeByte(writeTypes ? 78 : 84);
            } else {
                this.writeByte(110);
            }
            if (writeTypes) {
                this.writeByte(this.attrCount[pos]);
                this.writeShort(name);
                i = 0;
                n = this.attrCount[pos] * 2;
                while (i < n) {
                    type = this.currentRow[pos][i + 0];
                    this.writeByte(type);
                    this.writeShort(this.currentRow[pos][i + 1]);
                    i += 2;
                }
            }
            i = 0;
            n = this.attrCount[pos];
            while (i < n) {
                type = this.currentRow[pos][i * 2 + 0];
                switch (type) {
                    case 105: {
                        this.writeInt((int)this.currentIValues[pos][i]);
                        break;
                    }
                    case 108: {
                        this.writeLong(this.currentIValues[pos][i]);
                        break;
                    }
                    case 102: {
                        this.writeFloat(this.currentFValues[pos][i]);
                        break;
                    }
                    case 100: {
                        this.writeDouble(this.currentDValues[pos][i]);
                        break;
                    }
                    case 115: {
                        this.writeString((String)this.currentOValues[pos][i]);
                        break;
                    }
                    case 83: {
                        this.writeShort((int)this.currentIValues[pos][i]);
                        break;
                    }
                    case 73: {
                        this.writeIntArr((int[])this.currentOValues[pos][i]);
                    }
                }
                ++i;
            }
            this.nodeWritten[pos] = true;
        }
    }

    private void writeByte(int data) {
        if (this.nrOfBytes >= this.byteData.length) {
            this.byteData = ArrayUtils.setSize(this.byteData, this.nrOfBytes + 1024);
        }
        this.byteData[this.nrOfBytes++] = (byte)(data & 255);
    }

    private void writeShort(int data) {
        this.writeByte(data >> 8 & 255);
        this.writeByte(data & 255);
    }

    private void writeInt(int data) {
        this.writeByte(data >>> 24 & 255);
        this.writeByte(data >>> 16 & 255);
        this.writeByte(data >>> 8 & 255);
        this.writeByte(data & 255);
    }

    private void writeLong(long data) {
        this.writeByte((int)(data >>> 56) & 255);
        this.writeByte((int)(data >>> 48) & 255);
        this.writeByte((int)(data >>> 40) & 255);
        this.writeByte((int)(data >>> 32) & 255);
        this.writeByte((int)(data >>> 24) & 255);
        this.writeByte((int)(data >>> 16) & 255);
        this.writeByte((int)(data >>> 8) & 255);
        this.writeByte((int)(data & 255));
    }

    private void writeFloat(float data) {
        this.writeInt(Float.floatToIntBits(data));
    }

    private void writeDouble(double data) {
        this.writeLong(Double.doubleToLongBits(data));
    }

    private void writeString(String value) {
        int maxSize = this.getMaxUTF8Size(value);
        if (this.nrOfBytes + maxSize > this.byteData.length) {
            this.byteData = ArrayUtils.setSize(this.byteData, this.nrOfBytes + maxSize + 1024);
        }
        int len = this.writeUTF8(this.byteData, this.nrOfBytes, value);
        this.nrOfBytes += len;
    }

    private void writeAlias(int id, String name) {
        int maxSize = 3 + this.getMaxUTF8Size(name);
        if (this.aliasSize + maxSize > this.aliasData.length) {
            this.aliasData = ArrayUtils.setSize(this.aliasData, this.aliasSize + maxSize + 256);
        }
        this.aliasData[this.aliasSize++] = 65;
        this.aliasData[this.aliasSize++] = (byte)(id >> 8 & 255);
        this.aliasData[this.aliasSize++] = (byte)(id & 255);
        this.aliasSize += this.writeUTF8(this.aliasData, this.aliasSize, name);
    }

    private int getMaxUTF8Size(String value) {
        int len = value.length();
        return len * 3 + 2;
    }

    private int writeUTF8(byte[] buffer, int offset, String value) {
        int index = offset + 2;
        int i = 0;
        int len = value.length();
        while (i < len) {
            char c = value.charAt(i);
            if (c >= '\u0001' && c <= '') {
                buffer[index++] = (byte)c;
            } else if (c > '\u07ff') {
                buffer[index++] = (byte)(224 | c >> 12 & 15);
                buffer[index++] = (byte)(128 | c >> 6 & 63);
                buffer[index++] = (byte)(128 | c >> 0 & 63);
            } else {
                buffer[index++] = (byte)(192 | c >> 6 & 31);
                buffer[index++] = (byte)(128 | c >> 0 & 63);
            }
            ++i;
        }
        int size = index - offset - 2;
        if (size > 65535) {
            throw new IllegalArgumentException("too large string: " + value.length());
        }
        buffer[offset] = (byte)(size >> 8 & 255);
        buffer[offset + 1] = (byte)(size & 255);
        return size + 2;
    }

    private void writeIntArr(int[] value) {
        int len = value.length;
        if (this.nrOfBytes + len * 4 >= this.byteData.length) {
            this.byteData = ArrayUtils.setSize(this.byteData, this.nrOfBytes + len * 4 + 1024);
        }
        this.writeShort(len);
        int i = 0;
        int n = len;
        while (i < n) {
            this.writeInt(value[i]);
            ++i;
        }
    }

    private int getConstantID(String name) {
        Integer alias = (Integer)this.constantLookup.get(name);
        return alias != null ? alias : -1;
    }

    private int createConstantID(String name) {
        Integer alias = (Integer)this.constantLookup.get(name);
        if (alias != null) {
            return alias;
        }
        int id = this.nextID++;
        this.constantLookup.put(name, new Integer(id));
        this.writeAlias(id, name);
        return id;
    }

    public void finish() {
        if (this.nodeLevel > 0) {
            int i = 0;
            int n = this.nodeLevel;
            while (i < n) {
                this.endNode();
                ++i;
            }
        }
    }

    public void clear() {
        this.aliasSize = 0;
        this.nrOfBytes = 0;
        this.nodeLevel = 0;
        this.inNode = false;
        this.nodeName[0] = -1;
        this.nodeWritten[0] = false;
        this.nodeName[1] = -1;
        this.nodeWritten[1] = false;
    }

    private int setType(int type, int name) {
        int ac = this.attrCount[this.currentPos];
        if (ac >= this.currentIValues[this.currentPos].length) {
            int newSize = ac + 10;
            this.currentIValues[this.currentPos] = ArrayUtils.setSize(this.currentIValues[this.currentPos], newSize);
            this.currentFValues[this.currentPos] = ArrayUtils.setSize(this.currentFValues[this.currentPos], newSize);
            this.currentDValues[this.currentPos] = ArrayUtils.setSize(this.currentDValues[this.currentPos], newSize);
            this.currentOValues[this.currentPos] = ArrayUtils.setSize(this.currentOValues[this.currentPos], newSize);
            this.currentRow[this.currentPos] = ArrayUtils.setSize(this.currentRow[this.currentPos], newSize * 2);
        }
        this.currentRow[this.currentPos][ac * 2] = type;
        this.currentRow[this.currentPos][ac * 2 + 1] = name;
        int[] arrn = this.attrCount;
        int n = this.currentPos;
        int n2 = arrn[n];
        arrn[n] = n2 + 1;
        return n2;
    }
}

