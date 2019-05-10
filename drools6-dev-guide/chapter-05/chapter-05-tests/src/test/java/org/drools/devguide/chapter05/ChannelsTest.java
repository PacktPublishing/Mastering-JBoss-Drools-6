/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.drools.devguide.BaseTest;
import static java.util.stream.Collectors.toList;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderState;
import org.drools.devguide.eshop.model.SuspiciousOperation;
import org.drools.devguide.eshop.service.OrderService;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.OrderBuilder;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class ChannelsTest extends BaseTest{

    @Test
    public void detectSuspiciousAmountOperationsNotifyAuditService() throws FileNotFoundException {
        //Create 2 customers without any Order. Orders are going to be provided
        //by the OrderService.
        Customer customerA = new CustomerBuilder().withId(1L).build();
        Customer customerB = new CustomerBuilder().withId(2L).build();

        //Mock an instance of OrderService
        OrderService orderService = new OrderService() {

            @Override
            public Collection<Order> getOrdersByCustomer(Long customerId) {
                switch (customerId.toString()){
                    case "1":
                        return Arrays.asList(
                            new OrderBuilder(null)
                                    .withSate(OrderState.PENDING)
                                    .newLine()
                                        .withQuantity(2)
                                        .withItem()
                                        .withSalePrice(5000.0)
                                        .end()
                                    .end()
                                    .newLine()
                                        .withQuantity(5)
                                        .withItem()
                                        .withSalePrice(800.0)
                                        .end()
                                    .end()
                            .build()
                        );
                    case "2":
                        return Arrays.asList(
                            new OrderBuilder(null)
                                    .withSate(OrderState.PENDING)
                                    .newLine()
                                        .withQuantity(1)
                                        .withItem()
                                        .withSalePrice(1000.0)
                                    .end()
                                .end()
                            .build()
                        );
                    default:
                        return Collections.EMPTY_LIST;
                }
            }
        };
        
        //Implement a Channel that notifies AuditService when new instances of
        //SuspiciousOperation are available.
        final Set<SuspiciousOperation> results = new HashSet<>();
        Channel auditServiceChannel = new Channel(){

            @Override
            public void send(Object object) {
                //notify AuditService here. For testing purposes, we are just 
                //going to store the received object in a Set.
                results.add((SuspiciousOperation) object);
            }
            
        };
        
        //Create a session
        KieSession ksession = this.createSession("channelsKsession");
        
        //Before we insert any fact, we set the value of 'amountThreshold' global
        //to 500.0, the value of 'orderService' global to the mocked service
        //we have created.
        ksession.setGlobal("amountThreshold", 500.0);
        ksession.setGlobal("orderService", orderService);
        
        //We also register a new Channel under the name "audit-channel"
        ksession.registerChannel("audit-channel", auditServiceChannel);
        
        ksession.insert(customerA);
        ksession.insert(customerB);

        //Let's fire any activated rule now.
        ksession.fireAllRules();

        //After the rules are fired, 2 SuspiciousOperation objects are now 
        //present in the 'results' Set.
        assertThat(results, hasSize(2));
        assertThat(
                results.stream().map(so -> so.getCustomer().getCustomerId()).collect(toList())
                , containsInAnyOrder(1L, 2L)
        );
    }

}
