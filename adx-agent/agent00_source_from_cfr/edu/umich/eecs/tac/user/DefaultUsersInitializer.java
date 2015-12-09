/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.user.QueryState;
import edu.umich.eecs.tac.user.User;
import edu.umich.eecs.tac.user.UserTransitionManager;
import edu.umich.eecs.tac.user.UsersInitializer;
import java.util.Collection;
import java.util.logging.Logger;

public class DefaultUsersInitializer
implements UsersInitializer {
    protected Logger log = Logger.getLogger(DefaultUsersInitializer.class.getName());
    private UserTransitionManager userTransitionManager;

    public DefaultUsersInitializer(UserTransitionManager userTransitionManager) {
        if (userTransitionManager == null) {
            throw new NullPointerException("user transition manager cannot be null");
        }
        this.userTransitionManager = userTransitionManager;
    }

    @Override
    public void initialize(Collection<? extends User> users, int virtualDays) {
        this.log.finer("Running virtual initialization for " + virtualDays + " days.");
        int d = virtualDays;
        while (d >= 1) {
            this.userTransitionManager.nextTimeUnit(- d);
            for (User user : users) {
                user.setState(this.userTransitionManager.transition(user, false));
            }
            --d;
        }
    }
}

