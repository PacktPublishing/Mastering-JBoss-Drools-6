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
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;
import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

/**
 *
 * @author salaboy
 */
public class BusinessRuleTasksTest extends BaseTest {

    @Test
    public void testValidateOrder10PercentDiscount() {
        System.out.println(" ###### Starting Validate Order Process  ###### ");

        KieSession ksession = this.createDefaultSession();
        ksession.addEventListener(new SystemOutProcessEventListener());
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ksession.addEventListener(new TriggerRulesEventListener(ksession));
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
        Map<String, Object> params = new HashMap<String, Object>();        
        params.put("order", order);
        params.put("valid", "not-validated");

        ProcessInstance processInstance = ksession.startProcess("order-validation", params);
      
        assertThat(processInstance, notNullValue());
        assertThat(processInstance.getState(), equalTo(ProcessInstance.STATE_COMPLETED));
        assertThat(((WorkflowProcessInstance)processInstance).getVariable("valid").toString(), equalTo("discounted 10%"));
        assertThat(((WorkflowProcessInstance)processInstance).getVariable("order"), notNullValue());
        assertThat(((Order)((WorkflowProcessInstance)processInstance).getVariable("order")).getDiscount().getPercentage(), equalTo(0.1));


        System.out.println(" ###### Completing Validate Order Process  ###### ");
    }

    @Test
    public void testValidateOrder20PercentDiscount() {
        System.out.println(" ###### Starting Validate Order Process  ###### ");

        KieSession ksession = this.createDefaultSession();
        ksession.addEventListener(new SystemOutProcessEventListener());
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ksession.addEventListener(new TriggerRulesEventListener(ksession));
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
                .withQuantity(1)
                .end()
                .newLine()
                .withItem()
                .withId(2L)
                .withCost(75)
                .withName("Item B")
                .withSalePrice(130)
                .end()
                .withQuantity(1)
                .end()
                .end().build();
        System.out.println(" >> Order Total: "+ order.getTotal());
        Map<String, Object> params = new HashMap<String, Object>();        
        params.put("order", order);
        params.put("valid", "not-validated");

        ProcessInstance processInstance = ksession.startProcess("order-validation", params);
      
        assertThat(processInstance, notNullValue());
        assertThat(processInstance.getState(), equalTo(ProcessInstance.STATE_COMPLETED));
        assertThat(((WorkflowProcessInstance)processInstance).getVariable("valid").toString(), equalTo("discounted 20%"));
        assertThat(((WorkflowProcessInstance)processInstance).getVariable("order"), notNullValue());
        assertThat(((Order)((WorkflowProcessInstance)processInstance).getVariable("order")).getDiscount().getPercentage(), equalTo(0.2));


        System.out.println(" ###### Completing Validate Order Process  ###### ");
    }
    
    @Test
    public void testValidateOrderWithValidationError() {
        System.out.println(" ###### Starting Validate Order Process  ###### ");

        KieSession ksession = this.createDefaultSession();
        ksession.addEventListener(new SystemOutProcessEventListener());
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ksession.addEventListener(new TriggerRulesEventListener(ksession));
        // Creating a new customer to simulate the Customer that creates the order
        Customer customer = new CustomerBuilder().withId(1L).withName("salaboy")
                .withCategory(Customer.Category.GOLD).build();

        // New Order from Customer
        Order order = new OrderBuilder(customer)
                .build();
        
        Map<String, Object> params = new HashMap<String, Object>();        
        params.put("order", order);
        params.put("valid", "not-validated");

        ProcessInstance processInstance = ksession.startProcess("order-validation", params);
      
        assertThat(processInstance, notNullValue());
        assertThat(processInstance.getState(), equalTo(ProcessInstance.STATE_COMPLETED));
        assertThat(((WorkflowProcessInstance)processInstance).getVariable("valid").toString(), startsWith("validation error:"));
        assertThat(((WorkflowProcessInstance)processInstance).getVariable("order"), notNullValue());
        assertThat(((Order)((WorkflowProcessInstance)processInstance).getVariable("order")).getDiscount(), nullValue());


        System.out.println(" ###### Completing Validate Order Process  ###### ");
    }
    
}
