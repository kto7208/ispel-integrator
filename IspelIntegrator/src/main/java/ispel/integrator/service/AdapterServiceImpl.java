package ispel.integrator.service;

import generated.DMSextract;
import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.adapter.Result;
import ispel.integrator.domain.CarInfo;
import ispel.integrator.service.dms.DmsService;
import ispel.integrator.utils.ResponseResolver;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class AdapterServiceImpl implements AdapterService {

	private static final Logger logger = Logger
			.getLogger(AdapterServiceImpl.class);

	private static final ThreadLocal<XMLOutputter> xmlOutputter = new ThreadLocal<XMLOutputter>() {
		@Override
		protected XMLOutputter initialValue() {
			return new XMLOutputter(Format.getPrettyFormat());
		}
	};

	@Autowired
	private LogService logService;

	@Autowired
	private WebServiceTemplate ispelService;

	@Autowired
	private CarService carService;

	@Autowired
	ResponseResolver responseResolver;

	@Autowired
	private DmsService dmsService;

	private String irisUser;
	private String irisPwd;
	private String icoString;

	public Result getVinExpert(AdapterRequest request) {
		CarInfo carInfo = carService.getCarInfoById(request.getCarId());

		GetVinExpertRequest req = new GetVinExpertRequest.Builder()
				.vin(carInfo.getVinString()).ico(icoString).irisUser(irisUser)
				.irisPwd(irisPwd).build();

		Document vinExpertResult = null;
		Result result = Result.getInstance(request);
		result.setXmlInput(req.getXmlString());
		try {
			StringWriter respWriter = new StringWriter();
			ispelService.sendSourceAndReceiveToResult(new StreamSource(
					new StringReader(req.getXmlString())), new StreamResult(
					respWriter));
			result.setXmlOutput(respWriter.toString());
			vinExpertResult = responseResolver.getVINExpertResult(respWriter
					.toString());
			result.setXmlOutput(xmlOutputter.get()
					.outputString(vinExpertResult));
			carService.processGetVinExpert(carInfo, vinExpertResult);
		} catch (Exception e) {
			logger.error(e);
			result.setProcessed(Result.UNPROCESSED);
			result.setErrorText(e.getMessage());
		}

		try {
			logService.logResult(result);
		} catch (Exception e) {
			logger.error("Log result error.");
			logger.error(e);
		}
		return result;
	}

	public Result verifyCar(AdapterRequest request) {
		CarInfo carInfo = carService.getCarInfoById(request.getCarId());

		VerifyCarRequest req = new VerifyCarRequest.Builder()
				.vin(carInfo.getVinString()).ecv(carInfo.getEcvString())
				.ico(icoString).irisUser(irisUser).irisPwd(irisPwd).build();

		Document verifyCarResult = null;
		Result result = Result.getInstance(request);
		result.setXmlInput(req.getXmlString());
		try {
			StringWriter respWriter = new StringWriter();
			ispelService.sendSourceAndReceiveToResult(new StreamSource(
					new StringReader(req.getXmlString())), new StreamResult(
					respWriter));
			result.setXmlOutput(respWriter.toString());
			verifyCarResult = responseResolver.getVerifyCarResult(respWriter
					.toString());
			result.setXmlOutput(xmlOutputter.get()
					.outputString(verifyCarResult));
			carService.processVerifyCar(carInfo, verifyCarResult);
		} catch (Exception e) {
			logger.error(e);
			result.setProcessed(Result.UNPROCESSED);
			result.setErrorText(e.getMessage());
		}

		try {
			logService.logResult(result);
		} catch (Exception e) {
			logger.error("Log result error.");
			logger.error(e);
		}
		return result;
	}


	public Result submitInvoiceData(AdapterRequest request) {

		String documentGroup = request.getDocumentGroup();
		String documentNumber = request.getDocumentNumber();
		String documentType = request.getDocumentType();

		DMSextract dmsExtract = dmsService.buildDMS(documentType, documentNumber, documentGroup);


		Result result = Result.getInstance(request);
		return result;
	}

	public String getIrisUser() {
		return irisUser;
	}

	public void setIrisUser(String irisUser) {
		this.irisUser = irisUser;
	}

	public String getIrisPwd() {
		return irisPwd;
	}

	public void setIrisPwd(String irisPwd) {
		this.irisPwd = irisPwd;
	}

	public String getIcoString() {
		return icoString;
	}

	public void setIcoString(String icoString) {
		this.icoString = icoString;
	}

}
