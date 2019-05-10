package org.drools.devguide.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.devguide.eshop.model.Item;

@XmlRootElement(name = "shop-item")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbItem extends Item {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private Double cost;
	@XmlAttribute
	private String categoryStr;
	
	public JaxbItem() {
	}
	
	public JaxbItem(Double cost, Item.Category category) {
		setCategoryStr(category.toString());
		setCost(cost);
	}
	
	@Override
	public Category getCategory() {
		return Category.valueOf(categoryStr);
	}
	
	@Override
	public Double getCost() {
		return cost;
	}
	
	public String getCategoryStr() {
		return categoryStr;
	}
	
	public void setCategoryStr(String categoryStr) {
		this.categoryStr = categoryStr;
		super.setCategory(Category.valueOf(categoryStr));
	}
	
	public void setCategory(Category category) {
		this.categoryStr = category.toString();
		super.setCategory(category);
	}
	
	@Override
	public void setCost(Double cost) {
		this.cost = cost;
		super.setCost(cost);
	}
}
