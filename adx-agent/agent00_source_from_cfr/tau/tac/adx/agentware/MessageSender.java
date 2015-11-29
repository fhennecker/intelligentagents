/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agentware;

import com.botbox.util.ArrayQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.tasim.aw.Message;
import tau.tac.adx.agentware.ServerConnection;

public class MessageSender
extends Thread {
    private static final Logger log = Logger.getLogger(MessageSender.class.getName());
    private final ServerConnection connection;
    private final ArrayQueue messageQueue = new ArrayQueue();
    private boolean isClosed = false;

    public MessageSender(ServerConnection connection, String name) {
        super(name);
        this.connection = connection;
        this.start();
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public synchronized void close() {
        if (!this.isClosed) {
            this.isClosed = true;
            this.messageQueue.clear();
            this.messageQueue.add(null);
            this.notify();
        }
    }

    public synchronized boolean addMessage(Message message) {
        if (this.isClosed) {
            return false;
        }
        this.messageQueue.add(message);
        this.notify();
        return true;
    }

    private synchronized Message nextMessage() {
        while (this.messageQueue.size() == 0) {
            try {
                this.wait();
                continue;
            }
            catch (InterruptedException var1_1) {
                // empty catch block
            }
        }
        return (Message)this.messageQueue.remove(0);
    }

    @Override
    public void run() {
        do {
            Message msg = null;
            try {
                msg = this.nextMessage();
                if (msg == null) continue;
                this.connection.deliverMessage(msg);
                continue;
            }
            catch (ThreadDeath e) {
                log.log(Level.SEVERE, "message thread died", e);
                throw e;
            }
            catch (Throwable e) {
                log.log(Level.SEVERE, "could not handle message " + msg, e);
            }
        } while (!this.isClosed);
    }
}

