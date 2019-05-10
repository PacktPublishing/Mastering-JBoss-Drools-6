/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter05.evaluator;

import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction;
import org.drools.core.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.devguide.eshop.model.Order;

/**
 *
 * @author esteban
 */
public class ContainsItemEvaluator extends BaseEvaluator {
    
    private final boolean isNegated;

    public ContainsItemEvaluator(ValueType type, boolean isNegated) {
        super(type , 
                isNegated? 
                        ContainsItemEvaluatorDefinition.NOT_CONTAINS_ITEM : 
                        ContainsItemEvaluatorDefinition.CONTAINS_ITEM);
        this.isNegated = isNegated;
    }
    
    @Override
    public boolean evaluate(InternalWorkingMemory workingMemory, 
            InternalReadAccessor extractor, InternalFactHandle factHandle, 
            FieldValue value) {
        Object order = extractor.getValue(workingMemory, factHandle.getObject());
        
        return this.isNegated ^ this.evaluateUnsafe(order, value.getValue());
    }

    @Override
    public boolean evaluate(InternalWorkingMemory workingMemory, 
            InternalReadAccessor leftExtractor, InternalFactHandle left, 
            InternalReadAccessor rightExtractor, InternalFactHandle right) {
        Object order = leftExtractor.getValue(workingMemory, left.getObject());
        Object itemId = rightExtractor.getValue(workingMemory, right.getObject());
        
        return this.isNegated ^ this.evaluateUnsafe(order, itemId);
    }

    @Override
    public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, 
            VariableRestriction.VariableContextEntry context, 
            InternalFactHandle right) {
        Object order = context.getFieldExtractor().getValue(workingMemory, 
                right.getObject());
        Object itemId = ((ObjectVariableContextEntry)context).left;
        
        return this.isNegated ^ this.evaluateUnsafe(order, itemId);
    }

    @Override
    public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, 
            VariableRestriction.VariableContextEntry context, 
            InternalFactHandle left) {
        Object order = ((ObjectVariableContextEntry)context).right;
        Object itemId = context.getFieldExtractor().getValue(workingMemory, 
                left.getObject());
        
        return this.isNegated ^ this.evaluateUnsafe(order, itemId);
    }
    
    private boolean evaluateUnsafe(Object order, Object itemId){
        //if the object is not an Order return false.
        if (!(order instanceof Order)){
            throw new IllegalArgumentException(
                    order.getClass()+" can't be casted to type Order");
        }
        
        //if the value we are comparing aginst is not a Long, return false.
//        if (!(Long.class.isAssignableFrom(itemId.getClass()))){
        Long itemIdAsLong;
        try{
            itemIdAsLong = Long.parseLong(itemId.toString());
        } catch (NumberFormatException e){
            throw new IllegalArgumentException(
                    itemId.getClass()+" can't be converted to Long");
        }
        
        return this.evaluate((Order)order, itemIdAsLong);
    }
    
    private boolean evaluate(Order order, long itemId){
        //no order lines -> no item
        if (order.getOrderLines() == null){
            return false;
        }
        
        return order.getOrderLines().stream()
            .map(ol -> ol.getItem().getId())
            .anyMatch(id -> id.equals(itemId));
    }
    
}
