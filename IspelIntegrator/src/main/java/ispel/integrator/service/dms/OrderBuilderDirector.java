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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Service
public class OrderBuilderDirector {

    private static final Logger logger = Logger
            .getLogger(OrderBuilderDirector.class);

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
    private VehicleBuilder vehicleBuilder;

    @Autowired
    private ServiceInvoiceLinesBuilder serviceInvoiceLinesBuilder;

    @Autowired
    private PartsInvoiceLinesBuilder partsInvoiceLinesBuilder;

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


    public DMSextract construct(String documentGroup, String documentNumber) {
        OrderInfo orderInfo = dmsDao.getOrderInfo(documentNumber, documentGroup);
        CustomerInfo customerInfo = dmsDao.getCustomerInfo(orderInfo.getCi_reg());
        EmployeeInfo employeeInfo = dmsDao.getEmployeeInfo(orderInfo.getUserName());
        VehicleInfo vehicleInfo = dmsDao.getVehicleInfo(orderInfo.getCi_auto());
        List<WorkInfo> works = dmsDao.getWorkInfoList(documentNumber, documentGroup);
        List<PartInfo> parts = dmsDao.getPartInfoList(documentNumber, documentGroup);

        PartsInvoiceLine[] partsInvoiceLines = partsInvoiceLinesBuilder.newInstance()
                .withOrderInfo(orderInfo)
                .withParts(parts)
                .build();


        ServiceInvoiceLine[] serviceInvoiceLines = serviceInvoiceLinesBuilder.newInstance()
                .withOrderInfo(orderInfo)
                .withWorks(works)
                .build();

        Vehicle vehicle = vehicleBuilder.newInstance()
                .withVehicleInfo(vehicleInfo)
                .withCustomerInfo(customerInfo)
                .withOrderInfo(orderInfo)
                .withPartsInvoiceLines(partsInvoiceLines)
                .withServiceInvoiceLines(serviceInvoiceLines)
                .build();

        Invoice invoice = invoiceBuilder.newInstance()
                .withOrderInfo(orderInfo)
                .withCustomerInfo(customerInfo)
                .withEmployeeInfo(employeeInfo)
                .withWorks(works)
                .withParts(parts)
                .withVehicle(vehicle)
                .build();

        RepairOrders repairOrders = repairOrderBuilder.newInstance()
                .withCustomerInfo(customerInfo)
                .withEmployeeInfo(employeeInfo)
                .withOrderInfo(orderInfo)
                .withWorks(works)
                .build();

        PartsStk[] partsStks = partsStkBuilder.newInstance()
                .withParts(parts)
                .build();

        return  dmsBuilder.newInstance()
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
                   .withRepairOrders(repairOrders)
                   .build();
    }

}
