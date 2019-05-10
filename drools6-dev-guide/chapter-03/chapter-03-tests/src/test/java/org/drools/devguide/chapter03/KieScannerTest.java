/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter03;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import static java.util.stream.Collectors.joining;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.After;
import org.junit.Before;
import org.drools.core.util.FileManager;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.eshop.model.Order;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.MavenRepository;

/**
 * Tests related to KieScanner component.
 * These tests are different from the tests in this book because they require
 * KieJars to be programmatically generated and deployed into Maven. These
 * tests don't depend on any other KieModule.
 * @author esteban
 */
public class KieScannerTest {

    /**
     * FileManager is a utility class from drools-core (tests) that allow us 
     * to easily create temporary files.
     */
    private FileManager fileManager;
    
    /**
     * This class represents a Maven Repository. This class can be used to 
     * manually deploy artifacts into a repository.
     */
    private MavenRepository repository;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();

        repository = MavenRepository.getMavenRepository();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    /**
     * This test shows how a KieContainer can be updated using a KieScanner.
     * The KieScanner used in this test is never started. Instead of that,
     * the KieScanner is programmatically executed whenever we want it to
     * check for changes in the underlying KieContainer.
     * @throws IOException 
     */
    @Test
    public void kieContainerUpdateKieJarTest() throws IOException {

        //Create 2 SILVER customers and one Order for each of them.
        Customer customerA = new Customer();
        customerA.setCategory(Customer.Category.SILVER);
        Order orderA = new Order();
        orderA.setCustomer(customerA);

        Customer customerB = new Customer();
        customerB.setCategory(Customer.Category.SILVER);
        Order orderB = new Order();
        orderB.setCustomer(customerB);

        //Programmatically create a KieJar with org.drools.devguide:chapter-03-scanner-1:0.9
        //GAV.
        String groupId = "org.drools.devguide";
        String artifactId = "chapter-03-scanner-1";
        String version = "0.9";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        //The KieJar will contains a single rule that will give a 10% discount
        //to Orders from SILVER Customers.
        InternalKieModule originalKJar = createKieJar(ks, releaseId, createDiscountRuleForSilverCustomers(10.0));
        
        //Before we can create a kieContainer for the KieJar we just created, 
        //we need to deploy the KieJar into maven's repository.
        repository.deployArtifact(releaseId, originalKJar, createKPom(fileManager, releaseId));

        //Once the KieJar is deployed in maven, we can create a KieContainer for it,
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        
        //The KieContainer is wrapped by a KieScanner.
        //Note that we are neve starting the KieScanner because we want to control
        //when the upgrade process kicks in.
        KieScanner scanner = ks.newKieScanner(kieContainer);
        
        //Create a KieSession from the KieContainer
        KieSession ksession = kieContainer.newKieSession();

        //Calculate the dicount for OrderA using the provided KieSession. 
        //This method will assert that the calculated discount is 10%.
        this.calculateAndAssertDiscount(customerA, orderA, ksession, 10.0);
        
        
        //A new KieJar for the same GAV is created and deployed into maven.
        //This new version of the KieJar will give a 25% discount to SILVER
        //Customers' Orders.
        InternalKieModule newKJar = createKieJar(ks, releaseId, createDiscountRuleForSilverCustomers(25.0));
        repository.deployArtifact(releaseId, newKJar, createKPom(fileManager, releaseId));
        
        //Programmatically tells the scanner to look for modifications in the 
        //KieJar referenced by the KieContainer it is wrapping.
        //At this point, the KieSession we had previously created will be updated.
        scanner.scanNow();
        
        //Using THE SAME KieSession we used before, we now calculate the discount
        //for OrderB. This method is asserting that the calculated discount is
        //now 25% instead of 10%. This proves that the KieBase was modified
        //according to the latest version of org.drools.devguide:chapter-03-scanner-1:0.9
        this.calculateAndAssertDiscount(customerB, orderB, ksession, 25.0);

    }
    
    /**
     * Calculates the discount for a Order and asserts its value.
     * This method inserts both, the Customer and the Order into the provided
     * KieSession and executes any activated rule.
     * After the rules in the KieSession are executed, this method asserts that
     * the discount of the Order matches an expected value.
     * @param customer
     * @param order
     * @param ksession
     * @param expectedDiscount 
     */
    private void calculateAndAssertDiscount(Customer customer, Order order, KieSession ksession, double expectedDiscount) {
        ksession.insert(customer);
        ksession.insert(order);
        ksession.fireAllRules();

        assertThat(order.getDiscount(), is(notNullValue()));
        assertThat(order.getDiscount().getPercentage(), is(expectedDiscount));
    }

    /**
     * Programmatically creates a rule (String) to set the Discount for SILVER
     * Customers Orders. The value of the Discount can be parameterized using 
     * the 'discount' parameter.
     * @param discount
     * @return 
     */
    private String createDiscountRuleForSilverCustomers(double discount) {
        StringBuilder ruleBuilder = new StringBuilder();
        ruleBuilder.append("rule 'Silver Customers - Discount'\n");
        ruleBuilder.append("when\n");
        ruleBuilder.append("    $o: Order( $customer: customer, discount == null)\n");
        ruleBuilder.append("    $c: Customer( category == Category.SILVER, this == $customer )\n");
        ruleBuilder.append("then\n");
        ruleBuilder.append("    System.out.println(\"Executing Silver Customer ").append(discount).append("% Discount Rule!\");\n");
        ruleBuilder.append("    $o.setDiscount(new Discount(").append(discount).append("));\n");
        ruleBuilder.append("    update($o);\n");
        ruleBuilder.append("end\n");

        return ruleBuilder.toString();
    }

    /**
     * Creates a KieJar with a specific release id and a set of rules.
     * @param ks
     * @param releaseId
     * @param rules
     * @return
     * @throws IOException 
     */
    private InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, String... rules) throws IOException {
        //Create a base structure for a KIE Module jar with the KieFileSystem
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kproj = ks.newKieModuleModel();
        //We add a kmodule.xml
        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML(getPom(releaseId));

        //Create the DRL.
        StringBuilder packageBuilder = new StringBuilder();
        packageBuilder.append("package rules\n");
        packageBuilder.append("import org.drools.devguide.eshop.model.Customer;\n");
        packageBuilder.append("import org.drools.devguide.eshop.model.Order;\n");
        packageBuilder.append("import org.drools.devguide.eshop.model.Discount;\n");
        packageBuilder.append(Arrays.asList(rules).stream().collect(joining()));

        //Write the DRL.
        String file = "org/test/simple-discount-rules.drl";
        kfs.write("src/main/resources/KBase1/" + file,
                packageBuilder.toString());

        //Build the KieJar (compiling the DRL) and return the generated KieModule.
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertThat(kieBuilder.buildAll().getResults().getMessages(), is(empty()));
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    /**
     * Creates a pom.xml file for a specific release id.
     * @param fileManager
     * @param releaseId
     * @return
     * @throws IOException 
     */
    private File createKPom(FileManager fileManager, ReleaseId releaseId) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPom(releaseId));
        return pomFile;
    }

    /**
     * Generates the content of a pom.xml file for a specific release id.
     * The dependencies of the pom.xml file being created can also be specified
     * through 'dependencies' argument.
     * @param releaseId
     * @param dependencies
     * @return 
     */
    private String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom
                = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
                + " <modelVersion>4.0.0</modelVersion>\n"
                + "\n"
                + " <groupId>" + releaseId.getGroupId() + "</groupId>\n"
                + " <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n"
                + " <version>" + releaseId.getVersion() + "</version>\n"
                + "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += " <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += " <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += " <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }
}
