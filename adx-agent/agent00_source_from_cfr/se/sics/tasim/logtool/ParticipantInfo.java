/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.logtool;

public class ParticipantInfo {
    private int index;
    private String address;
    private int id;
    private String name;
    private int role;

    public ParticipantInfo(int index, String address, int id, String name, int role) {
        this.index = index;
        this.address = address;
        this.id = id;
        this.name = name == null ? address : name;
        this.role = role;
    }

    public int getIndex() {
        return this.index;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }

    public boolean isBuiltinAgent() {
        if (this.id < 0) {
            return true;
        }
        return false;
    }

    public int getUserID() {
        return this.id;
    }

    public int getRole() {
        return this.role;
    }
}

