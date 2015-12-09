/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import java.util.logging.Logger;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;

final class ExternalAgent
extends Agent {
    private static final Logger log = Logger.getLogger(ExternalAgent.class.getName());

    @Override
    protected void simulationSetup() {
        log.fine(String.valueOf(this.getName()) + " setup with address " + this.getAddress());
    }

    @Override
    protected void simulationFinished() {
    }

    @Override
    protected void messageReceived(Message message) {
        log.severe("no connection to agent " + this.getName() + ": ignoring message " + message);
    }
}

