/*
 * Decompiled with CFR 0_110.
 */
package com.botbox.util;

import java.lang.reflect.Array;

public class ArrayUtils {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static int indexOf(Object[] array, Object element) {
        if (array == null) return -1;
        if (element == null) {
            int i = 0;
            int n = array.length;
            while (i < n) {
                if (array[i] == null) {
                    return i;
                }
                ++i;
            }
            return -1;
        } else {
            int i = 0;
            int n = array.length;
            while (i < n) {
                if (element.equals(array[i])) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    public static int indexOf(Object[] array, int start, int end, Object element) {
        if (element == null) {
            int i = start;
            while (i < end) {
                if (array[i] == null) {
                    return i;
                }
                ++i;
            }
        } else {
            int i = start;
            while (i < end) {
                if (element.equals(array[i])) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static int keyValuesIndexOf(Object[] array, int nth, Object key) {
        if (array == null) return -1;
        if (key == null) {
            int i = 0;
            int n = array.length;
            while (i < n) {
                if (array[i] == null) {
                    return i;
                }
                i += nth;
            }
            return -1;
        } else {
            int i = 0;
            int n = array.length;
            while (i < n) {
                if (key.equals(array[i])) {
                    return i;
                }
                i += nth;
            }
        }
        return -1;
    }

    public static int keyValuesIndexOf(Object[] array, int nth, int start, int end, Object key) {
        if (key == null) {
            int i = start;
            while (i < end) {
                if (array[i] == null) {
                    return i;
                }
                i += nth;
            }
        } else {
            int i = start;
            while (i < end) {
                if (key.equals(array[i])) {
                    return i;
                }
                i += nth;
            }
        }
        return -1;
    }

    public static int indexOf(int[] array, int element) {
        if (array != null) {
            int i = 0;
            int n = array.length;
            while (i < n) {
                if (element == array[i]) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    public static int indexOf(int[] array, int start, int end, int element) {
        int i = start;
        while (i < end) {
            if (element == array[i]) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int keyValuesIndexOf(int[] array, int nth, int key) {
        if (array != null) {
            int i = 0;
            int n = array.length;
            while (i < n) {
                if (key == array[i]) {
                    return i;
                }
                i += nth;
            }
        }
        return -1;
    }

    public static int keyValuesIndexOf(int[] array, int nth, int start, int end, int key) {
        int i = start;
        while (i < end) {
            if (key == array[i]) {
                return i;
            }
            i += nth;
        }
        return -1;
    }

    public static int[] add(int[] array, int value) {
        if (array == null) {
            return new int[]{value};
        }
        int[] tmp = new int[array.length + 1];
        System.arraycopy(array, 0, tmp, 0, array.length);
        tmp[array.length] = value;
        return tmp;
    }

    public static Object[] add(Object[] array, Object value) {
        Object[] tmp = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
        System.arraycopy(array, 0, tmp, 0, array.length);
        tmp[array.length] = value;
        return tmp;
    }

    public static Object[] add(Class componentType, Object[] array, Object value) {
        Object[] tmp;
        if (array == null) {
            tmp = (Object[])Array.newInstance(componentType, 1);
        } else {
            tmp = (Object[])Array.newInstance(componentType, array.length + 1);
            System.arraycopy(array, 0, tmp, 0, array.length);
        }
        tmp[tmp.length - 1] = value;
        return tmp;
    }

    public static Object[] insert(Object[] array, int index, int number) {
        if (index < 0 || index > array.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        Object[] tmp = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + number);
        if (index > 0) {
            System.arraycopy(array, 0, tmp, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, tmp, index + number, array.length - index);
        }
        return tmp;
    }

    public static Object[] insert(Class componentType, Object[] array, int index, int number) {
        int len;
        int n = len = array == null ? 0 : array.length;
        if (index < 0 || index > len) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        Object[] tmp = (Object[])Array.newInstance(componentType, len + number);
        if (index > 0) {
            System.arraycopy(array, 0, tmp, 0, index);
        }
        if (index < len) {
            System.arraycopy(array, index, tmp, index + number, array.length - index);
        }
        return tmp;
    }

    public static Object[] concat(Object[] array1, Object[] array2) {
        if (array1 == null) {
            return array2;
        }
        if (array2 == null) {
            return array1;
        }
        Object[] tmp = (Object[])Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
        System.arraycopy(array1, 0, tmp, 0, array1.length);
        System.arraycopy(array2, 0, tmp, array1.length, array2.length);
        return tmp;
    }

    public static Object[] remove(Object[] array, int index) {
        if (index < 0 || index >= array.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (array.length == 1) {
            return null;
        }
        Object[] tmp = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
        if (index > 0) {
            System.arraycopy(array, 0, tmp, 0, index);
        }
        if (index < tmp.length) {
            System.arraycopy(array, index + 1, tmp, index, tmp.length - index);
        }
        return tmp;
    }

    public static Object[] remove(Object[] array, Object element) {
        int index = ArrayUtils.indexOf(array, element);
        return index >= 0 ? ArrayUtils.remove(array, index) : array;
    }

    public static Object[] remove(Object[] array, int index, int number) {
        if (index < 0 || index >= array.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (array.length - index < number) {
            number = array.length - index;
        }
        if (index == 0 && array.length == number) {
            return null;
        }
        Object[] tmp = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length - number);
        if (index > 0) {
            System.arraycopy(array, 0, tmp, 0, index);
        }
        if (index + number < array.length) {
            System.arraycopy(array, index + number, tmp, index, array.length - index - number);
        }
        return tmp;
    }

    public static Object[] setSize(Object[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        Object[] tmp = (Object[])Array.newInstance(array.getClass().getComponentType(), newSize);
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static char[] setSize(char[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        char[] tmp = new char[newSize];
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static byte[] setSize(byte[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        byte[] tmp = new byte[newSize];
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static float[] setSize(float[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        float[] tmp = new float[newSize];
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static double[] setSize(double[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        double[] tmp = new double[newSize];
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static boolean[] setSize(boolean[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        boolean[] tmp = new boolean[newSize];
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static int[] setSize(int[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        int[] tmp = new int[newSize];
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static long[] setSize(long[] array, int newSize) {
        if (array.length == newSize) {
            return array;
        }
        long[] tmp = new long[newSize];
        if (newSize > array.length) {
            System.arraycopy(array, 0, tmp, 0, array.length);
        } else {
            System.arraycopy(array, 0, tmp, 0, newSize);
        }
        return tmp;
    }

    public static String toString(Object[] array) {
        if (array == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(array.getClass().getComponentType().getName()).append('[');
        if (array.length > 0) {
            sb.append(array[0]);
            int i = 1;
            while (i < array.length) {
                sb.append(',').append(array[i]);
                ++i;
            }
        }
        return sb.append(']').toString();
    }
}

