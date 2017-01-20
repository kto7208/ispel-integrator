package ispel.integrator.service;

import org.jdom2.Element;

public abstract class CarDetailBuilder {

	public abstract void build(StringBuilder sb, Element root);

	public String getDate(String date) {
		return date.replace('T', ' ').substring(0, 19);
	}
}
