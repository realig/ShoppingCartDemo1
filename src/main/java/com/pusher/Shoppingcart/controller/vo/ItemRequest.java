package com.pusher.Shoppingcart.controller.vo;

import java.io.Serializable;

public class ItemRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
private Long id;
private Integer quantity;
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public Integer getQuantity() {
	return quantity;
}
public void setQuantity(Integer quantity) {
	this.quantity = quantity;
}

}
