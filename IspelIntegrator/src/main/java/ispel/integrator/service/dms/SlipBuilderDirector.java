package ispel.integrator.service.dms;

import com.google.common.collect.Sets;
import generated.*;
import ispel.integrator.dao.dms.DmsDao;
import ispel.integrator.dao.dms.DmsSequenceService;
import ispel.integrator.domain.dms.CustomerInfo;
import ispel.integrator.domain.dms.EmployeeInfo;
import ispel.integrator.domain.dms.SlipInfo;
import ispel.integrator.domain.dms.SlipPartInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

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

    public DMSextract construct(String sklad, String ci_dok) {
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
                .withDmsSequence(this.dmsSequenceService.getDmsSourceSequenceNextVal())
                .withDmsVendor(this.vendor)
                .withDmsProductName(this.productName)
                .withDmsVersion(this.dmsVersion)
                .withSiteSequence(this.dmsSequenceService.getDmsSiteSequenceNextVal())
                .withCountry(this.country)
                .withCurrency(this.currency)
                .withFranchise(this.franchise)
                .withFranchiseCode(this.franchiseCode)
                .withInvoices(Sets.<Invoice>newHashSet(invoice))
                .withPartStks(Sets.<PartsStk>newHashSet(partsStks))
                .build();
    }

    public DMSextract constructMultiple() {
        return null;
    }
}
