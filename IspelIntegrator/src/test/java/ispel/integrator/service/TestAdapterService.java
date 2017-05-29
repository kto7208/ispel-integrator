package ispel.integrator.service;

import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.adapter.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@TransactionConfiguration(transactionManager = "txMgr", defaultRollback = true)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test_config/system-test-config.xml" })
public class TestAdapterService {

	@Autowired
	public AdapterService adapterService;

	@Test
	public void getVinExpert() throws Exception {

		ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());

		AdapterRequest request = AdapterRequest.getEmptyRequest();
		request.setCarId("1118");
		request.setMethodName(AdapterRequest.MethodName.GetVinExpert);
		request.setDataSourceName("kto_1");

		Result result = adapterService.getVinExpert(request);
		System.out.println(result);

	}

	@Test
	public void verifyCar() throws Exception {
		ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());

		AdapterRequest request = AdapterRequest.getEmptyRequest();
		request.setCarId("1118");
		request.setMethodName(AdapterRequest.MethodName.VerifyCar);
		request.setDataSourceName("kto_1");

		adapterService.verifyCar(request);

	}

    @Test
    public void submitInvoiceData() throws Exception {
        ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());

        AdapterRequest request = AdapterRequest.getEmptyRequest();
        request.setDocumentGroup("17");
        request.setDocumentNumber("12140001");
        request.setDocumentType("ZAK");
        request.setMethodName(AdapterRequest.MethodName.SubmitInvoiceData);
        request.setDataSourceName("kto_1");

        adapterService.submitInvoiceData(request);

    }

	@Test
	public void submitSlipData() throws Exception {
		ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());

		AdapterRequest request = AdapterRequest.getEmptyRequest();
		request.setDocumentGroup("16");
		request.setDocumentNumber("0000600001");
		request.setDocumentType("VYD");
		request.setMethodName(AdapterRequest.MethodName.SubmitInvoiceData);
		request.setDataSourceName("kto_1");

		adapterService.submitInvoiceData(request);
	}

	@Test
	public void importSZV() throws Exception {
		ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());

		AdapterRequest request = AdapterRequest.getEmptyRequest();
		request.setDocumentGroup("17");
		request.setDocumentNumber("12140001");
		request.setDocumentType("ZAK");
		request.setMethodName(AdapterRequest.MethodName.ImportSZV);
		request.setDataSourceName("kto_1");

		adapterService.importSZV(request);
	}

}
