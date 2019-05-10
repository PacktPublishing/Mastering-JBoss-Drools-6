/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter07;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 *
 * @author esteban
 */
public class PMMLTest extends BaseTest{
    
    /**
     * Executes the simple customer classification PMML model
     * (/chapter07/pmml-simple/customer-classification-simple.pmml.xml).
     * Because PMML models can't handle concurrent evaluations, individual 
     * KieSessions are used to evaluate 5 different Customers.
     */
    @Test
    public void testSimpleDecisionTree(){
        
        String[] expectedCategory = new String[]{
            "NO_CHANGE",
            "NA",
            "BRONZE",
            "SILVER",
            "GOLD"
        };

        this.printGeneratedDRL(RuleTemplatesTest.class.getResourceAsStream("/chapter07/pmml-simple/customer-classification-simple.pmml.xml"), System.out);
        
        Customer[] customers = new Customer[]{
            new CustomerBuilder()
                .withId(1L)
                .withAge(19)
                .withCategory(Customer.Category.GOLD)
                .build(),
            new CustomerBuilder()
                .withId(2L)
                .withAge(19)
                .withCategory(Customer.Category.NA)
                .build(),
            new CustomerBuilder()
                .withId(3L)
                .withAge(27)
                .withCategory(Customer.Category.NA)
                .build(),
            new CustomerBuilder()
                .withId(4L)
                .withAge(32)
                .withCategory(Customer.Category.NA)
                .build(),
            new CustomerBuilder()
                .withId(5L)
                .withAge(60)
                .withCategory(Customer.Category.NA)
                .build()
        };
        
        KieBase kbase = this.createKieBaseFromPMML(RuleTemplatesTest.class.getResourceAsStream("/chapter07/pmml-simple/customer-classification-simple.pmml.xml"));
        
        for (int i = 0; i < customers.length; i++) {
            Customer customer = customers[i];

            KieSession ksession = kbase.newKieSession();
            
            //first fireAllRules to execute any configuration rule.
            ksession.fireAllRules();
            
            //Insert each field in the corresponding entry-point
            ksession.getEntryPoint("in_PreviousCategory").insert(customer.getCategory().name());
            ksession.getEntryPoint("in_Age").insert(customer.getAge());
            ksession.fireAllRules();
            
            String newCategory = this.getNewCategory(ksession);
            
            assertThat(newCategory, is(expectedCategory[i]));
            
            ksession.dispose();
        }
        
    }
    
    /**
     * Creates a KieBase from a PMML document. This method also includes the
     * chapter07/pmml-simple/utility.drl asset in the generated KieBase. This
     * DEL resource contains a query definition that can be used to extract the
     * result of the PMML model.
     * @param pmml the PMML document
     * @return a KieBase containing the compiled version of the passed PMML model
     * along with a query to retrieve its result.
     */
    private KieBase createKieBaseFromPMML(InputStream pmml){
        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(ResourceFactory.newInputStreamResource(pmml), ResourceType.PMML);
        
        //include the utility drl
        kieHelper.addResource(ResourceFactory.newClassPathResource("chapter07/pmml-simple/utility.drl"), ResourceType.DRL);
        
        
        Results results = kieHelper.verify();
        
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error: "+message.getText());
            }
            
            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }
        
        return kieHelper.build();
    }
    
    /**
     * Executes the getNewCategory query present in the passed KieSession and
     * returns its result.
     * @param ksession the KieSession where the query is executed.
     * @return the result of the getNewCategory query. 
     */
    private String getNewCategory(KieSession ksession){
        QueryResults queryResults = ksession.getQueryResults("getNewCategory");
        return (String) queryResults.iterator().next().get("$cat");
    }
    
    /**
     * Compiles a PMML resource and prints the result in the
     * passed OutputStream.
     * @param pmml the decision table to be converted.
     * @param target the stream where the generated DRL will be printed. 
     */
    private void printGeneratedDRL(InputStream pmml, OutputStream target){
        try{
            PMML4Compiler compiler = new PMML4Compiler();
            String drl = compiler.compile(pmml, PMMLTest.class.getClassLoader());

            IOUtils.copy(new ByteArrayInputStream(drl.getBytes()), target);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
}
