package ispel.integrator.service.dms;

import generated.PartsInvoiceLine;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.SlipPartInfo;
import org.springframework.stereotype.Component;

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
            return null;
        }
    }

    public SlipPartsInvoiceLinesBuilder.Builder newInstance() {
        return new SlipPartsInvoiceLinesBuilder.Builder();
    }
}
