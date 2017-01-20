package ispel.integrator.service;

import ispel.integrator.dao.Dao;
import ispel.integrator.domain.CarInfo;
import ispel.integrator.utils.ResponseResolver;

import java.io.File;
import java.util.Scanner;

import org.jdom2.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@TransactionConfiguration(transactionManager = "txMgr", defaultRollback = true)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test_config/system-test-config.xml" })
public class TestCarService {

	@Autowired
	CarServiceImpl carService;

	@Autowired
	ResponseResolver responseResolver;

	@Autowired
	Dao dao;

	@Test
	@Transactional
	@Rollback(false)
	public void processGetVinExpert() throws Exception {
		// CarInfo carInfo = new CarInfo();
		// carInfo.setCarId(1);
		ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());
		CarInfo carInfo = dao.getCarInfoById("1");
		ResponseResolver resolver = new ResponseResolver();
		Document vinExpertResult = resolver
				.getVINExpertResult(getResponse("src/test/resources/test_request/Response_1.xml"));
		carService.processGetVinExpert(carInfo, vinExpertResult);
	}

	@Test
	@Transactional
	@Rollback(false)
	public void processVerifyCar() throws Exception {
		// CarInfo carInfo = new CarInfo();
		// carInfo.setCarId(1);
		ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());
		CarInfo carInfo = dao.getCarInfoById("1");
		ResponseResolver resolver = new ResponseResolver();
		Document verifyCarResult = resolver
				.getVerifyCarResult(getResponse("src/test/resources/test_request/Response_2.xml"));
		carService.processVerifyCar(carInfo, verifyCarResult);
	}

	private String getResponse(String responseFile) throws Exception {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(responseFile));
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}
		return sb.toString();
	}

}
