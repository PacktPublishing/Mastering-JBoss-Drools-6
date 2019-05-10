/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.eshop.service;

import org.drools.devguide.eshop.model.SuspiciousOperation;

/**
 *
 * @author esteban
 */
public interface AuditService {
    public void notifySuspiciousOperation(SuspiciousOperation operation);
}
