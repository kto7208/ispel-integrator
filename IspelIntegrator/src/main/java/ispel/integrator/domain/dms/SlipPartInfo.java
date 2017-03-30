package ispel.integrator.domain.dms;

import java.math.BigDecimal;

public class SlipPartInfo {

    private String sklad;
    private String ci_dok;

    private BigDecimal pocet;
    private BigDecimal cena;
    private BigDecimal celkem_pro;
    private BigDecimal cena_prodej;

    private String druh_tovaru;
    private String katalog;
    private BigDecimal sklad_pocet;

    private String dt_vydej;
    private String dt_prijem;

    private BigDecimal cena_nakup;


    public String getSklad() {
        return sklad;
    }

    public void setSklad(String sklad) {
        this.sklad = sklad;
    }

    public String getCi_dok() {
        return ci_dok;
    }

    public void setCi_dok(String ci_dok) {
        this.ci_dok = ci_dok;
    }

    public BigDecimal getPocet() {
        return pocet;
    }

    public void setPocet(BigDecimal pocet) {
        this.pocet = pocet;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    public BigDecimal getCelkem_pro() {
        return celkem_pro;
    }

    public void setCelkem_pro(BigDecimal celkem_pro) {
        this.celkem_pro = celkem_pro;
    }

    public BigDecimal getCena_prodej() {
        return cena_prodej;
    }

    public void setCena_prodej(BigDecimal cena_prodej) {
        this.cena_prodej = cena_prodej;
    }

    public String getDruh_tovaru() {
        return druh_tovaru;
    }

    public void setDruh_tovaru(String druh_tovaru) {
        this.druh_tovaru = druh_tovaru;
    }

    public String getKatalog() {
        return katalog;
    }

    public void setKatalog(String katalog) {
        this.katalog = katalog;
    }

    public BigDecimal getSklad_pocet() {
        return sklad_pocet;
    }

    public void setSklad_pocet(BigDecimal sklad_pocet) {
        this.sklad_pocet = sklad_pocet;
    }

    public String getDt_vydej() {
        return dt_vydej;
    }

    public void setDt_vydej(String dt_vydej) {
        this.dt_vydej = dt_vydej;
    }

    public String getDt_prijem() {
        return dt_prijem;
    }

    public void setDt_prijem(String dt_prijem) {
        this.dt_prijem = dt_prijem;
    }

    public BigDecimal getCena_nakup() {
        return cena_nakup;
    }

    public void setCena_nakup(BigDecimal cena_nakup) {
        this.cena_nakup = cena_nakup;
    }
}
