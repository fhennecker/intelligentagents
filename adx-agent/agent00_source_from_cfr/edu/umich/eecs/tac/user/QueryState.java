/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

public enum QueryState {
    NON_SEARCHING(false, false),
    INFORMATIONAL_SEARCH(true, false),
    FOCUS_LEVEL_ZERO(true, true),
    FOCUS_LEVEL_ONE(true, true),
    FOCUS_LEVEL_TWO(true, true),
    TRANSACTED(false, false);
    
    private boolean searching;
    private boolean transacting;

    private QueryState(String searching, int transacting, boolean bl, boolean bl2) {
        this.searching = searching;
        this.transacting = transacting;
    }

    public boolean isSearching() {
        return this.searching;
    }

    public boolean isTransacting() {
        return this.transacting;
    }
}

