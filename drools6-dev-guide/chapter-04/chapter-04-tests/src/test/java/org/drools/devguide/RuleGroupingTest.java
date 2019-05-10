package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.drools.devguide.eshop.model.Discount;
import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.OrderLine;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;

public class RuleGroupingTest extends BaseTest {

    protected final String ksessionName = "ruleGroupingKsession";
    
    @Test
    public void testInsertModifyAndDelete() {
        KieSession ksession = createSession(ksessionName);
        
        Item item = new Item("pencil", 1.5, 2.0);
        OrderLine orderLine = new OrderLine();
        orderLine.setItem(item);
        orderLine.setQuantity(11);
        ksession.insert(orderLine);
        int firedRules = ksession.fireAllRules();
        assertThat(0, equalTo(firedRules));
        ksession.getAgenda().getAgendaGroup("promotions").setFocus();
        firedRules = ksession.fireAllRules();
        assertThat(3, equalTo(firedRules));
        
        Collection<?> discounts = ksession.getObjects(new DiscountObjectFilter());
        assertThat(discounts, notNullValue());
        assertThat(1, equalTo(discounts.size()));
        Discount discount = (Discount) discounts.iterator().next();
        assertThat(discount.getPercentage(), equalTo(0.10));
        //we remove the discount object from the ksession
        ksession.delete(ksession.getFactHandle(discount));
        
        OrderLine orderLine2 = new OrderLine();
        orderLine2.setItem(item);
        orderLine2.setQuantity(7);
        ksession.insert(orderLine2);
        //we need to reselect the promotions agenda group, because one rule execution changed it to MAIN
        ksession.getAgenda().getAgendaGroup("promotions").setFocus();
        firedRules = ksession.fireAllRules();
        assertThat(3, equalTo(firedRules));
        
        discounts = ksession.getObjects(new DiscountObjectFilter());
        assertThat(discounts, notNullValue());
        assertThat(1, equalTo(discounts.size()));
        Discount discount2 = (Discount) discounts.iterator().next();
        assertThat(discount2.getPercentage(), equalTo(0.05));
    }
    
    class DiscountObjectFilter implements ObjectFilter {
        @Override
        public boolean accept(Object obj) {
            return obj instanceof Discount;
        }
    }
}
