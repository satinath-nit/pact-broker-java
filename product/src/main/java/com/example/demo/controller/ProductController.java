package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Product;

@RestController
@RequestMapping(path = "/product")
public class ProductController {
	
	@GetMapping(path = "/{prodId}")
	public Product getProduct(@PathVariable(name = "prodId") int prodId) {
		
		return new Product(100, "Laptop", "DELL Laptop", 1000.0d);
	}

}
