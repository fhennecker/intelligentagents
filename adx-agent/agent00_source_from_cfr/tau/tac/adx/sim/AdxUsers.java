/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim;

import edu.umich.eecs.tac.sim.PublisherInfoSender;
import java.util.logging.Logger;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportSender;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportSender;
import tau.tac.adx.sim.Builtin;
import tau.tac.adx.sim.TACAdxSimulation;

public abstract class AdxUsers
extends Builtin
implements AdxPublisherReportSender,
AdNetworkReportSender {
    protected Logger log = Logger.getLogger(AdxUsers.class.getName());
    PublisherInfoSender[] publishers;

    public AdxUsers() {
        super("adxusers");
    }

    @Override
    protected void setup() {
    }

    public abstract void sendReportsToAll();

    @Override
    public void broadcastPublisherReport(AdxPublisherReport report) {
        this.getSimulation().broadcastPublisherReport(report);
    }

    @Override
    public void broadcastAdNetowrkReport(String adNetworkName, AdNetworkReport report) {
        this.getSimulation().broadcastAdNetowrkReport(adNetworkName, report);
    }
}

