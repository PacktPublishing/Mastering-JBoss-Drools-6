/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter08;

import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Discount;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.SuspiciousOperation;
import org.drools.devguide.eshop.service.SuspiciousOperationService;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.factories.ModelFactory;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

/**
 *
 * @author esteban
 */
public class NoMatchTest extends BaseTest {

    @Test
    public void fromTypesMismatchTest() {
        KieSession ksession = this.createSession("noMatchKsession");

        Customer customer = new CustomerBuilder()
                .withId(1L)
                .withCategory(Customer.Category.SILVER).build();

        Order order = ModelFactory.getOrderWithFiveHighRangeItems();
        order.setCustomer(customer);
        order.setDiscount(new Discount(95.0));

        ksession.insert(customer);
        ksession.insert(order);

        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults("Get All Suspicious Operations");

        //Becasue of an error in the rule we are testing, no Suspicious Operation
        //was detected.
        //Read Chapter-09 to know how to fix this problem.
        assertThat(results.size(), is(0));
    }

    @Test
    public void fromImplicitLoopTest() {
        KieSession ksession = this.createSession("noMatchKsession");

        Customer customer = new CustomerBuilder()
                .withId(1L)
                .withCategory(Customer.Category.GOLD).build();

        //Mock the SuspiciousOperationService interface
        SuspiciousOperationService service = (Long customerId) -> IntStream.range(0, 5)
                .mapToObj(i -> new SuspiciousOperation())
                .collect(toList()); //Generate 5 SuspiciousOperations
        
        ksession.setGlobal("suspiciousOperationService", service);
        ksession.insert(customer);

        ksession.fireAllRules();

        assertThat(customer.getCategory(), is(Customer.Category.GOLD));
    }
}
