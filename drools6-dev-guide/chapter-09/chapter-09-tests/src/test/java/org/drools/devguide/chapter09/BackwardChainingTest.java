/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter09;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.drools.devguide.chapter09.model.IsPartOf;
import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.eshop.model.OrderLine;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class BackwardChainingTest extends PhreakInspectorBaseTest {

    /**
     * In this test, an Order containing a Battery and a Distributor is inserted 
     * into a session. Because the two items are not related through the IsPartOf
     * relation the Order doesn't receive any discount.
     */
    @Test
    public void doNoRelatedItemsTest() {

        KieBase kbase = this.createKnowledgeBase("QueryBackwardChainingSampleKBase");
        KieSession ksession = kbase.newKieSession();
        
        //Items
        Item car = new Item("car", 15000D, 20000D);
        Item engine = new Item("engine", 5000D, 7000D);
        Item wheel = new Item("wheel", 50D, 75D);
        Item battery = new Item("battery", 100D, 150D);
        Item distributor = new Item("distributor", 200D, 280D);

        //Order
        Order order = new Order();
        OrderLine line1 = new OrderLine();
        line1.setItem(battery);
        line1.setQuantity(1);
        
        OrderLine line2 = new OrderLine();
        line2.setItem(distributor);
        line2.setQuantity(1);
        
        order.setItems(Arrays.asList(line1, line2));
        
        //Insert the Order as a fact
        ksession.insert(order);

        
        //Insert the relationship between the items as facts
        ksession.insert(new IsPartOf<>(car, engine));
        ksession.insert(new IsPartOf<>(car, wheel));
        ksession.insert(new IsPartOf<>(engine, battery));
        ksession.insert(new IsPartOf<>(engine, distributor));
        
        ksession.fireAllRules();
        
        assertThat(order.getDiscount(), is(nullValue()));
        
    }

    /**
     * In this test, an Order containing a Battery and a Car is inserted into
     * a session. Because the two items are related through the IsPartOf
     * relation (in a transitive way: battery -> engine -> car) the Order
     * receives a 5% discount.
     */
    @Test
    public void doRelatedItemsTest() {
        
        KieBase kbase = this.createKnowledgeBase("QueryBackwardChainingSampleKBase");
        KieSession ksession = kbase.newKieSession();
        
        //Items
        Item car = new Item("car", 15000D, 20000D);
        Item engine = new Item("engine", 5000D, 7000D);
        Item wheel = new Item("wheel", 50D, 75D);
        Item battery = new Item("battery", 100D, 150D);
        Item distributor = new Item("distributor", 200D, 280D);

        //Order
        Order order = new Order();
        OrderLine line1 = new OrderLine();
        line1.setItem(battery);
        line1.setQuantity(1);
        
        OrderLine line2 = new OrderLine();
        line2.setItem(car);
        line2.setQuantity(1);
        
        order.setItems(Arrays.asList(line1, line2));
        
        //Insert the Order as a fact
        ksession.insert(order);
        
        //Insert the relationship between the items as facts
        ksession.insert(new IsPartOf<>(car, engine));
        ksession.insert(new IsPartOf<>(car, wheel));
        ksession.insert(new IsPartOf<>(engine, battery));
        ksession.insert(new IsPartOf<>(engine, distributor));
        
        ksession.fireAllRules();
        
        assertThat(order.getDiscount(), not(nullValue()));
        assertThat(order.getDiscount().getPercentage(), is(0.05));
        
    }
    
    @Test
    public void grahpNetworkTest() throws IOException{
        String phreakNetworkImageName = "B01512_09_22.viz";
        
        KieBase kbase = this.createKnowledgeBase("QueryBackwardChainingSampleKBase");
        
        //Graph the PHREAK Network.
        this.inspector.addNodeLabel(10, "isItemContainedIn");
        this.inspector.addNodeLabel(16, "isItemContainedIn");
        this.inspector.addNodeLabel(14, "[OrderLine from $o.orderLines]");
        this.inspector.addNodeLabel(15, "[OrderLine from $o.orderLines]");
        this.inspector.addNodeLabel(18, "");
        
        InputStream phreakGraph = this.inspector.fromKieBase(kbase);
        
        
        this.writeFile(phreakNetworkImageName, phreakGraph);
    }
}
