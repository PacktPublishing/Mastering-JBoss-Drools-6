/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter09.model;

import org.kie.api.definition.type.Position;

/**
 *
 * @author esteban
 * @param <T>
 */
public class IsPartOf<T> {
    
    @Position(0)
    private final T whole;
    @Position(1)
    private final T part;
    
    public IsPartOf(T whole, T part){
        this.whole = whole;
        this.part = part;
    }
    
    public T getWhole(){
        return this.whole;
    }
    
    public T getPart(){
        return this.part;
    }
}
