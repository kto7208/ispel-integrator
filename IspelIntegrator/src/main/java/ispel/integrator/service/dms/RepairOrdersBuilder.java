package ispel.integrator.service.dms;

import generated.LabourRecord;
import generated.RepairOrder;
import generated.RepairOrders;
import ispel.integrator.domain.dms.*;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RepairOrdersBuilder {

    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    public class Builder {

        private CustomerInfo customerInfo;
        private OrderInfo orderInfo;
        private EmployeeInfo employeeInfo;
        private List<WorkInfo> works;
        private List<DescriptionInfo> descriptions;
        private boolean nissanPartsOnly;

        private Builder() {}

        public Builder withNissanPartsOnly(boolean nissanPartsOnly) {
            this.nissanPartsOnly = nissanPartsOnly;
            return this;
        }

        public Builder withCustomerInfo(CustomerInfo customerInfo) {
            this.customerInfo = customerInfo;
            return this;
        }

        public Builder withEmployeeInfo(EmployeeInfo employeeInfo) {
            this.employeeInfo = employeeInfo;
            return this;
        }

        public Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public Builder withWorks(List<WorkInfo> works) {
            this.works = works;
            return this;
        }

        public Builder withDescriptions(List<DescriptionInfo> descriptions) {
            this.descriptions = descriptions;
            return this;
        }

        public RepairOrders build() {
            if (orderInfo == null) {
                throw new IllegalStateException("orderInfo is null");
            }
            if (works == null) {
                throw new IllegalStateException("works is null");
            }
            if (customerInfo == null) {
                throw new IllegalStateException("customerInfo is null");
            }
            if (employeeInfo == null) {
                throw new IllegalStateException("employeeInfo is null");
            }

            RepairOrders repairOrders = new RepairOrders();
            RepairOrder repairOrder = new RepairOrder();
            repairOrder.setId(buildId());
            repairOrder.setStartDate(buildStartDate());
            repairOrder.setEndDate(orderInfo.getKdyUzavDoklad());
            repairOrder.setEmployee(buildEmployeeName());

            Map<Long, LabourRecord> map = new HashMap<Long, LabourRecord>();
            List<LabourRecord> labourRecords = new ArrayList<LabourRecord>();
            for (WorkInfo workInfo : works) {
                if (nissanPartsOnly && workInfo.isOther()) {
                    continue;
                }
                LabourRecord lr = null;
                if (!"A".equalsIgnoreCase(workInfo.getOstatni())) {
                    lr = map.get(workInfo.getPp_id());
                }
                if (lr == null) {
                    lr = new LabourRecord();
                    lr.setType(buildType(workInfo));
                    lr.setIsMechanical(buildMechanical(workInfo));
                    lr.setIsBodyshop(buildBodyshop(workInfo));
                    lr.setIsElectrical(buildElectrical(workInfo));
                    lr.setCode(workInfo.getPracpoz());
                    lr.setQuantity(workInfo.getNh().multiply(workInfo.getOpakovani()));
                    map.put(workInfo.getPp_id(), lr);
                    labourRecords.add(lr);
                }
            }
            repairOrder.getLabourRecord().addAll(labourRecords);
            repairOrder.setDescription(buildDescription());
            repairOrders.getRepairOrder().add(repairOrder);
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

        private String buildEmployeeName() {
            if (employeeInfo != null) {
                return new StringBuilder()
                        .append(this.employeeInfo.getPrijmeni())
                        .append(" ")
                        .append(this.employeeInfo.getJmeno())
                        .toString();
            } else {
                return "";
            }
        }

        private String buildType(WorkInfo workInfo) {
            if (orderInfo.getReklam_c() != null && orderInfo.getReklam_c().length() > 0
                    && Integer.valueOf(orderInfo.getReklam_c()) > 0) {

                if ("G".equalsIgnoreCase(workInfo.getSaga1())) {
                    return "P";
                } else {
                    return "W";
                }

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
                    "LAK".equalsIgnoreCase(workInfo.getDruh_pp()) ||
                    "KLA".equalsIgnoreCase(workInfo.getDruh_pp())) {
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

        private String buildDescription() {
            StringBuilder stringBuilder = new StringBuilder();
            int len = descriptions.size();
            int i = 0;
            for (DescriptionInfo descriptionInfo : descriptions) {
                stringBuilder.append(descriptionInfo.getPopis().trim());
                if (i < len - 1) {
                    stringBuilder.append(", ");
                }
                i++;
            }
            return stringBuilder.toString();
        }
    }

    public Builder newInstance() {
        return new Builder();
    }
}
