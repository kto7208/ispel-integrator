package ispel.integrator.service;

public class VerifyCarRequest {

	private String xmlString;

	public static class Builder {
		private String vin;
		private String ecv;
		private String irisUser;
		private String ico;
		private String irisPwd;
		private String nazovSUB = "";
		private String kontaktSUB = "";
		private int balicky = 1;

		public Builder vin(String value) {
			this.vin = value;
			return this;
		}

		public Builder ecv(String value) {
			this.ecv = value.replace("-", "");
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

		public VerifyCarRequest build() {
			VerifyCarRequest result = new VerifyCarRequest();
			result.xmlString = buildXmlString();
			return result;
		}

		private String buildXmlString() {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append(
					"<OverenieVozidla xmlns=\"https://ispel2.iris.sk/WS_ISPEL\">")
					.append("<VIN>").append(vin).append("</VIN>")
					.append("<ECV>").append(ecv).append("</ECV>")
					.append("<IU>").append(irisUser).append("</IU>")
					.append("<ICOFirma>").append(ico).append("</ICOFirma>")
					.append("<IRISHeslo>").append(irisPwd).append("</IRISHeslo>")
					.append("<NazovSUB>").append(nazovSUB).append("</NazovSUB>")
					.append("<KontaktSUB>").append(kontaktSUB).append("</KontaktSUB>")
					.append("<Balicky>").append(balicky).append("</Balicky>")
					.append("</OverenieVozidla>");
			return sBuilder.toString();
		}

	}

	private VerifyCarRequest() {
	}

	public String getXmlString() {
		return xmlString;
	}
}
