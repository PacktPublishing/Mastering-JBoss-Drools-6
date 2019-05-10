/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.eshop.model;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author salaboy
 */
public class Coupon {

    public enum CouponType {
        DISCOUNT, TWOFORONE, POINTS
    };

    private Customer customer;
    private Order order;
    private CouponType type;
    private Date validFrom;
    private Date validUntil;

    public Coupon() {
    }

    public Coupon(Customer customer, Order order, CouponType type) {
        this.customer = customer;
        this.order = order;
        this.type = type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.customer);
        hash = 37 * hash + Objects.hashCode(this.order);
        hash = 37 * hash + Objects.hashCode(this.type);
        hash = 37 * hash + Objects.hashCode(this.validFrom);
        hash = 37 * hash + Objects.hashCode(this.validUntil);
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
        final Coupon other = (Coupon) obj;
        if (!Objects.equals(this.customer, other.customer)) {
            return false;
        }
        if (!Objects.equals(this.order, other.order)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.validFrom, other.validFrom)) {
            return false;
        }
        if (!Objects.equals(this.validUntil, other.validUntil)) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "Coupon{" + "customer=" + customer + ", order=" + order + ", type=" + type + ", validFrom=" + validFrom + ", validUntil=" + validUntil + '}';
    }

    
    


}
