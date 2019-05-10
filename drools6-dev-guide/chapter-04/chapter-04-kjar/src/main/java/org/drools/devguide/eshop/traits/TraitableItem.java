package org.drools.devguide.eshop.traits;

import java.io.Serializable;
import java.util.Objects;

import org.drools.core.factmodel.traits.Traitable;

@Traitable
public class TraitableItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private Double cost;
    private Double salePrice;
    
    public TraitableItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.id);
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.cost);
        hash = 59 * hash + Objects.hashCode(this.salePrice);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraitableItem other = (TraitableItem) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.cost, other.cost)) {
            return false;
        }
        if (!Objects.equals(this.salePrice, other.salePrice)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TraitableItem{" + "id=" + id + ", name=" + name + ", cost=" + cost
                + ", salePrice=" + salePrice + '}';
    }
}
