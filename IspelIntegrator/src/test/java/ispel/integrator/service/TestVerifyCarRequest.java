package ispel.integrator.service;

import org.junit.Test;

public class TestVerifyCarRequest {

	@Test
	public void testRequest() {
		VerifyCarRequest request = new VerifyCarRequest.Builder()
				.vin("1234567890").ecv("KE-013DV").ico("01234567890")
				.irisUser("iris-user").irisPwd("iris-pwd").build();

		System.out.println(request.getXmlString());

	}
}
