/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05.utils;

import org.drools.devguide.eshop.model.Customer;

/**
 *
 * @author esteban
 */
public class CustomerUtils {

    public static String formatCustomer(Customer c) {
        return String.format(
                "[%s] %s", c.getCategory(), c.getName());
    }
}
