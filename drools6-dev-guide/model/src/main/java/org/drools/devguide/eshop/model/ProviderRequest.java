package org.drools.devguide.eshop.model;

import java.io.Serializable;

public class ProviderRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Provider provider;
    private Order order;
    
    public ProviderRequest() {
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        result = prime * result
                + ((provider == null) ? 0 : provider.hashCode());
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
        ProviderRequest other = (ProviderRequest) obj;
        if (order == null) {
            if (other.order != null)
                return false;
        } else if (!order.equals(other.order))
            return false;
        if (provider == null) {
            if (other.provider != null)
                return false;
        } else if (!provider.equals(other.provider))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProviderRequest [provider=" + provider + ", order=" + order
                + "]";
    }
}
