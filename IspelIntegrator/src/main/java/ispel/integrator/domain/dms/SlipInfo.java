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
}
