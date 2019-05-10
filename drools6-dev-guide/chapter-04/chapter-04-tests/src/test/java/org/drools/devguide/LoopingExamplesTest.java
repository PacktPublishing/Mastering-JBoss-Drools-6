package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderLine;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class LoopingExamplesTest extends BaseTest {

    protected final String ksessionName = "loopingExamplesKsession";
    
    @Test
    public void testNoLoop() {
        KieSession ksession1 = createSession(ksessionName);
        Item item1 = new Item("notepad", 10.00, 20.00);
        ksession1.insert(item1);
        //to break down the rules in groups, we use agenda groups
        ksession1.getAgenda().getAgendaGroup("withoutNoLoop").setFocus();
        //because it is going to go on an infinite loop, we limit the amount of rules 
        //it can run before forcefully stopping after an amount of rule executions
        int firedRules = ksession1.fireAllRules(100);
        assertThat(firedRules, equalTo(100));
        //after running the same rule so much, sale price gets reduced every time...
        assertThat(item1.getSalePrice(), lessThan(1.00));
        
        KieSession ksession2 = createSession(ksessionName);
        Item item2 = new Item("notepad", 10.00, 20.00);
        ksession2.insert(item2);
        //we activate the group where no loop is working 
        ksession2.getAgenda().getAgendaGroup("withNoLoop").setFocus();
        //now the rule should fire only once
        firedRules = ksession2.fireAllRules();
        assertThat(1, equalTo(firedRules));
        assertThat(item2.getSalePrice(), equalTo(18.00));
    }
    
    @Test
    public void testLockOnActive() {
        KieSession ksession1 = createSession(ksessionName);
        Order order1 = createLargeOrder();
        ksession1.insert(order1);
        //to break down the rules in groups, we use agenda groups
        ksession1.getAgenda().getAgendaGroup("withoutLockOnActive").setFocus();
        //because it is going to go on an infinite loop, we limit the amount of rules 
        //it can run before forcefully stopping after an amount of rule executions
        int firedRules = ksession1.fireAllRules(100);
        assertThat(firedRules, equalTo(100));
        //after running the same rules so much, discounts increase a lot...
        assertThat(order1.getDiscount().getPercentage(), greaterThan(1.00)); // they are greater than 100%!
        
        KieSession ksession2 = createSession(ksessionName);
        Order order2 = createLargeOrder();
        ksession2.insert(order2);
        //we activate the group where no loop is working 
        ksession2.getAgenda().getAgendaGroup("withLockOnActive").setFocus();
        //now each rule should fire only once
        firedRules = ksession2.fireAllRules();
        assertThat(2, equalTo(firedRules));
        assertThat(order2.getDiscount().getPercentage(), equalTo(0.04)); //discounts are exactly 4%
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
