/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.drools.devguide.BaseTest;
import static java.util.stream.Collectors.toSet;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderState;
import org.drools.devguide.eshop.model.SuspiciousOperation;
import org.drools.devguide.eshop.service.OrderService;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.OrderBuilder;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

/**
 *
 * @author esteban
 */
public class QueriesTest extends BaseTest{

    @Test
    public void getAllSuspiciousOperationsWithOnDemandQuery(){
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
        
        //Create a session
        KieSession ksession = this.createSession("queriesKsession");
        
        //Before we insert any fact, we set the value of 'amountThreshold' global
        //to 500.0 and the value of 'orderService' global to the mocked service
        //we have created.
        ksession.setGlobal("amountThreshold", 500.0);
        ksession.setGlobal("orderService", orderService);

        ksession.insert(customerA);
        ksession.insert(customerB);

        //Let's fire any activated rule now.
        ksession.fireAllRules();

        //After the rules are fired, 2 SuspiciousOperation objects are now 
        //present. These objects belong to Customer "A" and "B".
        //We can get these objects using a query.
        QueryResults results = ksession.getQueryResults("Get All Suspicious Operations");

        assertThat(results.size(), is(2));
        for (QueryResultsRow queryResult : results) {
            SuspiciousOperation so = (SuspiciousOperation) queryResult.get("$so");
            assertThat(so.getCustomer(), either(is(customerA)).or(is(customerB)));
        }
        
    }
    
    @Test
    public void getAllSuspiciousOperationsWithLiveQuery(){
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
        
        //Create a session
        KieSession ksession = this.createSession("queriesKsession");
        
        //Before we insert any fact, we set the value of 'amountThreshold' global
        //to 500.0 and the value of 'orderService' global to the mocked service
        //we have created.
        ksession.setGlobal("amountThreshold", 500.0);
        ksession.setGlobal("orderService", orderService);

        ksession.insert(customerA);
        ksession.insert(customerB);

        //We can open a live query to get notified about new SuspiciousOperation
        //objects in the session
        Set<SuspiciousOperation> results = new HashSet<>();
        ksession.openLiveQuery("Get All Suspicious Operations", null, new ViewChangedEventListener() {

            @Override
            public void rowInserted(Row row) {
                results.add((SuspiciousOperation) row.get("$so"));
            }

            @Override
            public void rowDeleted(Row row) {
            }

            @Override
            public void rowUpdated(Row row) {
            }
        });
        
        //Let's fire any activated rule now.
        ksession.fireAllRules();

        //After the rules are fired, 2 SuspiciousOperation objects are now 
        //present. These objects belong to Customer "A" and "B".
        //The live query captured these objects in the 'result' Set.

        assertThat(results.size(), is(2));
        assertThat(
            results.stream()
                .map(so -> so.getCustomer())
                .collect(toSet()),
                containsInAnyOrder(customerA, customerB)
        );
        
    }
    
}
