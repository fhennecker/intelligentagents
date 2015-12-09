/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayQueue;
import java.util.logging.Logger;
import se.sics.tasim.is.common.SimServer;

public class SimulationArchiver
implements Runnable {
    private static final Logger log = Logger.getLogger(SimulationArchiver.class.getName());
    private ArrayQueue simulationQueue = new ArrayQueue();
    private boolean isRunning = false;

    public synchronized void addSimulation(SimServer simServer, int simulationID) {
        this.simulationQueue.add(simServer);
        this.simulationQueue.add(new Integer(simulationID));
        if (!this.isRunning) {
            this.isRunning = true;
            new Thread((Runnable)this, "gameArchiver").start();
        } else {
            this.notify();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void run() {
        try {
            do {
                SimServer simServer;
                int simulationID;
                SimulationArchiver simulationArchiver = this;
                synchronized (simulationArchiver) {
                    do {
                        if (this.simulationQueue.size() != 0) {
                            simServer = (SimServer)this.simulationQueue.remove(0);
                            if (simServer != null) break;
                            return;
                        }
                        try {
                            this.wait();
                        }
                        catch (InterruptedException var4_4) {
                            // empty catch block
                        }
                    } while (true);
                    simulationID = (Integer)this.simulationQueue.remove(0);
                }
                this.generateResults(simServer, simulationID);
            } while (true);
        }
        finally {
            this.isRunning = false;
        }
    }

    private void generateResults(SimServer simServer, int simulationID) {
        simServer.generateResults(simulationID, true);
    }
}

