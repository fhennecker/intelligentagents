/*
 * Decompiled with CFR 0_110.
 */
package lombok.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ClassFileMetaData {
    private static final byte UTF8 = 1;
    private static final byte INTEGER = 3;
    private static final byte FLOAT = 4;
    private static final byte LONG = 5;
    private static final byte DOUBLE = 6;
    private static final byte CLASS = 7;
    private static final byte STRING = 8;
    private static final byte FIELD = 9;
    private static final byte METHOD = 10;
    private static final byte INTERFACE_METHOD = 11;
    private static final byte NAME_TYPE = 12;
    private static final byte METHOD_HANDLE = 15;
    private static final byte METHOD_TYPE = 16;
    private static final byte INVOKE_DYNAMIC = 18;
    private static final int NOT_FOUND = -1;
    private static final int START_OF_CONSTANT_POOL = 8;
    private final byte[] byteCode;
    private final int maxPoolSize;
    private final int[] offsets;
    private final byte[] types;
    private final String[] utf8s;
    private final int endOfPool;

    public ClassFileMetaData(byte[] byteCode) {
        this.byteCode = byteCode;
        this.maxPoolSize = this.readValue(8);
        this.offsets = new int[this.maxPoolSize];
        this.types = new byte[this.maxPoolSize];
        this.utf8s = new String[this.maxPoolSize];
        int position = 10;
        block8 : for (int i = 1; i < this.maxPoolSize; ++i) {
            byte type;
            this.types[i] = type = byteCode[position];
            this.offsets[i] = ++position;
            switch (type) {
                case 1: {
                    int length = this.readValue(position);
                    this.utf8s[i] = this.decodeString(position += 2, length);
                    position += length;
                    continue block8;
                }
                case 7: 
                case 8: 
                case 16: {
                    position += 2;
                    continue block8;
                }
                case 15: {
                    position += 3;
                    continue block8;
                }
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 18: {
                    position += 4;
                    continue block8;
                }
                case 5: 
                case 6: {
                    position += 8;
                    ++i;
                    continue block8;
                }
                case 0: {
                    continue block8;
                }
                default: {
                    throw new AssertionError((Object)("Unknown constant pool type " + type));
                }
            }
        }
        this.endOfPool = position;
    }

    private String decodeString(int pos, int size) {
        int end = pos + size;
        StringBuilder result = new StringBuilder(size);
        while (pos < end) {
            int first;
            int x;
            int y;
            if ((first = this.byteCode[pos++] & 255) < 128) {
                result.append((char)first);
                continue;
            }
            if ((first & 224) == 192) {
                x = (first & 31) << 6;
                y = this.byteCode[pos++] & 63;
                result.append((char)(x | y));
                continue;
            }
            x = (first & 15) << 12;
            y = (this.byteCode[pos++] & 63) << 6;
            int z = this.byteCode[pos++] & 63;
            result.append((char)(x | y | z));
        }
        return result.toString();
    }

    public boolean containsUtf8(String value) {
        return this.findUtf8(value) != -1;
    }

    public boolean usesClass(String className) {
        return this.findClass(className) != -1;
    }

    public boolean usesField(String className, String fieldName) {
        int classIndex = this.findClass(className);
        if (classIndex == -1) {
            return false;
        }
        int fieldNameIndex = this.findUtf8(fieldName);
        if (fieldNameIndex == -1) {
            return false;
        }
        for (int i = 1; i < this.maxPoolSize; ++i) {
            int nameAndTypeIndex;
            if (this.types[i] != 9 || this.readValue(this.offsets[i]) != classIndex || this.readValue(this.offsets[nameAndTypeIndex = this.readValue(this.offsets[i] + 2)]) != fieldNameIndex) continue;
            return true;
        }
        return false;
    }

    public boolean usesMethod(String className, String methodName) {
        int classIndex = this.findClass(className);
        if (classIndex == -1) {
            return false;
        }
        int methodNameIndex = this.findUtf8(methodName);
        if (methodNameIndex == -1) {
            return false;
        }
        for (int i = 1; i < this.maxPoolSize; ++i) {
            int nameAndTypeIndex;
            if (!this.isMethod(i) || this.readValue(this.offsets[i]) != classIndex || this.readValue(this.offsets[nameAndTypeIndex = this.readValue(this.offsets[i] + 2)]) != methodNameIndex) continue;
            return true;
        }
        return false;
    }

    public boolean usesMethod(String className, String methodName, String descriptor) {
        int classIndex = this.findClass(className);
        if (classIndex == -1) {
            return false;
        }
        int nameAndTypeIndex = this.findNameAndType(methodName, descriptor);
        if (nameAndTypeIndex == -1) {
            return false;
        }
        for (int i = 1; i < this.maxPoolSize; ++i) {
            if (!this.isMethod(i) || this.readValue(this.offsets[i]) != classIndex || this.readValue(this.offsets[i] + 2) != nameAndTypeIndex) continue;
            return true;
        }
        return false;
    }

    public boolean containsStringConstant(String value) {
        int index = this.findUtf8(value);
        if (index == -1) {
            return false;
        }
        for (int i = 1; i < this.maxPoolSize; ++i) {
            if (this.types[i] != 8 || this.readValue(this.offsets[i]) != index) continue;
            return true;
        }
        return false;
    }

    public boolean containsLong(long value) {
        for (int i = 1; i < this.maxPoolSize; ++i) {
            if (this.types[i] != 5 || this.readLong(i) != value) continue;
            return true;
        }
        return false;
    }

    public boolean containsDouble(double value) {
        boolean isNan = Double.isNaN(value);
        for (int i = 1; i < this.maxPoolSize; ++i) {
            double d;
            if (this.types[i] != 6 || (d = this.readDouble(i)) != value && (!isNan || !Double.isNaN(d))) continue;
            return true;
        }
        return false;
    }

    public boolean containsInteger(int value) {
        for (int i = 1; i < this.maxPoolSize; ++i) {
            if (this.types[i] != 3 || this.readInteger(i) != value) continue;
            return true;
        }
        return false;
    }

    public boolean containsFloat(float value) {
        boolean isNan = Float.isNaN(value);
        for (int i = 1; i < this.maxPoolSize; ++i) {
            float f;
            if (this.types[i] != 4 || (f = this.readFloat(i)) != value && (!isNan || !Float.isNaN(f))) continue;
            return true;
        }
        return false;
    }

    private long readLong(int index) {
        int pos = this.offsets[index];
        return (long)this.read32(pos) << 32 | (long)this.read32(pos + 4) & 0xFFFFFFFFL;
    }

    private double readDouble(int index) {
        return Double.longBitsToDouble(this.readLong(index));
    }

    private int readInteger(int index) {
        return this.read32(this.offsets[index]);
    }

    private float readFloat(int index) {
        return Float.intBitsToFloat(this.readInteger(index));
    }

    private int read32(int pos) {
        return (this.byteCode[pos] & 255) << 24 | (this.byteCode[pos + 1] & 255) << 16 | (this.byteCode[pos + 2] & 255) << 8 | this.byteCode[pos + 3] & 255;
    }

    public String getClassName() {
        return this.getClassName(this.readValue(this.endOfPool + 2));
    }

    public String getSuperClassName() {
        return this.getClassName(this.readValue(this.endOfPool + 4));
    }

    public List<String> getInterfaces() {
        int size = this.readValue(this.endOfPool + 6);
        if (size == 0) {
            return Collections.emptyList();
        }
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            result.add(this.getClassName(this.readValue(this.endOfPool + 8 + i * 2)));
        }
        return result;
    }

    public String poolContent() {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < this.maxPoolSize; ++i) {
            result.append(String.format("#%02x: ", i));
            int pos = this.offsets[i];
            switch (this.types[i]) {
                case 1: {
                    result.append("Utf8 ").append(this.utf8s[i]);
                    break;
                }
                case 7: {
                    result.append("Class ").append(this.getClassName(i));
                    break;
                }
                case 8: {
                    result.append("String \"").append(this.utf8s[this.readValue(pos)]).append("\"");
                    break;
                }
                case 3: {
                    result.append("int ").append(this.readInteger(i));
                    break;
                }
                case 4: {
                    result.append("float ").append(this.readFloat(i));
                    break;
                }
                case 9: {
                    this.appendAccess(result.append("Field "), i);
                    break;
                }
                case 10: 
                case 11: {
                    this.appendAccess(result.append("Method "), i);
                    break;
                }
                case 12: {
                    this.appendNameAndType(result.append("Name&Type "), i);
                    break;
                }
                case 5: {
                    result.append("long ").append(this.readLong(i));
                    break;
                }
                case 6: {
                    result.append("double ").append(this.readDouble(i));
                    break;
                }
                case 15: {
                    result.append("MethodHandle...");
                    break;
                }
                case 16: {
                    result.append("MethodType...");
                    break;
                }
                case 18: {
                    result.append("InvokeDynamic...");
                    break;
                }
                case 0: {
                    result.append("(cont.)");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    private void appendAccess(StringBuilder result, int index) {
        int pos = this.offsets[index];
        result.append(this.getClassName(this.readValue(pos))).append(".");
        this.appendNameAndType(result, this.readValue(pos + 2));
    }

    private void appendNameAndType(StringBuilder result, int index) {
        int pos = this.offsets[index];
        result.append(this.utf8s[this.readValue(pos)]).append(":").append(this.utf8s[this.readValue(pos + 2)]);
    }

    private String getClassName(int classIndex) {
        if (classIndex < 1) {
            return null;
        }
        return this.utf8s[this.readValue(this.offsets[classIndex])];
    }

    private boolean isMethod(int i) {
        byte type = this.types[i];
        return type == 10 || type == 11;
    }

    private int findNameAndType(String name, String descriptor) {
        int nameIndex = this.findUtf8(name);
        if (nameIndex == -1) {
            return -1;
        }
        int descriptorIndex = this.findUtf8(descriptor);
        if (descriptorIndex == -1) {
            return -1;
        }
        for (int i = 1; i < this.maxPoolSize; ++i) {
            if (this.types[i] != 12 || this.readValue(this.offsets[i]) != nameIndex || this.readValue(this.offsets[i] + 2) != descriptorIndex) continue;
            return i;
        }
        return -1;
    }

    private int findUtf8(String value) {
        for (int i = 1; i < this.maxPoolSize; ++i) {
            if (!value.equals(this.utf8s[i])) continue;
            return i;
        }
        return -1;
    }

    private int findClass(String className) {
        int index = this.findUtf8(className);
        if (index == -1) {
            return -1;
        }
        for (int i = 1; i < this.maxPoolSize; ++i) {
            if (this.types[i] != 7 || this.readValue(this.offsets[i]) != index) continue;
            return i;
        }
        return -1;
    }

    private int readValue(int position) {
        return (this.byteCode[position] & 255) << 8 | this.byteCode[position + 1] & 255;
    }
}

