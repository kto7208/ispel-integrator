package ispel.integrator.domain.dms;

import java.util.Date;

public class SlipInfo {

    private String vfpd;
    private String skupvfpd;
    private String sklad;
    private Date dtuzavreni;
    private String doklad;
    private String doklad_typ;
    private String cidok;
    private String ci_reg;
    private String user_name;

    public String getVfpd() {
        return vfpd;
    }

    public void setVfpd(String vfpd) {
        this.vfpd = vfpd;
    }

    public String getSkupvfpd() {
        return skupvfpd;
    }

    public void setSkupvfpd(String skupvfpd) {
        this.skupvfpd = skupvfpd;
    }

    public String getSklad() {
        return sklad;
    }

    public void setSklad(String sklad) {
        this.sklad = sklad;
    }

    public Date getDtuzavreni() {
        return dtuzavreni;
    }

    public void setDtuzavreni(Date dtuzavreni) {
        this.dtuzavreni = dtuzavreni;
    }

    public String getDoklad() {
        return doklad;
    }

    public void setDoklad(String doklad) {
        this.doklad = doklad;
    }

    public String getDoklad_typ() {
        return doklad_typ;
    }

    public void setDoklad_typ(String doklad_typ) {
        this.doklad_typ = doklad_typ;
    }

    public String getCidok() {
        return cidok;
    }

    public void setCidok(String cidok) {
        this.cidok = cidok;
    }

    public String getCi_reg() {
        return ci_reg;
    }

    public void setCi_reg(String ci_reg) {
        this.ci_reg = ci_reg;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
