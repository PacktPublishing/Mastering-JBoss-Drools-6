package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderLine;
import org.drools.devguide.eshop.types.IsLargeOrder;
import org.junit.Test;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;

public class RuleExceptionsTest extends BaseTest {

    private String ksessionName = "ruleExceptionsKsession";
    
    @Test
    public void testInsertModifyAndDelete() {
        KieSession ksession = createSession(ksessionName);
        Order order1 = createOrder("something cheap", 4.00, 40);
        Order order2 = createOrder("something expensive", 50.00, 4);
        Order order3 = createOrder("something VERY expensive", 100.00, 4);
        ksession.insert(order1);
        ksession.insert(order2);
        ksession.insert(order3);
        ksession.fireAllRules();
        Collection<?> largeOrders = ksession.getObjects(new ClassObjectFilter(IsLargeOrder.class));
        assertThat(largeOrders, notNullValue());
        assertThat(largeOrders.size(), equalTo(2));
    }

    private AtomicLong id = new AtomicLong(0l);
    
    private Order createOrder(String itemName, double price, int quantity) {
        Order order = new Order();
        order.setOrderId(id.incrementAndGet());
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine line1 = new OrderLine();
        line1.setItem(new Item(itemName, price / 1.5, price));
        line1.setQuantity(quantity);
        orderLines.add(line1);
        order.setItems(orderLines);
        return order;
    }
}
