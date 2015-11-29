/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.permutation;

public class PermutationOfEightGenerator {
    public static final int TOTAL_PERMUTATIONS = 40320;
    private int[] current = new int[8];
    private int remaining;

    public PermutationOfEightGenerator() {
        int i = 0;
        while (i < 8) {
            this.current[i] = i++;
        }
        this.remaining = 40320;
    }

    public boolean hasNext() {
        if (this.remaining > 0) {
            return true;
        }
        return false;
    }

    public int[] next() {
        if (this.remaining < 40320) {
            int j = this.current.length - 2;
            while (this.current[j] > this.current[j + 1]) {
                --j;
            }
            int k = this.current.length - 1;
            while (this.current[j] > this.current[k]) {
                --k;
            }
            int swap = this.current[k];
            this.current[k] = this.current[j];
            this.current[j] = swap;
            int r = this.current.length - 1;
            int s = j + 1;
            while (r > s) {
                swap = this.current[s];
                this.current[s] = this.current[r];
                this.current[r] = swap;
                --r;
                ++s;
            }
        }
        --this.remaining;
        return (int[])this.current.clone();
    }
}

