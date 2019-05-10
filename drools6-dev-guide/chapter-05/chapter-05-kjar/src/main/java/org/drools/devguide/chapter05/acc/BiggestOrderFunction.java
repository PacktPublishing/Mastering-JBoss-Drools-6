/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05.acc;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.drools.devguide.eshop.model.Order;
import org.kie.api.runtime.rule.AccumulateFunction;

/**
 *
 * @author esteban
 */
public class BiggestOrderFunction implements AccumulateFunction{

    public static class Context implements Externalizable{
        public Order maxOrder = null;
        public double maxTotal = -Double.MAX_VALUE;

        public Context() {}

        @Override
        public void readExternal(ObjectInput in) throws IOException, 
                ClassNotFoundException {
            maxOrder = (Order) in.readObject();
            maxTotal = in.readDouble();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(maxOrder);
            out.writeDouble(maxTotal);
        }

    } 
    
    @Override
    public Serializable createContext() {
        return new Context();
    }

    @Override
    public void init(Serializable context) throws Exception {
    }

    @Override
    public void accumulate(Serializable context, Object value) {
        Context c = (Context)context;
        
        Order order = (Order) value;
        double discount = 
                order.getDiscount() == null ? 0 : order.getDiscount()
                        .getPercentage();
        double orderTotal = order.getTotal() - (order.getTotal() * discount);

        if (orderTotal > c.maxTotal){
            c.maxOrder = order;
            c.maxTotal = orderTotal;
        }
        
    }

    @Override
    public boolean supportsReverse() {
        return false;
    }
    
    @Override
    public void reverse(Serializable context, Object value) throws Exception {
    }

    @Override
    public Object getResult(Serializable context) throws Exception {
        return ((Context)context).maxOrder;
    }

    @Override
    public Class<?> getResultType() {
        return Order.class;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }
    
}
