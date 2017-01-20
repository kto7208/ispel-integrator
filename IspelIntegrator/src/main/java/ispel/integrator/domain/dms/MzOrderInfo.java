package ispel.integrator.domain.dms;

import java.util.Date;

public class MzOrderInfo {

    private String documentGroup;
    private String documentNumber;

    private String vfpd;
    private String skupVfpd;
    private Long sklad;
    private Date dt_uzavreni;
    private String cis_pohybu;
    private String doklad_typ;
    private Long ci_dok;

    public String getDocumentGroup() {
        return documentGroup;
    }

    public void setDocumentGroup(String documentGroup) {
        this.documentGroup = documentGroup;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getVfpd() {
        return vfpd;
    }

    public void setVfpd(String vfpd) {
        this.vfpd = vfpd;
    }

    public String getSkupVfpd() {
        return skupVfpd;
    }

    public void setSkupVfpd(String skupVfpd) {
        this.skupVfpd = skupVfpd;
    }

    public Long getSklad() {
        return sklad;
    }

    public void setSklad(Long sklad) {
        this.sklad = sklad;
    }

    public Date getDt_uzavreni() {
        return dt_uzavreni;
    }

    public void setDt_uzavreni(Date dt_uzavreni) {
        this.dt_uzavreni = dt_uzavreni;
    }

    public String getCis_pohybu() {
        return cis_pohybu;
    }

    public void setCis_pohybu(String cis_pohybu) {
        this.cis_pohybu = cis_pohybu;
    }

    public String getDoklad_typ() {
        return doklad_typ;
    }

    public void setDoklad_typ(String doklad_typ) {
        this.doklad_typ = doklad_typ;
    }

    public Long getCi_dok() {
        return ci_dok;
    }

    public void setCi_dok(Long ci_dok) {
        this.ci_dok = ci_dok;
    }
}
