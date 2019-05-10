/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05.listener;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

/**
 *
 * @author esteban
 */
public class TestAgendaEventListener implements AgendaEventListener{

    private int numberOfMatches;
    private int numberOfFiredMatches;
    
    @Override
    public void matchCreated(MatchCreatedEvent event) {
        numberOfMatches++;
    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        numberOfMatches--;
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        numberOfFiredMatches++;
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    public int getNumberOfMatches() {
        return numberOfMatches;
    }

    public int getNumberOfFiredMatches() {
        return numberOfFiredMatches;
    }
    
}
