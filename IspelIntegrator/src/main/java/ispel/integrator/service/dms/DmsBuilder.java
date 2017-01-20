package ispel.integrator.service.dms;

import generated.*;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Set;

@Component
public class DmsBuilder {

    public class Builder {

        private String source;
        private BigInteger dmsSequence;
        private String dmsVendor;
        private String dmsProductName;
        private String dmsVersion;
        private BigInteger siteSequence;
        private String country;
        private String currency;
        private String franchise;
        private String franchiseCode;
        private Set<Invoice> invoices;
        private RepairOrders repairOrders;
        private Set<PartsStk> partsStks;

        private Builder() {
        }

        public Builder withSource(String source) {
            this.source = source;
            return this;
        }

        public Builder withDmsSequence(BigInteger dmsSequence) {
            this.dmsSequence = dmsSequence;
            return this;
        }

        public Builder withDmsVendor(String dmsVendor) {
            this.dmsVendor = dmsVendor;
            return this;
        }

        public Builder withDmsProductName(String dmsProductName) {
            this.dmsProductName = dmsProductName;
            return this;
        }

        public Builder withDmsVersion(String dmsVersion) {
            this.dmsVersion = dmsVersion;
            return this;
        }

        public Builder withSiteSequence(BigInteger siteSequence) {
            this.siteSequence = siteSequence;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder withFranchise(String franchise) {
            this.franchise = franchise;
            return this;
        }

        public Builder withFranchiseCode(String franchiseCode) {
            this.franchiseCode = franchiseCode;
            return this;
        }

        public Builder withInvoices(Set<Invoice> invoices) {
            this.invoices = invoices;
            return this;
        }

        public Builder withRepairOrders(RepairOrders repairOrders) {
            this.repairOrders = repairOrders;
            return this;
        }

        public Builder withPartStks(Set<PartsStk> partStks) {
            this.partsStks = partStks;
            return this;
        }

        public DMSextract build() {
            DMSextract dmsExtract = new DMSextract();
            dmsExtract.setSource(source);
            dmsExtract.setSequence(dmsSequence);
            dmsExtract.setDMSVendor(dmsVendor);
            dmsExtract.setDMSProductName(dmsProductName);
            dmsExtract.setDMSVersion(dmsVersion);

            Site site = new Site();
            site.setSequence(siteSequence);
            site.setCountry(country);
            site.setCurrency(currency);
            site.setFranchise(franchise);
            site.setFranchiseCode(franchiseCode);

            Transactions transactions = new Transactions();
            transactions.getInvoice().addAll(invoices);
            site.setTransactions(transactions);
            site.setRepairOrders(repairOrders);
            site.getPartsStk().addAll(partsStks);

            dmsExtract.getSite().add(site);
            return dmsExtract;
        }
    }

    public Builder newInstance() {
        return this.new Builder();
    }
}
