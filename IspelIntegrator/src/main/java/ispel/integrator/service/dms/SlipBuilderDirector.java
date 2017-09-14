package ispel.integrator.service.dms;

import com.google.common.collect.Sets;
import generated.*;
import ispel.integrator.dao.dms.DmsDao;
import ispel.integrator.dao.dms.DmsSequenceService;
import ispel.integrator.domain.dms.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SlipBuilderDirector {

    private static final Logger logger = Logger
            .getLogger(SlipBuilderDirector.class);

    @Autowired
    private DmsDao dmsDao;

    @Value("${ispel.dms.franchise}")
    private String franchise;

    @Value("${ispel.dms.vendor}")
    private String vendor;

    @Value("${ispel.dms.product.name}")
    private String productName;

    @Value("${ispel.dms.country}")
    private String country;

    @Value("${ispel.dms.currency}")
    private String currency;

    private String franchiseCode;

    private String dmsVersion;

    private String ico;

    @Autowired
    private DmsSequenceService dmsSequenceService;

    @Autowired
    private DmsBuilder dmsBuilder;

    @Autowired
    private SlipInvoiceBuilder invoiceBuilder;

    @Autowired
    private SlipVehicleBuilder vehicleBuilder;

    @Autowired
    private SlipPartsInvoiceLinesBuilder partsInvoiceLinesBuilder;

    @Autowired
    private SlipPartsStkBuilder partsStkBuilder;

    @PostConstruct
    private void postConstruct() {
        franchiseCode = dmsDao.getFranchiseCode(franchise);
        logger.debug("franchise: " + franchise);
        dmsVersion = dmsDao.getDmsVersion();
        logger.debug("dmsVersion: " + dmsVersion);
        ico = dmsDao.getIco();
        logger.debug("ico: " + ico);
    }

    public DMSextract construct(String sklad, String ci_dok, BigInteger siteSequence, BigInteger sourceSequence) {
        SlipInfo slipInfo = dmsDao.getSlipInfo(ci_dok, sklad);
        CustomerInfo customerInfo = dmsDao.getCustomerInfo(slipInfo.getCi_reg());
        EmployeeInfo employeeInfo = dmsDao.getEmployeeInfo(slipInfo.getUser_name());
        List<SlipPartInfo> parts = dmsDao.getSlipPartInfoList(ci_dok, sklad);

        PartsInvoiceLine[] partsInvoiceLines = partsInvoiceLinesBuilder.newInstance()
                .withParts(parts)
                .build();

        Vehicle vehicle = vehicleBuilder.newInstance()
                .withPartsInvoiceLines(partsInvoiceLines)
                .build();

        Invoice invoice = invoiceBuilder.newInstance()
                .withSlipInfo(slipInfo)
                .withCustomerInfo(customerInfo)
                .withEmployeeInfo(employeeInfo)
                .withParts(parts)
                .withVehicle(vehicle)
                .build();

        PartsStk[] partsStks = partsStkBuilder.newInstance()
                .withParts(parts)
                .build();

        return dmsBuilder.newInstance()
                .withSource(this.ico)
                .withDmsSequence(sourceSequence == null ?
                        this.dmsSequenceService.getDmsSourceSequenceNextVal() : sourceSequence)
                .withDmsVendor(this.vendor)
                .withDmsProductName(this.productName)
                .withDmsVersion(this.dmsVersion)
                .withSiteSequence(siteSequence == null ?
                        this.dmsSequenceService.getDmsSiteSequenceNextVal() : siteSequence)
                .withCountry(this.country)
                .withCurrency(this.currency)
                .withFranchise(this.franchise)
                .withFranchiseCode(this.franchiseCode)
                .withInvoices(Sets.<Invoice>newHashSet(invoice))
                .withPartStks(Sets.<PartsStk>newHashSet(partsStks))
                .build();
    }

    public DMSextract constructMultiple(List<OrderKey> keys) {
        DMSextract dms = null;
        BigInteger siteSequence = this.dmsSequenceService.getDmsSiteSequenceNextVal();
        BigInteger sourceSequence = this.dmsSequenceService.getDmsSourceSequenceNextVal();
        for (OrderKey key : keys) {
            DMSextract d = construct(String.valueOf(key.getSklad()), String.valueOf(key.getZakazka()),
                    siteSequence, sourceSequence);
            if (dms == null) {
                dms = d;
            } else {
                addInvoice(dms, d);
                addPartsStk(dms, d);
            }
        }
        return dms;
    }

    private void addInvoice(DMSextract dms, DMSextract invDms) {
        dms.getSite().get(0).getTransactions().getInvoice().add(
                invDms.getSite().get(0).getTransactions().getInvoice().get(0));
    }

    private void addPartsStk(DMSextract dms, DMSextract invDms) {
        Set<String> parts = new HashSet<String>();
        for (PartsStk partsStk : invDms.getSite().get(0).getPartsStk()) {
            PartsStk p = getPartsStk(dms, partsStk);
            if (p == null) {
                dms.getSite().get(0).getPartsStk().add(partsStk);
            } else {
                addPtStk(p, partsStk);
            }
        }
    }

    private PartsStk getPartsStk(DMSextract dms, PartsStk partsStk) {
        for (PartsStk p : dms.getSite().get(0).getPartsStk()) {
            if (p.getWarehouse().equals(partsStk.getWarehouse())) {
                return p;
            }
        }
        return null;
    }

    private void addPtStk(PartsStk p, PartsStk partsStk) {
        for (PtStk ptStk : partsStk.getPtStk()) {
            if (!hasPtStk(p, ptStk)) {
                p.getPtStk().add(ptStk);
            }
        }
    }

    private boolean hasPtStk(PartsStk partsStk, PtStk ptStk) {
        for (PtStk p : partsStk.getPtStk()) {
            if (p.getNum().equals(ptStk.getNum())) {
                return true;
            }
        }
        return false;
    }

}
