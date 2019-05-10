package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.drools.devguide.eshop.model.Order;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class CollectTest extends BaseTest {

    protected final String ksessionName = "collectKsession";

    @Test
    public void testInsertModifyAndDelete() {
        KieSession ksession = createSession(ksessionName);
        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();
        ksession.insert(order1);
        ksession.insert(order2);
        ksession.insert(order3);
        int firedRules = ksession.fireAllRules();
        assertThat(1, equalTo(firedRules));
        
        ksession.insert(new Order());
        firedRules = ksession.fireAllRules();
        assertThat(1, equalTo(firedRules));
    }
}
