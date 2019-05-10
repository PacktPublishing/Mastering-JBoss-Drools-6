/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05.listener;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

/**
 *
 * @author esteban
 */
public class TestRuleRuntimeEventListener implements RuleRuntimeEventListener {

    private int numberOfFacts;
    private int numberOfModifiedFacts;
    
    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        numberOfFacts++;
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        numberOfModifiedFacts++;
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        numberOfFacts--;
    }

    public int getNumberOfFacts() {
        return numberOfFacts;
    }

    public int getNumberOfModifiedFacts() {
        return numberOfModifiedFacts;
    }
    
}
