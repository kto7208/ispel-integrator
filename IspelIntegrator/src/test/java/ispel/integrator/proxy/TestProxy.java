package ispel.integrator.proxy;

import ispel.integrator.test.AbstractSystemTestCase;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

public class TestProxy extends AbstractSystemTestCase {

	@Test
	public void testHttpClient() throws HttpException, IOException {
		CloseableHttpClient httpClient = (CloseableHttpClient) this.applicationContext
				.getBean("httpClient");
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(new HttpGet("http://www.google.com"));
			System.out.println(response.getStatusLine());
		} finally {
			response.close();
			httpClient.close();
		}
	}

}
