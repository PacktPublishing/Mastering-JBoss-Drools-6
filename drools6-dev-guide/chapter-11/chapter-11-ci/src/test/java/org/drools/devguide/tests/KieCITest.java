package org.drools.devguide.tests;

import javax.inject.Inject;

import org.drools.devguide.eshop.model.Item;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Simple example of KReleaseId annotation reading a JAR from a 
 * repository outside of our current class path.
 */
@RunWith(Arquillian.class)
public class KieCITest {

    @Deployment
    public static JavaArchive createDeployment() {
        
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return jar;
    }
    
    @Inject
    @KSession
    @KReleaseId(groupId = "org.drools.devguide",artifactId = "chapter-11-kjar", version = "0.1-SNAPSHOT")
    private KieSession kSession;

    /**
     * This test reads the release from our current local repository, or 
     * remote repositories we have configured in our machine's settings.xml 
     */
    @Test
    public void simpleTest() {
        Assert.assertNotNull(kSession);
        Item item = new Item();
        item.setCost(199.0);
        item.setCategory(Item.Category.NA);
        kSession.insert(item);
        Assert.assertEquals(1, kSession.fireAllRules());
        Assert.assertEquals(Item.Category.LOW_RANGE, item.getCategory());
    }
    

    /**
     * The KieScanner can be used to scan changes in the 
     * dependency at regular intervals (in the current test, 
     * every 10 seconds)
     */
    @Test
    public void kieScannerTest() {
    	KieServices ks = KieServices.Factory.get();
    	ReleaseId rId = ks.newReleaseId("org.drools.devguide", "chapter-11-kjar", "0.1-SNAPSHOT");
    	KieContainer kContainer = ks.newKieContainer(rId);
    	KieScanner kscanner = ks.newKieScanner(kContainer);
    	//you can also configure the kie scanner to scan at a specific moment
    	//in time using kscanner.scanNow()
    	kscanner.start(10_000);
        KieSession kSession = kContainer.newKieSession();
        Assert.assertNotNull(kSession);
        Item item = new Item();
        item.setCost(199.0);
        item.setCategory(Item.Category.NA);
        kSession.insert(item);
        Assert.assertEquals(1, kSession.fireAllRules());
        Assert.assertEquals(Item.Category.LOW_RANGE, item.getCategory());
    }
}
