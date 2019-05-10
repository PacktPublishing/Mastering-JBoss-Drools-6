/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05;

import java.util.ArrayList;
import java.util.List;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.util.CustomerBuilder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class FunctionTest extends BaseTest{

    @Test
    public void inlineFunctionTest(){
        //Create a session
        KieSession ksession = this.createSession("functions1Ksession");
        
        //Set a global to collect the results
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
        ksession.fireAllRules();

        
        assertThat(globalList, hasSize(1));
        assertThat(globalList.get(0), is("[BRONZE] Jack Leigh"));
    }
    
    @Test
    public void importedFunctionTest(){
        //Create a session
        KieSession ksession = this.createSession("functions2Ksession");
        
        //Set a global to collect the results
        List<String> globalList = new ArrayList<>();
        ksession.setGlobal("globalList", globalList);
        
        //Create a customer
        Customer customer = new CustomerBuilder()
                .withId(1L)
                .withName("Jervis Gabriel")
                .withCategory(Customer.Category.BRONZE)
                .build();
        
        //Insert the customer in the session and fire all the activated rules.
        ksession.insert(customer);
        ksession.fireAllRules();

        
        assertThat(globalList, hasSize(1));
        assertThat(globalList.get(0), is("[BRONZE] Jervis Gabriel"));
    }
    
}
