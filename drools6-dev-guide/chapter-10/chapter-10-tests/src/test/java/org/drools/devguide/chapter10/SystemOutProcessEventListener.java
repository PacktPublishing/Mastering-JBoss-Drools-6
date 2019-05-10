/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter10;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

/**
 *
 * @author salaboy
 */
public class SystemOutProcessEventListener implements org.kie.api.event.process.ProcessEventListener {

        @Override
        public void beforeProcessStarted(ProcessStartedEvent pse) {
            System.out.println(">>> BeforeProcessStarted: " + pse.getProcessInstance().getProcessId());
        }

        @Override
        public void afterProcessStarted(ProcessStartedEvent pse) {
            System.out.println(">>> AfterProcessStarted: " + pse.getProcessInstance().getProcessId());
        }

        @Override
        public void beforeProcessCompleted(ProcessCompletedEvent pce) {
            System.out.println(">>> BeforeProcessCompleted: " + pce.getProcessInstance().getProcessId());
        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent pce) {
            System.out.println(">>> AfterProcessCompleted: " + pce.getProcessInstance().getProcessId());
        }

        @Override
        public void beforeNodeTriggered(ProcessNodeTriggeredEvent pnte) {
            System.out.println(">>> BeforeNodeTriggered: " + pnte.getNodeInstance().getNode().getName());
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent pnte) {
            System.out.println(">>> AfterNodeTriggered: " + pnte.getNodeInstance().getNode().getName());
        }

        @Override
        public void beforeNodeLeft(ProcessNodeLeftEvent pnle) {
            System.out.println(">>> BeforeNodeLeft: " + pnle.getNodeInstance().getNode().getName());
        }

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent pnle) {
            System.out.println(">>> AfterNodeLeft: " + pnle.getNodeInstance().getNode().getName());
        }

        @Override
        public void beforeVariableChanged(ProcessVariableChangedEvent pvce) {
            System.out.println(">>> BeforeVariableChanged: " + pvce.getVariableId() + " new Value: " + pvce.getNewValue() + " - old Value: " + pvce.getOldValue());
        }

        @Override
        public void afterVariableChanged(ProcessVariableChangedEvent pvce) {
            System.out.println(">>> AfterVariableChanged: " + pvce.getVariableId() + " new Value: " + pvce.getNewValue() + " - old Value: " + pvce.getOldValue());
        }
}
