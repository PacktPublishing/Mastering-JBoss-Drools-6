/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter07;

import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
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
public class DSLTest extends BaseTest {

    /**
     * A simple customer classification DSL scenario based on the Customer's
     * age.
     */
    @Test
    public void testSimpleDSL() {   

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

        KieSession ksession = this.createSession("dslSimpleKsession");

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
     * A more advanced customer classification DSL scenario based on the
     * Customer's number of orders.
     */
    @Test
    public void testAdvancedDSL() {
        
        KieSession ksession = this.createSession("dslAdvancedKsession");

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
     * Creates a new {@link Customer} instance and inserts it into the given KIE
     * Session. This method also creates as many {@link Order} objects
     * associated to this customer as <code>numberOfOrders</code> indicates. The
     * generated orders are also inserted into the passed session.
     *
     * @param ksession the KIE Session where the created Customer and Orders are
     * inserted.
     * @param customerId the id of the generated cutsomer.
     * @param numberOfOrders the number of orders to be created for the
     * customer.
     * @return
     */
    private Customer createAndInsertCustomerWithOrders(KieSession ksession, long customerId, int numberOfOrders) {
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
