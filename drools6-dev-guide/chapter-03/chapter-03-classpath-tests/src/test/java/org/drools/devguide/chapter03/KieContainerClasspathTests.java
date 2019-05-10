package org.drools.devguide.chapter03;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;

/**
 *
 * @author salaboy
 */
public class KieContainerClasspathTests {

    @Test
    public void loadingRulesFromLocalKieModule() {
        System.out.println("### Running loadingRulesFromLocalKieModule() Test ###");
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.newKieClasspathContainer();

        Results results = kContainer.verify();
        results.getMessages().stream().forEach((message) -> {
            System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
        });
        assertThat(false, is(results.hasMessages(Message.Level.ERROR)));
        kContainer.getKieBaseNames().stream().map((kieBase) -> {
            System.out.println(">> Loading KieBase: "+ kieBase );
            return kieBase;
        }).forEach((kieBase) -> {
            kContainer.getKieSessionNamesInKieBase(kieBase).stream().forEach((kieSession) -> {
                System.out.println("\t >> Containing KieSession: "+ kieSession );
            });
        });
        
        // Let's load the configurations for the kmodule.xml file 
        //  defined in the /src/test/resources/META-INF/ directory
        KieSession kieSession = kContainer.newKieSession("rules.cp.discount.session");

        Customer customer = new Customer();
        customer.setCategory(Customer.Category.BRONZE);

        Order order = new Order();
        order.setCustomer(customer);

        kieSession.insert(customer);
        kieSession.insert(order);

        int fired = kieSession.fireAllRules();

        assertThat(1, is(fired));
        assertThat(5.0, is(order.getDiscount().getPercentage()));

        System.out.println("### Finished loadingRulesFromLocalKieModule() Test ###");
    }
    
    @Test
    public void loadingRulesFromDependencyKieModule() {
        System.out.println("### Running loadingRulesFromDependencyKieModule() Test ###");
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.newKieClasspathContainer();

        Results results = kContainer.verify();
        results.getMessages().stream().forEach((message) -> {
            System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
        });
        assertThat(false, is(results.hasMessages(Message.Level.ERROR)));
        kContainer.getKieBaseNames().stream().map((kieBase) -> {
            System.out.println(">> Loading KieBase: "+ kieBase );
            return kieBase;
        }).forEach((kieBase) -> {
            kContainer.getKieSessionNamesInKieBase(kieBase).stream().forEach((kieSession) -> {
                System.out.println("\t >> Containing KieSession: "+ kieSession );
            });
        });
        
        // Let's load the configurations for the kmodule.xml file defined 
        //  in the chapter-03-simple-discounts/src/main/resources/META-INF/ directory
        // Notice that here the rule defined in the tests is not loaded
        KieSession kieSession = kContainer.newKieSession("rules.simple.discount");

        Customer customer = new Customer();
        customer.setCategory(Customer.Category.SILVER);

        Order order = new Order();
        order.setCustomer(customer);

        kieSession.insert(customer);
        kieSession.insert(order);

        int fired = kieSession.fireAllRules();

        assertThat(1, is(fired));
        assertThat(10.0, is(order.getDiscount().getPercentage()));

        System.out.println("### Finished loadingRulesFromDependencyKieModule() Test ###");
    }
    
    
     @Test
    public void statelessSessionTest() {
        System.out.println("### Running statelessSessionTest() Test ###");
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.newKieClasspathContainer();

        Results results = kContainer.verify();
        results.getMessages().stream().forEach((message) -> {
            System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
        });
        assertThat(false, is(results.hasMessages(Message.Level.ERROR)));
        kContainer.getKieBaseNames().stream().map((kieBase) -> {
            System.out.println(">> Loading KieBase: "+ kieBase );
            return kieBase;
        }).forEach((kieBase) -> {
            kContainer.getKieSessionNamesInKieBase(kieBase).stream().forEach((kieSession) -> {
                System.out.println("\t >> Containing KieSession: "+ kieSession );
            });
        });
        
        StatelessKieSession statelessKieSession = kContainer.newStatelessKieSession("rules.simple.sl.discount");
        
        Assert.assertNotNull(statelessKieSession);
         
        Customer customer = new Customer();
        customer.setCategory(Customer.Category.SILVER);

        Order order = new Order();
        order.setCustomer(customer);

        Command newInsertOrder = ks.getCommands().newInsert(order, "orderOut");
        Command newInsertCustomer = ks.getCommands().newInsert(customer);
        Command newFireAllRules = ks.getCommands().newFireAllRules("outFired");
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(newInsertOrder);
        cmds.add(newInsertCustomer);
        cmds.add(newFireAllRules);
        ExecutionResults execResults = statelessKieSession.execute(ks.getCommands().newBatchExecution(cmds));
        
        order = (Order)execResults.getValue("orderOut");
        int fired = (Integer)execResults.getValue("outFired");

        assertThat(1, is(fired));
        assertThat(10.0, is(order.getDiscount().getPercentage()));

        System.out.println("### Finished statelessSessionTest() Test ###");
    }

    @Test
    public void loadingRulesFromDependencyParentKieModule() {
        System.out.println("### Running loadingRulesFromDependencyParentKieModule() Test ###");
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.newKieClasspathContainer();

        Results results = kContainer.verify();
        results.getMessages().stream().forEach((message) -> {
            System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
        });
        assertThat(false, is(results.hasMessages(Message.Level.ERROR)));
        kContainer.getKieBaseNames().stream().map((kieBase) -> {
            System.out.println(">> Loading KieBase: "+ kieBase );
            return kieBase;
        }).forEach((kieBase) -> {
            kContainer.getKieSessionNamesInKieBase(kieBase).stream().forEach((kieSession) -> {
                System.out.println("\t >> Containing KieSession: "+ kieSession );
            });
        });
        
        // Let's load the configurations for the parent kmodule.xml file defined 
        //  in the chapter-03-kjar-parent/src/main/resources/META-INF/ directory
        KieSession kieSession = kContainer.newKieSession("rules.discount.all");

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setCategory(Customer.Category.SILVER);

        Order order = new Order();
        order.setCustomer(customer);

        kieSession.insert(customer);
        kieSession.insert(order);

        int fired = kieSession.fireAllRules();

        assertThat(1, is(fired));
        assertThat(10.0, is(order.getDiscount().getPercentage()));
        
        
        Customer customerGold = new Customer();
        customerGold.setCustomerId(2L);
        customerGold.setCategory(Customer.Category.GOLD);

        Order orderGold = new Order();
        orderGold.setCustomer(customerGold);

        kieSession.insert(customerGold);
        kieSession.insert(orderGold);

        fired = kieSession.fireAllRules();

        assertThat(1, is(fired));
        assertThat(20.0, is(orderGold.getDiscount().getPercentage()));

        System.out.println("### Finished loadingRulesFromDependencyParentKieModule() Test ###");
    }
    
}
