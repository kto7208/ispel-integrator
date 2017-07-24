package ispel.integrator.service;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import generated.DMSextract;
import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.adapter.Result;
import ispel.integrator.dao.dms.DmsDao;
import ispel.integrator.domain.CarInfo;
import ispel.integrator.domain.dms.*;
import ispel.integrator.service.dms.DmsService;
import ispel.integrator.utils.ResponseResolver;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import sk.iris.rpzv.ImportSZV;
import sk.iris.rpzv.ImportSZVResponse;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

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
	@Qualifier("ispelWSTemplate")
	private WebServiceTemplate ispelService;

    @Autowired
    @Qualifier("importSzvWSTemplate")
    private WebServiceTemplate importSzvService;

	@Autowired
	private CarService carService;

	@Autowired
	ResponseResolver responseResolver;

	@Value("${ispel.dms.directory}")
	private String dmsDirectory;

	@Value("${ispel.szv.ws.user}")
	private String wsUser;

	@Value("${ispel.szv.ws.password}")
	private String wsPassword;

    @Autowired
    private Jaxb2Marshaller dmsExtractMarshaller;


	@Autowired
	private DmsService dmsService;

    @Autowired
    private DmsDao dmsDao;

    @Autowired
    private ImportSzvBuilder importSzvBuilder;

    @Autowired
    private ImportSzvService importSZVService;

    @Autowired
    private Jaxb2Marshaller importSzvMarshallerFormattedOutput;

    @Autowired
    private ImportSzvResultBuilder importSzvResultBuilder;

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
		StringWriter stringWriter = new StringWriter();
		dmsExtractMarshaller.marshal(dmsExtract, new StreamResult(stringWriter));
        Result result = Result.getInstance(request);
        String sendResult = null;
        try {
            File f = FileUtils.getFile(dmsDirectory, getFileName(dmsExtract));
            Files.write(stringWriter.toString(),
                    f,
                    Charsets.UTF_8);
            result.setXmlInput(stringWriter.toString());
            sendResult = dmsService.sendData(f);
            result.setXmlOutput(sendResult);
			validateResult(sendResult);
			dmsService.updateOrder(documentGroup, documentNumber);
		} catch (Exception e) {
			logger.error(e);
            result.setProcessed(Result.UNPROCESSED);
            result.setErrorText(e.getClass().getCanonicalName() + " " + e.getMessage());
        }
        try {
            logService.logResult(result);
        } catch (Exception e) {
            logger.error("Log result error.");
            logger.error(e);
        }
        return result;
	}

    public Result submitMultipleInvoiceData(AdapterRequest request) {
        String documentType = request.getDocumentType();
        List<OrderKey> keys = dmsService.getOrdersForMultipleProcessing();
        DMSextract dmsExtract = dmsService.buildDMSMultiple(documentType, keys);
        Result result = Result.getInstance(request);
        if (dmsExtract != null) {
            StringWriter stringWriter = new StringWriter();
            dmsExtractMarshaller.marshal(dmsExtract, new StreamResult(stringWriter));
            String sendResult = null;
            try {
                File f = FileUtils.getFile(dmsDirectory, getFileNameMultiple());
                Files.write(stringWriter.toString(),
                        f,
                        Charsets.UTF_8);
                result.setXmlInput(stringWriter.toString());
                sendResult = dmsService.sendData(f);
                result.setXmlOutput(sendResult);
				validateResult(sendResult);
				dmsService.updateOrders(keys);
			} catch (Exception e) {
				logger.error(e);
                result.setProcessed(Result.UNPROCESSED);
                result.setErrorText(e.getClass().getCanonicalName() + " " + e.getMessage());
            }
        }
        try {
            logService.logResult(result);
        } catch (Exception e) {
            logger.error("Log result error.");
            logger.error(e);
        }
        return result;
    }

	private String validateResult(String result) throws Exception {
		if (result.contains("<Success Message")) {
			return result;
		} else {
			throw new Exception(result);
		}
	}

	public Result importSZV(AdapterRequest request) {
		String orderGroup = request.getDocumentGroup();
        String orderNumber = request.getDocumentNumber();

        OrderInfo orderInfo = dmsDao.getOrderInfo(orderNumber, orderGroup);
        VehicleInfo vehicleInfo = dmsDao.getVehicleInfo(orderInfo.getCi_auto());
        List<WorkInfo> works = dmsDao.getWorkInfoList(orderNumber, orderGroup);
        List<PartInfo> parts = dmsDao.getPartInfoList(orderNumber, orderGroup);
        String organizace = dmsDao.getOrganizace();

        ImportSZV importSZV = importSzvBuilder.newInstance()
                .withOrder(orderInfo)
                .withVehicle(vehicleInfo)
                .withParts(parts)
                .withWorks(works)
                .withOrganizace(organizace)
                .withWsUser(wsUser)
                .withWsPassword(wsPassword)
                .withUser("gerow")
                .build();

        Result result = null;
        ImportSzvResultBuilder.Builder builder = importSzvResultBuilder.newInstance()
                .withAdapterRequest(request)
                .withImportSZV(importSZV);
        try {
            ImportSZVResponse response = (ImportSZVResponse) importSzvService
                    .marshalSendAndReceive((ImportSZV) importSZV);

            result = builder
                    .withImportSZVResponse(response)
                    .build();
        } catch (Exception e) {
            logger.error(e);
            if (result == null) {
                result = builder.build();
            }
            result.setProcessed(Result.UNPROCESSED);
            result.setErrorText(e.getMessage());
        }

        if (result.getProcessed() == Result.PROCESSED) {
            importSZVService.updateOrder(orderNumber, orderGroup);
        }

        try {
            logService.logResult(result);
        } catch (Exception e) {
            logger.error("Log result error.");
            logger.error(e);
        }
        return result;
    }

	private String getFileName(DMSextract dmsExtract) {
		return new StringBuilder()
				.append("order")
				.append("-")
				.append(dmsExtract.getSite().get(0).getTransactions().getInvoice().get(0).getId())
				.append("-")
				.append(dmsExtract.getSite().get(0).getSequence())
				.append(".xml")
				.toString();
	}

    private String getFileNameMultiple() {
        return new StringBuilder()
                .append("orders")
                .append("-")
                .append(ServiceCallTimestampHolder.getAsYYMMDD())
                .append("-")
                .append(ServiceCallTimestampHolder.getAsHHMMSS())
                .append(".xml")
                .toString();
    }

    private String marshal(Object o) {
        StringWriter sw = new StringWriter();
        importSzvMarshallerFormattedOutput.marshal(o, new StreamResult(sw));
        return sw.toString();
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
