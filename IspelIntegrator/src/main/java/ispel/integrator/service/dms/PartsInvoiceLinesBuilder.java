package ispel.integrator.service.dms;

import generated.Part;
import generated.PartsInvoiceLine;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.PartInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PartsInvoiceLinesBuilder {

    public class Builder {

        private List<PartInfo> parts;
        private OrderInfo orderInfo;

        private Builder() {
        }

        public Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public Builder withParts(List<PartInfo> parts) {
            this.parts = parts;
            return this;
        }

        public PartsInvoiceLine[] build() {
            if (parts == null) {
                throw new IllegalStateException("parts is null");
            }
            List<PartsInvoiceLine> lines = new ArrayList<PartsInvoiceLine>();
            for (PartInfo partInfo : parts) {
                PartsInvoiceLine line = new PartsInvoiceLine();
                line.setType(buildType());
                Part part = new Part();
                part.setFranchiseName(partInfo.getKatalog());
                part.setFranchiseName(partInfo.getNazov_p1());
                part.setIsFranchise("A".equalsIgnoreCase(partInfo.getOriginal_nd()));
                line.setPart(part);
                line.setQuantity(partInfo.getMnozstvi());
                line.setUnitCost(partInfo.getCena_skl());
                line.setTotalPrice(partInfo.getCena_bdp());
                line.setUnitListPrice(partInfo.getCena_prodej());
                lines.add(line);
            }
            return lines.toArray(new PartsInvoiceLine[lines.size()]);
        }

        private String buildType() {
            if ("PP".equalsIgnoreCase(orderInfo.getForma_uhr())) {
                return "invoice";
            } else if ("A".equalsIgnoreCase(orderInfo.getStorno())) {
                return "return";
            } else {
                return "credit";
            }
        }
    }

    public Builder newInstance() {
        return new Builder();
    }
}
