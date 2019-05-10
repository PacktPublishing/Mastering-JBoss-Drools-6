/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05;

import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderLine;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class CustomOperatorTest extends BaseTest {
    
    protected final String ksessionName = "customOperatorKsession";
    
    @Test
    public void testOrderWith123Item(){
        KieSession ksession = createSession(ksessionName);
        
        Order order = this.createOrderWithItems(123);
        
        ksession.insert(order);
        ksession.fireAllRules();
        
        assertThat(order.getDiscount(), not(nullValue()));
        assertThat(order.getDiscount().getPercentage(), is(0.1));
        
    }
    
    @Test
    public void testOrderWithout123Item(){
        KieSession ksession = createSession(ksessionName);
        
        Order order = this.createOrderWithItems(456);
        
        ksession.insert(order);
        ksession.fireAllRules();
        
        assertThat(order.getDiscount(), not(nullValue()));
        assertThat(order.getDiscount().getPercentage(), is(0.05));
        
    }
    
    private Order createOrderWithItems(long... itemIds){
        Order order = new Order();
        for (long itemId : itemIds) {
            OrderLine line = new OrderLine();
            //for these tests we don't care about the price or name.
            line.setItem(new Item(itemId, "", 10.00, 21.00));
            line.setQuantity(10);
            order.getOrderLines().add(line);
        }
        return order;
    }
}
