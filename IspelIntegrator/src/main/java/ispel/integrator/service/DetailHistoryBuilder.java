package ispel.integrator.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetailHistoryBuilder extends CarDetailBuilder {
	private static final Logger log = Logger
			.getLogger(DetailHistoryBuilder.class);

	private static final Namespace ns = Namespace.getNamespace("diffgr",
			"urn:schemas-microsoft-com:xml-diffgram-v1");

	private static final String HISTORY_VIN = "VIN číslo vozidla";
	private static final String HISTORY_DATE = "Dátum odčítania km";
	private static final String HISTORY_KM = "Prejazdené kilometre";
	private static final String HISTORY_OFFICE = "Typ pracoviska";
	private static final String HISTORY_ID = "Evidenčné číslo";
	private static final String HISTORY_NO_DATA = "Nie je zápis v histórii km vozidla";

	@Autowired
	private DateKmHolder dateKmHolder;

	public void build(StringBuilder sb, Element root) {
		Element hkm = root.getChild("HistoriaKm");
		if (hkm != null) {
			Element dt = hkm.getChild("DataTable");
			if (dt != null) {
				Element diffgram = dt.getChild("diffgram", ns);
				if (diffgram != null) {
					Element de = diffgram.getChild("DocumentElement");
					if (de != null) {
						processDocumentElement(sb, de);
					} else {
						log.warn("Element <DocumentElement>  not found");
						sb.append("\r\n").append(HISTORY_NO_DATA)
								.append("\r\n");
					}
				} else {
					log.warn("Element <diffgr:diffgram> not found");
				}
			} else {
				log.warn("Element <DataTable> nto found");
			}

		} else {
			log.warn("Element <HistoriaKm> not found");
		}
	}

	private void processDocumentElement(StringBuilder sb, Element de) {
		List<Element> maList = de.getChildren("MileAge");
		List<Element> newList = new ArrayList<Element>(maList);
		Collections.sort(newList, new Comparator<Element>() {
			public int compare(Element e1, Element e2) {
				// String s1 = e1.getAttributeValue("id", ns);
				String s1 = e1.getChildText("Datum_odcitania");
				if (s1 != null) {
					return (-1)
							* s1.compareToIgnoreCase(e2
									.getChildText("Datum_odcitania"));
					// return
					// (-1)*s1.compareToIgnoreCase(e2.getAttributeValue("id",
					// ns));
				} else {
					// log.warn("Attribute diffgr:id not found");
					log.warn("Element <Datum_docitania> not found");
					return -1;
				}
			}
		});

		boolean isEKDateSet = false;
		boolean isTKDateSet = false;
		boolean isKODateSet = false;
		for (Element ma : newList) {
			if (ma.getChildText("VIN") != null) {
				sb.append("\r\n").append(HISTORY_VIN).append(": ")
						.append(ma.getChildText("VIN"));
			}
			if (ma.getChildText("Datum_odcitania") != null) {
				String date = ma.getChildText("Datum_odcitania").replace('T',
						' ');
				sb.append("\r\n").append(HISTORY_DATE).append(": ")
						.append(getDate(date));
			}
			if (ma.getChildText("Kilometre") != null) {
				sb.append("\r\n").append(HISTORY_KM).append(": ")
						.append(ma.getChildText("Kilometre"));
			}
			if (ma.getChildText("Typ_pracoviska") != null) {
				sb.append("\r\n").append(HISTORY_OFFICE).append(": ")
						.append(ma.getChildText("Typ_pracoviska"));
				if ("EK".equalsIgnoreCase(ma.getChildText("Typ_pracoviska")) && !isEKDateSet) {
					dateKmHolder.setKm_emis(ma.getChildText("Kilometre"));
					isEKDateSet = true;
				} else if ("TK".equalsIgnoreCase(ma
						.getChildText("Typ_pracoviska")) && !isTKDateSet) {
					dateKmHolder.setKm_stk(ma.getChildText("Kilometre"));
					isTKDateSet = true;
				} else if ("KO".equalsIgnoreCase(ma
						.getChildText("Typ_pracoviska")) && !isKODateSet) {
					dateKmHolder.setKm_ko(ma.getChildText("Kilometre"));
					dateKmHolder.setDt_ko(ma.getChildText("Datum_odcitania"));
					isKODateSet = true;
				}
			}
			if (ma.getChildText("ECV") != null) {
				sb.append("\r\n").append(HISTORY_ID).append(": ")
						.append(ma.getChildText("ECV"));
			}
			sb.append("\r\n");
		}
	}

}
