package org.drools.devguide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.drools.devguide.eshop.events.TransactionEvent;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CEPEntryPointsTest extends BaseTest {

    @Test
    public void testCEPRules() {
        KieSession ksession = createSession("cepKsession");
        Long customer1 = 1L;
        Long customer2 = 2L;
        List<TransactionEvent> smallListOfEvents1 = createTransactionEventList(customer1, 11);
        for (TransactionEvent event : smallListOfEvents1) {
            ksession.getEntryPoint("small-client-portal").insert(event);
        }
        int ruleCount = ksession.fireAllRules();
        assertThat(ruleCount, equalTo(1));
        
        List<TransactionEvent> smallListOfEvents2 = createTransactionEventList(customer2, 11);
        for (TransactionEvent event : smallListOfEvents2) {
            ksession.getEntryPoint("big-client-portal").insert(event);
        }
        ruleCount = ksession.fireAllRules();
        assertThat(ruleCount, equalTo(0));
        
        List<TransactionEvent> bigListOfEvents2 = createTransactionEventList(customer2, 101);
        for (TransactionEvent event : bigListOfEvents2) {
            ksession.getEntryPoint("big-client-portal").insert(event);
        }
        ruleCount = ksession.fireAllRules();
        assertThat(ruleCount, equalTo(1));
    }
    
    private List<TransactionEvent> createTransactionEventList(Long customerId, int size) {
        List<TransactionEvent> events = new ArrayList<TransactionEvent>(size);
        Random r = new Random(System.currentTimeMillis());
        for (int index = 0; index < size; index++) {
            events.add(new TransactionEvent(customerId, (r.nextDouble() * 10)));
        }
        return events;
    }
}
