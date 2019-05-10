/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter07;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.devguide.BaseTest;
import org.drools.devguide.eshop.model.Customer;
import org.drools.devguide.util.CustomerBuilder;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.ObjectDataCompiler;
import org.drools.template.jdbc.ResultSetGenerator;
import org.drools.template.objects.ArrayDataProvider;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

/**
 *
 * @author esteban
 */
public class RuleTemplatesTest extends BaseTest{
    
    /**
     * Tests customer-classification-simple.drt template using the configuration 
     * present in kmodule.xml.
     */
    @Test
    public void testSimpleTemplateWithSpreadsheet1(){
        
        KieSession ksession = this.createSession("templateSimpleKsession");
        
        this.doTest(ksession);
    }
    
    /**
     * Tests customer-classification-simple.drt template by manually creating
     * the corresponding DRL using a spreadsheet as the data source.
     */
    @Test
    public void testSimpleTemplateWithSpreadsheet2(){
        
        InputStream template = RuleTemplatesTest.class.getResourceAsStream("/chapter07/template-dtable/customer-classification-simple.drt");
        InputStream data = RuleTemplatesTest.class.getResourceAsStream("/chapter07/template-dtable/template-data.xls");
        
        ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        String drl = converter.compile(data, template, 3, 2);
        
        System.out.println(drl);
        
        KieSession ksession = this.createKieSessionFromDRL(drl);
        
        this.doTest(ksession);
    }
    
    /**
     * Tests customer-classification-simple.drt template by manually creating
     * the corresponding DRL using a bidimensional array of Strings 
     * as the data source.
     */
    @Test
    public void testSimpleTemplateWithArrays(){
        
        InputStream template = RuleTemplatesTest.class.getResourceAsStream("/chapter07/template-dtable/customer-classification-simple.drt");
        
        DataProvider dataProvider = new ArrayDataProvider(new String[][]{
            new String[]{"18", "21", "NA", "NA"},
            new String[]{"22", "30", "NA", "BRONZE"},
            new String[]{"31", "40", "NA", "SILVER"},
            new String[]{"41", "150", "NA", "GOLD"},
        });
        
        DataProviderCompiler converter = new DataProviderCompiler();
        String drl = converter.compile(dataProvider, template);
        
        System.out.println(drl);
        
        KieSession ksession = this.createKieSessionFromDRL(drl);
        
        this.doTest(ksession);
    }
    
    /**
     * Tests customer-classification-simple.drt template by manually creating
     * the corresponding DRL using a collection of Objects as the data source.
     */
    @Test
    public void testSimpleTemplateWithObjects(){
        
        InputStream template = RuleTemplatesTest.class.getResourceAsStream("/chapter07/template-dtable/customer-classification-simple.drt");
        
        List<ClassificationTemplateModel> data = new ArrayList<>();
        
        data.add(new ClassificationTemplateModel(18, 21, Customer.Category.NA, Customer.Category.NA));
        data.add(new ClassificationTemplateModel(22, 30, Customer.Category.NA, Customer.Category.BRONZE));
        data.add(new ClassificationTemplateModel(31, 40, Customer.Category.NA, Customer.Category.SILVER));
        data.add(new ClassificationTemplateModel(41, 150, Customer.Category.NA, Customer.Category.GOLD));
        
        ObjectDataCompiler converter = new ObjectDataCompiler();
        String drl = converter.compile(data, template);
        
        System.out.println(drl);
        
        KieSession ksession = this.createKieSessionFromDRL(drl);
        
        this.doTest(ksession);
    }
    
    /**
     * Tests customer-classification-simple.drt template by manually creating
     * the corresponding DRL using an embedded database as the data source.
     */
    @Test
    public void testSimpleTemplateWithDatabase() throws Exception{
        
        InputStream template = RuleTemplatesTest.class.getResourceAsStream("/chapter07/template-dtable/customer-classification-simple.drt");
        
        // setup the HSQL database with our rules.
        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:drools-templates", "sa", "");

        try {
            executeInDB("CREATE TABLE ClassificationRules ( id INTEGER IDENTITY, minAge INTEGER, maxAge INTEGER, previousCategory VARCHAR(256), newCategory VARCHAR(256) )", conn);

            executeInDB("INSERT INTO ClassificationRules VALUES (1, 18, 21, 'NA', 'NA')", conn);
            executeInDB("INSERT INTO ClassificationRules VALUES (2, 22, 30, 'NA', 'BRONZE')", conn);
            executeInDB("INSERT INTO ClassificationRules VALUES (3, 31, 40, 'NA', 'SILVER')", conn);
            executeInDB("INSERT INTO ClassificationRules VALUES (4, 41, 150, 'NA', 'GOLD')", conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize in memory database", e);
        }

        Statement sta = conn.createStatement();
        ResultSet rs = sta.executeQuery("SELECT minAge, maxAge, previousCategory, newCategory " +
                                        " FROM ClassificationRules");

        final ResultSetGenerator converter = new ResultSetGenerator();
        final String drl = converter.compile(rs, template);
        
        System.out.println(drl);
        
        KieSession ksession = this.createKieSessionFromDRL(drl);
        
        this.doTest(ksession);
    }
    
    
    private void doTest(KieSession ksession){
        Customer customer1 = new CustomerBuilder()
                .withId(1L)
                .withAge(19)
                .build();
        
        Customer customer2 = new CustomerBuilder()
                .withId(2L)
                .withAge(27)
                .build();
        
        Customer customer3 = new CustomerBuilder()
                .withId(3L)
                .withAge(32)
                .build();
        
        Customer customer4 = new CustomerBuilder()
                .withId(4L)
                .withAge(60)
                .build();
        
        ksession.insert(customer1);
        ksession.insert(customer2);
        ksession.insert(customer3);
        ksession.insert(customer4);
        
        ksession.fireAllRules();
        
        assertThat(customer1.getCategory(), is(Customer.Category.NA));
        assertThat(customer2.getCategory(), is(Customer.Category.BRONZE));
        assertThat(customer3.getCategory(), is(Customer.Category.SILVER));
        assertThat(customer4.getCategory(), is(Customer.Category.GOLD));
    }
    
    /**
     * Executes an update statement into a database.
     * @param expression the SQL expression to be executed.
     * @param conn the connection the the database where the statement will be
     * executed.
     * @throws SQLException 
     */
    private void executeInDB(String expression, Connection conn) throws SQLException {
        Statement st;
        st = conn.createStatement();
        int i = st.executeUpdate(expression);
        if (i == -1) {
            System.out.println("db error : " + expression);
        }

        st.close();
    }

    
    private KieSession createKieSessionFromDRL(String drl){
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);
        
        Results results = kieHelper.verify();
        
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error: "+message.getText());
            }
            
            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }
        
        return kieHelper.build().newKieSession();
    }
}
