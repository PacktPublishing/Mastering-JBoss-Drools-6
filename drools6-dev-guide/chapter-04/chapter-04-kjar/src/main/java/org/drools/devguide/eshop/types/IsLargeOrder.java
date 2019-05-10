package org.drools.devguide.eshop.types;

import java.io.Serializable;
import java.util.Objects;

public class IsLargeOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long orderId;
    
    public IsLargeOrder() {
    }
    
    public IsLargeOrder(Long orderId) {
        super();
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public int hashCode() {
        return 17 * 3 + Objects.hashCode(this.orderId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IsLargeOrder other = (IsLargeOrder) obj;
        if (!Objects.equals(this.orderId, other.orderId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IsLargeOrder[orderId = " + this.orderId + "]";
    }
}
