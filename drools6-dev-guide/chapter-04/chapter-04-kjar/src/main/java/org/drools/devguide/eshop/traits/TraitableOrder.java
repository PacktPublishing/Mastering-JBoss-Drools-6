package org.drools.devguide.eshop.traits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.drools.core.factmodel.traits.Traitable;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Discount;
import org.drools.devguide.eshop.model.OrderLine;
import org.drools.devguide.eshop.model.OrderState;

@Traitable
public class TraitableOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Date date;
    private Customer customer;
    private List<OrderLine> orderLines = new ArrayList<OrderLine>();
    private OrderState state;
    private Discount discount;

    public TraitableOrder() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.orderId);
        hash = 17 * hash + Objects.hashCode(this.date);
        hash = 17 * hash + Objects.hashCode(this.customer);
        hash = 17 * hash + Objects.hashCode(this.orderLines);
        hash = 17 * hash + Objects.hashCode(this.state);
        hash = 17 * hash + Objects.hashCode(this.discount);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraitableOrder other = (TraitableOrder) obj;
        if (!Objects.equals(this.orderId, other.orderId)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.customer, other.customer)) {
            return false;
        }
        if (!Objects.equals(this.orderLines, other.orderLines)) {
            return false;
        }
        if (this.state != other.state) {
            return false;
        }
        if (!Objects.equals(this.discount, other.discount)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TraitableOrder [ id = " + orderId + ", customer=" + customer + ", date=" + date + ", lines=" + orderLines + ", state=" + state + ']';
    }
}
