package ispel.integrator.service;

import ispel.integrator.utils.ResponseResolver;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test_config/system-test-config.xml" })
public class TestCarDetailBuilder {

	@Autowired
	DetailVINExpertBuilder detailWINExpert;

	@Autowired
	DetailOriginalityBuilder detailOriginality;

	@Autowired
	DetailHistoryBuilder detailHistory;

	@Autowired
	DetailTechnicalInspectionBuilder detailTechnicalInspection;

	@Autowired
	DetailEmissionControlBuilder detailEmissionControl;

	@Autowired
	DetailExecutionBuilder detailExecution;

	@Autowired
	DetailLeasingBuilder detailLeasing;

	@Autowired
	DetailVINExpertBuilder detailVINExpert;

	@Autowired
	DetailSearchBuilder detailSearchBuilder;
	
	@Autowired
	DetailPhotoBuilder detailPhoto;

	@Before
	public void before() {
		ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());
	}

	@Test
	public void testDetailExecution() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_Execution.xml"));
		StringBuilder sb = new StringBuilder();
		detailVINExpert.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailExecutionMsg() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_ExecutionMsg.xml"));
		StringBuilder sb = new StringBuilder();
		detailExecution.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailLeasing() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_Leasing.xml"));
		StringBuilder sb = new StringBuilder();
		detailLeasing.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailLeasingMsg() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_LeasingMsg.xml"));
		StringBuilder sb = new StringBuilder();
		detailLeasing.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailOriginality() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_Originality.xml"));
		StringBuilder sb = new StringBuilder();
		detailOriginality.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailOriginalityMsg() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_OriginalityMsg.xml"));
		StringBuilder sb = new StringBuilder();
		detailOriginality.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailHistory() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_History.xml"));
		StringBuilder sb = new StringBuilder();
		detailHistory.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailTI() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_TK.xml"));
		StringBuilder sb = new StringBuilder();
		detailTechnicalInspection.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailEC() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_EK.xml"));
		StringBuilder sb = new StringBuilder();
		detailEmissionControl.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}

	@Test
	public void testDetailSearch() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_Search.xml"));
		StringBuilder sb = new StringBuilder();
		detailSearchBuilder.build(sb, verifyCarResult.getRootElement());
		System.out.println(sb.toString());
	}
	
	@Test
	public void testDetailPhoto() throws Exception {
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("test_request/Response_Originality.xml"));
		detailPhoto.build(verifyCarResult.getRootElement(), 1);
	}

	private String getResponse(String responseFile) throws Exception {
		return Resources.toString(Resources.getResource(responseFile), Charsets.UTF_8);
	}

}
