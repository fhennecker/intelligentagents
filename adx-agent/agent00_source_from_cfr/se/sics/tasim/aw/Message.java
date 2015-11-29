/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.aw;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class Message
implements Transportable {
    private String sender;
    private String receiver;
    private Transportable content;

    public Message(String receiver, Transportable content) {
        if (receiver == null) {
            throw new NullPointerException("receiver");
        }
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.receiver = receiver;
        this.content = content;
    }

    public Message(String sender, String receiver, Transportable content) {
        this(receiver, content);
        this.sender = sender;
    }

    public Message() {
    }

    public String getSender() {
        return this.sender;
    }

    public synchronized void setSender(String sender) {
        if (this.sender != null) {
            throw new IllegalStateException("sender already set");
        }
        this.sender = sender;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public Transportable getContent() {
        return this.content;
    }

    public Message createReply(Transportable content) {
        return new Message(this.receiver, this.sender, content);
    }

    public String toString() {
        return "Message[" + this.sender + ',' + this.receiver + ',' + this.content + ']';
    }

    @Override
    public String getTransportName() {
        return "message";
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.receiver != null) {
            throw new IllegalStateException("already initialized");
        }
        String receiver = reader.getAttribute("receiver");
        String sender = reader.getAttribute("sender");
        reader.nextNode(true);
        this.content = reader.readTransportable();
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public void write(TransportWriter writer) {
        if (this.receiver == null) {
            throw new IllegalStateException("not initalized");
        }
        if (this.sender == null) {
            throw new IllegalStateException("no sender");
        }
        writer.attr("sender", this.sender);
        writer.attr("receiver", this.receiver);
        writer.write(this.content);
    }
}

