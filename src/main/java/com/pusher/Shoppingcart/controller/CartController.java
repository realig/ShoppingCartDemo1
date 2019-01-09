package com.pusher.Shoppingcart.controller;

import com.pusher.Shoppingcart.model.Product;
import com.pusher.Shoppingcart.constants.*;
import com.pusher.Shoppingcart.controller.vo.ItemRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.pusher.client.Pusher;


@RestController
@SessionAttributes(GeneralConstants.ID_SESSION_SHOPPING_CART)
public class CartController {
	
	private List<Product> products = new ArrayList<Product>();

	  private Pusher pusher;

	  @PostConstruct
	  public void configure() {
	    pusher=new Pusher(PusherConstants.PUSHER_APP_ID);
	    pusher=new Pusher(PusherConstants.PUSHER_APP_KEY);
	    pusher=new Pusher(PusherConstants.PUSHER_APP_SECRET);
	    
	    //or
	    /*pusher=new pusher(
	   PusherCostants.PUSHER_APP_ID,PusherConstants.PUSHER_APP_KEY,PusherConstants.PUSHER_APP_SECRET); 
*/
	    Product product = new Product();
	    product.setId(1L);
	    product.setName("Office Chair");
	    product.setPrice(new BigDecimal("55.99"));
	    products.add(product);

	    product = new Product();
	    product.setId(2L);
	    product.setName("Sunglasses");
	    product.setPrice(new BigDecimal("99.99"));
	    products.add(product);

	    product = new Product();
	    product.setId(3L);
	    product.setName("Wireless Headphones");
	    product.setPrice(new BigDecimal("349.01"));
	    products.add(product);

	    product = new Product();
	    product.setId(4L);
	    product.setName("External Hard Drive");
	    product.setPrice(new BigDecimal("89.99"));
	    products.add(product);
	  }

	 @RequestMapping(value="/products",method=RequestMethod.GET, produces="application/json")
	 public List<Product>getProducts(){
		 return products;
	 }
	 @RequestMapping(value = "/cart/items", 
			    method = RequestMethod.GET,  
			    produces = "application/json")
			  public List<Product> getCartItems(@SessionAttribute(GeneralConstants.ID_SESSION_SHOPPING_CART) List<Product> shoppingCart) {
			    return shoppingCart;
	 }
	 private Optional<Product> getProductById(Stream<Product> stream, Long id) {
		  return stream
		    .filter(product -> product.getId().equals(id))
		    .findFirst();
	}
	 @RequestMapping(value = "/cart/item", 
	            method = RequestMethod.POST, 
	            consumes = "application/json")
	  public String addItem(@RequestBody ItemRequest request, @SessionAttribute(GeneralConstants.ID_SESSION_SHOPPING_CART) List<Product> shoppingCart) {
	    Product newProduct = new Product();
	    Optional<Product> optional = getProductById(products.stream(), request.getId());

	    if (optional.isPresent()) {
	      Product product = optional.get();

	      newProduct.setId(product.getId());
	      newProduct.setName(product.getName());
	      newProduct.setPrice(product.getPrice());
	      newProduct.setQuantity(request.getQuantity());

	      Optional<Product> productInCart = getProductById(shoppingCart.stream(), product.getId());
	      String event;

	      if(productInCart.isPresent()) {
	        productInCart.get().setQuantity(request.getQuantity());
	        event = "itemUpdated";
	      } else {
	        shoppingCart.add(newProduct);
	        event = "itemAdded";
	      }

	      pusher.trigger(PusherConstants.CHANNEL_NAME, event, newProduct);
	    //  pusher.
	    }

	    return "OK";
	  }
	 @RequestMapping(value = "/cart/item", 
	            method = RequestMethod.DELETE, 
	            consumes = "application/json")
	  public String deleteItem(@RequestBody ItemRequest request, @SessionAttribute(GeneralConstants.ID_SESSION_SHOPPING_CART) List<Product> shoppingCart) {
	    Optional<Product> optional = getProductById(products.stream(), request.getId());

	    if (optional.isPresent()) {
	      Product product = optional.get();

	      Optional<Product> productInCart = getProductById(shoppingCart.stream(), product.getId());

	      if(productInCart.isPresent()) {
	        shoppingCart.remove(productInCart.get());
	        pusher.trigger(PusherConstants.CHANNEL_NAME, "itemRemoved", product);
	      }
	    }

	    return "OK";
	  }
	 @RequestMapping(value = "/cart", 
	            method = RequestMethod.DELETE)
	  public String emptyCart(Model model) {
	    model.addAttribute(GeneralConstants.ID_SESSION_SHOPPING_CART, new ArrayList<Product>());
	    pusher.trigger(PusherConstants.CHANNEL_NAME, "cartEmptied", "");

	    return "OK";
	  }
	 
	}

	 
