package ispel.integrator.service;

import org.jdom2.Element;
import org.springframework.stereotype.Component;

@Component
public class DetailLeasingBuilder extends CarDetailBuilder {

	private static final String LEASING_REG_NUMBER = "Evidenčné číslo";
	private static final String LEASING_COMPANY = "Názov leasingovej spoločnosti";
	private static final String LEASING_CAR_TYPE = "Typ vozidla";
	private static final String LEASING_VIN = "VIN číslo vozidla";

	public void build(StringBuilder sb, Element root) {
		Element lsMsg = root.getChild("LeasingMsg");
		Element ls;
		if (lsMsg != null) {
			sb.append("\r\n").append(lsMsg.getChildText("leasingEmpty"));
		} else if ((ls = root.getChild("ArrayOfLeasingVoz")) != null) {
			for (Element e : ls.getChildren("LeasingVoz")) {
				sb.append("\r\n").append(LEASING_REG_NUMBER).append(": ")
						.append(e.getChildText("EC"));
				sb.append("\r\n").append(LEASING_COMPANY).append(": ")
						.append(e.getChildText("LS"));
				sb.append("\r\n").append(LEASING_CAR_TYPE).append(": ")
						.append(e.getChildText("TYP"));
				sb.append("\r\n").append(LEASING_VIN).append(": ")
						.append(e.getChildText("VIN"));
			}
		}
	}

}
