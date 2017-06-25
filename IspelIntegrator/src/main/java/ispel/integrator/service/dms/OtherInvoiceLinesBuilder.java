package ispel.integrator.service.dms;

import generated.OtherInvoiceLine;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.WorkInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OtherInvoiceLinesBuilder {

    public class Builder {

        private List<WorkInfo> works;
        private OrderInfo orderInfo;

        private Builder() {
        }

        public OtherInvoiceLinesBuilder.Builder withWorks(List<WorkInfo> works) {
            this.works = works;
            return this;
        }

        public OtherInvoiceLinesBuilder.Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public OtherInvoiceLine[] build() {
            if (works == null) {
                throw new IllegalStateException("works is null");
            }
            if (orderInfo == null) {
                throw new IllegalStateException("orderInfo is null");
            }
            Map<Long, OtherInvoiceLine> map = new HashMap<Long, OtherInvoiceLine>();
            List<OtherInvoiceLine> lines = new ArrayList<OtherInvoiceLine>();
            for (WorkInfo workInfo : works) {
                if ("A".equalsIgnoreCase(workInfo.getOstatni()) &&
                        !"A".equalsIgnoreCase(workInfo.getVlastna_pp())) {
                    OtherInvoiceLine line = new OtherInvoiceLine();
                    line.setTotalPrice(buildTotalPrice(workInfo));
                    line.setTotalCost(buildTotalCost(workInfo));
                    line.setTotalListPrice(buildTotalListPrice(workInfo));
                    line.setType(buildType(workInfo));
                    line.setCode(workInfo.getPracpoz());
                    line.setDescription(workInfo.getPopis_pp());
                    line.setQuantity(buildQuantity(workInfo));
                    lines.add(line);
                }
            }
            return lines.toArray(new OtherInvoiceLine[lines.size()]);
        }

        private String buildType(WorkInfo workInfo) {
            /*
            if ("PP".equalsIgnoreCase(orderInfo.getForma_uhr())) {
                return "invoice";
            } else if ("A".equalsIgnoreCase(orderInfo.getStorno())){
                return "return";
            } else {
                return "credit";
            }
            */
            /*
            if ("A".equalsIgnoreCase(orderInfo.getStorno())) {
                return "credit";
            } else {
                return "invoice";
            }
            */
            if (workInfo.getCenabdph().compareTo(BigDecimal.ZERO) >= 0) {
                return "invoice";
            } else {
                return "credit";
            }
        }


        private BigDecimal buildQuantity(WorkInfo workInfo) {
            return workInfo.getNh().multiply(workInfo.getOpakovani());
        }

        private BigDecimal buildTotalCost(WorkInfo workInfo) {
            //return workInfo.getCena().abs().multiply(workInfo.getNh()).multiply(workInfo.getOpakovani());
            return BigDecimal.ZERO;
        }

        private BigDecimal buildTotalPrice(WorkInfo workInfo) {
            return workInfo.getCenabdph();
        }

        private BigDecimal buildTotalListPrice(WorkInfo workInfo) {
            return workInfo.getCena_jednotkova().multiply(workInfo.getOpakovani()).multiply(workInfo.getNh());
        }

    }

    public OtherInvoiceLinesBuilder.Builder newInstance() {
        return new Builder();
    }
}
