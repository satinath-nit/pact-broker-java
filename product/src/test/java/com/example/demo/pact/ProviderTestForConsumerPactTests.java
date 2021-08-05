package com.example.demo.pact;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.controller.ProductController;
import com.example.demo.entity.Product;

import au.com.dius.pact.provider.junit.Consumer;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;

@RunWith(SpringRestPactRunner.class)
@SpringBootTest
@Provider("product")
@Consumer("customer")
@PactBroker(host = "ec2-18-191-124-226.us-east-2.compute.amazonaws.com",port = "80")
//@PactFolder("P:\\SatinathWorkspace\\Code\\customer\\target\\pacts")
public class ProviderTestForConsumerPactTests {
	
	@TestTarget
	public Target target = new HttpTarget(9080);
	
	
	@State("Get product details")
	public void testConsumerPact() {
		System.setProperty("pact.verifier.publishResults", "true");
		Product product = new Product(100, "Laptop", "DELL Laptop", 1000.0);
		ProductController mockController = Mockito.mock(ProductController.class);
		Mockito.when(mockController.getProduct(100)).thenReturn(product);
	}


}
