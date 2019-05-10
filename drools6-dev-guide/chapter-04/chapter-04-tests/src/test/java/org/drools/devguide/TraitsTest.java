package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.OrderLine;
import org.drools.devguide.eshop.traits.TraitableItem;
import org.drools.devguide.eshop.traits.TraitableOrder;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;

public class TraitsTest  extends BaseTest {

    protected final String ksessionName = "traitsKsession";

    @Test
    public void testInsertModifyAndDelete() {
        KieSession ksession = createSession(ksessionName);
        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                System.out.println(event.getMatch().getRule().getName());
            }
        });
        
        TraitableOrder order = createSchoolItemsOrder();
        TraitableItem item = createToyItem();
        ksession.insert(item);
        ksession.insert(order);
        //when we fire the rules, we should see new traits created
        int firedRules = ksession.fireAllRules();
        assertThat(firedRules, equalTo(4));

        //get KidFriendly objects
        Collection<?> kidFriendlyFlags = ksession.getObjects(new KidFriendlyObjectFilter());
        assertThat(kidFriendlyFlags, notNullValue());
        assertThat(kidFriendlyFlags.size(), equalTo(2));
    }
    
    class KidFriendlyObjectFilter implements ObjectFilter {
        @Override
        public boolean accept(Object object) {
            String className = object.getClass().getName();
            return className.contains("KidFriendly");
        }
    }
    
    private TraitableItem createToyItem() {
        TraitableItem item = new TraitableItem();
        item.setName("cool toy for kids");
        return item;
    }

    private TraitableOrder createSchoolItemsOrder() {
        TraitableOrder order = new TraitableOrder();
        OrderLine line = new OrderLine();
        line.setItem(new Item("school textboox", 10.00, 21.00));
        line.setQuantity(1);
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        orderLines.add(line);
        order.setOrderLines(orderLines);
        return order;
    }
}
