package ispel.integrator.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.stereotype.Component;

@Component
public class DetailOriginalityBuilder extends CarDetailBuilder {

	private static final Logger log = Logger
			.getLogger(DetailOriginalityBuilder.class);

	private static final Namespace ns = Namespace
			.getNamespace("http://IRISWebServices.sk/");

	private static final String ORIGINALITY_DATE = "Dátum kontroly";
	private static final String ORIGINALITY_CLASSIFICATION = "Klasifikácia";
	private static final String ORIGINALITY_NOTE = "Poznámka v posudku";
	private static final String ORIGINALITY_BLOCKING = "Blokácia";
	private static final String ORIGINALITY_SEARCH = "Pátranie";
	private static final String ORIGINALITY_MANUFACTORY = "Továrenská značka";
	private static final String ORIGINALITY_VIN = "VIN";
	private static final String ORIGINALITY_ID = "EČV";
	private static final String ORIGINALITY_YEAR_DOCUMENT = "Rok výroby doklady";
	private static final String ORIGINALITY_YEAR_CONTROL = "Rok výroby kontrola";
	private static final String ORIGINALITY_NOTE_CONTROL = "Poznámky z kontroly";
	private static final String ORIGINALITY_EXPERT_EVIDENCE_NUMBER = "Číslo posudku";
	private static final String ORIGINALITY_LABEL_NUMBER = "Číslo nálepky";
	private static final String ORIGINALITY_PKO_NUMBER = "Číslo pracoviska";
	private static final String ORIGINALITY_MILEAGE = "Prejazdená vzdialenosť";
	private static final String ORIGINALITY_COAT = "Povlakové vrstvy (miesto merania, kód, počet meraní, hodnota)";

	public void build(StringBuilder sb, Element root) {
		Element koMsg = root.getChild("KOMsg");
		if (koMsg != null) {
			sb.append("\r\n").append(koMsg.getChildText("koEmpty"));
		} else {
			Element ko = root.getChild("ArrayOfReturnValueKOData");
			if (ko != null) {
				Element rv = ko.getChild("ReturnValueKOData");
				if (rv != null) {
					Element vk = rv.getChild("VysledokKontroly", ns);
					if (vk == null) {
						log.warn("Element <VysledokKontroly> not found");
					}
					Element uv = rv.getChild("UdajeOVozidle", ns);
					if (uv == null) {
						log.warn("Element <UdajeOVozidle> not found");
					}
					Element pv = rv.getChild("PovlakovaVrstva", ns);
					if (uv == null) {
						log.warn("Element <PovlakovaVrstva> not found");
					}
					buildFromCarData(sb, vk, uv, pv);

				} else {
					log.warn("Element <ReturnValueKOData> not found");
				}
			} else {
				log.warn("Element <ArrayOfReturnValueKOData> not found");
			}
		}
	}

	private void buildFromCarData(StringBuilder sb, Element vk, Element uv,
			Element pv) {
		if (uv != null) {
			sb.append("\r\n").append(ORIGINALITY_PKO_NUMBER).append(": ")
					.append(uv.getChildText("CisloPKO", ns));
			sb.append("\r\n").append(ORIGINALITY_DATE).append(": ")
					.append(getDate(uv.getChildText("CasKontroly", ns)));
		}
		if (vk != null) {
			sb.append("\r\n").append(ORIGINALITY_CLASSIFICATION).append(": ")
					.append(vk.getChildText("Klasifikacia", ns));
			sb.append("\r\n").append(ORIGINALITY_NOTE).append(": ")
					.append(vk.getChildText("PoznamkaVPosudku", ns));
			sb.append("\r\n").append(ORIGINALITY_BLOCKING).append(": ")
					.append(vk.getChildText("Blokacia", ns));
			sb.append("\r\n").append(ORIGINALITY_SEARCH).append(": ")
					.append(vk.getChildText("Patranie", ns));
		}
		if (uv != null) {
			sb.append("\r\n").append(ORIGINALITY_MANUFACTORY).append(": ")
					.append(uv.getChildText("TovarenskaZnacka", ns));
			sb.append("\r\n").append(ORIGINALITY_VIN).append(": ")
					.append(uv.getChildText("VIN", ns));
			sb.append("\r\n").append(ORIGINALITY_ID).append(": ")
					.append(uv.getChildText("EVC", ns));
			sb.append("\r\n").append(ORIGINALITY_YEAR_DOCUMENT).append(": ")
					.append(uv.getChildText("RokVyrobyDoklady", ns));
			sb.append("\r\n").append(ORIGINALITY_YEAR_CONTROL).append(": ")
					.append(uv.getChildText("RokVyrobyKontrola", ns));
			sb.append("\r\n").append(ORIGINALITY_MILEAGE).append(": ")
					.append(uv.getChildText("PrejazdenaVzdialenost", ns));
			sb.append("\r\n").append(ORIGINALITY_EXPERT_EVIDENCE_NUMBER)
					.append(": ").append(uv.getChildText("CisloPosudku", ns));
			sb.append("\r\n").append(ORIGINALITY_LABEL_NUMBER).append(": ")
					.append(uv.getChildText("CisloNalepky", ns));
			sb.append("\r\n").append(ORIGINALITY_NOTE_CONTROL).append(": ")
					.append(uv.getChildText("PoznamkyKontroly", ns));
		}
		if (pv != null) {
			sb.append("\r\n").append(ORIGINALITY_COAT).append(":");
			List<Element> pvList = pv.getChildren("PovlakovaVrstva", ns);
			for (Element e : pvList) {
				sb.append("\r\n");
				sb.append(e.getChildText("Kod", ns).replace(":", "")).append(
						" (");
				sb.append(e.getChildText("MiestoMerania", ns).replace(":", ""))
						.append("),");
				sb.append(e.getChildText("PocetMerani", ns).replace(":", ""))
						.append(",");
				sb.append(e.getChildText("Hodnota", ns));
			}
		}
	}
}
