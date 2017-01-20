package ispel.integrator.service;

import java.util.List;

import org.jdom2.Element;
import org.springframework.stereotype.Component;

@Component
public class DetailVINExpertBuilder extends CarDetailBuilder {

	private static final String CONST = "Bezvýznamová konštanta";

	public void build(StringBuilder sb, Element root) {
		List<Element> elms = root.getChildren("ValueMutation");
		for (Element e : elms) {
			String description = e.getChildText("description");
			String label = e.getChildText("label");
			if (!CONST.equals(label)) {
				sb.append("\r\n").append(label).append(": ")
						.append(description);
			}
		}
	}

}
