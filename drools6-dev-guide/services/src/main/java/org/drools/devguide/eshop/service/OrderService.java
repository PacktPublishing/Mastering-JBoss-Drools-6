/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.eshop.service;

import java.util.Collection;
import org.drools.devguide.eshop.model.Order;

/**
 *
 * @author esteban
 */
public interface OrderService {

    public Collection<Order> getOrdersByCustomer(Long customerId);
}
