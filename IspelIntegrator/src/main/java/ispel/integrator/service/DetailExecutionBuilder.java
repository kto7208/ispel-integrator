package ispel.integrator.service;

import org.jdom2.Element;
import org.springframework.stereotype.Component;

@Component
public class DetailExecutionBuilder extends CarDetailBuilder {

	private static final String EXECUTION_NUMBER = "Číslo exekúcie";
	private static final String EXECUTION_OFFICE = "Názov exekútorského úradu";
	private static final String EXECUTION_START = "Dátum začiatku exekúcie";
	private static final String EXECUTION_UPDATE = "Dátum poslednej aktualizácie údajov";
	private static final String EXECUTION_EMAIL = "Mailová adresa exekútorského úradu";
	private static final String EXECUTION_PHONE = "Telefonický kontakt exekútorského úradu";
	private static final String EXECUTION_EMPTY = "";

	public void build(StringBuilder sb, Element root) {
		Element e = root.getChild("ArrayOfExekucie");
		if (e != null) {
			for (Element el : e.getChildren("Exekucie")) {
				sb.append("\r\n").append(EXECUTION_NUMBER).append(": ")
						.append(el.getChildText("CisEX"));
				sb.append("\r\n").append(EXECUTION_OFFICE).append(": ")
						.append(el.getChildText("ExekUrad"));
				sb.append("\r\n").append(EXECUTION_START).append(": ")
						.append(el.getChildText("DOd"));
				sb.append("\r\n").append(EXECUTION_UPDATE).append(": ")
						.append(el.getChildText("DPosAktual"));
				sb.append("\r\n").append(EXECUTION_EMAIL).append(": ")
						.append(el.getChildText("Email"));
				sb.append("\r\n").append(EXECUTION_PHONE).append(": ")
						.append(el.getChildText("Telefon"));
			}
		} else {
			Element exMsg = root.getChild("ExekucieMsg");
			if (exMsg != null) {
				sb.append(exMsg.getChildText("exekucieEmpty"));
			}
		}
	}

}
