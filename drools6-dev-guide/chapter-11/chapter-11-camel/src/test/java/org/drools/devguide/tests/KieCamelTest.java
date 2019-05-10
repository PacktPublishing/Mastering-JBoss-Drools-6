package org.drools.devguide.tests;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.devguide.eshop.model.Item;
import org.junit.Test;
import org.kie.spring.InternalKieSpringUtils;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class KieCamelTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return (AbstractXmlApplicationContext) InternalKieSpringUtils.getSpringContext(
                new ReleaseIdImpl("org.drools.devguide", "chapter-11-camel", "0.1-SNAPSHOT"), 
                getClass().getResource("/cxf-rs-spring.xml"));
    }

    @Test
    public void testInvocation() throws Exception {
        String cmd = "";
        cmd += "<batch-execution lookup=\"ksession1\">\n";
        cmd += "  <insert out-identifier=\"myItem\">\n";
        cmd += "      <org.drools.devguide.eshop.model.Item>\n";
        cmd += "         <cost>119.0</cost>\n";
        cmd += "         <category>NA</category>\n";
        cmd += "      </org.drools.devguide.eshop.model.Item>\n";
        cmd += "   </insert>\n";
        cmd += "   <fire-all-rules/>\n";
        cmd += "</batch-execution>\n";

        Exchange exchange = this.createExchangeWithBody(cmd);
        
        ProducerTemplate producer = this.context.createProducerTemplate();
        producer.setDefaultEndpointUri("direct://http");
        producer.send(exchange);
        
        Object obj = this.applicationContext.getBean("myItemList");
        assertNotNull(obj);
        assertTrue(obj instanceof List);
        List<?> list = (List<?>) obj;
        assertFalse(list.isEmpty());
        assertEquals(list.size(), 1);
        Object subObj = list.iterator().next();
        assertNotNull(subObj);
        assertTrue(subObj instanceof Item);
        Item item = (Item) subObj;
        assertEquals(item.getCost().doubleValue(), 119.0, 0.1);
    }
    
}
