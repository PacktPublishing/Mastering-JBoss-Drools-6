/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.util;

/**
 * Utility class that can be used to set reference to objects from different
 * threads/scopes. A typical use of this class is as a global in a ksession.
 * Rules can set the value of this type of globals and they can then be accessed
 * by the scope where the ksession is running.
 * @author esteban
 */
public class ObjectHolder<T> {
    
    private T object;

    public ObjectHolder() {
    }

    public ObjectHolder(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
    
}
