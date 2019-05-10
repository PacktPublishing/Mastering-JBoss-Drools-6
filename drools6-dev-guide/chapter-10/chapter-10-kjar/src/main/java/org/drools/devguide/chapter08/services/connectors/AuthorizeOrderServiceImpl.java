/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter08.services.connectors;

import org.drools.devguide.eshop.model.Customer.Category;
import org.drools.devguide.eshop.model.Order;

/**
 *
 * @author salaboy
 */
public class AuthorizeOrderServiceImpl {

    public String authorizeOrder(Order order) {
        if(order.getCustomer().getCategory().equals(Category.GOLD)){
            return "approved";
        }else{
            return "failed";
        }
    }

}
