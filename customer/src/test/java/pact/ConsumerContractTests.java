package pact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.example.demo.service.CustomerService;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;

//@RunWith(SpringRunner.class)
//@SpringBootTest
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
