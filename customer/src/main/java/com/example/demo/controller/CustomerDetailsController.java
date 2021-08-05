package com.example.demo.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Product;
import com.example.demo.service.CustomerService;

@RestController
@RequestMapping(path = "/customer")
public class CustomerDetailsController {
	
	@Autowired
	CustomerService customerService; 
	
	@GetMapping(path = "/{customerId}")
	public Customer getCustomerDetails(@PathVariable(name = "customerId") String customerId) {
		//Get products for the customer 
		Product productDetails = customerService.getProductDetails(100);
		return new Customer("C100", "Satinath Mondal", "Chicago", "2244001814","sam@gmail.com",Arrays.asList(productDetails));
	}


}
