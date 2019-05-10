package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.drools.devguide.eshop.model.Discount;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

public class PropertyReactiveTest extends BaseTest {

    protected final String ksessionName = "propertyReactiveKsession";

    @Test
    public void testLargeOrder() throws Exception {
        KieSession ksession = createSession(ksessionName);
        FactType type = ksession.getKieBase().getFactType("chapter04.propertyReactive", "PropertyReactiveOrder");
        Object order = type.newInstance();
        type.set(order, "totalItems", 21);
        ksession.insert(order);
        int firedRules = ksession.fireAllRules();
        assertThat(firedRules, equalTo(1));
        assertThat(type.get(order, "discount"), notNullValue());
        Discount discount = (Discount) type.get(order, "discount");
        assertThat(discount.getPercentage(), equalTo(0.05));
    }
}    
