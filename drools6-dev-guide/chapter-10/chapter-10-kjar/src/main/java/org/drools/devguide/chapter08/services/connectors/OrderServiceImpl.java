/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter08.services.connectors;

import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderState;

/**
 *
 * @author salaboy
 */
public class OrderServiceImpl {
    public Order updateShippedOrder(Order order){
        order.setState(OrderState.SHIPPED);
        System.out.println("Updating Order to shipped!");
        return order;
    }
    public Order updateFailedOrder(Order order){
        order.setState(OrderState.FAILED);
        System.out.println("Updating Order to Failed!");
        return order;
    }
    
}
