/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.util;

import java.util.Optional;
import org.drools.devguide.eshop.model.OrderLine;

/**
 *
 * @author esteban
 */
public class OrderLineBuilder {
    
    private final OrderBuilder superBuilder;
    private final OrderLine instance;
    
    private Optional<ItemBuilder> itemBuilder = Optional.empty();
    
    public OrderLineBuilder(OrderBuilder superBuilder) {
        this.superBuilder = superBuilder;
        
        this.instance = new OrderLine();
        this.instance.setQuantity(0);
    }
    
    public OrderLineBuilder withQuantity(int quantity){
        this.instance.setQuantity(quantity);
        return this;
    }
    
    public ItemBuilder withItem(){
        this.itemBuilder = Optional.of(new ItemBuilder(this));
        return this.itemBuilder.get();
    }
    
    public OrderLine build(){
        if (this.itemBuilder.isPresent()){
            this.instance.setItem(this.itemBuilder.get().build());
        }
        return this.instance;
    }
    
    public OrderBuilder end(){
        return superBuilder;
    }
    
}
