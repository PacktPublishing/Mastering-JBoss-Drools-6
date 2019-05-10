/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter09;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.kie.api.KieBase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 *
 * @author esteban
 */
public class PhreakInspectorTest extends PhreakInspectorBaseTest {
    
    @Test
    public void doOTNTest() throws IOException {
        this.doTest("OTNKBase", "B01512_09_02.viz");
    }
    
    @Test
    public void doAlphaSimpleTest() throws IOException {
        this.doTest("AlphaSimpleKBase", "B01512_09_03.viz");
    }
    
    @Test
    public void doAlphaORTest() throws IOException {
        this.doTest("AlphaORKBase", "alpha-or-phreak.viz");
    }
    
    @Test
    public void doAlphaNodeSharing1Test() throws IOException {
        this.doTest("AlphaNodeSharing1KBase", "B01512_09_04.viz");
    }
    
    @Test
    public void doAlphaNodeSharing2Test() throws IOException {
        this.doTest("AlphaNodeSharing2KBase", "B01512_09_05.viz");
    }
    
    @Test
    public void doBetaSimpleTest() throws IOException {
        this.doTest("BetaSimpleKBase", "B01512_09_06.viz");
    }
    
    @Test
    public void doBetaConstraintTest() throws IOException {
        this.doTest("BetaConstraintKBase", "B01512_09_07.viz");
    }
    
    @Test
    public void doBetaNodeSharing1Test() throws IOException {
        this.doTest("BetaNodeSharing1KBase", "B01512_09_08.viz");
    }
    
    @Test
    public void doBetaNodeSharing2Test() throws IOException {
        this.doTest("BetaNodeSharing2KBase", "B01512_09_09.viz");
    }
    
    @Test
    public void doBetaNodeSharing3Test() throws IOException {
        this.doTest("BetaNodeSharing3KBase", "B01512_09_10.viz");
    }
    
    @Test
    public void doBetaORTest() throws IOException {
        this.doTest("BetaORKBase", "B01512_09_11.viz");
    }
    
    @Test
    public void doBetaORSplitTest() throws IOException {
        this.doTest("BetaORSplitKBase", "beta-or-split-phreak.viz");
    }
    
    @Test
    public void doNotNode1Test() throws IOException {
        this.doTest("NotNode1KBase", "B01512_09_12.viz");
    }
    
    @Test
    public void doNotNode2Test() throws IOException {
        this.doTest("NotNode2KBase", "B01512_09_13.viz");
    }
    
    @Test
    public void doNotNode3Test() throws IOException {
        this.doTest("NotNode3KBase", "not-node3-phreak.viz");
    }
    
    @Test
    public void doExistsNode1Test() throws IOException {
        this.doTest("ExistsNode1KBase", "B01512_09_14.viz");
    }
    
    @Test
    public void doExistsNode2Test() throws IOException {
        this.doTest("ExistsNode2KBase", "B01512_09_15.viz");
    }
    
    @Test
    public void doAccumulateNode1Test() throws IOException {
        this.inspector.addNodeLabel(6, "[Order] count(1)");
        this.doTest("AccumulateNode1KBase", "B01512_09_16.viz");
    }
    
    @Test
    public void doAccumulateNode2Test() throws IOException {
        this.inspector.addNodeLabel(5, "[Order] count(1)");
        this.doTest("AccumulateNode2KBase", "B01512_09_17.viz");
    }
    
    @Test
    public void doFromNode1Test() throws IOException {
        this.doTest("FromNode1KBase", "B01512_09_18.viz");
    }
    
    private void doTest(String kbaseName, String targetFileName) throws IOException{
        KieBase kbase = this.createKnowledgeBase(kbaseName);

        InputStream phreakGraph = this.inspector.fromKieBase(kbase);
        
        assertThat(phreakGraph, notNullValue());
        
        this.writeFile(targetFileName, phreakGraph);
    }
    
}
