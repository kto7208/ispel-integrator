package ispel.integrator.service;

import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.PartInfo;
import ispel.integrator.domain.dms.VehicleInfo;
import ispel.integrator.domain.dms.WorkInfo;
import localhost.*;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ImportSzvBuilder {

    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            return df;
        }
    };

    public ImportSzvBuilder.Builder newInstance() {
        return new Builder();
    }

    public class Builder {

        private OrderInfo orderInfo;
        private VehicleInfo vehicleInfo;
        private List<WorkInfo> works;
        private List<PartInfo> parts;
        private String organizace;
        private String user;
        private String wsUser;
        private String wsPassword;

        private Builder() {
        }

        public ImportSzvBuilder.Builder withVehicle(VehicleInfo vehicleInfo) {
            this.vehicleInfo = vehicleInfo;
            return this;
        }

        public ImportSzvBuilder.Builder withOrder(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public ImportSzvBuilder.Builder withOrganizace(String organizace) {
            this.organizace = organizace;
            return this;
        }

        public ImportSzvBuilder.Builder withWorks(List<WorkInfo> works) {
            this.works = works;
            return this;
        }

        public ImportSzvBuilder.Builder withParts(List<PartInfo> parts) {
            this.parts = parts;
            return this;
        }

        public ImportSzvBuilder.Builder withUser(String user) {
            this.user = user;
            return this;
        }

        public ImportSzvBuilder.Builder withWsUser(String wsUser) {
            this.wsUser = wsUser;
            return this;
        }

        public ImportSzvBuilder.Builder withWsPassword(String wsPasword) {
            this.wsPassword = wsPassword;
            return this;
        }

        public ImportSZV build() {
            Vozidlo vozidlo = buildVozidlo();
            ArrayOfPraca arrayOfPraca = buildPracaArray();
            ArrayOfMaterial arrayOfMaterial = buildMaterialArray();

            Zakazka zakazka = new Zakazka();
            zakazka.setZakazkaVozidlo(vozidlo);
            zakazka.setZakazkaPraca(arrayOfPraca);
            zakazka.setZakazkaMaterial(arrayOfMaterial);

            zakazka.setIU(user);
            zakazka.setNazovServisBazar(organizace);
            zakazka.setCisloZakazka(buildCisloZakazka());
            zakazka.setCenaZakazka(orderInfo.getCelkem_sm().doubleValue());
            zakazka.setDatumZakazka(orderInfo.getKdyUzavDoklad());
            zakazka.setWsUser(wsUser);
            zakazka.setWsPassword(wsPassword);
            ImportSZV importSZV = new ImportSZV();
            importSZV.setZakazka(zakazka);
            return importSZV;
        }

        private String buildCisloZakazka() {
            return orderInfo.getDocumentNumber() + "-" + orderInfo.getDocumentGroup();
        }

        private Vozidlo buildVozidlo() {
            Vozidlo vozidlo = new Vozidlo();
            vozidlo.setVin(vehicleInfo.getVin());
            vozidlo.setEvc(vehicleInfo.getSpz());
            vozidlo.setVyrobca(vehicleInfo.getVyrobce());
            vozidlo.setModel(vehicleInfo.getModel());
            vozidlo.setRokVyroby(buildRokVyroby());
            vozidlo.setKategoria(vehicleInfo.getTyp_vozidla());
            vozidlo.setFarba(buildFarba());
            vozidlo.setSpecifikacia(vehicleInfo.getPopis());
            vozidlo.setDatumPredaja(buildDatumPredaja());
            vozidlo.setTachometer(orderInfo.getStav_tach());
            return vozidlo;
        }

        private Date buildDatumPredaja() {
            try {
                if (vehicleInfo.getDt_prod() != null &&
                        vehicleInfo.getDt_prod().length() > 0 &&
                        !"00000000".equals(vehicleInfo.getDt_prod())) {
                    return dateFormat.get().parse(vehicleInfo.getDt_prod());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private int buildRokVyroby() {
            if (vehicleInfo.getRok() != null &&
                    vehicleInfo.getRok().compareTo(0) != 0) {
                return vehicleInfo.getRok();
            } else if (vehicleInfo.getDt_vyroby() != null &&
                    !vehicleInfo.getDt_vyroby().isEmpty() &&
                    !"00000000".equals(vehicleInfo.getDt_vyroby())) {
                return Integer.getInteger(vehicleInfo.getDt_vyroby().substring(0, 4)).intValue();
            } else {
                Integer.getInteger(vehicleInfo.getDt_prod().substring(0, 4)).intValue();
            }
            return 0;
        }

        private String buildFarba() {
            return vehicleInfo.getBarva() == null ? "" : vehicleInfo.getBarva() + "-" +
                    vehicleInfo.getBarva_nazev() == null ? "" : vehicleInfo.getBarva_nazev();
        }

        private ArrayOfPraca buildPracaArray() {
            ArrayOfPraca arrayOfPraca = new ArrayOfPraca();
            for (WorkInfo work : works) {
                Praca praca = new Praca();
                praca.setKodPraca(work.getPracpoz());
                praca.setNazovPraca(work.getPopis_pp());
                praca.setCenaPraca(work.getCenabdph().doubleValue());
                praca.setMnozstvoPraca(buildMonzstvoPraca(work));
                arrayOfPraca.getPraca().add(praca);
            }
            return arrayOfPraca;
        }

        private double buildMonzstvoPraca(WorkInfo work) {
            return work.getNh().multiply(work.getOpakovani()).doubleValue();
        }

        private ArrayOfMaterial buildMaterialArray() {
            ArrayOfMaterial arrayOfMaterial = new ArrayOfMaterial();
            for (PartInfo part : parts) {
                Material material = new Material();
                material.setNazovMaterial(part.getNazev());
                material.setCenaMaterial(part.getCena_bdp().doubleValue());
                material.setMnozstvoMaterial(part.getMnozstvi().doubleValue());
                material.setKatalogCislo(part.getKatalog());
                arrayOfMaterial.getMaterial().add(material);
            }
            return arrayOfMaterial;
        }
    }
}
