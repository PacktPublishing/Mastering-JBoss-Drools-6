package org.drools.devguide.kieserver;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.drools.devguide.jaxb.JaxbItem;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

@RunWith(Arquillian.class)
public class RESTClientExampleTest {
	
	@Deployment
	public static WebArchive deployKieServer() throws Exception {
		String s = File.separator;
        String warFile = System.getProperty("user.home") + s + ".m2" 
        		+ s + "repository" + s + "org" + s + "drools" + s + "devguide" 
        		+ s + "kie-server-war" + s + "0.1-SNAPSHOT" 
        		+ s + "kie-server-war-0.1-SNAPSHOT-custom.war";
        File destFile = new File(System.getProperty("java.io.tmpdir") + s + "kie-server.war");
        FileUtils.copyFile(new File(warFile), destFile);
        return ShrinkWrap.createFromZipFile(WebArchive.class, destFile);
	}
	
	@Test
	@RunAsClient
	public void runSimpleRules() throws Exception {
		String USER = "testuser";
	    String PASSWORD = "test";
	    Set<Class<?>> extraJaxbClasses = new HashSet<Class<?>>(Arrays.asList(JaxbItem.class));

	    //Deploy a container in KIE Server
	    KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(
				"http://localhost:8080/kie-server/services/rest/server", 
				USER, PASSWORD, 60000);
		config.addJaxbClasses(extraJaxbClasses);
		KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
	    KieContainerResource kContainer = new KieContainerResource();
	    ReleaseId releaseId = new ReleaseId();
	    releaseId.setGroupId("org.drools.devguide");
	    releaseId.setArtifactId("chapter-11-kjar");
	    releaseId.setVersion("0.1-SNAPSHOT");
	    kContainer.setReleaseId(releaseId);
	    kContainer.setContainerId("my-deploy");
	    ServiceResponse<KieContainerResource> resp = client.createContainer("my-deploy", kContainer);
	    Assert.assertNotNull(resp);
	    Assert.assertEquals(KieContainerStatus.STARTED, resp.getResult().getStatus());
	    //Server is now available to receive requests
	}
}
