package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderLine;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

public class DeclaredTypesTest extends BaseTest {

    protected final String ksessionName = "declaredTypesKsession";

    @Test
    public void testLargeOrder() throws Exception {
        KieSession ksession = createSession(ksessionName);
        Order order1 = createLargeOrder();
        ksession.insert(order1);
        int firedRules = ksession.fireAllRules();
        assertThat(firedRules, equalTo(2));
        assertThat(order1.getDiscount().getPercentage(), equalTo(0.05));
        
        final FactType type = ksession.getKieBase().getFactType("chapter04.declaredTypes", "SpecialOrder");
        Object specialOffer = type.newInstance();
        type.set(specialOffer, "order", order1);
        
        ksession.insert(specialOffer);
        firedRules = ksession.fireAllRules();
        //because we added a SpecialOffer, we fire "Apply special order discount" once more
        assertThat(firedRules, equalTo(1));
    }
    
    private Order  createLargeOrder() {
        Order order = new Order();
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine1 = new OrderLine();
        orderLine1.setItem(new Item("paper block", 5.00, 8.00));
        orderLine1.setQuantity(50);
        OrderLine orderLine2 = new OrderLine();
        orderLine2.setItem(new Item("pen", 1.00, 1.50));
        orderLine2.setQuantity(100);
        orderLines.add(orderLine1);
        orderLines.add(orderLine2);
        order.setItems(orderLines);
        return order;
    }
}
