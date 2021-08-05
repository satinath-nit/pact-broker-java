package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.Product;

@Service
public class CustomerService {
	//http://localhost:9080/product/100
	
	 public Product getProductDetails(int prodId) {
	        return new RestTemplate().getForObject("http://localhost:9080/product/"+prodId, Product.class);
	    }

}
