/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

public class AgentInfo {
    private int id;
    private int parentID;
    private String name;
    private String password;

    public AgentInfo(String name, String password, int id, int parentID) {
        if (id < 0) {
            throw new IllegalArgumentException("id can not be below zero");
        }
        if (password == null) {
            throw new NullPointerException("Password can not be null");
        }
        this.name = name;
        this.password = password;
        this.id = id;
        this.parentID = parentID;
    }

    public int getID() {
        return this.id;
    }

    public boolean hasParent() {
        if (this.parentID >= 0) {
            return true;
        }
        return false;
    }

    public int getParent() {
        return this.parentID;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
}

