/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.inet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class InetServer {
    private static final Logger log = Logger.getLogger(InetServer.class.getName());
    private String name;
    private String host;
    private Server server;
    private int port;
    private static String localHostAddress;
    private static String localHostAddress2;
    private static String localHostName;
    private static String localHostName2;

    public InetServer(String name, int port) {
        this(name, null, port);
    }

    public InetServer(String name, String host, int port) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName() {
        return this.name;
    }

    public String getBindAddress() {
        return String.valueOf(this.host == null ? "*" : this.host) + ':' + this.port;
    }

    public String getHost() {
        return this.host == null ? InetServer.getLocalHostName() : this.host;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isRunning() {
        if (this.server != null) {
            return true;
        }
        return false;
    }

    public final void start() throws IOException {
        if (this.server == null) {
            this.server = new Server(this, this.host, this.port);
            this.server.start();
            this.serverStarted();
        }
    }

    public final void stop() {
        if (this.server != null) {
            this.server.shutdown();
            this.server = null;
            this.serverShutdown();
        }
    }

    protected abstract void serverStarted();

    protected abstract void serverShutdown();

    protected abstract void newConnection(Socket var1) throws IOException;

    public static String getLocalHostName() {
        if (localHostAddress == null) {
            InetAddress localHost;
            String address = null;
            try {
                localHost = InetAddress.getLocalHost();
                address = localHost.getHostAddress();
                localHostName = localHost.getHostName();
            }
            catch (Exception e) {
                log.log(Level.WARNING, "could not retrieve local host", e);
            }
            if (address == null) {
                address = "127.0.0.1";
            }
            try {
                localHost = InetAddress.getByName(address);
                localHostAddress2 = localHost.getHostAddress();
                localHostName2 = localHost.getHostName();
            }
            catch (Exception e) {
                log.log(Level.WARNING, "could not retrieve local host", e);
            }
            if (localHostName2 == null) {
                localHostName2 = localHostAddress2 != null ? localHostAddress2 : (localHostName != null ? localHostName : address);
            }
            if (localHostName != null && localHostName.equalsIgnoreCase(localHostName2)) {
                localHostName = null;
            }
            if (localHostAddress2 != null && localHostAddress2.equalsIgnoreCase(address)) {
                localHostAddress2 = null;
            }
            localHostAddress = address;
        }
        return localHostName2;
    }

    static /* synthetic */ Logger access$1() {
        return log;
    }

    private static class Server
    extends Thread {
        private boolean stopped = false;
        private ServerSocket socket;
        private InetServer inet;

        Server(InetServer inet, String host, int port) throws IOException {
            super(inet.name);
            if (host != null) {
                this.socket = new ServerSocket();
                this.socket.bind(new InetSocketAddress(host, port));
            } else {
                this.socket = new ServerSocket(port);
            }
            this.inet = inet;
        }

        void shutdown() {
            if (!this.stopped) {
                this.stopped = true;
                try {
                    this.interrupt();
                    this.socket.close();
                }
                catch (Exception var1_1) {
                    // empty catch block
                }
            }
        }

        /*
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Lifted jumps to return sites
         */
        @Override
        public void run() {
            try {
                try {
                    do lbl-1000: // 3 sources:
                    {
                        if (this.stopped) {
                            return;
                        }
                        connection = this.socket.accept();
                        try {
                            this.inet.newConnection(connection);
                        }
                        catch (ThreadDeath e) {
                            throw e;
                        }
                        catch (Throwable e) {
                            InetServer.access$1().log(Level.SEVERE, String.valueOf(InetServer.access$0(this.inet)) + ": failed to handle new connection", e);
                            try {
                                connection.close();
                            }
                            catch (Exception var3_5) {
                                // empty catch block
                            }
                            continue;
                        }
                        break;
                    } while (true);
                }
                catch (Exception exception) {
                    InetServer.access$1().log(Level.SEVERE, String.valueOf(InetServer.access$0(this.inet)) + ": listening error", exception);
                    this.stopped = true;
                    try {
                        this.socket.close();
                        this.inet.stop();
                        return;
                    }
                    catch (Exception var5_6) {
                        return;
                    }
                }
                ** GOTO lbl-1000
            }
            finally {
                this.stopped = true;
                try {
                    this.socket.close();
                    this.inet.stop();
                }
                catch (Exception var5_8) {}
            }
        }
    }

}

