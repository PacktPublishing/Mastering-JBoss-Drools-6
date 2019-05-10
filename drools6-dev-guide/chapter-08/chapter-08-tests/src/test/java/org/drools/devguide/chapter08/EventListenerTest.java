/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter08;

import java.util.stream.IntStream;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Customer.Category;
import org.drools.devguide.eshop.model.SuspiciousOperation;
import org.drools.devguide.util.CustomerBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class EventListenerTest extends BaseTest {

    @Test
    public void matchCancelledTest() {
        KieSession ksession = this.createSession("matchCancelledKsession");
        ksession.addEventListener(new DebugAgendaEventListener());
        ksession.addEventListener(new DebugRuleRuntimeEventListener());

        Customer customer = new CustomerBuilder()
                .withId(1L)
                .withAge(24)
                .withCategory(Customer.Category.GOLD).build();

        
        ksession.insert(customer);
        
        //Generate 5 SuspiciousOperations for the customer and insert them
        //into the session.
        IntStream.range(0, 5)
                .mapToObj(i -> new SuspiciousOperation(customer, SuspiciousOperation.Type.SUSPICIOUS_AMOUNT))
                .forEach(so -> ksession.insert(so));

        ksession.fireAllRules();

        try{
            //The final result if we are testing the "Low category of GOLD customers with suspicious operations"
            //should be SILVER, but there is a conflict between 2 rules.
            assertThat(customer.getCategory(), is(Category.SILVER));
            fail("Exception expected");
        } catch (AssertionError ae){
            //Expected.
        }
    }

}
