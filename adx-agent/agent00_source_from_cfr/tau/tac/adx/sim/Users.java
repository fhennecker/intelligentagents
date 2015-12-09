/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim;

import edu.umich.eecs.tac.sim.PublisherInfoSender;
import edu.umich.eecs.tac.user.UserEventListener;
import java.util.logging.Logger;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.sim.Builtin;
import tau.tac.adx.sim.TACAdxSimulation;

public abstract class Users
extends Builtin {
    private static final String CONF = "users.";
    protected Logger log = Logger.getLogger(Users.class.getName());
    PublisherInfoSender[] publishers;

    public Users() {
        super("users.");
    }

    @Override
    protected void setup() {
        SimulationAgent[] publish = this.getSimulation().getPublishers();
        this.publishers = new PublisherInfoSender[publish.length];
        int i = 0;
        int n = publish.length;
        while (i < n) {
            this.publishers[i] = (PublisherInfoSender)((Object)publish[i].getAgent());
            ++i;
        }
    }

    public abstract void broadcastUserDistribution();

    protected void finalize() throws Throwable {
        Logger.global.info("USER " + this.getName() + " IS BEING GARBAGED");
        super.finalize();
    }

    public abstract boolean addUserEventListener(UserEventListener var1);

    public abstract boolean containsUserEventListener(UserEventListener var1);

    public abstract boolean removeUserEventListener(UserEventListener var1);

    protected void transact(String advertiser, double amount) {
        this.getSimulation().transaction(this.getAddress(), advertiser, amount);
    }
}

