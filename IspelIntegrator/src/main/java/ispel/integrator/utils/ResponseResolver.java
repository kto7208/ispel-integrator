package ispel.integrator.utils;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Namespace;
import org.jdom2.input.JDOMParseException;
//import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
//import org.jdom2.xpath.XPathExpression;
//import org.jdom2.xpath.XPathFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

@Component
public class ResponseResolver {

	private static final Logger logger = Logger
			.getLogger(ResponseResolver.class);

	// private static final ThreadLocal<XPathExpression<String>> xPathExpr = new
	// ThreadLocal<XPathExpression<String>>() {
	// @Override
	// protected XPathExpression<String> initialValue() {
	// XPathFactory xpath = XPathFactory.instance();
	// return xpath.compile("//ispel:GetVINexpertResult/ispel:string",
	// Filters.fstring(), null, Namespace.getNamespace("ispel",
	// "https://ispel2.iris.sk/WS_ISPEL"));
	// }
	// };

	private static final ThreadLocal<SAXBuilder> saxBuilder = new ThreadLocal<SAXBuilder>() {
		@Override
		protected SAXBuilder initialValue() {
			return new SAXBuilder();
		}
	};

	public Document getVINExpertResult(String getVINExpertResponse)
			throws Exception {
		SAXBuilder builder = saxBuilder.get();
		Document document = builder.build(new InputSource(
				new ByteArrayInputStream(getVINExpertResponse.getBytes())));
		// better use XPath here but does not work
		String result = document
				.getRootElement()
				.getChild(
						"GetVINexpertResult",
						Namespace
								.getNamespace("https://ispel2.iris.sk/WS_ISPEL"))
				.getChildText(
						"string",
						Namespace
								.getNamespace("https://ispel2.iris.sk/WS_ISPEL"));
		Document resultDoc;
		try {
			resultDoc = builder.build(new InputSource(new ByteArrayInputStream(
					result.getBytes())));
		} catch (JDOMParseException e) {
			logger.error(e);
			Exception ex = new Exception(result, e);
			throw ex;
		}
		return resultDoc;
	}

	public Document getVerifyCarResult(String verifyCarResponse)
			throws Exception {
		SAXBuilder builder = saxBuilder.get();
		Document document = builder.build(new InputSource(
				new ByteArrayInputStream(verifyCarResponse.getBytes())));
		// better use XPath here but does not work
		String result = document.getRootElement().getChildText(
				"OverenieVozidlaResult",
				Namespace.getNamespace("https://ispel2.iris.sk/WS_ISPEL"));
		Document resultDoc;
		try {
			resultDoc = builder.build(new InputSource(new ByteArrayInputStream(
					result.getBytes())));
		} catch (JDOMParseException e) {
			logger.error(e);
			Exception ex = new Exception(result, e);
			throw ex;
		}
		return resultDoc;
	}

}
