package ispel.integrator.service;

public class GetVinExpertRequest {

	private String xmlString;

	public static class Builder {
		private String vin;
		private String irisUser;
		private String ico;
		private String irisPwd;

		public Builder vin(String value) {
			this.vin = value;
			return this;
		}

		public Builder irisUser(String value) {
			this.irisUser = value;
			return this;
		}

		public Builder irisPwd(String value) {
			this.irisPwd = value;
			return this;
		}

		public Builder ico(String value) {
			ico = value;
			return this;
		}

		public GetVinExpertRequest build() {
			GetVinExpertRequest result = new GetVinExpertRequest();
			result.xmlString = buildXmlString();
			return result;
		}

		private String buildXmlString() {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append(
					"<GetVINexpert xmlns=\"https://ispel2.iris.sk/WS_ISPEL\">")
					.append("<VIN>").append(vin).append("</VIN>")
					.append("<IU>").append(irisUser).append("</IU>")
					.append("<ICOFirma>").append(ico).append("</ICOFirma>")
					.append("<IRISHeslo>").append(irisPwd)
					.append("</IRISHeslo>").append("</GetVINexpert>");
			return sBuilder.toString();
		}

	}

	private GetVinExpertRequest() {
	}

	public String getXmlString() {
		return xmlString;
	}
}
