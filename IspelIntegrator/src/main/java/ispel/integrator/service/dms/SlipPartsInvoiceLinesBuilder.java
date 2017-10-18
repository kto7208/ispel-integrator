package ispel.integrator.service.dms;

import generated.Part;
import generated.PartsInvoiceLine;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.SlipPartInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class SlipPartsInvoiceLinesBuilder {

    public class Builder {
        private List<SlipPartInfo> parts;
        private OrderInfo orderInfo;

        public Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public Builder withParts(List<SlipPartInfo> parts) {
            this.parts = parts;
            return this;
        }

        public PartsInvoiceLine[] build() {
            if (parts == null) {
                throw new IllegalStateException("parts is null");
            }
            List<PartsInvoiceLine> lines = new ArrayList<PartsInvoiceLine>();
            for (SlipPartInfo partInfo : parts) {
                PartsInvoiceLine line = new PartsInvoiceLine();
                line.setType(buildType(partInfo));
                Part part = new Part();
                part.setFranchiseName(buildFranchiseName(partInfo));
                part.setPartNumber(partInfo.getKatalog());
                part.setIsFranchise("A".equalsIgnoreCase(partInfo.getOriginal_nd()));
                line.setPart(part);
                line.setQuantity(partInfo.getPocet().abs());
                line.setTotalCost(partInfo.getCena().multiply(partInfo.getPocet()).abs());
                line.setTotalListPrice(partInfo.getCena_prodej().multiply(partInfo.getPocet()).abs());
                line.setTotalPrice(partInfo.getCelkem_pro().abs());
                lines.add(line);
            }
            return lines.toArray(new PartsInvoiceLine[lines.size()]);
        }

        private String buildFranchiseName(SlipPartInfo partInfo) {
            if (partInfo.isNissan()) {
                return "nissan";
            } else {
                return "other";
            }

        }

        private String buildType(SlipPartInfo slipPartInfo) {
            if (slipPartInfo.getCelkem_pro().compareTo(BigDecimal.ZERO) >= 0) {
                return "invoice";
            } else {
                return "credit";
            }
        }
    }

    public SlipPartsInvoiceLinesBuilder.Builder newInstance() {
        return new SlipPartsInvoiceLinesBuilder.Builder();
    }
}
