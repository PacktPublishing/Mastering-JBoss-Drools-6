package org.drools.devguide.chapter02;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Item.Category;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author salaboy
 */
public class ClassifyItemsTest extends BaseTest {

    @Test
    public void lowRangeItemClassificationTest() {
        KieSession kSession = createDefaultSession();
        Item item = new Item("A", 123.0, 234.0);
        kSession.insert(item);
        int fired = kSession.fireAllRules();
        assertThat(1, is(fired));
        assertThat(Category.LOW_RANGE, is(item.getCategory()));
    }
    
//    @Test
//    public void midRangeItemClassificationTest() {
//    }
    
//    @Test
//    public void highRangeItemClassificationTest() {
//    }
}
