/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter10;

import java.util.HashMap;
import java.util.Map;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderState;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.OrderBuilder;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;

/**
 *
 * @author salaboy
 */
public class ProcessOrderTest extends BaseTest {

    @Test
    public void testProcessOrderBPMN2Shipped() {
        System.out.println(" ###### Starting Process Order BPMN2  ###### ");

        KieSession ksession = this.createDefaultSession();
        ksession.addEventListener(new SystemOutProcessEventListener());
        
        // Creating a new customer to simulate the Customer that creates the order
        Customer customer = new CustomerBuilder().withId(1L).withName("salaboy")
                .withCategory(Customer.Category.GOLD).build();

        // New Order from Customer
        Order order = new OrderBuilder(customer)
                .newLine()
                    .withItem()
                        .withId(1L)
                        .withCost(100)
                        .withName("Item A")
                        .withSalePrice(150)
                    .end()
                    .withQuantity(3)
                .end()
                .newLine()
                    .withItem()
                        .withId(2L)
                        .withCost(75)
                        .withName("Item B")
                        .withSalePrice(130)
                    .end()
                    .withQuantity(6)
                .end()
            .end().build();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new HumanTaskExampleWorkItemHandler(customer, order));
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
        ProcessInstance processInstance = ksession.startProcess("process-order", null);

        assertThat(processInstance, notNullValue());
        WorkflowProcessInstance wpi = (WorkflowProcessInstance) processInstance;
        assertThat(OrderState.SHIPPED, is(((Order) wpi.getVariable("order")).getState()));
        assertThat("shipped", is(wpi.getVariable("order_status")));

        System.out.println(" ###### Completed Process Order BPMN2  ###### ");
    }

    @Test
    public void testProcessOrderBPMN2Failed() {
        System.out.println(" ###### Starting Process Order BPMN2  ###### ");

        KieSession ksession = this.createDefaultSession();
        ksession.addEventListener(new SystemOutProcessEventListener());
        
         // Creating a new customer to simulate the Customer that creates the order
        Customer customer = new CustomerBuilder().withId(1L).withName("salaboy")
                .withCategory(Customer.Category.BRONZE).withEmail("salaboy@mail.com").build();

        // New Order from Customer
        Order order = new OrderBuilder(customer)
                .newLine()
                    .withItem()
                        .withId(1L)
                        .withCost(100)
                        .withName("Item A")
                        .withSalePrice(150)
                    .end()
                    .withQuantity(3)
                .end()
                .newLine()
                    .withItem()
                        .withId(2L)
                        .withCost(75)
                        .withName("Item B")
                        .withSalePrice(130)
                    .end()
                    .withQuantity(6)
                .end()
            .end().build();
        
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new HumanTaskExampleWorkItemHandler(customer, order));
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
        ProcessInstance processInstance = ksession.startProcess("process-order", null);

        assertThat(processInstance, notNullValue());
        WorkflowProcessInstance wpi = (WorkflowProcessInstance) processInstance;
        assertThat(OrderState.FAILED, is(((Order) wpi.getVariable("order")).getState()));
        assertThat("failed", is(wpi.getVariable("order_status")));

        System.out.println(" ###### Completed Process Order BPMN2  ###### ");
    }

    private class HumanTaskExampleWorkItemHandler implements WorkItemHandler {

        private Customer customer;
        private Order order;

        public HumanTaskExampleWorkItemHandler(Customer customer, Order order) {
            this.customer = customer;
            this.order = order;
        }

        @Override
        public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
            String groupId = (String) wi.getParameters().get("GroupId");
            Map<String, Object> results = new HashMap<String, Object>();
            if (groupId.equals("customer")) {

                results.put("customer", this.customer);
                results.put("order", this.order);
                wim.completeWorkItem(wi.getId(), results);

            } else if (groupId.equals("shipping")) {
                System.out.println(">> The Shipping group needs to do some work here ...");
                results.put("order_status", "shipped");
                wim.completeWorkItem(wi.getId(), results);

            }

        }

        @Override
        public void abortWorkItem(WorkItem wi, WorkItemManager wim) {

        }

    }
}
