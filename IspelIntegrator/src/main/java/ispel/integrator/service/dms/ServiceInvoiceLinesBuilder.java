package ispel.integrator.service.dms;

import generated.MainOperationType;
import generated.ServiceInvoiceLine;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.WorkInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServiceInvoiceLinesBuilder {

    public class Builder {

        private List<WorkInfo> works;
        private OrderInfo orderInfo;

        private Builder() {
        }

        public Builder withWorks(List<WorkInfo> works) {
            this.works = works;
            return this;
        }

        public Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }


        public ServiceInvoiceLine[] build() {
            if (works == null) {
                throw new IllegalStateException("works is null");
            }
            if (orderInfo == null) {
                throw new IllegalStateException("orderInfo is null");
            }

            Map<Long, ServiceInvoiceLine> map = new HashMap<Long, ServiceInvoiceLine>();
            List<ServiceInvoiceLine> lines = new ArrayList<ServiceInvoiceLine>();
            for (WorkInfo workInfo : works) {
                ServiceInvoiceLine line = null;
                if (!"A".equalsIgnoreCase(workInfo.getOstatni())) {
                    line = map.get(workInfo.getPp_id());
                }
                if (line == null) {
                    line = new ServiceInvoiceLine();
                    line.setTotalPrice(BigDecimal.ZERO);
                    line.setType(buildType(workInfo));
                    line.setCode(workInfo.getPracpoz());
                    line.setDescription(workInfo.getPopis_pp());
                    line.setQuantity(buildQuantity(workInfo));
                    line.setUnitCost(workInfo.getCena());
                    line.setTotalPrice(workInfo.getCenabdph());
                    line.setUnitListPrice(workInfo.getCena_jednotkova());
                    line.setMainOperation(buildMainOperation(workInfo));
                    map.put(workInfo.getPp_id(), line);
                    lines.add(line);
                }
                line.setTotalPrice(line.getTotalPrice().add(workInfo.getCenabdph()));
            }
            return lines.toArray(new ServiceInvoiceLine[lines.size()]);
        }

        private String buildType(WorkInfo workInfo) {
           if ("PP".equalsIgnoreCase(orderInfo.getForma_uhr())) {
               return "invoice";
           } else if ("A".equalsIgnoreCase(orderInfo.getStorno())){
               return "return";
           } else {
               return "credit";
           }
        }

        private BigDecimal buildQuantity(WorkInfo workInfo) {
            return workInfo.getNh().multiply(workInfo.getOpakovani());
        }

        private MainOperationType buildMainOperation(WorkInfo workInfo) {
            if ("A".equalsIgnoreCase(workInfo.getHlavna_pp())) {
                return MainOperationType.Y;
            } else {
                return MainOperationType.N;
            }
        }
    }

    public ServiceInvoiceLinesBuilder.Builder newInstance() {
        return new Builder();
    }
}
