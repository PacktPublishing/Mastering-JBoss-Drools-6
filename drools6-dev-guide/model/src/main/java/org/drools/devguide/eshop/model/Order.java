package org.drools.devguide.eshop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Date date;
    private Customer customer;
    private List<OrderLine> orderLines = new ArrayList<>();
    private OrderState state;
    private Discount discount;

    public Order() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setItems(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public double getTotal() {
        return this.getOrderLines().stream()
                .mapToDouble(item -> item.getItem().getSalePrice() * item.getQuantity())
                .sum();
    }
    
    public int getTotalItems() {
        return this.getOrderLines().stream()
                .mapToInt(item -> item.getQuantity())
                .sum();
    }
    
    public void increaseDiscount(double increase) {
        if (discount == null) {
            discount = new Discount(0.0);
        }
        discount.setPercentage(discount.getPercentage() + increase);
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
        final Order other = (Order) obj;
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
        return "Order [ id = " + orderId + ", customer=" + customer + ", date=" + date + ", lines=" + orderLines + ", state=" + state + ']';
    }
}
