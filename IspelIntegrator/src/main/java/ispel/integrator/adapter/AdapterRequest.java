package ispel.integrator.adapter;

import org.apache.log4j.Logger;

public class AdapterRequest {
	private static final Logger log = Logger.getLogger(AdapterRequest.class);

	public enum MethodName {
		GetVinExpert,
		VerifyCar,
		SubmitInvoiceData,
		SubmitMultipleInvoiceData,
		ImportSZV
	};

	public enum Direction {
		SEND("S"), RECEIVE("R");

		private String code;

		private Direction(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	private String carId;
	private MethodName methodName;
	private String dataSourceName;
	private Direction direction;
	private String documentType;
	private String documentNumber;
	private String documentGroup;

	protected AdapterRequest() {
	}

	public static AdapterRequest getEmptyRequest() {
		return new AdapterRequest();
	}

	public static AdapterRequest getRequest(String s) {
		AdapterRequest req = new AdapterRequest();
		if ("010".equals(s.substring(0, 3))) {
			req.methodName = MethodName.GetVinExpert;
			req.carId = String.valueOf(Long.valueOf(s.substring(3, 13).trim()));
			req.dataSourceName = s.substring(13, 43).trim();
			req.direction = Direction.SEND;
		} else if ("020".equals(s.substring(0, 3))) {
			req.methodName = MethodName.VerifyCar;
			req.carId = String.valueOf(Long.valueOf(s.substring(3, 13).trim()));
			req.dataSourceName = s.substring(13, 43).trim();
			req.direction = Direction.SEND;
		} else if ("110".equals(s.substring(0, 3))) {
			req.methodName = MethodName.SubmitInvoiceData;
			req.dataSourceName = s.substring(3,33).trim();
			req.documentType = s.substring(33,36).trim();
			req.documentNumber = s.substring(36,46).trim();
			req.documentGroup = s.substring(46,48).trim();
		} else if ("030".equals(s.substring(0, 3))) {
			req.methodName = MethodName.ImportSZV;
			req.dataSourceName = s.substring(3, 33).trim();
			req.documentType = s.substring(33, 36).trim();
			req.documentNumber = s.substring(36, 46).trim();
			req.documentGroup = s.substring(46, 48).trim();
		} else if ("120".equals(s.substring(0, 3))) {
			req.methodName = MethodName.SubmitMultipleInvoiceData;
			req.dataSourceName = s.substring(3, 33).trim();
			req.documentType = s.substring(33, 36).trim();
		}
		log.debug(req);
		return req;
	}

	public MethodName getMethodName() {
		return methodName;
	}

	public void setMethodName(MethodName methodName) {
		this.methodName = methodName;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getDocumentType() {
		return this.documentType;
	}

	public String getDocumentNumber() {
		return this.documentNumber;
	}

	public String getDocumentGroup() {
		return this.documentGroup;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public void setDocumentGroup(String documentGroup) {
		this.documentGroup = documentGroup;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("AdapterRequest: ")
				.append(this.methodName).append(", ")
				.append(this.carId).append(", ")
				.append(this.dataSourceName).append(", ")
				.append(this.documentType).append(", ")
				.append(this.documentNumber).append(", ")
				.append(this.documentGroup).append(", ")
				.toString();
	}
}
