/*
 * Decompiled with CFR 0_110.
 */
package com.botbox.util;

import com.botbox.util.ArrayQueue;
import com.botbox.util.ArrayUtils;
import com.botbox.util.JobStatus;
import com.botbox.util.PoolThread;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

public final class ThreadPool {
    private static Hashtable poolTable = new Hashtable();
    private String name;
    private PoolThread[] idleThreads = null;
    private int idleThreadCount = 0;
    private PoolThread[] poolThreads = null;
    private int threadCount;
    private int threadID = 0;
    private int minThreads = 1;
    private int maxThreads = 255;
    private int maxIdleThreads = 100;
    private boolean isDaemon = true;
    private ArrayQueue pendingJobs = null;
    private int pendingJobCount = 0;
    private int millisBetweenChecks;
    private int millisBeforeInterrupt = 0;
    private long nextWorkingThreadCheck;
    private Object lock = new Object();

    public static Enumeration getThreadPools() {
        return poolTable.elements();
    }

    public static ThreadPool getThreadPool(String name) {
        Hashtable hashtable = poolTable;
        synchronized (hashtable) {
            ThreadPool pool = (ThreadPool)poolTable.get(name);
            if (pool == null) {
                pool = new ThreadPool(name);
                poolTable.put(name, pool);
            }
            return pool;
        }
    }

    public static ThreadPool getDefaultThreadPool() {
        return ThreadPool.getThreadPool("default");
    }

    public static JobStatus getJobStatus() {
        Thread thread = Thread.currentThread();
        return thread instanceof PoolThread ? (JobStatus)((Object)thread) : null;
    }

    public ThreadPool(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getThreads() {
        return this.threadCount;
    }

    public int getIdleThreads() {
        return this.idleThreadCount;
    }

    public String getThreadStatus() {
        return this.getThreadStatus(new StringBuffer()).toString();
    }

    public StringBuffer getThreadStatus(StringBuffer sb) {
        int count = this.threadCount;
        sb.append("ThreadPool ").append(this.getName()).append(" (threads=").append(count).append(" idle=").append(this.idleThreadCount).append(')');
        if (count > 0) {
            PoolThread[] threads = this.poolThreads;
            int index = 0;
            int i = 0;
            while (i < count) {
                PoolThread pt = threads[i];
                if (pt != null) {
                    sb.append('\n').append(++index).append(": ");
                    pt.getStatus(sb);
                }
                ++i;
            }
        }
        return sb;
    }

    public boolean isDaemon() {
        return this.isDaemon;
    }

    public void setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
    }

    public int getMinThreads() {
        return this.minThreads;
    }

    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

    public int getMaxThreads() {
        return this.maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getMaxIdleThreads() {
        return this.maxIdleThreads;
    }

    public void setMaxIdleThreads(int maxIdleThreads) {
        this.maxIdleThreads = maxIdleThreads;
    }

    public void setInterruptThreadsAfter(int milliSeconds) {
        if (milliSeconds > 0) {
            int timeBetweenChecks = milliSeconds / 3;
            this.millisBetweenChecks = timeBetweenChecks < 1000 ? 1000 : timeBetweenChecks;
        }
        this.millisBeforeInterrupt = milliSeconds;
    }

    public int getQueueSize() {
        return this.pendingJobCount;
    }

    public long getQueueTime() {
        return 0;
    }

    public void invokeLater(Runnable job) {
        this.invokeLater(job, null);
    }

    public void invokeLater(Runnable job, String description) {
        PoolThread thread;
        Object object = this.lock;
        synchronized (object) {
            if (this.idleThreadCount > 0) {
                thread = this.idleThreads[--this.idleThreadCount];
                thread.setIdleIndex(-1);
                this.idleThreads[this.idleThreadCount] = null;
            } else if (this.threadCount < this.maxThreads) {
                thread = this.createThread(false);
            } else {
                if (this.pendingJobs == null) {
                    this.pendingJobs = new ArrayQueue();
                }
                this.pendingJobs.add(job);
                this.pendingJobs.add(description);
                ++this.pendingJobCount;
                thread = null;
            }
        }
        if (thread != null) {
            thread.invoke(job, description);
        } else {
            this.checkWorkingThreads(System.currentTimeMillis());
        }
    }

    private PoolThread createThread(boolean isIdle) {
        if (this.poolThreads == null) {
            this.poolThreads = new PoolThread[10];
        } else if (this.poolThreads.length == this.threadCount) {
            this.poolThreads = (PoolThread[])ArrayUtils.setSize(this.poolThreads, this.threadCount + 100);
        }
        String threadName = String.valueOf(this.getName()) + '.' + ++this.threadID;
        PoolThread thread = this.poolThreads[this.threadCount] = new PoolThread(this, threadName, isIdle);
        thread.setThreadIndex(this.threadCount);
        ++this.threadCount;
        return thread;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    final boolean addThread(PoolThread thread) {
        String description;
        Runnable job;
        block9 : {
            int idleIndex = thread.getIdleIndex();
            if (idleIndex >= 0) {
                return true;
            }
            Object object = this.lock;
            synchronized (object) {
                if (this.pendingJobCount > 0) {
                    --this.pendingJobCount;
                    job = (Runnable)this.pendingJobs.remove(0);
                    description = (String)this.pendingJobs.remove(0);
                    break block9;
                }
                if (this.idleThreadCount >= this.maxIdleThreads) {
                    return false;
                }
                if (this.idleThreads == null) {
                    this.idleThreads = new PoolThread[this.maxIdleThreads];
                } else if (this.idleThreads.length <= this.idleThreadCount) {
                    this.idleThreads = (PoolThread[])ArrayUtils.setSize(this.idleThreads, this.maxIdleThreads);
                }
                this.idleThreads[this.idleThreadCount] = thread;
                thread.setIdleIndex(this.idleThreadCount);
                ++this.idleThreadCount;
                return true;
            }
        }
        thread.invoke(job, description);
        return true;
    }

    final void threadDied(PoolThread thread) {
        Object object = this.lock;
        synchronized (object) {
            int index;
            --this.threadCount;
            int idleIndex = thread.getIdleIndex();
            if (idleIndex >= 0 && idleIndex < this.idleThreadCount && this.idleThreads[idleIndex] == thread) {
                --this.idleThreadCount;
                this.idleThreads[idleIndex] = this.idleThreads[this.idleThreadCount];
                this.idleThreads[this.idleThreadCount] = null;
                if (this.idleThreads[idleIndex] != null) {
                    this.idleThreads[idleIndex].setIdleIndex(idleIndex);
                }
                thread.setIdleIndex(-1);
            }
            if ((index = thread.getThreadIndex()) < this.threadCount && index >= 0 && this.poolThreads[index] == thread) {
                --this.threadCount;
                this.poolThreads[index] = this.poolThreads[this.threadCount];
                this.poolThreads[this.threadCount] = null;
                if (this.poolThreads[index] != null) {
                    this.poolThreads[index].setThreadIndex(index);
                }
                thread.setThreadIndex(-1);
            }
        }
    }

    final void checkWorkingThreads(long currentTime) {
        if (this.millisBeforeInterrupt > 0 && currentTime > this.nextWorkingThreadCheck) {
            this.nextWorkingThreadCheck = currentTime + (long)this.millisBetweenChecks;
            Object object = this.lock;
            synchronized (object) {
                int i = 0;
                while (i < this.threadCount) {
                    PoolThread pt = this.poolThreads[i];
                    if (pt.isWorking() && pt.addActive(1) > 3) {
                        PoolThread.log.warning("interrupting overdue job " + pt.getStatus());
                        pt.interrupt();
                        pt.stillAlive();
                    }
                    ++i;
                }
            }
        }
    }
}

