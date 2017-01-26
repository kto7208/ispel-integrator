package ispel.integrator.domain.dms;

public class DescriptionInfo {

    private String documentNumber;
    private String documentGroup;

    private String popis;
    private Long poradi;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentGroup() {
        return documentGroup;
    }

    public void setDocumentGroup(String documentGroup) {
        this.documentGroup = documentGroup;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public Long getPoradi() {
        return poradi;
    }

    public void setPoradi(Long poradi) {
        this.poradi = poradi;
    }
}
