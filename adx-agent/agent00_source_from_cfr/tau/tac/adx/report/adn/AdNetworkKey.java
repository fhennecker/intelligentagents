/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.report.adn;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;

public class AdNetworkKey
implements Transportable {
    private static final String PUBLISHER_KEY = "PUBLISHER_KEY";
    private static final String DEVICE_KEY = "DEVICE_KEY";
    private static final String AD_TYPE_KEY = "AD_TYPE_KEY";
    private static final String GENDER_TYPE_KEY = "GENDER_TYPE_KEY";
    private static final String INCOME_TYPE_KEY = "INCOME_TYPE_KEY";
    private static final String AGE_TYPE_KEY = "AGE_TYPE_KEY";
    private static final String CMAPAIGN_ID_TYPE_KEY = "CMAPAIGN_ID_TYPE_KEY";
    private Age age;
    private Income income;
    private Gender gender;
    private String publisher;
    private Device device;
    private AdType adType;
    private int campaignId;

    public String toString() {
        return "AdNetworkKey [age=" + (Object)((Object)this.age) + ", income=" + (Object)((Object)this.income) + ", gender=" + (Object)((Object)this.gender) + ", publisher=" + this.publisher + ", device=" + (Object)((Object)this.device) + ", adType=" + (Object)((Object)this.adType) + ", campaignId=" + this.campaignId + "]";
    }

    public AdNetworkKey(AdxUser adxUser, String publisher, Device device, AdType adType, int campaignId) {
        this.age = adxUser.getAge();
        this.income = adxUser.getIncome();
        this.gender = adxUser.getGender();
        this.publisher = publisher;
        this.device = device;
        this.adType = adType;
        this.campaignId = campaignId;
    }

    public AdNetworkKey() {
    }

    public Age getAge() {
        return this.age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public Income getIncome() {
        return this.income;
    }

    public void setIncome(Income income) {
        this.income = income;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public AdType getAdType() {
        return this.adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }

    public int getCampaignId() {
        return this.campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public String getTransportName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        this.publisher = reader.getAttribute("PUBLISHER_KEY", null);
        this.device = Device.valueOf(reader.getAttribute("DEVICE_KEY", null));
        this.adType = AdType.valueOf(reader.getAttribute("AD_TYPE_KEY", null));
        this.gender = Gender.valueOf(reader.getAttribute("GENDER_TYPE_KEY", null));
        this.income = Income.valueOf(reader.getAttribute("INCOME_TYPE_KEY", null));
        this.age = Age.valueOf(reader.getAttribute("AGE_TYPE_KEY", null));
        this.campaignId = reader.getAttributeAsInt("CMAPAIGN_ID_TYPE_KEY", -1);
    }

    @Override
    public void write(TransportWriter writer) {
        if (this.publisher != null) {
            writer.attr("PUBLISHER_KEY", this.publisher);
        }
        if (this.device != null) {
            writer.attr("DEVICE_KEY", this.device.toString());
        }
        if (this.adType != null) {
            writer.attr("AD_TYPE_KEY", this.adType.toString());
        }
        if (this.gender != null) {
            writer.attr("GENDER_TYPE_KEY", this.gender.toString());
        }
        if (this.income != null) {
            writer.attr("INCOME_TYPE_KEY", this.income.toString());
        }
        if (this.age != null) {
            writer.attr("AGE_TYPE_KEY", this.age.toString());
        }
        writer.attr("CMAPAIGN_ID_TYPE_KEY", this.campaignId);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.adType == null ? 0 : this.adType.hashCode());
        result = 31 * result + (this.age == null ? 0 : this.age.hashCode());
        result = 31 * result + this.campaignId;
        result = 31 * result + (this.device == null ? 0 : this.device.hashCode());
        result = 31 * result + (this.gender == null ? 0 : this.gender.hashCode());
        result = 31 * result + (this.income == null ? 0 : this.income.hashCode());
        result = 31 * result + (this.publisher == null ? 0 : this.publisher.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AdNetworkKey other = (AdNetworkKey)obj;
        if (this.adType != other.adType) {
            return false;
        }
        if (this.age != other.age) {
            return false;
        }
        if (this.campaignId != other.campaignId) {
            return false;
        }
        if (this.device != other.device) {
            return false;
        }
        if (this.gender != other.gender) {
            return false;
        }
        if (this.income != other.income) {
            return false;
        }
        if (this.publisher == null ? other.publisher != null : !this.publisher.equals(other.publisher)) {
            return false;
        }
        return true;
    }
}

