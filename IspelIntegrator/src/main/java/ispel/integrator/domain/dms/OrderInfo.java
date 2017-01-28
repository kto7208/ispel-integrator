package ispel.integrator.domain.dms;


import java.math.BigDecimal;
import java.util.Date;

public class OrderInfo {

    private String documentGroup;
    private String documentNumber;

    private String vfpd;
    private String skupVfpd;
    private Date kdyUzavDoklad;
    private String typD;
    private String ci_reg;
    private String userName;
    private String stav_tach;
    private String storno;
    private String forma_uhr;
    private String datum;
    private String reklam_c;
    private String ci_auto;
    private BigDecimal celkem_sm;

    private String oznaceni_svf;
    private String oznaceni_spd;
    private Integer interne;


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

    public Date getKdyUzavDoklad() {
        return kdyUzavDoklad;
    }

    public void setKdyUzavDoklad(Date kdyUzavDoklad) {
        this.kdyUzavDoklad = kdyUzavDoklad;
    }

    public String getTypD() {
        return typD;
    }

    public void setTypD(String typD) {
        this.typD = typD;
    }

    public String getCi_reg() {
        return ci_reg;
    }

    public void setCi_reg(String ci_reg) {
        this.ci_reg = ci_reg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStav_tach() {
        return stav_tach;
    }

    public void setStav_tach(String stav_tach) {
        this.stav_tach = stav_tach;
    }

    public String getStorno() {
        return storno;
    }

    public void setStorno(String storno) {
        this.storno = storno;
    }

    public String getForma_uhr() {
        return forma_uhr;
    }

    public void setForma_uhr(String forma_uhr) {
        this.forma_uhr = forma_uhr;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getReklam_c() {
        return reklam_c;
    }

    public void setReklam_c(String reklam_c) {
        this.reklam_c = reklam_c;
    }

    public String getCi_auto() {
        return ci_auto;
    }

    public void setCi_auto(String ci_auto) {
        this.ci_auto = ci_auto;
    }

    public BigDecimal getCelkem_sm() {
        return celkem_sm;
    }

    public void setCelkem_sm(BigDecimal celkem_sm) {
        this.celkem_sm = celkem_sm;
    }

    public String getOznaceni_svf() {
        return oznaceni_svf;
    }

    public void setOznaceni_svf(String oznaceni_svf) {
        this.oznaceni_svf = oznaceni_svf;
    }

    public String getOznaceni_spd() {
        return oznaceni_spd;
    }

    public void setOznaceni_spd(String oznaceni_spd) {
        this.oznaceni_spd = oznaceni_spd;
    }

    public Integer getInterne() {
        return interne;
    }

    public void setInterne(Integer interne) {
        this.interne = interne;
    }
}
