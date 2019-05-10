/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.eshop.model;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class Manager implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;

    public Manager(String name) {
        this.name = name;
    }

    public Manager() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
    
}
