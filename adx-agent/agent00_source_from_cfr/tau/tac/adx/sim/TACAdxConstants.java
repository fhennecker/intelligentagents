/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim;

public final class TACAdxConstants {
    public static final String ADX_EVENT_BUS_NAME = "AdX";
    public static final double MAX_SIMPLE_PUBLISHER_AD_PRICE = 100.0;
    public static final String[] SUPPORTED_TYPES = new String[]{"tac13adx"};
    public static final int TYPE_NONE = 0;
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_WARNING = 2;
    public static final int DU_NETWORK_AVG_RESPONSE = 64;
    public static final int DU_NETWORK_LAST_RESPONSE = 65;
    public static final int DU_BANK_ACCOUNT = 100;
    public static final int DU_NON_SEARCHING = 200;
    public static final int DU_INFORMATIONAL_SEARCH = 201;
    public static final int DU_FOCUS_LEVEL_ZERO = 202;
    public static final int DU_FOCUS_LEVEL_ONE = 203;
    public static final int DU_FOCUS_LEVEL_TWO = 204;
    public static final int DU_TRANSACTED = 205;
    public static final int DU_BIDS = 300;
    public static final int DU_IMPRESSIONS = 301;
    public static final int DU_CLICKS = 302;
    public static final int DU_CONVERSIONS = 303;
    public static final int DU_QUERY_REPORT = 304;
    public static final int DU_SALES_REPORT = 305;
    public static final int DU_PUBLISHER_INFO = 306;
    public static final int DU_ADVERTISER_INFO = 307;
    public static final int DU_PUBLISHER_QUERY_REPORT = 400;
    public static final int DU_AD_NETWORK_REPORT = 401;
    public static final int DU_ADX_BIDS = 402;
    public static final int DU_INITIAL_CAMPAIGN = 403;
    public static final int DU_CAMPAIGN_OPPORTUNITY = 404;
    public static final int DU_CAMPAIGN_REPORT = 405;
    public static final int DU_DEMAND_DAILY_REPORT = 406;
    public static final int DU_AD_NETWORK_WIN_COUNT = 407;
    public static final int DU_AD_NETWORK_QUALITY_RATING = 408;
    public static final int DU_AD_NETWORK_REVENUE = 409;
    public static final int DU_AD_NETWORK_EXPENSE = 410;
    public static final int DU_AD_NETWORK_UCS_EXPENSE = 411;
    public static final int DU_AD_NETWORK_ADX_EXPENSE = 412;
    public static final int DU_AD_NETWORK_BANK_ACCOUNT = 413;
    public static final int DU_CAMPAIGN_AUCTION_REPORT = 414;
    public static final int PUBLISHER = 0;
    public static final int ADVERTISER = 1;
    public static final int USERS = 2;
    public static final int ADX_AGENT_ROLE_ID = 3;
    public static final int DEMAND_AGENT_ROLE_ID = 4;
    public static final int AD_NETOWRK_ROLE_ID = 5;
    public static final String DEMAND_AGENT_NAME = "demand";
    public static final String ADX_AGENT_NAME = "adxusers";
    public static final String[] ROLE_NAME = new String[]{"Publisher", "Advertiser", "User", "ADX Agent", "Demand Agent", "Ad Newtork"};

    private TACAdxConstants() {
    }
}

