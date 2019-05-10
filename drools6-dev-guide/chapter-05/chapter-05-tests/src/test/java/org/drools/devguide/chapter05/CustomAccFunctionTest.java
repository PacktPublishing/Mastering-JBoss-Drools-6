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
import org.drools.devguide.util.ObjectHolder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class CustomAccFunctionTest extends BaseTest {
    
    protected final String ksessionName = "customAccFunctionKsession";
    
    @Test
    public void testCustomAccFunction(){
        
        ObjectHolder biggestOrder = new ObjectHolder();
        
        KieSession ksession = createSession(ksessionName);
        
        ksession.setGlobal("biggestOrder", biggestOrder);
        
        //total including discount: 68 - (68 * 0.25) = 51
        Order order1 = this.createOrderWithItems(0.25, 20, 25, 19, 4);
        
        //total including discount: 60 - (60 * 0.1) = 54
        Order order2 = this.createOrderWithItems(0.1, 10, 8, 2, 40);
        
        //total including discount: 15 - (15 * 0.15) = 12.75
        Order order3 = this.createOrderWithItems(0.15, 10, 5);
        
        
        ksession.insert(order1);
        ksession.insert(order2);
        ksession.insert(order3);
        ksession.fireAllRules();
        
        assertThat(biggestOrder.getObject(), not(nullValue()));
        assertThat(biggestOrder.getObject(), is(order2));
        
    }
    
    private Order createOrderWithItems(double discount, double... itemsSalesPrice){
        Order order = new Order();
        for (double itemSalesPrice : itemsSalesPrice) {
            OrderLine line = new OrderLine();
            //for these tests we don't care about the id, name or cost.
            line.setItem(new Item(System.nanoTime(), "", 0.0, itemSalesPrice));
            line.setQuantity(1);
            order.getOrderLines().add(line);
        }
        order.increaseDiscount(discount);
        return order;
    }
    
}
