package ispel.integrator.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarInfo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private long carId;
	private String vinString;
	private String ecvString;
	private String dt_stk;
	private String dt_stk_nasl;
	private String dt_emis;
	private String dt_emis_nasl;
	private String dt_ko;
	private int km_stk;
	private int km_emis;
	private int km_ko;
	private Timestamp dt_overenievozidla;
	private Timestamp dt_getvinexpert;

	private Map<CarDetail.Type, CarDetail> details = new HashMap<CarDetail.Type, CarDetail>(
			7);

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVinString() {
		return vinString;
	}

	public void setVinString(String vinString) {
		this.vinString = vinString;
	}

	public String getEcvString() {
		return ecvString;
	}

	public void setEcvString(String ecvString) {
		this.ecvString = ecvString;
	}

	public long getCarId() {
		return carId;
	}

	public void setCarId(long carId) {
		this.carId = carId;
	}

	public String getDt_stk() {
		return dt_stk;
	}

	public void setDt_stk(String dt_stk) {
		this.dt_stk = dt_stk;
	}

	public String getDt_stk_nasl() {
		return dt_stk_nasl;
	}

	public void setDt_stk_nasl(String dt_stk_nasl) {
		this.dt_stk_nasl = dt_stk_nasl;
	}

	public String getDt_emis() {
		return dt_emis;
	}

	public void setDt_emis(String dt_emis) {
		this.dt_emis = dt_emis;
	}

	public String getDt_emis_nasl() {
		return dt_emis_nasl;
	}

	public void setDt_emis_nasl(String dt_emis_nasl) {
		this.dt_emis_nasl = dt_emis_nasl;
	}

	public String getDt_ko() {
		return dt_ko;
	}

	public void setDt_ko(String dt_ko) {
		this.dt_ko = dt_ko;
	}

	public int getKm_stk() {
		return km_stk;
	}

	public void setKm_stk(int km_stk) {
		this.km_stk = km_stk;
	}

	public int getKm_emis() {
		return km_emis;
	}

	public void setKm_emis(int km_emis) {
		this.km_emis = km_emis;
	}

	public int getKm_ko() {
		return km_ko;
	}

	public void setKm_ko(int km_ko) {
		this.km_ko = km_ko;
	}

	public void setDetail(CarDetail detail) {
		details.put(detail.getType(), detail);
	}

	public Timestamp getDt_overenievozidla() {
		return dt_overenievozidla;
	}

	public void setDt_overenievozidla(Timestamp dt_overenievozidla) {
		this.dt_overenievozidla = dt_overenievozidla;
	}

	public Timestamp getDt_getvinexpert() {
		return dt_getvinexpert;
	}

	public void setDt_getvinexpert(Timestamp dt_getvinexpert) {
		this.dt_getvinexpert = dt_getvinexpert;
	}

	public CarDetail getDetail(CarDetail.Type type) {
		CarDetail detail = details.get(type);
		if (detail == null) {
			detail = new CarDetail();
			detail.setType(type);
			details.put(detail.getType(), detail);
		}
		return detail;
	}

	public List<CarDetail> getDetails() {
		return new ArrayList<CarDetail>(details.values());
	}

	public long getDt_overenievozidlaAsLong() {
		// TODO
		return 0;
	}

	public long getDt_getvinexpertAsLong() {
		// TODO
		return 0;
	}
}
