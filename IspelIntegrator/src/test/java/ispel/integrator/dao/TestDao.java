package ispel.integrator.dao;

import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.adapter.Result;
import ispel.integrator.domain.CarDetail;
import ispel.integrator.domain.CarInfo;
import ispel.integrator.test.AbstractDaoTestCase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class TestDao extends AbstractDaoTestCase {

	@Autowired
	private Dao dao;

	@Test
	public void testGetVin() {
		CarInfo carInfo = dao.getCarInfoById("4107");
		System.out.println("vin: " + carInfo.getVinString());
		System.out.println("ecv: " + carInfo.getEcvString());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void updateCarDetail() {
		CarInfo carInfo = dao.getCarInfoById("4107");
		CarDetail detail = carInfo.getDetail(CarDetail.Type.VinInfo);
		StringBuilder sb = new StringBuilder(detail.getText());
		sb.append("\n").append("test: " + System.currentTimeMillis());
		detail.setText(sb.toString());
		dao.persistCarInfo(carInfo, AdapterRequest.MethodName.GetVinExpert);
	}

	@Test
	@Transactional
	@Rollback(false)
	public void insertCarDetail() {
		CarInfo carInfo = dao.getCarInfoById("1");
		CarDetail detail = carInfo.getDetail(CarDetail.Type.VinInfo);
		StringBuilder sb = new StringBuilder(detail.getText() == null ? ""
				: detail.getText());
		sb.append("\n").append("test: " + System.currentTimeMillis());
		detail.setText(sb.toString());
		dao.persistCarInfo(carInfo, AdapterRequest.MethodName.GetVinExpert);
	}

	@Test
	@Transactional
	@Rollback(false)
	public void logResult() {
		AdapterRequest request = AdapterRequest.getEmptyRequest();
		request.setCarId("1");
		request.setMethodName(AdapterRequest.MethodName.GetVinExpert);
		request.setDataSourceName("DATA_SOURCE");

		Result result = Result.getInstance(request);
		result.setErrorText("");
		result.setProcessed(1);
		result.setUrl("https://[server]/wsispel/ws_ispel.asmx");
		result.setXmlInput("xml input");
		result.setXmlOutput("xml output");
		dao.logResult(result);
	}
}
