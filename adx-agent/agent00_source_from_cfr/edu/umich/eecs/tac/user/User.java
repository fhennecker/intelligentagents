/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.user.QueryState;

public class User {
    private QueryState state;
    private Product product;

    public User() {
    }

    public User(QueryState state, Product product) {
        this.state = state;
        this.product = product;
    }

    public QueryState getState() {
        return this.state;
    }

    public void setState(QueryState state) {
        this.state = state;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isSearching() {
        return this.state.isSearching();
    }

    public boolean isTransacting() {
        return this.state.isTransacting();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        User user = (User)o;
        if (this.product != null ? !this.product.equals(user.product) : user.product != null) {
            return false;
        }
        if (this.state != user.state) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.state != null ? this.state.hashCode() : 0;
        result = 31 * result + (this.product != null ? this.product.hashCode() : 0);
        return result;
    }
}

