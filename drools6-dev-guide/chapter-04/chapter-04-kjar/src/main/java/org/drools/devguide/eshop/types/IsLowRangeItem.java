package org.drools.devguide.eshop.types;

import org.drools.devguide.eshop.model.Item;

public class IsLowRangeItem {

    private Item item;
    
    public IsLowRangeItem() {
    }

    public IsLowRangeItem(Item item) {
        super();
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IsLowRangeItem other = (IsLowRangeItem) obj;
        if (item == null) {
            if (other.item != null)
                return false;
        } else if (!item.equals(other.item))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IsLowRangeItem [item=" + item + "]";
    }
}
