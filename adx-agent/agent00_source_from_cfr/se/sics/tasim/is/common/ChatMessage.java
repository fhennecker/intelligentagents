/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import se.sics.isl.transport.TransportWriter;

public class ChatMessage {
    private long time;
    private String serverName;
    private String userName;
    private String message;

    public ChatMessage(long time, String serverName, String userName, String message) {
        this.setMessage(time, serverName, userName, message);
    }

    public long getTime() {
        return this.time;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(long time, String serverName, String userName, String message) {
        this.time = time;
        this.serverName = serverName;
        this.userName = userName;
        this.message = message;
    }

    public void writeMessage(TransportWriter writer) {
        writer.node("chat").attr("time", this.time).attr("server", this.serverName).attr("user", this.userName).attr("message", this.message).endNode("chat");
    }
}

