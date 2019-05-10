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
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.OrderBuilder;
import org.drools.devguide.eshop.model.Manager;
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

/**
 *
 * @author salaboy
 */
public class ProcessInstancesAsFactsTest extends BaseTest {

    @Test
    public void testProcessOrderAsFact() {
        System.out.println(" ###### Starting Process Order as Fact  ###### ");

        KieSession ksession = this.createDefaultSession();
        ksession.addEventListener(new SystemOutProcessEventListener());
        ksession.addEventListener(new RuleAwareProcessEventLister());
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
        System.out.println(" >> Order Total: "+ order.getTotal());
        Map<Long, Long> workItemIdPerProcess = new HashMap<Long, Long>();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new HumanTaskExampleWorkItemHandler(workItemIdPerProcess));
        
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());

        ProcessInstance processInstance = ksession.startProcess("process-order", null);
      
        int fired = ksession.fireAllRules();
        assertThat(fired, equalTo(2));
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", customer);
        params.put("order", order);
        
        ksession.getWorkItemManager().completeWorkItem(workItemIdPerProcess.get(processInstance.getId()), params);

        fired = ksession.fireAllRules();
        
        assertThat(fired, equalTo(3));

        System.out.println(" ###### Completed Process Order as Fact  ###### ");
    }

    
    @Test
    public void testTooManyOrdersForAManager() {
        System.out.println(" ###### Starting Too Many Process Orders for a manager  ###### ");

        KieSession ksession = this.createDefaultSession();
        ksession.addEventListener(new SystemOutProcessEventListener());
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ksession.insert(new Manager("salaboy"));
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
        System.out.println(" >> Order Total: "+ order.getTotal());
        Map<Long, Long> workItemIdPerProcess = new HashMap<Long, Long>();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new HumanTaskExampleWorkItemHandler(workItemIdPerProcess));
        
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());

        ProcessInstance processInstance = ksession.startProcess("process-order", null);
        ProcessInstance processInstance2 = ksession.startProcess("process-order", null);
        ProcessInstance processInstance3 = ksession.startProcess("process-order", null);
        ProcessInstance processInstance4 = ksession.startProcess("process-order", null);
      
        int fired = ksession.fireAllRules();
        assertThat(fired, equalTo(9));
       
       

        System.out.println(" ###### Completed Too Many Process Orders for a manager  ###### ");
    }
    
    private class HumanTaskExampleWorkItemHandler implements WorkItemHandler {

        private Map<Long, Long> processIdWorkItemId;

        public HumanTaskExampleWorkItemHandler(Map<Long, Long> processIdWorkItemId) {
            this.processIdWorkItemId = processIdWorkItemId;
        }

        @Override
        public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
            // do nothing, wait for external interaction
            processIdWorkItemId.put(wi.getProcessInstanceId(), wi.getId());
        }

        @Override
        public void abortWorkItem(WorkItem wi, WorkItemManager wim) {

        }

    }
}
