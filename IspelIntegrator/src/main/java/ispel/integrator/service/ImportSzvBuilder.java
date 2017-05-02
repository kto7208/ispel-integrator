package ispel.integrator.service;

import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.PartInfo;
import ispel.integrator.domain.dms.VehicleInfo;
import ispel.integrator.domain.dms.WorkInfo;
import localhost.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImportSzvBuilder {

    public ImportSzvBuilder.Builder newInstance() {
        return new Builder();
    }

    public class Builder {

        private OrderInfo orderInfo;
        private VehicleInfo vehicleInfo;
        private List<WorkInfo> works;
        private List<PartInfo> parts;

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

        public ImportSzvBuilder.Builder withWorks(List<WorkInfo> works) {
            this.works = works;
            return this;
        }

        public ImportSzvBuilder.Builder withParts(List<PartInfo> parts) {
            this.parts = parts;
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

            ImportSZV importSZV = new ImportSZV();
            importSZV.setZakazka(zakazka);
            return importSZV;
        }

        private Vozidlo buildVozidlo() {
            Vozidlo vozidlo = new Vozidlo();
            return vozidlo;
        }

        private ArrayOfPraca buildPracaArray() {
            ArrayOfPraca arrayOfPraca = new ArrayOfPraca();
            return arrayOfPraca;
        }

        private ArrayOfMaterial buildMaterialArray() {
            ArrayOfMaterial arrayOfMaterial = new ArrayOfMaterial();
            return arrayOfMaterial;
        }
    }
}
