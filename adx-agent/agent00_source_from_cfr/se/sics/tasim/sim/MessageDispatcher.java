/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import com.botbox.util.ArrayQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.report.demand.InitialCampaignMessage;

final class MessageDispatcher
extends Thread {
    private static final boolean DEBUG = true;
    private static final Logger log = Logger.getLogger(MessageDispatcher.class.getName());
    private final Admin admin;
    private Simulation simulation;
    private ArrayQueue messageQueue = new ArrayQueue();
    private boolean isRunning = true;

    public MessageDispatcher(Admin admin, Simulation simulation, String name) {
        super(name);
        this.admin = admin;
        this.simulation = simulation;
    }

    public void startDispatcher() {
        this.start();
    }

    public void stopDispatcher() {
        this.doDeliver("stop");
    }

    private synchronized void doDeliver(Object message) {
        this.messageQueue.add(message);
        this.notify();
    }

    private synchronized Object getNext() {
        while (this.messageQueue.size() == 0) {
            try {
                this.wait(5000);
                continue;
            }
            catch (InterruptedException e) {
                log.log(Level.WARNING, "*** interrupted", e);
            }
        }
        return this.messageQueue.remove(0);
    }

    @Override
    public void run() {
        try {
            do {
                Object message = this.getNext();
                if (!this.isRunning) break;
                if (message instanceof Message) {
                    if (((Message)message).getContent() instanceof InitialCampaignMessage) {
                        boolean bl = true;
                    }
                    this.deliverMessage((Message)message);
                    continue;
                }
                if (message instanceof Delivery) {
                    ((Delivery)message).perform(this);
                    continue;
                }
                if (message instanceof SimulationAgent) {
                    this.simulation.callRecoverAgent((SimulationAgent)message);
                    continue;
                }
                if (message instanceof Runnable) {
                    this.call((Runnable)message);
                    continue;
                }
                if (message == "prepareStop") {
                    this.simulation.prepareStop();
                    continue;
                }
                if (message == "stop") {
                    this.isRunning = false;
                    this.admin.stopSimulation(this.simulation);
                    break;
                }
                log.severe("*** unknown delivery type: " + message);
            } while (this.isRunning);
        }
        finally {
            log.finer("message dispatcher " + this.getName() + " stopped");
            this.simulation = null;
            this.messageQueue.clear();
            System.gc();
        }
    }

    final void deliver(Message message) {
        this.doDeliver(message);
    }

    final void deliverToRole(SimulationAgent senderAgent, int role, Transportable content) {
        this.doDeliver(new Delivery(senderAgent, role, content));
    }

    final void callRunnable(Runnable target) {
        this.doDeliver(target);
    }

    final void callAgentUnblock(SimulationAgent agentToUnblock) {
        this.doDeliver(new Delivery(2, agentToUnblock));
    }

    final void callAgentRecovery(SimulationAgent agentToRecover) {
        this.doDeliver(agentToRecover);
    }

    final void callNextTimeUnit(int timeUnit) {
        this.doDeliver(new Delivery(1, timeUnit));
    }

    final void callPrepareStop() {
        this.doDeliver("prepareStop");
    }

    private void nextTimeUnit(int timeUnit) {
        this.simulation.callNextTimeUnit(timeUnit);
    }

    private void call(Runnable target) {
        try {
            target.run();
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "could not invoke " + target, e);
        }
    }

    private void deliverMessage(Message message) {
        String receiver = message.getReceiver();
        try {
            if ("admin".equals(receiver)) {
                log.finest("delivering " + message);
                this.admin.messageReceived(this.simulation, message);
            } else if ("coordinator".equals(receiver)) {
                log.finest("delivering " + message);
                this.simulation.messageReceived(message);
            } else {
                SimulationAgent agent = this.simulation.getAgent(receiver);
                if (agent != null) {
                    if (this.simulation.validateMessage(agent, message)) {
                        log.finest("delivering " + message);
                        agent.messageReceived(this.simulation, message);
                        log.finest("delivered to " + receiver);
                    } else {
                        log.warning("message not permitted: " + message);
                    }
                } else {
                    log.warning("unknown receiver '" + receiver + "' for " + message);
                }
            }
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "could not deliver message " + message, e);
        }
    }

    private void deliverMessageToRole(int role, Transportable content) {
        if (this.simulation.validateMessageToRole(role, content)) {
            SimulationAgent[] agents = this.simulation.getAgents(role);
            if (agents != null) {
                log.finest("delivering to role " + role + ": " + content);
                int i = 0;
                int n = agents.length;
                while (i < n) {
                    SimulationAgent agent = agents[i];
                    try {
                        String receiver = agent.getAddress();
                        log.finest("delivering to " + receiver);
                        agent.messageReceived(this.simulation, new Message("coordinator", receiver, content));
                        log.finest("delivered to " + receiver);
                    }
                    catch (ThreadDeath e) {
                        throw e;
                    }
                    catch (Throwable e) {
                        log.log(Level.SEVERE, "agent " + agent.getName() + " could not handle message " + content, e);
                    }
                    ++i;
                }
            }
        } else {
            log.warning("message from coordinator to role " + role + " not permitted: " + content);
        }
    }

    private void deliverMessageToRole(SimulationAgent senderAgent, int role, Transportable content) {
        String senderName = senderAgent.getName();
        if (this.simulation.validateMessageToRole(senderAgent, role, content)) {
            SimulationAgent[] agents = this.simulation.getAgents(role);
            if (agents != null) {
                String senderAddress = senderAgent.getAddress();
                log.finest("delivering from " + senderName + " (" + senderAddress + ") to role " + role + ": " + content);
                int i = 0;
                int n = agents.length;
                while (i < n) {
                    SimulationAgent a = agents[i];
                    if (a != senderAgent) {
                        try {
                            String receiver = a.getAddress();
                            log.finest("delivering to " + receiver);
                            a.messageReceived(this.simulation, new Message(senderAddress, receiver, content));
                            log.finest("delivered to " + receiver);
                        }
                        catch (ThreadDeath e) {
                            throw e;
                        }
                        catch (Throwable e) {
                            log.log(Level.SEVERE, "agent " + a.getName() + " could not handle message " + content, e);
                        }
                    }
                    ++i;
                }
            }
        } else {
            log.warning("message from " + senderName + " to role " + role + " not permitted: " + content);
        }
    }

    protected void finalize() throws Throwable {
        log.info("Message dispatcher " + this.getName() + " IS BEING GARBAGED");
        super.finalize();
    }

    private static class Delivery {
        public static final int MESSAGE = 0;
        public static final int TIMECALL = 1;
        public static final int UNBLOCK = 2;
        private final int flag;
        private final SimulationAgent senderAgent;
        private int role;
        private Transportable content;
        private int timeUnit;

        public Delivery(SimulationAgent senderAgent, int role, Transportable content) {
            this.flag = 0;
            this.senderAgent = senderAgent;
            this.role = role;
            this.content = content;
        }

        public Delivery(int flag, SimulationAgent agent) {
            this.flag = flag;
            this.senderAgent = agent;
        }

        public Delivery(int flag, int timeUnit) {
            this.flag = flag;
            this.senderAgent = null;
            this.timeUnit = timeUnit;
        }

        public void perform(MessageDispatcher dispatcher) {
            switch (this.flag) {
                case 0: {
                    if (this.senderAgent == null) {
                        dispatcher.deliverMessageToRole(this.role, this.content);
                        break;
                    }
                    dispatcher.deliverMessageToRole(this.senderAgent, this.role, this.content);
                    break;
                }
                case 1: {
                    dispatcher.nextTimeUnit(this.timeUnit);
                    break;
                }
                case 2: {
                    if (this.senderAgent == null) break;
                    this.senderAgent.setBlocked(false);
                }
            }
        }
    }

}

