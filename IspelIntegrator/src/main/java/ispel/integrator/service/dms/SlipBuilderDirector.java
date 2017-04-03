package ispel.integrator.service.dms;

import generated.DMSextract;
import generated.PartsInvoiceLine;
import generated.Vehicle;
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
    private InvoiceBuilder invoiceBuilder;

    @Autowired
    private SlipVehicleBuilder vehicleBuilder;

    @Autowired
    private ServiceInvoiceLinesBuilder serviceInvoiceLinesBuilder;

    @Autowired
    private OtherInvoiceLinesBuilder otherInvoiceLinesBuilder;

    @Autowired
    private SlipPartsInvoiceLinesBuilder partsInvoiceLinesBuilder;

    @Autowired
    private RepairOrdersBuilder repairOrderBuilder;

    @Autowired
    private PartsStkBuilder partsStkBuilder;

    @PostConstruct
    private void postConstruct() {
        franchiseCode = dmsDao.getGetFranchiseCode(franchise);
        logger.debug("franchise: " + franchise);
        dmsVersion = dmsDao.getGetDmsVersion();
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


//        return  dmsBuilder.newInstance()
//                .withSource(this.ico)
//                .withDmsSequence(this.dmsSequenceService.getDmsSourceSequenceNextVal())
//                .withDmsVendor(this.vendor)
//                .withDmsProductName(this.productName)
//                .withDmsVersion(this.dmsVersion)
//                .withSiteSequence(this.dmsSequenceService.getDmsSiteSequenceNextVal())
//                .withCountry(this.country)
//                .withCurrency(this.currency)
//                .withFranchise(this.franchise)
//                .withFranchiseCode(this.franchiseCode)
//                .withInvoices(Sets.<Invoice>newHashSet(invoice))
//                .withPartStks(Sets.<PartsStk>newHashSet(partsStks))
//                .withRepairOrders(repairOrders)
//                .build();
        return null;
    }
}
