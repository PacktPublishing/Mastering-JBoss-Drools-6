/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter07;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.OrderBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class DecisionTablesTest extends BaseTest{
    
    /**
     * Executes the simple customer classification scenario from a 
     * decision table.
     */
    @Test
    public void testSimpleDecisionTable(){
        
        this.printGeneratedDRL(DecisionTablesTest.class.getResourceAsStream("/chapter07/dtable-simple/customer-classification-simple.xls"), System.out);
        
        Customer customer1 = new CustomerBuilder()
                .withId(1L)
                .withAge(19)
                .build();
        
        Customer customer2 = new CustomerBuilder()
                .withId(2L)
                .withAge(27)
                .build();
        
        Customer customer3 = new CustomerBuilder()
                .withId(3L)
                .withAge(32)
                .build();
        
        Customer customer4 = new CustomerBuilder()
                .withId(4L)
                .withAge(60)
                .build();
        
        KieSession ksession = this.createSession("dtableSimpleKsession");
        
        ksession.insert(customer1);
        ksession.insert(customer2);
        ksession.insert(customer3);
        ksession.insert(customer4);
        
        ksession.fireAllRules();
        
        assertThat(customer1.getCategory(), is(Customer.Category.NA));
        assertThat(customer2.getCategory(), is(Customer.Category.BRONZE));
        assertThat(customer3.getCategory(), is(Customer.Category.SILVER));
        assertThat(customer4.getCategory(), is(Customer.Category.GOLD));
        
        
    }
    
    /**
     * Executes the simple customer classification scenario from a 
     * decision table. In this case, the source spreadsheet contains more
     * advanced features such as hidden columns, merged cells and functions.
     * 
     */
    @Test
    public void testEnhancedDecisionTable(){
        
        this.printGeneratedDRL(DecisionTablesTest.class.getResourceAsStream("/chapter07/dtable-enhanced/customer-classification-enhanced.xls"), System.out);
        
        Customer customer1 = new CustomerBuilder()
                .withId(1L)
                .withAge(19)
                .build();
        
        Customer customer2 = new CustomerBuilder()
                .withId(2L)
                .withAge(27)
                .build();
        
        Customer customer3 = new CustomerBuilder()
                .withId(3L)
                .withAge(32)
                .build();
        
        Customer customer4 = new CustomerBuilder()
                .withId(4L)
                .withAge(60)
                .build();
        
        KieSession ksession = this.createSession("dtableEnhancedKsession");
        
        ksession.insert(customer1);
        ksession.insert(customer2);
        ksession.insert(customer3);
        ksession.insert(customer4);
        
        ksession.fireAllRules();
        
        assertThat(customer1.getCategory(), is(Customer.Category.NA));
        assertThat(customer2.getCategory(), is(Customer.Category.BRONZE));
        assertThat(customer3.getCategory(), is(Customer.Category.SILVER));
        assertThat(customer4.getCategory(), is(Customer.Category.GOLD));
        
        
    }
    
    /**
     * Executes the advanced customer classification scenario from a 
     * decision table.
     */
    @Test
    public void testAdvancedDecisionTable(){
        
        this.printGeneratedDRL(DecisionTablesTest.class.getResourceAsStream("/chapter07/dtable-advanced/customer-classification-advanced.xls"), System.out);
        
        KieSession ksession = this.createSession("dtableAdvancedKsession");
        
        Customer customer1 = createAndInsertCustomerWithOrders(ksession, 1, 3);
        Customer customer2 = createAndInsertCustomerWithOrders(ksession, 2, 7);
        Customer customer3 = createAndInsertCustomerWithOrders(ksession, 3, 15);
        Customer customer4 = createAndInsertCustomerWithOrders(ksession, 4, 50);
        
        ksession.fireAllRules();
        
        assertThat(customer1.getCategory(), is(Customer.Category.NA));
        assertThat(customer2.getCategory(), is(Customer.Category.BRONZE));
        assertThat(customer3.getCategory(), is(Customer.Category.SILVER));
        assertThat(customer4.getCategory(), is(Customer.Category.GOLD));
        
    }
    
    /**
     * Converts a decision table into DRL and prints the result in the
     * passed OutputStream.
     * @param decisionTable the decision table to be converted.
     * @param target the stream where the generated DRL will be printed.
     */
    private void printGeneratedDRL(InputStream decisionTable, OutputStream target){
        try {
            DecisionTableProviderImpl dtp = new DecisionTableProviderImpl();
            String drl = dtp.loadFromInputStream(decisionTable, null);
            
            IOUtils.copy(new ByteArrayInputStream(drl.getBytes()), target);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private Customer createAndInsertCustomerWithOrders(KieSession ksession, long customerId, int numberOfOrders){
        Customer customer = new CustomerBuilder()
                .withId(customerId)
                .build();
        
        ksession.insert(customer);
        
        for (int i = 0; i < numberOfOrders; i++) {
            ksession.insert(new OrderBuilder(customer).build());
        }
        
        return customer;
    }
    
}
