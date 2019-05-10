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
import static java.util.stream.Collectors.toList;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.SuspiciousOperation;
import org.drools.devguide.eshop.service.AuditService;
import org.drools.devguide.eshop.service.OrderService;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.factories.ModelFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class GlobalsTest extends BaseTest{

    @Test
    public void detectSuspiciousAmountOperationsWithFixedThresholdTest() {
        
        //Create a customer with PENDING orders for a value > 10000
        Customer customer1 = new CustomerBuilder()
                .withId(1L).build();
        
        Order customer1Order = ModelFactory.getPendingOrderWithTotalValueGreaterThan10000(customer1);

        //Create a customer with PENDING orders for a value < 10000 
        Customer customer2 = new CustomerBuilder()
                .withId(2L).build();
        
        Order customer2Order = ModelFactory.getPendingOrderWithTotalValueLessThan10000(customer2);

        //Create a session and insert the 2 customers and their orders.
        KieSession ksession = this.createSession("globals1Ksession");

        ksession.insert(customer1);
        ksession.insert(customer1Order);
        ksession.insert(customer2);
        ksession.insert(customer2Order);

        //Before we fire any activated rule, we check that the session doesn't
        //have any object of type SuspiciousOperation in it.
        Collection<SuspiciousOperation> suspiciousOperations
                = this.getFactsFromKieSession(ksession, SuspiciousOperation.class);
        assertThat(suspiciousOperations, hasSize(0));

        //Let's fire any activated rule now.
        ksession.fireAllRules();

        //After the rules are fired, a SuspiciousOperation object is now 
        //present. This object belongs to Customer "1".
        suspiciousOperations
                = this.getFactsFromKieSession(ksession, SuspiciousOperation.class);

        assertThat(suspiciousOperations, hasSize(1));
        assertThat(suspiciousOperations.iterator().next().getCustomer().getCustomerId(),
                is(1L));

        ksession.dispose();

    }

    @Test
    public void detectSuspiciousAmountOperationsWithVariableThresholdTest() {

        //Create a customer with PENDING orders for a value > 10000
        Customer customer1 = new CustomerBuilder()
                .withId(1L).build();
        
        Order customer1Order = ModelFactory.getPendingOrderWithTotalValueGreaterThan10000(customer1);

        //Create a customer with PENDING orders for a value < 10000 
        Customer customer2 = new CustomerBuilder()
                .withId(2L).build();
        
        Order customer2Order = ModelFactory.getPendingOrderWithTotalValueLessThan10000(customer2);

        //Create a session and insert the 2 customers and their orders.
        KieSession ksession = this.createSession("globals2Ksession");
        
        //Before we insert any fact, we set the value of 'amountThreshold' global
        //to 500.0
        ksession.setGlobal("amountThreshold", 500.0);

        ksession.insert(customer1);
        ksession.insert(customer1Order);
        ksession.insert(customer2);
        ksession.insert(customer2Order);

        //Before we fire any activated rule, we check that the session doesn't
        //have any object of type SuspiciousOperation in it.
        Collection<SuspiciousOperation> suspiciousOperations
                = this.getFactsFromKieSession(ksession, SuspiciousOperation.class);
        assertThat(suspiciousOperations, hasSize(0));

        //Let's fire any activated rule now.
        ksession.fireAllRules();

        //After the rules are fired, 2 SuspiciousOperation objects are now 
        //present. These objects belong to Customer "1" and "2".
        suspiciousOperations
                = this.getFactsFromKieSession(ksession, SuspiciousOperation.class);

        assertThat(suspiciousOperations, hasSize(2));
        assertThat(
                suspiciousOperations.stream().map(so -> so.getCustomer().getCustomerId()).collect(toList())
                , containsInAnyOrder(1L, 2L)
        );
        
    }

    @Test
    public void detectSuspiciousAmountOperationsWithOrderServiceTest() {

        //Create 2 customers without any Order. Orders are going to be provided
        //by the OrderService.
        final Customer customer1 = new CustomerBuilder().withId(1L).build();
        final Customer customer2 = new CustomerBuilder().withId(2L).build();

        //Mock an instance of OrderService
        OrderService orderService = new OrderService() {

            @Override
            public Collection<Order> getOrdersByCustomer(Long customerId) {
                switch (customerId.toString()){
                    case "1":
                        return Arrays.asList(
                            ModelFactory.getPendingOrderWithTotalValueGreaterThan10000(customer1)
                        );
                    case "2":
                        return Arrays.asList(
                            ModelFactory.getPendingOrderWithTotalValueLessThan10000(customer2)
                        );
                    default:
                        return Collections.EMPTY_LIST;
                }
            }
        };
        
        //Create a session
        KieSession ksession = this.createSession("globals3Ksession");
        
        //Before we insert any fact, we set the value of 'amountThreshold' global
        //to 500.0 and the value of 'orderService' global to the mocked service
        //we have created.
        ksession.setGlobal("amountThreshold", 500.0);
        ksession.setGlobal("orderService", orderService);

        ksession.insert(customer1);
        ksession.insert(customer2);

        //Before we fire any activated rule, we check that the session doesn't
        //have any object of type SuspiciousOperation in it.
        Collection<SuspiciousOperation> suspiciousOperations
                = this.getFactsFromKieSession(ksession, SuspiciousOperation.class);
        assertThat(suspiciousOperations, hasSize(0));

        //Let's fire any activated rule now.
        ksession.fireAllRules();

        //After the rules are fired, 2 SuspiciousOperation objects are now 
        //present. These objects belong to Customer "1" and "2".
        suspiciousOperations
                = this.getFactsFromKieSession(ksession, SuspiciousOperation.class);

        assertThat(suspiciousOperations, hasSize(2));
        assertThat(
                suspiciousOperations.stream().map(so -> so.getCustomer().getCustomerId()).collect(toList())
                , containsInAnyOrder(1L, 2L)
        );
        
    }

    @Test
    public void detectSuspiciousAmountOperationsCollectInGlobalList() {

        //Create 2 customers without any Order. Orders are going to be provided
        //by the OrderService.
        Customer customer1 = new CustomerBuilder().withId(1L).build();
        Customer customer2 = new CustomerBuilder().withId(2L).build();

        //Mock an instance of OrderService
        OrderService orderService = new OrderService() {

            @Override
            public Collection<Order> getOrdersByCustomer(Long customerId) {
                switch (customerId.toString()){
                    case "1":
                        return Arrays.asList(
                            ModelFactory.getPendingOrderWithTotalValueGreaterThan10000(customer1)
                        );
                    case "2":
                        return Arrays.asList(
                            ModelFactory.getPendingOrderWithTotalValueLessThan10000(customer2)
                        );
                    default:
                        return Collections.EMPTY_LIST;
                }
            }
        };
        
        //Create a session
        KieSession ksession = this.createSession("globals4Ksession");
        
        //Before we insert any fact, we set the value of 'amountThreshold' global
        //to 500.0 and the value of 'orderService' global to the mocked service
        //we have created.
        ksession.setGlobal("amountThreshold", 500.0);
        ksession.setGlobal("orderService", orderService);
        
        //We will also set a global Set to collect all the SuspiciousOperation
        //objects generated in the session.
        Set<SuspiciousOperation> results = new HashSet<>();
        ksession.setGlobal("results", results);

        ksession.insert(customer1);
        ksession.insert(customer2);

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
    
    
    @Test
    public void detectSuspiciousAmountOperationsNotifyAuditService() {

        //Create 2 customers without any Order. Orders are going to be provided
        //by the OrderService.
        Customer customer1 = new CustomerBuilder().withId(1L).build();
        Customer customer2 = new CustomerBuilder().withId(2L).build();

        //Mock an instance of OrderService
        OrderService orderService = new OrderService() {

            @Override
            public Collection<Order> getOrdersByCustomer(Long customerId) {
                switch (customerId.toString()){
                    case "1":
                        return Arrays.asList(
                            ModelFactory.getPendingOrderWithTotalValueGreaterThan10000(customer1)
                        );
                    case "2":
                        return Arrays.asList(
                            ModelFactory.getPendingOrderWithTotalValueLessThan10000(customer2)
                        );
                    default:
                        return Collections.EMPTY_LIST;
                }
            }
        };
        
        //Create a mocked implementation of AuditService that collects its results
        //into a set
        final Set<SuspiciousOperation> results = new HashSet<>();
        AuditService auditService = new AuditService() {

            @Override
            public void notifySuspiciousOperation(SuspiciousOperation operation) {
                results.add(operation);
            }
        };
        
        //Create a session
        KieSession ksession = this.createSession("globals5Ksession");
        
        //Before we insert any fact, we set the value of 'amountThreshold' global
        //to 500.0, the value of 'orderService' global to the mocked service
        //we have created and the value of 'auditService' global to the mocked
        //service we have created.
        ksession.setGlobal("amountThreshold", 500.0);
        ksession.setGlobal("orderService", orderService);
        ksession.setGlobal("auditService", auditService);
        
        ksession.insert(customer1);
        ksession.insert(customer2);

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
