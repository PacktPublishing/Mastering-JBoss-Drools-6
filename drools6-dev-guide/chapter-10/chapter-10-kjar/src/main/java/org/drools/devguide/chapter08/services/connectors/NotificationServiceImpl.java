/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter08.services.connectors;

import org.drools.devguide.eshop.model.Customer;

/**
 *
 * @author salaboy
 */
public class NotificationServiceImpl {
    public String notify(Customer customer){
        System.out.println(">> Sending email to customer: " + customer.getEmail());
        return "failed";
    }
}
