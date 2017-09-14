package ispel.integrator.domain.dms;

public class OrderKey {

    private Long zakazka;
    private Integer skupina;
    private Long sklad;

    public Long getZakazka() {
        return zakazka;
    }

    public void setZakazka(Long zakazka) {
        this.zakazka = zakazka;
    }

    public Integer getSkupina() {
        return skupina;
    }

    public void setSkupina(Integer skupina) {
        this.skupina = skupina;
    }

    public Long getSklad() {
        return sklad;
    }

    public void setSklad(Long sklad) {
        this.sklad = sklad;
    }
}
