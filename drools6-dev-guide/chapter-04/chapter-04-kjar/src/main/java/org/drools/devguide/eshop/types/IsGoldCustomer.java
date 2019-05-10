package org.drools.devguide.eshop.types;

import org.drools.devguide.eshop.model.Customer;

public class IsGoldCustomer {

    private Customer customer;
    
    public IsGoldCustomer() {
    }

    public IsGoldCustomer(Customer customer) {
        super();
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((customer == null) ? 0 : customer.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IsGoldCustomer other = (IsGoldCustomer) obj;
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IsGoldCustomer [customer=" + customer + "]";
    }
}
