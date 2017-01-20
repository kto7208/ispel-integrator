package ispel.integrator.service;

import org.jdom2.Element;
import org.springframework.stereotype.Component;

@Component
public class DetailSearchBuilder extends CarDetailBuilder {

	public void build(StringBuilder sb, Element root) {
		Element srchMsg = root.getChild("PatranieMsg");
		if (srchMsg != null) {
			sb.append("\r\n").append(srchMsg.getChildText("patrMsg"));
		}
	}

}
