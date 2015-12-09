/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.users;

import se.sics.tasim.aw.TimeListener;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.users.AdxUser;

public interface AdxUserQueryManager
extends TimeListener {
    public AdxQuery generateQuery(AdxUser var1);
}

