/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.logtool;

public class ValueSet {
    private int min = -1;
    private int max = -1;
    private int[] intervals;
    private int intervalsCount;
    private int[] values;
    private int valuesCount;

    public ValueSet(String info) {
        this.parseInfo(info);
    }

    private void parseInfo(String info) {
        int len = info.length();
        int index = 0;
        int currentValue = -1;
        int startValue = -1;
        while (index < len) {
            char c;
            if ((c = info.charAt(index++)) <= ' ') {
                while (index < len && (c = info.charAt(index++)) <= ' ') {
                }
                if (c >= '0' && c <= '9' && currentValue >= 0) {
                    --index;
                    c = ',';
                } else if (c <= ' ') {
                    c = ',';
                }
            }
            if (c >= '0' && c <= '9') {
                if (currentValue >= 0) {
                    currentValue = currentValue * 10 + (c - 48);
                    continue;
                }
                currentValue = c - 48;
                continue;
            }
            if (c == ',') {
                if (currentValue >= 0) {
                    if (startValue >= 0) {
                        this.addInterval(startValue, currentValue);
                    } else {
                        this.addValue(currentValue);
                    }
                    startValue = -1;
                    currentValue = -1;
                    continue;
                }
                if (startValue < 0) continue;
                throw new IllegalArgumentException("no interval end: " + startValue + "-...");
            }
            if (c == '-') {
                if (startValue >= 0) {
                    throw new IllegalArgumentException("continuous interval: " + startValue + "-...-");
                }
                if (currentValue < 0) {
                    startValue = 1;
                    continue;
                }
                startValue = currentValue;
                currentValue = -1;
                continue;
            }
            throw new IllegalArgumentException("illegal character: " + c);
        }
        if (currentValue >= 0) {
            if (startValue >= 0) {
                this.addInterval(startValue, currentValue);
            } else {
                this.addValue(currentValue);
            }
        } else if (startValue >= 0) {
            throw new IllegalArgumentException("no interval end: " + startValue + "-...");
        }
    }

    private void addInterval(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("illegal interval: " + start + '-' + end);
        }
        if (start == end) {
            this.addValue(start);
            return;
        }
        if (this.intervals == null) {
            this.intervals = new int[10];
        } else if (this.intervals.length == this.intervalsCount) {
            this.intervals = this.setSize(this.intervals, this.intervalsCount + 10);
        }
        this.intervals[this.intervalsCount] = start;
        this.intervals[this.intervalsCount + 1] = end;
        this.intervalsCount += 2;
        this.setMaxMin(start, end);
    }

    private void addValue(int value) {
        if (this.values == null) {
            this.values = new int[10];
        } else if (this.values.length == this.valuesCount) {
            this.values = this.setSize(this.values, this.valuesCount + 10);
        }
        this.values[this.valuesCount++] = value;
        this.setMaxMin(value, value);
    }

    private void setMaxMin(int start, int end) {
        if (start < this.min || this.min < 0) {
            this.min = start;
        }
        if (end > this.max || this.max < 0) {
            this.max = end;
        }
    }

    private int[] setSize(int[] array, int size) {
        int[] tmp = new int[size];
        System.arraycopy(array, 0, tmp, 0, array.length);
        return tmp;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public boolean hasValues() {
        if (this.min >= 0 && this.max >= this.min) {
            return true;
        }
        return false;
    }

    public boolean isIncluded(int value) {
        if (value > this.max || value < this.min) {
            return false;
        }
        int i = 0;
        int n = this.intervalsCount;
        while (i < n) {
            if (value >= this.intervals[i] && value <= this.intervals[i + 1]) {
                return true;
            }
            i += 2;
        }
        i = 0;
        n = this.valuesCount;
        while (i < n) {
            if (this.values[i] == value) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean comma = false;
        int i = 0;
        int n = this.intervalsCount;
        while (i < n) {
            if (comma) {
                sb.append(',');
            } else {
                comma = true;
            }
            sb.append(this.intervals[i]).append('-').append(this.intervals[i + 1]);
            i += 2;
        }
        i = 0;
        n = this.valuesCount;
        while (i < n) {
            if (comma) {
                sb.append(',');
            } else {
                comma = true;
            }
            sb.append(this.values[i]);
            ++i;
        }
        return sb.toString();
    }
}

