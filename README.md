
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
# pact-broker-java
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Example for using Pact Broker in Java using JUnit

![image](https://user-images.githubusercontent.com/32492604/128455297-703b335b-2b70-4bf8-bad5-fbd140551336.png)

•	Let’s use Amazon EC2 instance and install pact broker
1.	Login to AWS management console and go to EC2 under Compute services.
2.	Create a free tier instance(Amazon Linux) and install docker
  
$sudo yum update -y
$sudo amazon-linux-extras install docker
$docker –version

3.	Install docker compose
$sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
$sudo chmod +x /usr/local/bin/docker-compose
$docker-compose version

4.	Create a docker-compose.yml file to build and run the docker images necessary to deploy pact-broker docker container in docker environment
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
docker-compose.yml
---------------------------------------------------------

version: "3"

services:
  postgres:
    image: postgres
    healthcheck:
      test: psql postgres --command "select 1" -U postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
      POSTGRES_DB: postgres

  pact-broker:
    image: dius/pact-broker
    # build:
    #   context: .
    ports:
      - "80:80"
    depends_on:
      - postgres
    environment:
      PACT_BROKER_DATABASE_USERNAME: postgres
      PACT_BROKER_DATABASE_PASSWORD: password
      PACT_BROKER_DATABASE_HOST: postgres
      PACT_BROKER_DATABASE_NAME: postgres
      PACT_BROKER_LOG_LEVEL: INFO
    # If you remove nginx, enable the following
    # ports:
    #  - "80:80"

![image](https://user-images.githubusercontent.com/32492604/128455593-b8bf12bb-f5eb-479c-a7ff-a2d6c600a5f0.png)

-----------------------------------------------------------------------

5.	Run the docker compose 
![image](https://user-images.githubusercontent.com/32492604/128455449-7a68ee24-cd76-4ee0-a9b4-df679651e9b5.png)

6.	Check the pact url using AWS public DNS 
![image](https://user-images.githubusercontent.com/32492604/128455485-62dc07d4-3db9-4365-9eed-f45938bf104c.png)


![image](https://user-images.githubusercontent.com/32492604/128455492-d4758341-620a-4764-9340-188333d1053d.png)


7.	Pact Broker is live in AWS EC2 docker environment

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Develop Provider Service
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1.	Implement product service API which will be consumed by customer service API
2.	Product Service code is at https://github.com/satinath-nit/pact-broker-java/tree/main/product
3.	Run the product service API and access the endpoint- http://localhost:9080/product/100
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Develop Consumer Service
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1.	Implement customer service API  
2.	Customer Service code is at https://github.com/satinath-nit/pact-broker-java/tree/main/customer
3.	Run the product service API and access the endpoint- http://localhost:9081/customer/C100

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Writing Consumer Contract in Pact JSON file
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 
 Pact Test - This generates the pact json file under target/pacts folder
 
 Test Class:
 -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 
 package pact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;

public class ConsumerContractTests {

	private static final String HOST_NAME = "localhost";
	private static final int PORT = 8081;

	/*
	 * @Autowired private CustomerService customerService;
	 */

	@Rule
	public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("product", HOST_NAME, PORT, this);
	
	 @Pact(consumer = "customer", provider = "product")
	  public RequestResponsePact createPactForGetLastUpdatedTimestamp(PactDslWithProvider builder) {
	    PactDslJsonBody body = new PactDslJsonBody()
	            .integerType("id",100)
	            .stringType("name", "Laptop")
	            .stringType("desc", "DELL Laptop")
	            .decimalType("price", 1000.0)
	           ;

	    Map<String,String> headers = new HashMap();
	    headers.put("Content-Type","application/json");

	    return builder
	        .given("Get product details")
	        .uponReceiving("Get product details by product id")
	        .path("/product/100")
	        .method(HttpMethod.GET.name())
	        .willRespondWith()
	        .status(HttpStatus.OK.value())
	        .headers(headers)
	        .body(body)
	        .toPact();
	  }

	  @Test
	  @PactVerification(value = "product")
	  public void testGetProductFromProductService() {
		// when
		    ResponseEntity<String> response = new RestTemplate()
		      .getForEntity(mockProvider.getUrl() + "/product/100", String.class);

		    // then
		    assertThat(response.getStatusCode().value()).isEqualTo(200);
		    assertThat(response.getHeaders().get("Content-Type").contains("application/json")).isTrue();
		    assertThat(response.getBody()).contains("id", "100", "name", "Laptop");
	  }

}
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Pact file -
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
![image](https://user-images.githubusercontent.com/32492604/128457376-6a3b7372-b984-495b-8a78-4adb16fdc0bb.png)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Maven plugin to publish the pact to pact-broker
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

![image](https://user-images.githubusercontent.com/32492604/128457434-2fc404d1-91c6-4dcb-9b7d-fe697a85adb4.png)

Run the maven goal to publish in pact broker
![image](https://user-images.githubusercontent.com/32492604/128457528-5e9d7b76-76ab-4dc9-bcb6-00ba66fce367.png)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------


One the publish complete - check out to the pact broker URL-
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
![image](https://user-images.githubusercontent.com/32492604/128457616-8c93deb7-9a29-425e-8095-b04fccbce4b6.png)


Customer and Product relationship is published

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Now its time for provider to validate the Pact and verify against pact broker


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Provider Verify Pact Tests
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Run the test and verify in Pact server setting "pact.verifier.publishResults" as "true"
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Done!!!
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------



