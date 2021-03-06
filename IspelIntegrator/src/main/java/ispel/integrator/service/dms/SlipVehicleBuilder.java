package ispel.integrator.service.dms;

import generated.PartsInvoiceLine;
import generated.Vehicle;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SlipVehicleBuilder {

    public SlipVehicleBuilder.Builder newInstance() {
        return new SlipVehicleBuilder.Builder();
    }

    public class Builder {

        private PartsInvoiceLine[] partsInvoiceLines;

        private Builder() {
        }

        public SlipVehicleBuilder.Builder withPartsInvoiceLines(PartsInvoiceLine[] partsInvoiceLines) {
            this.partsInvoiceLines = partsInvoiceLines;
            return this;
        }

        public Vehicle build() {
            Vehicle vehicle = new Vehicle();
            // todo
            vehicle.setIdentityNotKnown("");
            vehicle.getPartsInvoiceLineOrServiceInvoiceLineOrOtherInvoiceLine().addAll(Arrays.asList(partsInvoiceLines));
            return vehicle;
        }


    }

}
