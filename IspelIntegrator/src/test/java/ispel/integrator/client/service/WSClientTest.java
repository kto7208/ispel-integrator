package ispel.integrator.client.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.springframework.ws.client.core.WebServiceTemplate;

import ispel.integrator.service.GetVinExpertRequest;
import ispel.integrator.service.VerifyCarRequest;
import ispel.integrator.test.AbstractSystemTestCase;

public class WSClientTest extends AbstractSystemTestCase {

	@Test
	public void testWS_1() throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(
					"src/test/resources/test_request/Request_1.xml"));
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}
		WebServiceTemplate wsTemplate = (WebServiceTemplate) this.applicationContext
				.getBean("ispelWSTemplate");
		StringWriter respWriter = new StringWriter();
		StreamSource source = new StreamSource(new StringReader(sb.toString()));
		StreamResult result = new StreamResult(respWriter);
		wsTemplate.sendSourceAndReceiveToResult(source, result);
		System.out.println(respWriter.toString());
	}

	@Test
	public void testWS_2() throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(
					"src/test/resources/test_request/Request_2.xml"));
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}
		WebServiceTemplate wsTemplate = (WebServiceTemplate) this.applicationContext
				.getBean("ispelWSTemplate");
		StringWriter respWriter = new StringWriter();
		StreamSource source = new StreamSource(new StringReader(sb.toString()));
		StreamResult result = new StreamResult(respWriter);
		wsTemplate.sendSourceAndReceiveToResult(source, result);
		System.out.println(respWriter.toString());
	}

	@Test
	public void testWS_3() throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(
					"src/test/resources/test_request/Request_3.xml"));
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}
		WebServiceTemplate wsTemplate = (WebServiceTemplate) this.applicationContext
				.getBean("ispelWSTemplate");
		StringWriter respWriter = new StringWriter();
		StreamSource source = new StreamSource(new StringReader(sb.toString()));
		StreamResult result = new StreamResult(respWriter);
		wsTemplate.sendSourceAndReceiveToResult(source, result);
		System.out.println(respWriter.toString());
	}

	@Test
	public void test_WS4() {
		VerifyCarRequest request = new VerifyCarRequest.Builder()
				.vin("VSSZZZ6KZVR026970").ecv("KE013DV").ico("0036216810")
				.irisUser("wsClientZlin").irisPwd("kl95_We8").build();

		WebServiceTemplate wsTemplate = (WebServiceTemplate) this.applicationContext
				.getBean("ispelWSTemplate");
		StringWriter respWriter = new StringWriter();
		StreamSource source = new StreamSource(new StringReader(
				request.getXmlString()));
		StreamResult result = new StreamResult(respWriter);
		wsTemplate.sendSourceAndReceiveToResult(source, result);
		System.out.println(respWriter.toString());
	}

	@Test
	public void test_WS5() {
		GetVinExpertRequest request = new GetVinExpertRequest.Builder()
				.vin("VSSZZZ6KZVR026970").ico("0036216810")
				.irisUser("wsClientZlin").irisPwd("kl95_We8").build();

		WebServiceTemplate wsTemplate = (WebServiceTemplate) this.applicationContext
				.getBean("ispelWSTemplate");
		StringWriter respWriter = new StringWriter();
		StreamSource source = new StreamSource(new StringReader(
				request.getXmlString()));
		StreamResult result = new StreamResult(respWriter);
		wsTemplate.sendSourceAndReceiveToResult(source, result);
		System.out.println(respWriter.toString());
	}

}
