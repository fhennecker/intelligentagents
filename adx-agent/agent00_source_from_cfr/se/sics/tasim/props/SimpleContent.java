/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.props;

import com.botbox.util.ArrayUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.logging.Logger;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public abstract class SimpleContent
implements Transportable,
Serializable {
    private static final long serialVersionUID = 3025394464668122965L;
    private static final Logger log = Logger.getLogger(SimpleContent.class.getName());
    private static final int NAME = 0;
    private static final int VALUE = 1;
    private static final int PARTS = 2;
    private Object[] attributePairs;
    private int attributeCount;
    private boolean isLocked = false;

    protected SimpleContent() {
    }

    public String getAttribute(String name) {
        return this.getAttribute(name, null);
    }

    public String getAttribute(String name, String defaultValue) {
        Object value = this.get(name);
        return value == null ? defaultValue : value.toString();
    }

    public int getAttributeAsInt(String name, int defaultValue) {
        Object value = this.get(name);
        if (value != null) {
            if (value instanceof Integer) {
                return (Integer)value;
            }
            try {
                if (value instanceof Long) {
                    return (int)((Long)value).longValue();
                }
                return Integer.parseInt(value.toString());
            }
            catch (Exception e) {
                log.warning("attribute '" + name + "' has a non-integer value '" + value + '\'');
            }
        }
        return defaultValue;
    }

    public long getAttributeAsLong(String name, long defaultValue) {
        Object value = this.get(name);
        if (value != null) {
            if (value instanceof Long) {
                return (Long)value;
            }
            if (value instanceof Integer) {
                return ((Integer)value).intValue();
            }
            try {
                return Long.parseLong(value.toString());
            }
            catch (Exception e) {
                log.warning("attribute '" + name + "' has a non-long value '" + value + '\'');
            }
        }
        return defaultValue;
    }

    public float getAttributeAsFloat(String name, float defaultValue) {
        Object value = this.get(name);
        if (value != null) {
            if (value instanceof Float) {
                return ((Float)value).floatValue();
            }
            try {
                if (value instanceof Integer) {
                    return ((Integer)value).intValue();
                }
                return Float.parseFloat(value.toString());
            }
            catch (Exception e) {
                log.warning("attribute '" + name + "' has a non-float value '" + value + '\'');
            }
        }
        return defaultValue;
    }

    private Object get(String name) {
        int index = ArrayUtils.keyValuesIndexOf(this.attributePairs, 2, 0, this.attributeCount * 2, name);
        return index >= 0 ? this.attributePairs[index + 1] : null;
    }

    public void setAttribute(String name, String value) {
        if (value == null) {
            this.removeAttribute(name);
        } else {
            this.set(name, value);
        }
    }

    public void setAttribute(String name, int value) {
        this.set(name, new Integer(value));
    }

    public void setAttribute(String name, long value) {
        this.set(name, new Long(value));
    }

    public void setAttribute(String name, float value) {
        this.set(name, new Float(value));
    }

    private void set(String name, Object value) {
        if (this.isLocked) {
            throw new IllegalStateException("locked");
        }
        int index = ArrayUtils.keyValuesIndexOf(this.attributePairs, 2, 0, this.attributeCount * 2, name);
        if (index >= 0) {
            this.attributePairs[index + 1] = value;
        } else {
            index = this.attributeCount * 2;
            if (this.attributePairs == null) {
                this.attributePairs = new Object[8];
            } else if (index == this.attributePairs.length) {
                this.attributePairs = ArrayUtils.setSize(this.attributePairs, index + 20);
            }
            this.attributePairs[index + 0] = name;
            this.attributePairs[index + 1] = value;
            ++this.attributeCount;
        }
    }

    public void removeAttribute(String name) {
        if (this.isLocked) {
            throw new IllegalStateException("locked");
        }
        int index = ArrayUtils.keyValuesIndexOf(this.attributePairs, 2, 0, this.attributeCount * 2, name);
        if (index >= 0) {
            --this.attributeCount;
            int lastIndex = this.attributeCount * 2;
            this.attributePairs[index + 0] = this.attributePairs[lastIndex + 0];
            this.attributePairs[index + 1] = this.attributePairs[lastIndex + 1];
            this.attributePairs[lastIndex + 0] = null;
            this.attributePairs[lastIndex + 1] = null;
        }
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public void lock() {
        this.isLocked = true;
    }

    protected StringBuffer params(StringBuffer buf) {
        buf.append('[');
        if (this.attributeCount > 0) {
            buf.append(this.attributePairs[0]).append('=').append(this.attributePairs[1]);
            int i = 2;
            int n = this.attributeCount * 2;
            while (i < n) {
                buf.append(',').append(this.attributePairs[i + 0]).append('=').append(this.attributePairs[i + 1]);
                i += 2;
            }
        }
        return buf.append(']');
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        boolean lock;
        if (this.isLocked) {
            throw new IllegalStateException("locked");
        }
        boolean bl = lock = reader.getAttributeAsInt("lock", 0) > 0;
        if (reader.nextNode("params", false)) {
            int i = 0;
            int n = reader.getAttributeCount();
            while (i < n) {
                String name = reader.getAttributeName(i);
                this.setAttribute(name, reader.getAttribute(i));
                ++i;
            }
        }
        this.isLocked = lock;
    }

    @Override
    public void write(TransportWriter writer) {
        if (this.isLocked) {
            writer.attr("lock", 1);
        }
        if (this.attributeCount > 0) {
            writer.node("params");
            int i = 0;
            int n = this.attributeCount * 2;
            while (i < n) {
                String name = this.attributePairs[i + 0].toString();
                Object value = this.attributePairs[i + 1];
                if (value instanceof Integer) {
                    writer.attr(name, (Integer)value);
                } else if (value instanceof Long) {
                    writer.attr(name, (Long)value);
                } else if (value instanceof Float) {
                    writer.attr(name, ((Float)value).floatValue());
                } else {
                    writer.attr(name, value.toString());
                }
                i += 2;
            }
            writer.endNode("params");
        }
    }
}

