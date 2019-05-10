/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter07;

import org.drools.devguide.eshop.model.Customer;

/**
 *
 * @author esteban
 */
public class ClassificationTemplateModel {
    
    private int minAge;
    private int maxAge;
    private Customer.Category previousCategory;
    private Customer.Category newCategory;

    public ClassificationTemplateModel(int minAge, int maxAge, Customer.Category previousCategory, Customer.Category newCategory) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.previousCategory = previousCategory;
        this.newCategory = newCategory;
    }
    

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public Customer.Category getPreviousCategory() {
        return previousCategory;
    }

    public void setPreviousCategory(Customer.Category previousCategory) {
        this.previousCategory = previousCategory;
    }

    public Customer.Category getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(Customer.Category newCategory) {
        this.newCategory = newCategory;
    }
    
}
