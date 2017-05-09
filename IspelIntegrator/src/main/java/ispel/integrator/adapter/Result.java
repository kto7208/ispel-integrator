package ispel.integrator.adapter;

public class Result {

	public static final int PROCESSED = 1;
	public static final int UNPROCESSED = 0;

	private String url = "";
	private String errorText = "";
	private String xmlInput = "";
	private String xmlOutput = "";
	private String dataSourceName;
	private String methodName;
	private int carId;
	private int processed;
    private int orderNumber;
    private int orderGroup;

	public static Result getInstance(AdapterRequest request) {
		return new Result(request);
	}

	private Result(AdapterRequest adapterRequest) {
		this.processed = Result.PROCESSED;
		this.methodName = adapterRequest.getMethodName().name();
		this.carId = Integer.valueOf((adapterRequest.getCarId() == null) ? "0" : adapterRequest.getCarId());
		this.dataSourceName = adapterRequest.getDataSourceName();
        this.orderNumber = adapterRequest.getDocumentNumber() != null ?
                Integer.valueOf(adapterRequest.getDocumentNumber()) : 0;
        this.orderGroup = adapterRequest.getDocumentGroup() != null ?
                Integer.valueOf(adapterRequest.getDocumentGroup()) : 0;
    }

	public String getXmlInput() {
		return xmlInput;
	}

	public void setXmlInput(String xmlInput) {
		this.xmlInput = xmlInput;
	}

	public String getXmlOutput() {
		return xmlOutput;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getProcessed() {
		return processed;
	}

	public void setProcessed(int processed) {
		this.processed = processed;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public String getErrorText() {
		return errorText;
	}

	public String getUrl() {
		return url;
	}

	public int getCarId() {
		return carId;
	}

	public void setXmlOutput(String xmlOutput) {
		this.xmlOutput = xmlOutput;
	}

    public int getOrderNumber() {
        return orderNumber;
    }

    public int getOrderGroup() {
        return orderGroup;
    }
}
