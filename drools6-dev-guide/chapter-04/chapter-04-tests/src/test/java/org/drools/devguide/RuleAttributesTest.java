package org.drools.devguide;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.drools.devguide.eshop.model.Item;
import org.drools.devguide.eshop.model.Item.Category;
import org.drools.devguide.eshop.service.EShopConfigService;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class RuleAttributesTest extends BaseTest {

    protected final String ksessionName = "ruleAttributesKsession";
    
    @Test
    public void testRuleAttributes() {
        KieSession ksession = createSession(ksessionName);
        EShopConfigService mockService = mock(EShopConfigService.class);
        when(mockService.isMidHighCategoryEnabled()).thenReturn(true);
        ksession.setGlobal("configService", mockService);
        Item item = new Item("item 1", 350.00, 500.00);
        ksession.insert(item);
        int fired = ksession.fireAllRules();
        assertThat(1, equalTo(fired));
        assertThat(Category.SPECIAL_MIDHIGH_RANGE, equalTo(item.getCategory()));
    }
    
    @Test
    public void testDisabledRule() {
        KieSession ksession = createSession(ksessionName);
        EShopConfigService mockService = mock(EShopConfigService.class);
        when(mockService.isMidHighCategoryEnabled()).thenReturn(false);
        ksession.setGlobal("configService", mockService);
        Item item = new Item("item 1", 350.00, 500.00);
        ksession.insert(item);
        int fired = ksession.fireAllRules();
        assertThat(1, equalTo(fired));
        assertThat(Category.MID_RANGE, equalTo(item.getCategory()));
    }
}
