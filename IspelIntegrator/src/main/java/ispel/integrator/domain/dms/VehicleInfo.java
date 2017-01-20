package ispel.integrator.domain.dms;

import java.util.Date;

public class VehicleInfo {

    private String ci_auto;

    private String spz;
    private String vin;
    private String vyrobce;
    private String model;
    private String dt_prod;
    private String dt_stk_nasl;
    private String dt_emis_nasl;
    private String tel;

    public String getSpz() {
        return spz;
    }

    public void setSpz(String spz) {
        this.spz = spz;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVyrobce() {
        return vyrobce;
    }

    public void setVyrobce(String vyrobce) {
        this.vyrobce = vyrobce;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDt_prod() {
        return dt_prod;
    }

    public void setDt_prod(String dt_prod) {
        this.dt_prod = dt_prod;
    }

    public String getDt_stk_nasl() {
        return dt_stk_nasl;
    }

    public void setDt_stk_nasl(String dt_stk_nasl) {
        this.dt_stk_nasl = dt_stk_nasl;
    }

    public String getDt_emis_nasl() {
        return dt_emis_nasl;
    }

    public void setDt_emis_nasl(String dt_emis_nasl) {
        this.dt_emis_nasl = dt_emis_nasl;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCi_auto() {
        return ci_auto;
    }

    public void setCi_auto(String ci_auto) {
        this.ci_auto = ci_auto;
    }
}
