package ispel.integrator.service.dms;

import generated.*;
import ispel.integrator.domain.dms.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InvoiceBuilder {

    public Builder newInstance() {
        return new Builder();
    }

    public class Builder {

        private Vehicle vehicle;
        private OrderInfo orderInfo;
        private CustomerInfo customerInfo;
        private EmployeeInfo employeeInfo;
        private List<WorkInfo> works;
        private List<PartInfo> parts;

        private Builder() {
        }

        public Builder withVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
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

        public Builder withWorks(List<WorkInfo> works) {
            this.works = works;
            return this;
        }

        public Builder withParts(List<PartInfo> parts) {
            this.parts = parts;
            return this;
        }

        public Invoice build() {
            Invoice invoice = new Invoice();
            invoice.setId(buildId());
            invoice.setDate(orderInfo.getKdyUzavDoklad());
            invoice.setType(buildType());

            Customer customer = new Customer();
            customer.setAccount(this.customerInfo.getCi_reg());
            customer.setType(this.customerInfo.getTyp());
            customer.setVATNumber(this.customerInfo.getIcdph());
            customer.setName(buildName());
            customer.setAddress(buildCustomerAddress());
            customer.setPostcode(this.customerInfo.getPsc());
            customer.setUseForMarketing(buildUseForMarketing());

            invoice.setEmployee(buildEmployeeName());
            invoice.setOrderRef(buildOrderRef());

            invoice.setCustomer(customer);
            invoice.getVehicle().add(this.vehicle);

            BigDecimal subtotalLabourBody = buildSubtotalLabourBody();
            BigDecimal subtotalLabourPaint = buildSubtotalLabourPaint();
            BigDecimal subtotalLabourBodyPaint = subtotalLabourBody.add(subtotalLabourPaint);
            BigDecimal subtotalLabourWkshp = buildSubtotalLabourWkshp();
            BigDecimal subtotalPaintMaterial = buildSubtotalPaintMaterial();
            BigDecimal subtotalParts = buildSubtotalParts();
            BigDecimal subtotalSubcontract = BigDecimal.ZERO;

            InvoiceSummary invoiceSummary = new InvoiceSummary();
            invoiceSummary.setInvoiceTotal(orderInfo.getCelkem_sm());
            invoiceSummary.setSubtotalLabourBody(subtotalLabourBody);
            invoiceSummary.setSubtotalLabourPaint(subtotalLabourPaint);
            invoiceSummary.setSubtotalLabourBodyPaint(null);
            invoiceSummary.setSubtotalLabourWkshp(subtotalLabourWkshp);
            invoiceSummary.setSubtotalPaintMaterial(subtotalPaintMaterial);
            invoiceSummary.setSubtotalParts(subtotalParts);
            invoiceSummary.setSubtotalSubcontract(subtotalSubcontract);
            invoice.setInvoiceSummary(invoiceSummary);
            return invoice;
        }

        private String buildId() {
            return new StringBuilder()
                    .append(orderInfo.getVfpd())
                    .append("-")
                    .append(orderInfo.getSkupVfpd())
                    .append("-")
                    .append(orderInfo.getDocumentGroup())
                    .toString();
        }

        private String buildType() {
            if (isInternal() && isBodyshop()) {
                return "BodyshopInternal";
            } else if (isInternal() && !isBodyshop()) {
                return "WorkshopInternal";
            } else if (isWarranty() && isBodyshop()) {
                return "BodyshopWarranty";
            } else if (isWarranty() && !isBodyshop()) {
                return "WorkshopWarranty";
            } else if (isVF() && isBodyshop()) {
                return "BodyshopAccount";
            } else if (isVF() && !isBodyshop()) {
                return "WorkshopAccount";
            } else if (isPD() && isBodyshop()) {
                return "BodyshopCash";
            } else if (isPD() && !isBodyshop()) {
                return "WorkshopCash";
            } else {
                throw new IllegalStateException("wrong invoice type");
            }
        }

        private boolean isInternal() {
            return ("VF".equalsIgnoreCase(orderInfo.getTypD())
                    && orderInfo.getOznaceni_svf().toUpperCase().contains("INTERN")) ||
                    ("PD".equalsIgnoreCase(orderInfo.getTypD())
                            && orderInfo.getOznaceni_spd().toUpperCase().contains("INTERN"));
        }

        private boolean isBodyshop() {
            for (WorkInfo workInfo : works) {
                if (("KAR").equalsIgnoreCase(workInfo.getDruh_pp())) {
                    return true;
                } else if (("LAK").equalsIgnoreCase(workInfo.getDruh_pp())) {
                    return true;
                }
            }
            return false;
        }

        private boolean isWarranty() {
            if (orderInfo.getReklam_c() != null && Integer.valueOf(orderInfo.getReklam_c()) > 0) {
                return true;
            } else {
                return false;
            }
        }

        private boolean isVF() {
            return "VF".equalsIgnoreCase(orderInfo.getTypD());
        }

        private boolean isPD() {
            return "PD".equalsIgnoreCase(orderInfo.getTypD());
        }

        private String buildName() {
            if (customerInfo == null) {
                throw new IllegalStateException("customerInfo is null");
            }
            if (customerInfo.getOrganizace() != null &&
                    customerInfo.getOrganizace().length() > 0) {
                return customerInfo.getOrganizace();
            }
            return new StringBuilder()
                    .append(customerInfo.getPrijmeni())
                    .append(" ")
                    .append(customerInfo.getJmeno())
                    .toString();
        }

        private Address buildCustomerAddress() {
            Address address = new Address();
            address.getAddressLine().add(this.customerInfo.getUlice());
            address.getAddressLine().add(this.customerInfo.getMesto());
            if (this.customerInfo.getStat1() != null && this.customerInfo.getStat1().length() > 0) {
                address.getAddressLine().add(this.customerInfo.getStat1());
            }
            return address;
        }

        private MarketingInformationType buildUseForMarketing() {
            if ("1".equalsIgnoreCase(this.customerInfo.getEmail_souhlas()) ||
                    "1".equalsIgnoreCase(this.customerInfo.getSms_souhlas())) {
                return MarketingInformationType.Y;
            } else {
                return MarketingInformationType.N;
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

        private String buildOrderRef() {
            return new StringBuilder()
                    .append(this.orderInfo.getDocumentNumber())
                    .append("-")
                    .append(this.orderInfo.getDocumentGroup())
                    .toString();
        }

        private BigDecimal buildSubtotalLabourBody() {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (WorkInfo work : works) {
                if ("KAR".equalsIgnoreCase(work.getDruh_pp())) {
                    subtotal = subtotal.add(work.getCenabdph());
                }
            }
            return subtotal;
        }

        private BigDecimal buildSubtotalLabourPaint() {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (WorkInfo work : works) {
                if ("LAK".equalsIgnoreCase(work.getDruh_pp())) {
                    subtotal = subtotal.add(work.getCenabdph());
                }
            }
            return subtotal;
        }

        private BigDecimal buildSubtotalLabourWkshp() {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (WorkInfo work : works) {
                if (!"LAK".equalsIgnoreCase(work.getDruh_pp()) &&
                        !"KAR".equalsIgnoreCase(work.getDruh_pp())) {
                    subtotal = subtotal.add(work.getCenabdph());
                }
            }
            return subtotal;
        }

        private BigDecimal buildSubtotalPaintMaterial() {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (PartInfo part : parts) {
                if ("LAK".equalsIgnoreCase(part.getDruh_tovaru()) ||
                        "LKD".equalsIgnoreCase(part.getDruh_tovaru())) {
                    subtotal = subtotal.add(part.getCena_bdp());
                }
            }
            return subtotal;
        }

        private BigDecimal buildSubtotalParts() {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (PartInfo part : parts) {
                if (!"LAK".equalsIgnoreCase(part.getDruh_tovaru()) &&
                        !"LKD".equalsIgnoreCase(part.getDruh_tovaru())) {
                    subtotal = subtotal.add(part.getCena_bdp());
                }
            }
            return subtotal;
        }
    }
}
