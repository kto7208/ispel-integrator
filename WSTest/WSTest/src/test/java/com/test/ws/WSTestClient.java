package com.test.ws;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

@ContextConfiguration(locations = {"classpath:/spring/test-ws-client.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class WSTestClient {

	@Autowired
	WebServiceTemplate wsTemplate;
	
	@Test
	public void testWS() throws Exception {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(
					new File("src/test/resources/HolidayRequest.xml"));
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}
		StringWriter respWriter = new StringWriter();
		StreamSource source = new StreamSource(new StringReader(sb.toString()));
		StreamResult result = new StreamResult(respWriter);
		wsTemplate.sendSourceAndReceiveToResult(source, result);
		System.out.println(respWriter.toString());

	}
}
