package ispel.integrator.service.dms;

import generated.Customer;
import generated.Invoice;
import generated.InvoiceSummary;
import generated.Vehicle;
import ispel.integrator.domain.dms.CustomerInfo;
import ispel.integrator.domain.dms.EmployeeInfo;
import ispel.integrator.domain.dms.SlipInfo;
import ispel.integrator.domain.dms.SlipPartInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SlipInvoiceBuilder {

    private class Builder {

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
                // todo
                //customer.setIdentityNotKnown();
            }
            invoice.setCustomer(customer);

            invoice.setEmployee(buildEmployeeName());
            invoice.setOrderRef(buildOrderRef());
            invoice.getVehicle().add(this.vehicle);

            BigDecimal subtotalLabourBody = buildSubtotalLabourBody();
            BigDecimal subtotalLabourPaint = buildSubtotalLabourPaint();
            BigDecimal subtotalLabourBodyPaint = subtotalLabourBody.add(subtotalLabourPaint);
            BigDecimal subtotalLabourWkshp = buildSubtotalLabourWkshp();
            BigDecimal subtotalPaintMaterial = buildSubtotalPaintMaterial();
            BigDecimal subtotalParts = buildSubtotalParts();
            BigDecimal subtotalSubcontract = buildSubtotalSubcontract();

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
                    .append(slipInfo.getVfpd())
                    .append("-")
                    .append(slipInfo.getSkupvfpd())
                    .append("-")
                    .append(slipInfo.getSklad())
                    .toString();
        }

        private String buildType() {
            if ("VYD_REZIE".equalsIgnoreCase(slipInfo.getDoklad())) {
                return "PartsInternal";
            } else if ("VF".equalsIgnoreCase(slipInfo.getDoklad_typ())) {
                return "PartsAccount";
            } else if ("PD".equalsIgnoreCase(slipInfo.getDoklad_typ())) {
                return "PartsCash";
            } else {
                return "PartsOther";
            }
        }
    }

    public SlipInvoiceBuilder.Builder newInstance() {
        return new SlipInvoiceBuilder.Builder();
    }
}
