/*
 * Decompiled with CFR 0_110.
 */
package com.botbox.util;

import com.botbox.util.JobStatus;
import com.botbox.util.ThreadPool;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PoolThread
extends Thread
implements JobStatus {
    static final Logger log = Logger.getLogger(PoolThread.class.getName());
    private static final boolean VERBOSE_DEBUG = false;
    private int status = 0;
    private ThreadPool pool;
    private Runnable nextJob = null;
    private String description = null;
    private Runnable runningJob = null;
    private long startTime;
    private int threadIndex = -1;
    private int idleIndex = -1;
    private int activeCount = 0;

    PoolThread(ThreadPool pool, String name, boolean isIdle) {
        super(name);
        this.pool = pool;
        if (isIdle) {
            this.status = 1;
        }
        this.setDaemon(pool.isDaemon());
        this.start();
    }

    final int getThreadIndex() {
        return this.threadIndex;
    }

    final void setThreadIndex(int threadIndex) {
        this.threadIndex = threadIndex;
    }

    final int getIdleIndex() {
        return this.idleIndex;
    }

    final void setIdleIndex(int idleIndex) {
        this.idleIndex = idleIndex;
    }

    final boolean isWorking() {
        if (this.status == 3) {
            return true;
        }
        return false;
    }

    final int addActive(int value) {
        return this.activeCount += value;
    }

    final String getStatus() {
        return this.getStatus(new StringBuffer()).toString();
    }

    final StringBuffer getStatus(StringBuffer sb) {
        int status = this.status;
        sb.append(this.getName()).append('[');
        if (status < 2) {
            sb.append("initializing");
        } else if (status == 2) {
            sb.append("waiting");
        } else if (status == 4) {
            sb.append("died");
        } else {
            long startTime = this.startTime;
            String description = this.description;
            Runnable job = this.runningJob;
            sb.append("invoked ");
            if (description != null) {
                sb.append(description).append(" (").append(job).append(')');
            } else {
                sb.append(job);
            }
            if (startTime > 0) {
                sb.append(" at ").append(new Date(startTime));
                sb.append(',').append(startTime % 1000);
            }
        }
        if (this.idleIndex >= 0) {
            sb.append(",idle");
        }
        sb.append(",active=").append(this.activeCount);
        return sb.append(']');
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void stillAlive() {
        this.activeCount = 0;
    }

    void invoke(Runnable job, String description) {
        this.description = description;
        this.invoke(job);
    }

    synchronized void invoke(Runnable job) {
        this.nextJob = job;
        this.startTime = System.currentTimeMillis();
        this.notify();
    }

    private synchronized Runnable getJob() {
        Runnable newJob;
        while ((newJob = this.nextJob) == null) {
            try {
                this.wait();
                continue;
            }
            catch (InterruptedException var2_2) {
                // empty catch block
            }
        }
        this.nextJob = null;
        this.runningJob = newJob;
        return newJob;
    }

    private void clearJob() {
        this.runningJob = null;
        this.description = null;
        this.activeCount = 0;
    }

    @Override
    public void run() {
        try {
            if (this.status != 1 || this.pool.addThread(this)) {
                this.status = 2;
                this.handleJobs();
            }
        }
        finally {
            this.status = 4;
            this.pool.threadDied(this);
        }
    }

    private void handleJobs() {
        int priority = this.getPriority();
        do {
            block9 : {
                Runnable myJob;
                if ((myJob = this.getJob()) == null) continue;
                try {
                    try {
                        this.status = 3;
                        this.activeCount = 0;
                        myJob.run();
                    }
                    catch (ThreadDeath e) {
                        log.log(Level.SEVERE, "thread was killed", e);
                        throw e;
                    }
                    catch (Throwable e) {
                        log.log(Level.SEVERE, "could not execute job " + (this.description != null ? this.description : "") + ":", e);
                        this.status = 2;
                        this.clearJob();
                        break block9;
                    }
                }
                catch (Throwable var4_6) {
                    this.status = 2;
                    this.clearJob();
                    throw var4_6;
                }
                this.status = 2;
                this.clearJob();
            }
            if (this.getPriority() != priority) {
                this.setPriority(priority);
            }
            if (PoolThread.interrupted()) {
                log.log(Level.SEVERE, "***interrupted");
            }
            long currentTime = System.currentTimeMillis();
            this.pool.checkWorkingThreads(currentTime);
        } while (this.pool.addThread(this));
    }
}

