package org.drools.devguide.chapter02;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Collection;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Coupon;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.util.factories.ModelFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author salaboy
 */
public class OrderDiscountTest extends BaseTest {

    @Test
    public void highRangeOrderDiscountTest() {
        KieSession kSession = createDefaultSession();

        Order o = ModelFactory.getOrderWithFiveHighRangeItems();
        
        kSession.insert(o.getCustomer());
        kSession.insert(o.getOrderLines().get(0));
        kSession.insert(o.getOrderLines().get(1));
        kSession.insert(o.getOrderLines().get(2));
        kSession.insert(o.getOrderLines().get(3));
        kSession.insert(o.getOrderLines().get(4));
        kSession.insert(o.getOrderLines().get(0).getItem());
        kSession.insert(o.getOrderLines().get(1).getItem());
        kSession.insert(o.getOrderLines().get(2).getItem());
        kSession.insert(o.getOrderLines().get(3).getItem());
        kSession.insert(o.getOrderLines().get(4).getItem());
        kSession.insert(o);
        
        int fired = kSession.fireAllRules();

        // We have 5 Items that are categorized -> 5 rules were fired
        // We have 1 Customer that needs to be categorized -> 1 rule fired
        // We have just one order with all HIGH RAnge items -> 1 rule fired
        // One Coupon is created for the SILVER Customer -> 1 rule fired
        assertThat(8, is(fired));
        assertThat(o.getCustomer().getCategory(), is(Customer.Category.SILVER));
        assertThat(o.getDiscount(), not(nullValue()));
        assertThat(o.getDiscount().getPercentage(), is(10.0));
        assertThat(o.getOrderLines().get(0).getItem().getCategory(), is(Item.Category.HIGH_RANGE));
        assertThat(o.getOrderLines().get(1).getItem().getCategory(), is(Item.Category.HIGH_RANGE));
        assertThat(o.getOrderLines().get(2).getItem().getCategory(), is(Item.Category.HIGH_RANGE));
        assertThat(o.getOrderLines().get(3).getItem().getCategory(), is(Item.Category.HIGH_RANGE));
        assertThat(o.getOrderLines().get(4).getItem().getCategory(), is(Item.Category.HIGH_RANGE));
        // The Coupon Object was created by the Rule Engine so we need to get it from the KieSession
        Collection<Coupon> coupons = getFactsFromKieSession(kSession, Coupon.class);
        assertThat(1, is(coupons.size()));

    }
}
