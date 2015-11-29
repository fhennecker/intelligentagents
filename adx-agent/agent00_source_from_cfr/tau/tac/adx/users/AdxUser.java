/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import edu.umich.eecs.tac.user.User;
import tau.tac.adx.Adx;
import tau.tac.adx.users.TacUser;
import tau.tac.adx.users.properties.Age;
import tau.tac.adx.users.properties.Gender;
import tau.tac.adx.users.properties.Income;
import tau.tac.adx.users.properties.UserState;

public class AdxUser
extends User
implements Cloneable,
TacUser<Adx> {
    private final Age age;
    private final Gender gender;
    private final Income income;
    private double pContinue;
    private UserState userState;
    private int uniqueId;

    public AdxUser(Age age, Gender gender, Income income, double pContinue, int uniqueId) {
        this.age = age;
        this.gender = gender;
        this.income = income;
        this.pContinue = pContinue;
        this.userState = UserState.IDLE;
        this.uniqueId = uniqueId;
    }

    public double getpContinue() {
        return this.pContinue;
    }

    public void setpContinue(double pContinue) {
        this.pContinue = pContinue;
    }

    public UserState getUserState() {
        return this.userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public Age getAge() {
        return this.age;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Income getIncome() {
        return this.income;
    }

    public int getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Object clone() {
        return new AdxUser(this.age, this.gender, this.income, this.pContinue, this.uniqueId);
    }

    public AdxUser ignoreMinorAttributes() {
        this.pContinue = Double.NaN;
        this.userState = UserState.IDLE;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AdxUser other = (AdxUser)obj;
        if (this.age != other.age) {
            return false;
        }
        if (this.gender != other.gender) {
            return false;
        }
        if (this.income != other.income) {
            return false;
        }
        if (Double.doubleToLongBits(this.pContinue) != Double.doubleToLongBits(other.pContinue)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.age == null ? 0 : this.age.hashCode());
        result = 31 * result + (this.gender == null ? 0 : this.gender.hashCode());
        result = 31 * result + (this.income == null ? 0 : this.income.hashCode());
        long temp = Double.doubleToLongBits(this.pContinue);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    public String toString() {
        return "AdxUser [age=" + (Object)((Object)this.age) + ", gender=" + (Object)((Object)this.gender) + ", income=" + (Object)((Object)this.income) + ", pContinue=" + this.pContinue + ", userState=" + (Object)((Object)this.userState) + ", uniqueId=" + this.uniqueId + "]";
    }
}

