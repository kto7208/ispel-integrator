package ispel.integrator.domain.dms;

import java.math.BigDecimal;

public class PartInfo {

    private String orderNumber;
    private String orderGroup;

    private String katalog;
    private String nazov_p1;
    private String original_nd;

    private BigDecimal mnozstvi;
    private BigDecimal cena_skl;
    private BigDecimal cena_bdp;
    private BigDecimal cena_prodej;
    private BigDecimal cena_nakup;

    private BigDecimal pocet;
    private String dt_vydej;
    private String dt_prijem;
    private String druh_tovaru;

    private Long sklad;
    private BigDecimal cena_dopor;
    private String ostatni;

    private String nazev;


    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderGroup() {
        return orderGroup;
    }

    public void setOrderGroup(String orderGroup) {
        this.orderGroup = orderGroup;
    }

    public String getKatalog() {
        return katalog;
    }

    public void setKatalog(String katalog) {
        this.katalog = katalog;
    }

    public String getNazov_p1() {
        return nazov_p1;
    }

    public void setNazov_p1(String nazov_p1) {
        this.nazov_p1 = nazov_p1;
    }

    public String getOriginal_nd() {
        return original_nd;
    }

    public void setOriginal_nd(String original_nd) {
        this.original_nd = original_nd;
    }

    public BigDecimal getMnozstvi() {
        return mnozstvi;
    }

    public void setMnozstvi(BigDecimal mnozstvi) {
        this.mnozstvi = mnozstvi;
    }

    public BigDecimal getCena_skl() {
        return cena_skl;
    }

    public void setCena_skl(BigDecimal cena_skl) {
        this.cena_skl = cena_skl;
    }

    public BigDecimal getCena_bdp() {
        return cena_bdp;
    }

    public void setCena_bdp(BigDecimal cena_bdp) {
        this.cena_bdp = cena_bdp;
    }

    public BigDecimal getCena_prodej() {
        return cena_prodej;
    }

    public void setCena_prodej(BigDecimal cena_prodej) {
        this.cena_prodej = cena_prodej;
    }

    public BigDecimal getCena_nakup() {
        return cena_nakup;
    }

    public void setCena_nakup(BigDecimal cena_nakup) {
        this.cena_nakup = cena_nakup;
    }

    public BigDecimal getPocet() {
        return pocet;
    }

    public void setPocet(BigDecimal pocet) {
        this.pocet = pocet;
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

    public String getDruh_tovaru() {
        return druh_tovaru;
    }

    public void setDruh_tovaru(String druh_tovaru) {
        this.druh_tovaru = druh_tovaru;
    }

    public Long getSklad() {
        return sklad;
    }

    public void setSklad(Long sklad) {
        this.sklad = sklad;
    }

    public BigDecimal getCena_dopor() {
        return cena_dopor;
    }

    public void setCena_dopor(BigDecimal cena_dopor) {
        this.cena_dopor = cena_dopor;
    }

    public String getOstatni() {
        return ostatni;
    }

    public void setOstatni(String ostatni) {
        this.ostatni = ostatni;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }
}
