/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter09;

import java.io.IOException;
import java.io.InputStream;
import org.drools.devguide.chapter09.model.IsPartOf;
import org.drools.devguide.eshop.model.Item;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;

/**
 *
 * @author esteban
 */
public class PhreakInspectorQueryTest extends PhreakInspectorBaseTest {

    //Facts
    private Item car = new Item("car", 15000D, 20000D);
    private Item engine = new Item("engine", 5000D, 7000D);
    private Item wheel = new Item("wheel", 50D, 75D);
    private Item battery = new Item("battery", 100D, 150D);
    private Item distributor = new Item("distributor", 200D, 280D);

    @Test
    public void doQuerySimpleTest() throws IOException {
        String phreakNetworkImageName = "B01512_09_20.viz";

        KieBase kbase = this.createKnowledgeBase("QuerySimpleKBase");

        //Graph the PHREAK Network.
        InputStream phreakGraph = this.inspector.fromKieBase(kbase);
        this.writeFile(phreakNetworkImageName, phreakGraph);

        //Create a session and populate it with facts.
        KieSession ksession = kbase.newKieSession();
        this.populateSession(ksession);
        
        //The engine is part of the car
        QueryResults qr = ksession.getQueryResults("isItemContainedIn", engine, car);
        assertThat(qr.size(), is(1));
        
        //The engine is not part of the wheel
        qr = ksession.getQueryResults("isItemContainedIn", engine, wheel);
        assertThat(qr.size(), is(0));
        
        //Because the query is not using unification, the distributor is not
        //part of the car.
        qr = ksession.getQueryResults("isItemContainedIn", distributor, car);
        assertThat(qr.size(), is(0));
        
    }

    @Test
    public void doQueryUnificationTest() throws IOException {
        String phreakNetworkImageName = "query-unification.viz";

        KieBase kbase = this.createKnowledgeBase("QueryUnificationKBase");

        //Graph the PHREAK Network.
        InputStream phreakGraph = this.inspector.fromKieBase(kbase);
        this.writeFile(phreakNetworkImageName, phreakGraph);

        //Create a session and populate it with facts.
        KieSession ksession = kbase.newKieSession();
        this.populateSession(ksession);

        //Unbound parameters
        QueryResults qr = ksession.getQueryResults("isItemContainedIn", Variable.v, Variable.v);
        this.printQueryResults("Unbound parameters", qr);
        
        //'whole' parameter bound
        qr = ksession.getQueryResults("isItemContainedIn", Variable.v, car);
        this.printQueryResults("Car Parts", qr);
        
        //'part' parameter bound
        qr = ksession.getQueryResults("isItemContainedIn", engine, Variable.v);
        this.printQueryResults("Engine is Part of", qr);
        
    }
    
    @Test
    public void doQueryPositionalTest() throws IOException {
        String phreakNetworkImageName = "query-positional.viz";

        KieBase kbase = this.createKnowledgeBase("QueryPositionalKBase");

        //Graph the PHREAK Network.
        InputStream phreakGraph = this.inspector.fromKieBase(kbase);
        this.writeFile(phreakNetworkImageName, phreakGraph);

        //Create a session and populate it with facts.
        KieSession ksession = kbase.newKieSession();
        this.populateSession(ksession);

        //Unbound parameters
        QueryResults qr = ksession.getQueryResults("isItemContainedIn", Variable.v, Variable.v);
        this.printQueryResults("Unbound parameters", qr);
        
        //'whole' parameter bound
        qr = ksession.getQueryResults("isItemContainedIn", Variable.v, car);
        this.printQueryResults("Car Parts", qr);
        
        //'part' parameter bound
        qr = ksession.getQueryResults("isItemContainedIn", engine, Variable.v);
        this.printQueryResults("Engine is Part of", qr);
        
    }
    
    
    @Test
    public void doQueryBackwardChainingTest() throws IOException {
        String phreakNetworkImageName = "query-backward-chaining.viz";

        KieBase kbase = this.createKnowledgeBase("QueryBackwardChainingKBase");

        //Graph the PHREAK Network.
        InputStream phreakGraph = this.inspector.fromKieBase(kbase);
        this.writeFile(phreakNetworkImageName, phreakGraph);

        //Create a session and populate it with facts.
        KieSession ksession = kbase.newKieSession();
        this.populateSession(ksession);
        
        //The engine is part of the car
        QueryResults qr = ksession.getQueryResults("isItemContainedIn", engine, car);
        assertThat(qr.size(), is(1));
        
        //The engine is not part of the wheel
        qr = ksession.getQueryResults("isItemContainedIn", engine, wheel);
        assertThat(qr.size(), is(0));
        
        //The query this time is recursive and understands that a distributor
        //is part of a car.
        qr = ksession.getQueryResults("isItemContainedIn", distributor, car);
        assertThat(qr.size(), is(1));
        
        //We can also use the query to (recursively) ask for all the parts of 
        //a car.
        qr = ksession.getQueryResults("isItemContainedIn", Variable.v, car);
        assertThat(qr.size(), is(4));
        this.printQueryResults("Car Parts", qr);
        
        
        
    }

    private void populateSession(KieSession ksession) {

        //Insert the Items as facts        
        ksession.insert(car);
        ksession.insert(engine);
        ksession.insert(wheel);
        ksession.insert(battery);
        ksession.insert(distributor);

        //Insert the relationship between the items as facts
        ksession.insert(new IsPartOf<>(car, engine));
        ksession.insert(new IsPartOf<>(car, wheel));
        ksession.insert(new IsPartOf<>(engine, battery));
        ksession.insert(new IsPartOf<>(engine, distributor));
    }
    
    private void printQueryResults(String label, QueryResults qr){
        if (qr.size() > 0) {
            System.out.println(label);
            for (QueryResultsRow row : qr) {
                Item part = (Item) row.get("p");
                Item whole = (Item) row.get("w");

                System.out.println("\t" + part.getName() + " is part of " + whole.getName());
            }
        } else {
            System.out.println(label +" -> 0 results");
        }
        System.out.println("");
    }

}
