package ispel.integrator.domain;

public class CarDetail implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String text;
	private Type type;

	public enum Type {
		Execution("EXEKUCIA"), Originality("KO"), Leasing("LEASING"), Detection(
				"PATRANIE"), History("HIST_KM"), TechnicaInspection("STK"), EmissionControl(
				"EK"), VinInfo("VIN_INFO");

		private String code;

		private Type(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public static Type getTypeByCode(String code) {
			Type[] values = Type.values();
			Type type = null;
			for (Type value : values) {
				if (value.getCode().equals(code)) {
					type = value;
					break;
				}
			}
			return type;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
