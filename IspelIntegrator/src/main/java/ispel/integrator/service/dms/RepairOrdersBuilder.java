package ispel.integrator.service.dms;

import generated.LabourRecord;
import generated.RepairOrder;
import generated.RepairOrders;
import ispel.integrator.domain.dms.CustomerInfo;
import ispel.integrator.domain.dms.EmployeeInfo;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.WorkInfo;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepairOrdersBuilder {

    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyymmdd");
        }
    };

    public class Builder {

        private CustomerInfo customerInfo;
        private OrderInfo orderInfo;
        private EmployeeInfo employeeInfo;
        private List<WorkInfo> works;

        private Builder() {}

        public Builder withCustomerInfo(CustomerInfo customerInfo) {
            this.customerInfo = customerInfo;
            return this;
        }

        public Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public Builder withEmployeeInfo(EmployeeInfo employeeInfo) {
            this.employeeInfo = employeeInfo;
            return this;
        }

        public Builder withWorks(List<WorkInfo> works) {
            this.works = works;
            return this;
        }

        public RepairOrders build() {
            if (orderInfo == null) {
                throw new IllegalStateException("orderInfo is null");
            }
            if (employeeInfo == null) {
                throw new IllegalStateException("employeeInfo is null");
            }
            if (works == null) {
                throw new IllegalStateException("works is null");
            }
            if (customerInfo == null) {
                throw new IllegalStateException("customerInfo is null");
            }

            RepairOrders repairOrders = new RepairOrders();
            Map<Long, RepairOrder> map = new HashMap<Long,RepairOrder>();
            for (WorkInfo workInfo : works) {
                RepairOrder repairOrder = map.get(workInfo.getPp_id());
                if (repairOrder == null) {
                    repairOrder = new RepairOrder();
                    repairOrder.setId(buildId());
                    repairOrder.setStartDate(buildStartDate());
                    repairOrder.setEndDate(orderInfo.getKdyUzavDoklad());
                    repairOrder.setEmployee(buildEmployee(workInfo));
                    LabourRecord labourRecord = new LabourRecord();
                    labourRecord.setType(buildType(workInfo));
                    labourRecord.setIsMechanical(buildMechanical(workInfo));
                    labourRecord.setIsBodyshop(buildBodyshop(workInfo));
                    labourRecord.setIsElectrical(buildElectrical(workInfo));
                    labourRecord.setCode(workInfo.getPracpoz());
                    labourRecord.setQuantity(workInfo.getNh());
                    repairOrder.getLabourRecord().add(labourRecord);
                    repairOrder.setDescription(workInfo.getPopis_pp());
                    map.put(workInfo.getPp_id(), repairOrder);
                }
            }
            repairOrders.getRepairOrder().addAll(map.values());
            return repairOrders;
        }

        private String buildId() {
            return new StringBuilder()
                    .append(orderInfo.getDocumentNumber())
                    .append("-")
                    .append(orderInfo.getDocumentGroup())
                    .toString();
        }

        private Date buildStartDate() {
            try {
                return dateFormat.get().parse(orderInfo.getDatum());
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }
        }

        private String buildEmployee(WorkInfo workInfo) {
            WorkInfo selectedWork = workInfo;
            for (WorkInfo work : works) {
                if (selectedWork.getPp_id().equals(work.getPp_id()) &&
                        work.getProcento() > selectedWork.getProcento()) {
                    selectedWork = work;
                }
            }
            return new StringBuilder()
                    .append(selectedWork.getPrijmeni())
                    .append(" ")
                    .append(selectedWork.getJmeno())
                    .toString();
        }

        private String buildType(WorkInfo workInfo) {
            if (orderInfo.getReklam_c() != null && orderInfo.getReklam_c().length() > 0) {
                return "W";
            } else if (workInfo.getPopis_pp() != null &&
                        (workInfo.getPopis_pp().toLowerCase().contains("prehl") ||
                                workInfo.getPopis_pp().toLowerCase().contains("prohl"))) {
                return "S";
            } else {
                return "R";
            }
        }

        private boolean buildMechanical(WorkInfo workInfo) {
            if ("MCH".equalsIgnoreCase(workInfo.getDruh_pp())) {
                return true;
            } else {
                return false;
            }
        }

        private boolean buildBodyshop(WorkInfo workInfo) {
            if ("KAR".equalsIgnoreCase(workInfo.getDruh_pp()) ||
                    "LAK".equalsIgnoreCase(workInfo.getDruh_pp())) {
                return true;
            } else {
                return false;
            }
        }

        private boolean buildElectrical(WorkInfo workInfo) {
            if ("ELE".equalsIgnoreCase(workInfo.getDruh_pp())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Builder newInstance() {
        return new Builder();
    }
}
