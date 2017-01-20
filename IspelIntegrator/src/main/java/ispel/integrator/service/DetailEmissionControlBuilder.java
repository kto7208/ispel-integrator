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
public class DetailEmissionControlBuilder extends CarDetailBuilder {

	private static final Logger log = Logger
			.getLogger(DetailEmissionControlBuilder.class);

	private static final Namespace ns = Namespace.getNamespace("diffgr",
			"urn:schemas-microsoft-com:xml-diffgram-v1");

	private static final String EC_DATE = "Dátum odčítania km";
	private static final String EC_NEXT_DATE = "Dátum nasledujúcej kontroly";
	private static final String EC_OFFICE = "Typ pracoviska";
	private static final String EC_RESULT = "Výsledok kontroly";
	private static final String EC_NO_DATA = "Nie je zápis v sekcii o emisnej kontrole vozidla";

	@Autowired
	private DateKmHolder dateKmHolder;

	public void build(StringBuilder sb, Element root) {
		Element hkm = root.getChild("TK_EK");
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
						sb.append("\n").append(EC_NO_DATA).append("\n");
					}
				} else {
					log.warn("Element <diffgr:diffgram> not found");
				}
			} else {
				log.warn("Element <DataTable> nto found");
			}

		} else {
			log.warn("Element <TK_EK> not found");
		}
	}

	private void processDocumentElement(StringBuilder sb, Element de) {
		List<Element> maList = de.getChildren("TechEms");
		List<Element> newList = new ArrayList<Element>();
		for (Element e : maList) {
			if ("EK".equalsIgnoreCase(e.getChildText("Typ_pracoviska"))) {
				newList.add(e);
			}
		}
		Collections.sort(newList, new Comparator<Element>() {
			public int compare(Element e1, Element e2) {
				// String s1 = e1.getAttributeValue("id", ns);
				String s1 = e1.getChildText("Datum_odcitania");
				if (s1 != null) {
					return (-1)
							* s1.compareToIgnoreCase(e2
									.getChildText("Datum_odcitania"));
					// return (-1) *
					// s1.compareToIgnoreCase(e2.getAttributeValue("id", ns));
				} else {
					// log.warn("Attribute diffgr:id not found");
					log.warn("Element <Datum_docitania> not found");
					return -1;
				}
			}
		});
		boolean isDtEmisSet = false;
		boolean isDtEmisNaslSet = false;
		for (Element ma : newList) {
			if (ma.getChildText("Typ_pracoviska") != null) {
				sb.append("\r\n").append(EC_OFFICE).append(": ")
						.append(ma.getChildText("Typ_pracoviska"));
			}
			if (ma.getChildText("Datum_odcitania") != null) {
				sb.append("\r\n").append(EC_DATE).append(": ")
						.append(getDate(ma.getChildText("Datum_odcitania")));
				if (!isDtEmisSet) {
					dateKmHolder.setDt_emis(ma.getChildText("Datum_odcitania"));
					isDtEmisSet = true;
				}
			}
			if (ma.getChildText("Vysledok_kontroly") != null) {
				sb.append("\r\n").append(EC_RESULT).append(": ")
						.append(ma.getChildText("Vysledok_kontroly"));
			}
			if (ma.getChildText("Datum_nasledujucej_kontroly") != null) {
				sb.append("\r\n")
						.append(EC_NEXT_DATE)
						.append(": ")
						.append(getDate(ma
								.getChildText("Datum_nasledujucej_kontroly")));
				if (!isDtEmisNaslSet) {
					dateKmHolder.setDt_emis_nasl(ma
						.getChildText("Datum_nasledujucej_kontroly"));
					isDtEmisNaslSet = true;
				}
			}
			sb.append("\r\n");
		}
	}

}
