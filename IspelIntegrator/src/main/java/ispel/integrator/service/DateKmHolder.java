package ispel.integrator.service;

import org.springframework.stereotype.Component;

@Component
public class DateKmHolder {

	private static final ThreadLocal<String> dt_stk = new ThreadLocal<String>();
	private static final ThreadLocal<String> dt_stk_nasl = new ThreadLocal<String>();

	private static final ThreadLocal<String> dt_emis = new ThreadLocal<String>();
	private static final ThreadLocal<String> dt_emis_nasl = new ThreadLocal<String>();

	private static final ThreadLocal<String> km_stk = new ThreadLocal<String>();
	private static final ThreadLocal<String> km_emis = new ThreadLocal<String>();
	private static final ThreadLocal<String> km_ko = new ThreadLocal<String>();
	private static final ThreadLocal<String> dt_ko = new ThreadLocal<String>();

	public void reset() {
		dt_stk.set(null);
		dt_stk_nasl.set(null);
		dt_emis.set(null);
		dt_emis_nasl.set(null);
		dt_ko.set(null);
		km_stk.set(null);
		km_emis.set(null);
		km_ko.set(null);
	}

	public String getDt_stk() {
		return getDateAsVarchar8(dt_stk.get());
	}

	public String getDt_stk_nasl() {
		return getDateAsVarchar8(dt_stk_nasl.get());
	}

	public String getDt_emis() {
		return getDateAsVarchar8(dt_emis.get());
	}

	public String getDt_emis_nasl() {
		return getDateAsVarchar8(dt_emis_nasl.get());
	}

	public String getDt_ko() {
		return getDateAsVarchar8(dt_ko.get());
	}

	public int getKm_stk() {
		return getKmAsInt(km_stk.get());
	}

	public int getKm_emis() {
		return getKmAsInt(km_emis.get());
	}

	public int getKm_ko() {
		return getKmAsInt(km_ko.get());
	}

	public void setDt_stk(String value) {
		dt_stk.set(value);
	}

	public void setDt_stk_nasl(String value) {
		dt_stk_nasl.set(value);
	}

	public void setDt_emis(String value) {
		dt_emis.set(value);
	}

	public void setDt_emis_nasl(String value) {
		dt_emis_nasl.set(value);
	}

	public void setDt_ko(String value) {
		dt_ko.set(value);
	}

	public void setKm_stk(String value) {
		km_stk.set(value);
	}

	public void setKm_emis(String value) {
		km_emis.set(value);
	}

	public void setKm_ko(String value) {
		km_ko.set(value);
	}

	private String getDateAsVarchar8(String s) {
		if (s != null) {
			return s.substring(0, 10).replace("-", "");
		} else {
			return null;
		}
	}

	private int getKmAsInt(String s) {
		if (s != null) {
			return Integer.parseInt(s);
		} else {
			return 0;
		}
	}
}
