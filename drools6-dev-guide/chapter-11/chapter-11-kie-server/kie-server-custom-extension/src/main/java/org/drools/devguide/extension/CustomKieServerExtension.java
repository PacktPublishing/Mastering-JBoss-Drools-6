package org.drools.devguide.extension;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.management.DroolsManagementAgent;
import org.kie.server.services.api.KieContainerInstance;
import org.kie.server.services.api.KieServerExtension;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;
import org.kie.server.services.impl.KieServerImpl;

public class CustomKieServerExtension implements KieServerExtension {

    public static final String EXTENSION_NAME = "Statistics";

    private KieServerRegistry registry;

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void init(KieServerImpl kieServer, KieServerRegistry registry) {
        this.registry = registry;
        if (getDroolsExtension(registry) != null) {
        	//try to get the MBeanServer to load (only if using drools kieserver extension)
        	ManagementFactory.getPlatformMBeanServer();
        }
    }

	private KieServerExtension getDroolsExtension(KieServerRegistry registry) {
		List<KieServerExtension> extensions = registry.getServerExtensions();
    	KieServerExtension droolsExtension = null;
    	if (extensions != null) {
    		for (KieServerExtension extension : extensions) {
    			if ("Drools".equals(extension.toString())) {
    				droolsExtension = extension;
    				break;
    			}
    		}
    	}
		return droolsExtension;
	}

    @Override
    public void destroy(KieServerImpl kieServer, KieServerRegistry registry) {
        // no-op
    }

    @Override
    public void createContainer(String id, KieContainerInstance kieContainerInstance, Map<String, Object> parameters) {
    	if (getDroolsExtension(registry) != null) {
    		// create kbases to register the KnowledgeBases in the statistics module
    		Collection<String> kbases = kieContainerInstance.getKieContainer().getKieBaseNames();
    		for (String kbase : kbases) {
    			InternalKnowledgeBase kbaseObject = (InternalKnowledgeBase) 
    					kieContainerInstance.getKieContainer().getKieBase(kbase);
    			DroolsManagementAgent.getInstance().registerKnowledgeBase(kbaseObject);
    		}
    	}
    }

    @Override
    public void disposeContainer(String id, KieContainerInstance kieContainerInstance, Map<String, Object> parameters) {
    }

    @Override
    public List<Object> getAppComponents(SupportedTransports type) {
    	return Collections.emptyList();
    }

    @Override
    public <T> T getAppComponents(Class<T> serviceType) {
        return null;
    }

    @Override
    public String getImplementedCapability() {
        return EXTENSION_NAME;
    }

    @Override
    public String toString() {
        return EXTENSION_NAME + " KIE Server extension";
    }
}
