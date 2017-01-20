package ispel.integrator.client.service;

import ispel.integrator.utils.ResponseResolver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.xml.sax.InputSource;

public class TestResponse {

	@Test
	public void testResponse1() throws Exception {
		Document document = new SAXBuilder().build(new InputSource(
				new FileInputStream(
						"src/test/resources/test_request/Response_1.xml")));
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = "//GetVINexpertResult/string";
		String result = xPath.compile(expression).evaluate(document);
		System.out.println(result);

		Document doc1 = new SAXBuilder().build(new InputSource(
				new ByteArrayInputStream(result.getBytes())));
		XPath xPath1 = XPathFactory.newInstance().newXPath();
		String expression1 = "//ValueMutation[Order=80]/description";
		String result1 = xPath1.compile(expression1).evaluate(doc1);
		System.out.println("result1: " + result1);

	}

	@Test
	public void testResponseResolver() throws Exception {

		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(
					"src/test/resources/test_request/Response_1.xml"));
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}

		ResponseResolver resolver = new ResponseResolver();
		Document d = resolver.getVINExpertResult(sb.toString());
		System.out.println(d);
	}
}
