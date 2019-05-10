/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05;

import java.util.ArrayList;
import java.util.List;
import org.drools.devguide.BaseTest;
import org.drools.devguide.chapter05.listener.TestAgendaEventListener;
import org.drools.devguide.chapter05.listener.TestRuleRuntimeEventListener;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.util.CustomerBuilder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 *
 * @author esteban
 */
public class EventListenerTest extends BaseTest{

    @Test
    public void agendaEventListenerTest(){
        //This test will reuse the session defined for the functions tests.
        KieSession ksession = this.createSession("functions1Ksession");
        
        //attach an event listener
        TestAgendaEventListener agendaEventListener = new TestAgendaEventListener();
        ksession.addEventListener(agendaEventListener);
        
        //Set the required global
        List<String> globalList = new ArrayList<>();
        ksession.setGlobal("globalList", globalList);
        
        //Create a customer
        Customer customer = new CustomerBuilder()
                .withId(1L)
                .withName("Jack Leigh")
                .withCategory(Customer.Category.BRONZE)
                .build();
        
        //Insert the customer in the session and fire all the activated rules.
        ksession.insert(customer);
        
        //In Drools 5, this insertion would cause a match (called "activation"
        //in Drools 5). Drools 6 uses Phreak wich is a "lazy" modification of
        //Rete. One of the implicances of this is that matches don't happen
        //until fireAllRules is invoked.
        assertThat(agendaEventListener.getNumberOfMatches(), is (0));
        
        //because we haven't called fireAllRules() yet, no match was fired.
        assertThat(agendaEventListener.getNumberOfFiredMatches(), is (0));
        
        //We now fire all the rules and check the counters again.
        ksession.fireAllRules();
        assertThat(agendaEventListener.getNumberOfMatches(), is (1));
        assertThat(agendaEventListener.getNumberOfMatches(), is (1));

    }
    
    @Test
    public void ruleRuntimeEventListenerTest(){
        //This test will reuse the session defined for the functions tests.
        KieSession ksession = this.createSession("functions1Ksession");
        
        //attach an event listener
        TestRuleRuntimeEventListener runtimeEventListener = new TestRuleRuntimeEventListener();
        ksession.addEventListener(runtimeEventListener);
        
        //Set the required global
        List<String> globalList = new ArrayList<>();
        ksession.setGlobal("globalList", globalList);
        
        //Create a customer
        Customer customer = new CustomerBuilder()
                .withId(1L)
                .withName("Jack Leigh")
                .withCategory(Customer.Category.BRONZE)
                .build();
        
        //Insert the customer in the session and fire all the activated rules.
        FactHandle customerFH = ksession.insert(customer);
        
        //when a fact is inserted, all the RuleRuntimeEventListeners are 
        //notified.
        assertThat(runtimeEventListener.getNumberOfFacts(), is(1));
        
        //we will now modify the fact we have previously inserted. Remember
        //that this modification could also happen inside a rule.
        customer.setAge(35);
        ksession.update(customerFH, customer);
        assertThat(runtimeEventListener.getNumberOfFacts(), is(1));
        assertThat(runtimeEventListener.getNumberOfModifiedFacts(), is(1));
        
        //we will now retract the fact. Again, remember that this could also
        //happen inside a rule.
        ksession.delete(customerFH);
        assertThat(runtimeEventListener.getNumberOfFacts(), is(0));

    }
    
}
