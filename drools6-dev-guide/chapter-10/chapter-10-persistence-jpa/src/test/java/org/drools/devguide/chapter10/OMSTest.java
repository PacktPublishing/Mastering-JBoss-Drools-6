package org.drools.devguide.chapter10;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.command.Context;
import org.kie.internal.io.ResourceFactory;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class OMSTest {
    private KieBase kbase;
    private Environment environment;
    private EntityManagerFactory emf;

    private PoolingDataSource ds = new PoolingDataSource();

    @Before
    public void setUp() {
        ds.setUniqueName("jdbc/jbpm-ds");
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.setMaxPoolSize(3);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "sa");
        ds.getDriverProperties().put("password", "sasa");
        ds.getDriverProperties().put("URL", "jdbc:h2:workflow;MVCC=TRUE;DB_CLOSE_ON_EXIT=FALSE");
        ds.init();
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
		this.emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		this.environment = EnvironmentFactory.newEnvironment();
		this.environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
		this.environment.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
		this.environment.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );
		
		//this initializes the object marshaling strategies
		this.environment.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[] {
				new JPAPlaceholderResolverStrategy(emf),
				new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
		});
    }

	private KieSession newSession() {
		return KieServices.Factory.get().getStoreServices().
				newKieSession(kbase, null, this.environment);
	}

	@Test
	public void testPersistentProcess() {
		//first session will use JPA strategy
		this.environment.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[] {
				new JPAPlaceholderResolverStrategy(emf),
				new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
		});
		KieSession ksession1 = newSession();
		long sessionId1 = ksession1.getIdentifier();
		
		//second session will use serialize strategy only
		
		this.environment.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[] {
				new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
		});
		KieSession ksession2 = newSession();
		long sessionId2 = ksession2.getIdentifier();
		
    	//Insert the same objects in both sessions
		MyDbEntity entity1 = new MyDbEntity();
		entity1.setData1("data1");
		entity1.setData2("data2");
		ksession1.insert(entity1);
		ksession1.insert("Other type of object");
		ksession2.insert(entity1);
		ksession2.insert("Other type of object");
		
		//Obtain the two SessionInfo objects
		EntityManager em = emf.createEntityManager();
		SessionInfo info1 = em.find(SessionInfo.class, sessionId1);
		SessionInfo info2 = em.find(SessionInfo.class, sessionId2);
		int info1Size = info1.getData().length;
		int info2Size = info2.getData().length;
		assertThat(info2Size, not(equalTo(info1Size)));
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
		this.emf = null;
		//close datasource
        ds.close();
	}
}

