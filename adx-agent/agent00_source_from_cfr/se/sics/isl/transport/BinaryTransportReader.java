/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.transport;

import com.botbox.util.ArrayUtils;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Hashtable;
import se.sics.isl.transport.BinaryTransport;
import se.sics.isl.transport.TransportReader;

public class BinaryTransportReader
extends TransportReader
implements BinaryTransport {
    private static final int DEF_SIZE = 10;
    private int currentPosition = -1;
    private int currentNode = -1;
    private int currentValue = -1;
    private byte[] messageData;
    private int dataOffset;
    private int dataLen;
    private int[] nodeStack = new int[10];
    private int nodeLevel = 0;
    private boolean nodeEntered = false;
    private String[] aliases = new String[48];
    private Hashtable nameLookup = new Hashtable();

    public void setMessage(byte[] messageData) {
        this.setMessage(messageData, 0, messageData.length);
    }

    public void setMessage(byte[] messageData, int offset, int length) {
        if ((offset | length | messageData.length - offset - length) < 0) {
            throw new IndexOutOfBoundsException();
        }
        this.messageData = messageData;
        this.dataOffset = offset;
        this.dataLen = offset + length;
        this.nodeLevel = 0;
        this.reset();
    }

    public void clear() {
        this.nodeLevel = 0;
        this.dataLen = 0;
        this.dataOffset = 0;
        this.reset();
        this.messageData = null;
    }

    @Override
    public void reset() {
        if (this.nodeLevel == 0) {
            this.currentPosition = this.dataOffset - 1;
            this.nodeEntered = false;
            this.currentValue = -1;
            this.currentNode = -1;
        } else {
            this.currentNode = this.currentPosition = this.nodeStack[this.nodeLevel - 1];
            this.currentValue = this.getValuePosForNode(this.currentNode);
            this.nodeEntered = true;
        }
    }

    @Override
    protected int getPosition() {
        return this.currentPosition;
    }

    private int getAlias(String alias) {
        Integer i = (Integer)this.nameLookup.get(alias);
        return i == null ? -1 : i;
    }

    private int addAliases(int pos) throws ParseException {
        while (pos < this.dataLen && (this.messageData[pos] & 255) == 65) {
            pos = this.addAlias(pos);
        }
        return pos;
    }

    private int addAlias(int pos) throws ParseException {
        int len;
        String alias;
        if (pos + 5 >= this.dataLen) {
            throw new ParseException("unexpected EOF", pos);
        }
        int id = ((this.messageData[pos] & 255) << 8) + (this.messageData[++pos + 1] & 255);
        pos += 2;
        if (this.nameLookup.get(alias = this.getSValue(pos += 2, len = ((this.messageData[pos] & 255) << 8) + (this.messageData[pos + 1] & 255))) == null) {
            this.nameLookup.put(alias, new Integer(id));
        }
        if (this.aliases.length <= id) {
            this.aliases = (String[])ArrayUtils.setSize(this.aliases, id + 32);
        }
        this.aliases[id] = alias;
        return pos + len;
    }

    @Override
    public boolean hasMoreNodes() throws ParseException {
        int mark = this.currentPosition;
        int node = this.currentNode;
        int value = this.currentValue;
        boolean entered = this.nodeEntered;
        if (this.nextNode(false)) {
            this.currentPosition = mark;
            this.currentNode = node;
            this.currentValue = value;
            this.nodeEntered = entered;
            return true;
        }
        return false;
    }

    @Override
    public boolean nextNode(boolean isRequired) throws ParseException {
        if (this.skipToNextNode()) {
            return true;
        }
        if (isRequired) {
            throw new ParseException("no more nodes", this.currentPosition);
        }
        return false;
    }

    private boolean skipToNextNode() throws ParseException {
        if (this.currentPosition < this.dataOffset) {
            if (this.dataLen == this.dataOffset) {
                return false;
            }
            this.dataOffset = this.addAliases(this.dataOffset);
            if (this.dataOffset >= this.dataLen) {
                return false;
            }
            this.currentPosition = this.dataOffset;
            if ((this.messageData[this.currentPosition] & 255) == 84) {
                throw new ParseException("table without type", this.currentPosition);
            }
            this.currentNode = this.currentPosition;
            this.currentValue = this.getValuePosForNode(this.currentNode);
            return true;
        }
        int pos = this.currentPosition;
        int op = this.messageData[pos] & 255;
        if (this.nodeEntered && (op == 78 || op == 84)) {
            return false;
        }
        pos = this.skipNode(this.currentNode, this.currentValue);
        if (pos >= this.dataLen) {
            return false;
        }
        if (op == 78 || op == 84) {
            if ((pos = this.addAliases(pos)) < this.dataLen) {
                switch (this.messageData[pos] & 255) {
                    case 78: 
                    case 110: {
                        this.currentNode = this.currentPosition = pos;
                        this.currentValue = this.getValuePosForNode(this.currentNode);
                        return true;
                    }
                    case 84: {
                        this.currentPosition = pos;
                        this.currentValue = pos + 1;
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        int level = 1;
        int levelTarget = this.nodeEntered ? 1 : 0;
        int lastNode = this.currentNode;
        while (pos < this.dataLen && level >= 0) {
            op = this.messageData[pos] & 255;
            switch (op) {
                case 10: {
                    if (this.nodeEntered && --level == 0) {
                        return false;
                    }
                    ++pos;
                    lastNode = -1;
                    break;
                }
                case 65: {
                    pos = this.addAlias(pos);
                    break;
                }
                case 78: {
                    if (level == levelTarget) {
                        this.currentNode = this.currentPosition = pos;
                        this.currentValue = this.getValuePosForNode(this.currentNode);
                        this.nodeEntered = false;
                        return true;
                    }
                    lastNode = pos;
                    pos = this.skipNode(pos, this.getValuePosForNode(pos));
                    break;
                }
                case 84: {
                    if (level == levelTarget) {
                        this.currentPosition = pos;
                        this.currentValue = pos + 1;
                        this.nodeEntered = false;
                        return true;
                    }
                    pos = this.skipNode(lastNode, pos + 1);
                    break;
                }
                case 110: {
                    if (level == levelTarget) {
                        this.currentNode = this.currentPosition = pos;
                        this.currentValue = this.getValuePosForNode(this.currentNode);
                        this.nodeEntered = false;
                        return true;
                    }
                    lastNode = pos;
                    pos = this.skipNode(pos, this.getValuePosForNode(pos));
                    ++level;
                    break;
                }
                default: {
                    throw new ParseException("unknown op '" + op + '\'', pos);
                }
            }
        }
        return false;
    }

    private int getValuePosForNode(int node) {
        return node + 4 + 3 * (this.messageData[node + 1] & 255);
    }

    private int skipNode(int nodePos, int valPos) {
        if (nodePos + 4 >= this.dataLen) {
            return this.dataLen;
        }
        int attNo = this.messageData[nodePos + 1] & 255;
        nodePos += 4;
        int i = 0;
        while (i < attNo) {
            switch (this.messageData[nodePos] & 255) {
                int slen;
                case 102: 
                case 105: {
                    valPos += 4;
                    break;
                }
                case 100: 
                case 108: {
                    valPos += 8;
                    break;
                }
                case 115: {
                    slen = ((this.messageData[valPos] & 255) << 8) + (this.messageData[valPos + 1] & 255);
                    valPos += slen + 2;
                    break;
                }
                case 83: {
                    valPos += 2;
                    break;
                }
                case 73: {
                    slen = ((this.messageData[valPos] & 255) << 8) + (this.messageData[valPos + 1] & 255);
                    valPos += slen * 4 + 2;
                }
            }
            nodePos += 3;
            ++i;
        }
        return valPos;
    }

    private int getIValue(int pos) {
        return ((this.messageData[pos] & 255) << 24) + ((this.messageData[pos + 1] & 255) << 16) + ((this.messageData[pos + 2] & 255) << 8) + (this.messageData[pos + 3] & 255);
    }

    private long getLValue(int pos) {
        long v = (((long)this.messageData[pos] & 255) << 56) + (((long)this.messageData[pos + 1] & 255) << 48) + (((long)this.messageData[pos + 2] & 255) << 40) + (((long)this.messageData[pos + 3] & 255) << 32) + (((long)this.messageData[pos + 4] & 255) << 24) + (((long)this.messageData[pos + 5] & 255) << 16) + (((long)this.messageData[pos + 6] & 255) << 8) + ((long)this.messageData[pos + 7] & 255);
        return v;
    }

    private String getSValue(int pos, int length) throws ParseException {
        int end = pos + length;
        if (end > this.dataLen) {
            throw new ParseException("unexpected EOF", pos);
        }
        char[] buf = new char[length];
        int index = 0;
        while (pos < end) {
            int c = this.messageData[pos] & 255;
            switch (c >> 4) {
                int char2;
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    ++pos;
                    buf[index++] = (char)c;
                    break;
                }
                case 12: 
                case 13: {
                    if ((pos += 2) > end) {
                        throw new ParseException("malformed UTF-8", pos);
                    }
                    char2 = this.messageData[pos - 1] & 255;
                    if ((char2 & 192) != 128) {
                        throw new ParseException("malformed UTF-8", pos - 2);
                    }
                    buf[index++] = (char)((c & 31) << 6 | char2 & 63);
                    break;
                }
                case 14: {
                    if ((pos += 3) > end) {
                        throw new ParseException("malformed UTF-8", pos);
                    }
                    char2 = this.messageData[pos - 2] & 255;
                    int char3 = this.messageData[pos - 1] & 255;
                    if ((char2 & 192) != 128 || (char3 & 192) != 128) {
                        throw new ParseException("malformed UTF-8", pos - 3);
                    }
                    buf[index++] = (char)((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63) << 0);
                    break;
                }
                default: {
                    throw new ParseException("malformed UTF-8", pos);
                }
            }
        }
        return new String(buf, 0, index);
    }

    private String getCValue(int pos) throws ParseException {
        int id = ((this.messageData[pos] & 255) << 8) + (this.messageData[pos + 1] & 255);
        return this.getName(id);
    }

    private String getName(int id) throws ParseException {
        if (id >= this.aliases.length || this.aliases[id] == null) {
            throw new ParseException("no alias for id " + id, this.currentPosition);
        }
        return this.aliases[id];
    }

    @Override
    public boolean nextNode(String name, boolean isRequired) throws ParseException {
        int oldPos = this.currentPosition;
        int oldNode = this.currentNode;
        int oldValue = this.currentValue;
        boolean oldEntered = this.nodeEntered;
        while (this.nextNode(false)) {
            if (!this.isNode(name)) continue;
            return true;
        }
        this.currentPosition = oldPos;
        this.currentNode = oldNode;
        this.currentValue = oldValue;
        this.nodeEntered = oldEntered;
        if (isRequired) {
            throw new ParseException("node '" + name + "' not found", this.currentPosition);
        }
        return false;
    }

    @Override
    public String getNodeName() throws ParseException {
        if (this.currentNode < 0) {
            throw new ParseException("before first node", 0);
        }
        int nameID = ((this.messageData[this.currentNode + 2] & 255) << 8) + (this.messageData[this.currentNode + 3] & 255);
        return this.getName(nameID);
    }

    @Override
    public boolean isNode() throws ParseException {
        if (this.currentNode >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNode(String name) throws ParseException {
        if (this.currentNode < 0) {
            return false;
        }
        int nameID = ((this.messageData[this.currentNode + 2] & 255) << 8) + (this.messageData[this.currentNode + 3] & 255);
        if (nameID == this.getAlias(name)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean enterNode() throws ParseException {
        if (this.nodeEntered || this.currentNode < 0) {
            return false;
        }
        if (this.nodeLevel >= this.nodeStack.length) {
            int newSize = this.nodeStack.length + 10;
            this.nodeStack = ArrayUtils.setSize(this.nodeStack, newSize);
        }
        this.nodeStack[this.nodeLevel++] = this.currentPosition;
        this.nodeEntered = true;
        return true;
    }

    @Override
    public boolean exitNode() throws ParseException {
        if (this.nodeLevel > 0) {
            --this.nodeLevel;
            this.currentPosition = this.nodeStack[this.nodeLevel];
            if ((this.messageData[this.currentPosition] & 255) == 84) {
                this.currentValue = this.currentPosition + 1;
            } else {
                this.currentNode = this.currentPosition;
                this.currentValue = this.getValuePosForNode(this.currentNode);
            }
            this.nodeEntered = false;
            return true;
        }
        return false;
    }

    @Override
    public int getAttributeCount() {
        if (this.currentNode >= 0) {
            return this.messageData[this.currentNode + 1] & 255;
        }
        return 0;
    }

    @Override
    public String getAttributeName(int index) throws ParseException {
        int count = this.getAttributeCount();
        if (index >= count || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
        }
        int pos = this.currentNode + 5 + index * 3;
        int nid = ((this.messageData[pos] & 255) << 8) + (this.messageData[pos + 1] & 255);
        return this.getName(nid);
    }

    private Object getAttributeAsObject(int id) throws ParseException {
        if (this.currentNode < 0) {
            return null;
        }
        int attNo = this.messageData[this.currentNode + 1] & 255;
        int pos = this.currentNode + 4;
        int valPos = this.currentValue;
        int i = 0;
        while (i < attNo) {
            int nid = ((this.messageData[pos + 1] & 255) << 8) + (this.messageData[pos + 2] & 255);
            switch (this.messageData[pos] & 255) {
                int slen;
                case 105: {
                    if (nid == id) {
                        return Integer.toString(this.getIValue(valPos));
                    }
                    valPos += 4;
                    break;
                }
                case 108: {
                    if (nid == id) {
                        return Long.toString(this.getLValue(valPos));
                    }
                    valPos += 8;
                    break;
                }
                case 102: {
                    if (nid == id) {
                        return Float.toString(Float.intBitsToFloat(this.getIValue(valPos)));
                    }
                    valPos += 4;
                    break;
                }
                case 100: {
                    if (nid == id) {
                        return Double.toString(Double.longBitsToDouble(this.getLValue(valPos)));
                    }
                    valPos += 8;
                    break;
                }
                case 115: {
                    slen = ((this.messageData[valPos] & 255) << 8) + (this.messageData[valPos + 1] & 255);
                    if (nid == id) {
                        return this.getSValue(valPos + 2, slen);
                    }
                    valPos += slen + 2;
                    break;
                }
                case 83: {
                    if (nid == id) {
                        return this.getCValue(valPos);
                    }
                    valPos += 2;
                    break;
                }
                case 73: {
                    slen = ((this.messageData[valPos] & 255) << 8) + (this.messageData[valPos + 1] & 255);
                    if (nid == id) {
                        return this.getAttributeAsIntArray(valPos + 2, slen);
                    }
                    valPos += slen * 4 + 2;
                }
            }
            pos += 3;
            ++i;
        }
        return null;
    }

    private String getAttributeAsString(int id) throws ParseException {
        Object aVal = this.getAttributeAsObject(id);
        if (aVal == null) {
            return null;
        }
        if (aVal instanceof int[]) {
            int[] tmp = (int[])aVal;
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            int i = 0;
            int n = tmp.length;
            while (i < n) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append("" + tmp[i]);
                ++i;
            }
            sb.append(']');
            return sb.toString();
        }
        if (aVal instanceof String) {
            return (String)aVal;
        }
        throw new ParseException("Illegal object type: " + aVal, this.currentPosition);
    }

    @Override
    protected String getAttribute(String name, String defaultValue, boolean isRequired) throws ParseException {
        String value;
        Integer id = (Integer)this.nameLookup.get(name);
        String string = value = id != null ? this.getAttributeAsString(id) : null;
        if (value == null) {
            if (isRequired) {
                throw new ParseException("attribute " + name + " not found", this.currentPosition);
            }
            return defaultValue;
        }
        return value;
    }

    private int[] getAttributeAsIntArray(int valPos, int slen) {
        int[] tmp = new int[slen];
        int i = 0;
        int n = slen;
        while (i < n) {
            tmp[i] = this.getIValue(valPos + i * 4);
            ++i;
        }
        return tmp;
    }

    @Override
    protected int[] getAttributeAsIntArray(String name, boolean isRequired) throws ParseException {
        Object val2;
        Integer id = (Integer)this.nameLookup.get(name);
        Object object = val2 = id != null ? this.getAttributeAsObject(id) : null;
        if (val2 != null) {
            if (val2 instanceof int[]) {
                return (int[])val2;
            }
            throw new ParseException("Illegal value type, expected int[] got " + val2, 0);
        }
        return null;
    }

    public void printMessage() throws ParseException {
        this.printMessage(System.out);
    }

    public void printMessage(PrintStream out) throws ParseException {
        while (this.exitNode()) {
        }
        this.reset();
        this.printNodes(out, "");
    }

    private boolean printNodes(PrintStream out, String tab) throws ParseException {
        boolean nodes = false;
        String subTab = String.valueOf(tab) + "  ";
        while (this.nextNode(false)) {
            if (!nodes) {
                nodes = true;
                if (tab != "") {
                    out.println('>');
                }
            }
            out.print(tab);
            out.print('<');
            out.print(this.getNodeName());
            int i = 0;
            int atts = this.getAttributeCount();
            while (i < atts) {
                out.print(String.valueOf(' ') + this.getAttributeName(i) + "=\"" + this.getAttribute(i) + '\"');
                ++i;
            }
            this.enterNode();
            boolean subNodes = this.printNodes(out, subTab);
            this.exitNode();
            if (subNodes) {
                out.print(tab);
                out.print("</");
                out.print(this.getNodeName());
                out.println('>');
                continue;
            }
            out.println(" />");
        }
        return nodes;
    }
}

