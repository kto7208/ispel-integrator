package ispel.integrator.service.dms;

import generated.*;
import ispel.integrator.domain.dms.CustomerInfo;
import ispel.integrator.domain.dms.EmployeeInfo;
import ispel.integrator.domain.dms.SlipInfo;
import ispel.integrator.domain.dms.SlipPartInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SlipInvoiceBuilder {

    public class Builder {

        private Vehicle vehicle;
        private SlipInfo slipInfo;
        private CustomerInfo customerInfo;
        private EmployeeInfo employeeInfo;
        private List<SlipPartInfo> parts;

        private Builder() {
        }

        public SlipInvoiceBuilder.Builder withVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public SlipInvoiceBuilder.Builder withSlipInfo(SlipInfo slipInfo) {
            this.slipInfo = slipInfo;
            return this;
        }

        public SlipInvoiceBuilder.Builder withCustomerInfo(CustomerInfo customerInfo) {
            this.customerInfo = customerInfo;
            return this;
        }

        public SlipInvoiceBuilder.Builder withEmployeeInfo(EmployeeInfo employeeInfo) {
            this.employeeInfo = employeeInfo;
            return this;
        }

        public SlipInvoiceBuilder.Builder withParts(List<SlipPartInfo> parts) {
            this.parts = parts;
            return this;
        }

        public Invoice build() {
            Invoice invoice = new Invoice();
            invoice.setId(buildId());
            invoice.setDate(slipInfo.getDtuzavreni());
            invoice.setType(buildType());

            Customer customer = new Customer();
            if (customerInfo != null) {
                customer.setAccount(this.customerInfo.getCi_reg());
                customer.setType(buildCustomerType());
                customer.setVATNumber(this.customerInfo.getIcdph());
                customer.setName(buildName());
                customer.setAddress(buildCustomerAddress());
                customer.setPostcode(this.customerInfo.getPsc());
                customer.setUseForMarketing(buildUseForMarketing());
            } else {
                customer.setIdentityNotKnown("");
            }
            invoice.setCustomer(customer);
            invoice.setEmployee(buildEmployeeName());
            invoice.setOrderRef(buildOrderRef());
            invoice.getVehicle().add(this.vehicle);

            InvoiceSummary invoiceSummary = new InvoiceSummary();
            invoiceSummary.setInvoiceTotal(buildInvoiceTotal());
            invoiceSummary.setSubtotalLabourBody(BigDecimal.ZERO);
            invoiceSummary.setSubtotalLabourPaint(BigDecimal.ZERO);
            invoiceSummary.setSubtotalLabourBodyPaint(BigDecimal.ZERO);
            invoiceSummary.setSubtotalLabourWkshp(BigDecimal.ZERO);
            invoiceSummary.setSubtotalPaintMaterial(buildSubtotalPaintMaterial());
            invoiceSummary.setSubtotalParts(buildSubtotalParts());
            invoiceSummary.setSubtotalSubcontract(BigDecimal.ZERO);
            invoice.setInvoiceSummary(invoiceSummary);
            return invoice;
        }

        private String buildId() {
            return new StringBuilder()
                    .append(slipInfo.getVfpd())
                    .append("-")
                    .append(slipInfo.getSkupvfpd())
                    .append("-")
                    .append(slipInfo.getSklad())
                    .toString();
        }

        private String buildType() {
            if ("VYD-REZIE".equalsIgnoreCase(slipInfo.getDoklad())) {
                return "PartsInternal";
            } else if ("VF".equalsIgnoreCase(slipInfo.getDoklad_typ())) {
                return "PartsAccount";
            } else if ("PD".equalsIgnoreCase(slipInfo.getDoklad_typ())) {
                return "PartsCash";
            } else {
                return "PartsOther";
            }
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
                    "1".equalsIgnoreCase(this.customerInfo.getSms_souhlas()) ||
                    "1".equalsIgnoreCase(customerInfo.getSouhlas())) {
                return MarketingInformationType.Y;
            } else {
                return MarketingInformationType.N;
            }
        }

        private String buildCustomerType() {
            if (Integer.valueOf("0").equals(customerInfo.getForma())) {
                return "physical person";
            } else if (Integer.valueOf("1").equals(customerInfo.getForma())) {
                return "legal entity";
            } else if (Integer.valueOf("2").equals(customerInfo.getForma())) {
                return "self-employed person";
            } else {
                throw new IllegalStateException("wrong customer type");
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
                    .append(this.slipInfo.getCidok())
                    .append("-")
                    .append(this.slipInfo.getSklad())
                    .toString();
        }

        private BigDecimal buildInvoiceTotal() {
            BigDecimal invoiceTotal = BigDecimal.ZERO;
            for (SlipPartInfo slipPartInfo : parts) {
                invoiceTotal = invoiceTotal.add(slipPartInfo.getCelkem_pro());
            }
            return invoiceTotal;
        }

        private BigDecimal buildSubtotalPaintMaterial() {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (SlipPartInfo part : parts) {
                if ("LAK".equalsIgnoreCase(part.getDruh_tovaru()) ||
                        "LKD".equalsIgnoreCase(part.getDruh_tovaru())) {
                    subtotal = subtotal.add(part.getCelkem_pro());
                }
            }
            return subtotal;
        }

        private BigDecimal buildSubtotalParts() {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (SlipPartInfo part : parts) {
                if (!"LAK".equalsIgnoreCase(part.getDruh_tovaru()) &&
                        !"LKD".equalsIgnoreCase(part.getDruh_tovaru())) {
                    subtotal = subtotal.add(part.getCelkem_pro());
                }
            }
            return subtotal;
        }
    }

    public SlipInvoiceBuilder.Builder newInstance() {
        return new SlipInvoiceBuilder.Builder();
    }
}
