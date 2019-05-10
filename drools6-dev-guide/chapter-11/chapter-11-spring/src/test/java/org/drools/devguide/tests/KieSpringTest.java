package org.drools.devguide.tests;

import org.drools.devguide.eshop.model.Item;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-context.xml"})
public class KieSpringTest {

	@Autowired
	private KieSession ksession1;

	@Test
	public void testSpringRuleExecution() {
		Assert.assertNotNull(ksession1);

		//rules in ksession1 still havent fired 
		final Item item = createItem();
		ksession1.insert(item);
		int countRules = ksession1.fireAllRules();
		Assert.assertEquals(1, countRules);
		Assert.assertEquals(Item.Category.LOW_RANGE, item.getCategory());
}

	private Item createItem() {
		Item item = new Item();
		item.setCost(198.0);
		item.setCategory(Item.Category.NA);
		return item;
	}
}
