/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter08.services.connectors;

import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderLine;

/**
 *
 * @author salaboy
 */
public class UpdateStockServiceImpl {
    public void updateStock(Order order){
        System.out.println(">> Updating Stock for Order: "+order);
        for(OrderLine ol : order.getOrderLines()){
            System.out.println("Reducing stock from item:  "+ ol.getItem().getName() + " by " + ol.getQuantity() + " units.");
        }
    }
}
