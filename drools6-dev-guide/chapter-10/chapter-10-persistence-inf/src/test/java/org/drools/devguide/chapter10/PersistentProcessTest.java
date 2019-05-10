package org.drools.devguide.chapter10;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.devguide.util.OrderBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.command.Context;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.infinispan.InfinispanKnowledgeService;

import bitronix.tm.TransactionManagerServices;

public class PersistentProcessTest {

    private KieBase kbase;
    private KieSession ksession;
    private Environment environment;

    private OrderAsyncWorkItemHandler htHandler;
	
    @Before
    public void setUp() throws Exception {
		KieServices ks = KieServices.Factory.get();
        //Get a kie base
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write(ResourceFactory.newClassPathResource("process-order.bpmn2"));
		KieBuilder kbuilder = ks.newKieBuilder(kfs);
		kbuilder.buildAll();
		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			throw new IllegalArgumentException("Couldn't compile process: " + kbuilder.getResults());
		}
		KieContainer kc = ks.newKieContainer(kbuilder.getKieModule().getReleaseId());	
        this.kbase = kc.getKieBase();
		//creation of persistence context
		DefaultCacheManager cm = new DefaultCacheManager("infinispan.xml");
		this.environment = EnvironmentFactory.newEnvironment();
		this.environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, cm);
		this.environment.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
		this.environment.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );
		//creation of work item handlers
		this.htHandler = new OrderAsyncWorkItemHandler();
		//creation of kie session from KieStoreServices
		newSession();
    }

	private void newSession() {
		ksession = InfinispanKnowledgeService.newStatefulKnowledgeSession(kbase, null, environment);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", this.htHandler);
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
	}

	public void reloadSession(long sessionId) {
		ksession = InfinispanKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, environment);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", this.htHandler);
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
	}
	
	@Test
	public void testPersistentProcess() {
		long sessionId = ksession.getIdentifier();
		
    	//Start the process using its id
        ProcessInstance processInstance1 = ksession.startProcess("process-order", null);
        long processInstanceId = processInstance1.getId();
        
        assertThat(ProcessInstance.STATE_ACTIVE, equalTo(processInstance1.getState()));
		//Load the session from the database again into ksession
		reloadSession(sessionId);

		//reload Process Instance (old bean is stale after session disposal)
		ProcessInstance processInstance2 = this.ksession.getProcessInstance(processInstanceId);
		assertThat(ProcessInstance.STATE_ACTIVE, equalTo(processInstance2.getState()));
		assertAtNode(ksession, processInstance2.getId(), "Create Order");
		ksession.getWorkItemManager().completeWorkItem(htHandler.getWorkItemId(), getResults());
		assertAtNode(ksession, processInstance2.getId(), "Prepare and Package");

		//We reload the kie Session again and the process instance
		reloadSession(sessionId);
		ProcessInstance processInstance3 = this.ksession.getProcessInstance(processInstanceId);
		assertThat(ProcessInstance.STATE_ACTIVE, equalTo(processInstance3.getState()));
		assertAtNode(ksession, processInstance3.getId(), "Prepare and Package");
		ksession.getWorkItemManager().completeWorkItem(htHandler.getWorkItemId(), getResults());
		
		assertThat(ProcessInstance.STATE_COMPLETED, equalTo(processInstance3.getState()));
	}
	
	protected void assertAtNode(KieSession ksession, final Long processInstanceId, final String nodeName) {
		boolean atNode = ksession.execute(new GenericCommand<Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Boolean execute(Context ctx) {
				KnowledgeCommandContext kctx = (KnowledgeCommandContext) ctx;
				KieSession ksession = kctx.getKieSession();
				ProcessInstance instance = ksession.getProcessInstance(processInstanceId);
				WorkflowProcessInstance wfInstance = (WorkflowProcessInstance) instance;
				boolean atNode = false;
				for (NodeInstance node : wfInstance.getNodeInstances()) {
					if (nodeName.equals(node.getNodeName())) {
						atNode = true;
						break;
					}
				}
				return atNode;
			}
		});
		assertThat(true, equalTo(atNode));
	}

	@After
	public void tearDown() {
		this.kbase = null;
		this.environment = null;
		this.ksession = null;
	}
	
	private Map<String, Object> getResults() {
        Customer customer = new CustomerBuilder().withId(1L).withName("salaboy")
                .withCategory(Customer.Category.GOLD).build();
        Order order = new OrderBuilder(customer)
                .newLine().withItem().withId(1L).withCost(100)
                        .withName("Item A").withSalePrice(150).end()
                    .withQuantity(3).end()
                .newLine().withItem().withId(2L).withCost(75)
                        .withName("Item B").withSalePrice(130).end()
                    .withQuantity(6).end()
            .end().build();
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("order", order);
		results.put("customer", customer);
		return results;
	}
	

    public static class OrderAsyncWorkItemHandler implements WorkItemHandler {
    	
    	private long workItemId;
    	
    	@Override
    	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    		//do nothing
    	}
    	
    	@Override
    	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    		// Register work item id to know when to continue
    		this.workItemId = workItem.getId();
    		System.out.println("Entering task " + workItem.getName());
    		System.out.println("Parameters:");
    		for (Map.Entry<String, Object> entry : workItem.getParameters().entrySet()) {
    			System.out.println(">>>" + entry.getKey() + " = " + String.valueOf(entry.getValue()));
    		}
    	}
    	
    	public long getWorkItemId() {
			return workItemId;
		}
    }
}

