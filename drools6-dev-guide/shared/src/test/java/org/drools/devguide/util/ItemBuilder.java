/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.util;

import org.drools.devguide.eshop.model.Item;

/**
 *
 * @author esteban
 */
public class ItemBuilder {
    private final OrderLineBuilder superBuilder;
    private final Item instance;

    public ItemBuilder(OrderLineBuilder superBuilder) {
        this.superBuilder = superBuilder;
        
        this.instance = new Item();
        //default values
        this.instance.setId(1L);
        this.instance.setCost(0.0);
        this.instance.setName("");
        this.instance.setSalePrice(0.0);
        this.instance.setCategory(Item.Category.NA);
    }
    
    public ItemBuilder withId(long id){
        this.instance.setId(id);
        return this;
    }
    
    public ItemBuilder withCost(double cost){
        this.instance.setCost(cost);
        return this;
    }
    
    public ItemBuilder withName(String name){
        this.instance.setName(name);
        return this;
    }
    
    public ItemBuilder withSalePrice(double salePrice){
        this.instance.setSalePrice(salePrice);
        return this;
    }
    
    public ItemBuilder withCategory(Item.Category category){
        this.instance.setCategory(category);
        return this;
    }
    
    public Item build(){
        return this.instance;
    }
    
    public OrderLineBuilder end(){
        return superBuilder;
    } 

}
