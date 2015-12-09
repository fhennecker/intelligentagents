/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.props;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.ManufacturerComponentComposable;
import edu.umich.eecs.tac.props.Pricing;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.props.ReserveInfo;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.props.UserClickModel;
import edu.umich.eecs.tac.props.UserPopulationState;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.ContextFactory;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.props.Alert;
import se.sics.tasim.props.Ping;
import se.sics.tasim.props.ServerConfig;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.props.AdLink;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportEntry;
import tau.tac.adx.report.demand.CampaignReportKey;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportEntry;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReportKey;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;

public class AdxInfoContextFactory
implements ContextFactory {
    private static final String ADX_CONTEXT_NAME = "adxcontext";
    private static Context lastContext;

    @Override
    public final Context createContext() {
        return this.createContext(null);
    }

    @Override
    public final Context createContext(Context parentContext) {
        Context con = lastContext;
        if (con != null && con.getParent() == parentContext) {
            return con;
        }
        con = new Context("adxcontext", parentContext);
        con.addTransportable(new Ping());
        con.addTransportable(new Alert());
        con.addTransportable(new BankStatus());
        con.addTransportable(new AdminContent());
        con.addTransportable(new SimulationStatus());
        con.addTransportable(new StartInfo());
        con.addTransportable(new SlotInfo());
        con.addTransportable(new ReserveInfo());
        con.addTransportable(new PublisherInfo());
        con.addTransportable(new ServerConfig());
        con.addTransportable(new Query());
        con.addTransportable(new Product());
        con.addTransportable(new Ad());
        con.addTransportable(new AdLink());
        con.addTransportable(new SalesReport());
        con.addTransportable(new SalesReport.SalesReportEntry());
        con.addTransportable(new QueryReport());
        con.addTransportable(new QueryReport.QueryReportEntry());
        con.addTransportable(new QueryReport.DisplayReportEntry());
        con.addTransportable(new QueryReport.DisplayReport());
        con.addTransportable(new RetailCatalog());
        con.addTransportable(new RetailCatalog.RetailCatalogEntry());
        con.addTransportable(new BidBundle());
        con.addTransportable(new BidBundle.BidEntry());
        con.addTransportable(new Ranking());
        con.addTransportable(new Ranking.Slot());
        con.addTransportable(new Pricing());
        con.addTransportable(new UserClickModel());
        con.addTransportable(new Auction());
        con.addTransportable(new AdvertiserInfo());
        con.addTransportable(new ManufacturerComponentComposable());
        con.addTransportable(new UserPopulationState());
        con.addTransportable(new UserPopulationState.UserPopulationEntry());
        con.addTransportable(new AdxQuery());
        con.addTransportable(new PublisherCatalog());
        con.addTransportable(new PublisherCatalogEntry());
        con.addTransportable(new AdxPublisherReportEntry());
        con.addTransportable(new AdxPublisherReport());
        con.addTransportable(new AdNetworkReportEntry());
        con.addTransportable(new AdNetworkReport());
        con.addTransportable(new AdNetworkKey());
        con.addTransportable(new AdxBidBundle());
        con.addTransportable(new AdxBidBundle.BidEntry());
        con.addTransportable(new InitialCampaignMessage());
        con.addTransportable(new CampaignOpportunityMessage());
        con.addTransportable(new CampaignReport());
        con.addTransportable(new CampaignReportEntry());
        con.addTransportable(new CampaignReportKey());
        con.addTransportable(new AdNetworkDailyNotification());
        con.addTransportable(new AdNetBidMessage());
        con.addTransportable(new CampaignAuctionReport());
        con.addTransportable(new CampaignAuctionReportEntry());
        con.addTransportable(new CampaignAuctionReportKey());
        lastContext = con;
        return con;
    }
}

