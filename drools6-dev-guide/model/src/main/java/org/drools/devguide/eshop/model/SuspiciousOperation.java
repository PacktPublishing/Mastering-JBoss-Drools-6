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
 * @author esteban
 */
public class SuspiciousOperation {
    
    public static enum Type {
        SUSPICIOUS_AMOUNT,
        SUSPICIOUS_DISCOUNT,
        SUSPICIOUS_FREQUENCY;
    }
    
    private Customer customer;
    private Type type;
    private Date date;
    private String comment;

    public SuspiciousOperation() {
    }

    public SuspiciousOperation(Customer customer, Type type) {
        this.customer = customer;
        this.type = type;
    }

    public SuspiciousOperation(Customer customer, Type type, Date date) {
        this.customer = customer;
        this.type = type;
        this.date = date;
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.customer);
        hash = 47 * hash + Objects.hashCode(this.type);
        hash = 47 * hash + Objects.hashCode(this.date);
        hash = 47 * hash + Objects.hashCode(this.comment);
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
        final SuspiciousOperation other = (SuspiciousOperation) obj;
        if (!Objects.equals(this.customer, other.customer)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SuspiciousOperation [" + "customer=" + customer + ", type=" + type + ", date=" + date + ", comment=" + comment + ']';
    }
    
}
