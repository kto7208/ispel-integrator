package ispel.integrator.service.dms;

import com.google.common.collect.Sets;
import generated.*;
import ispel.integrator.dao.dms.DmsDao;
import ispel.integrator.dao.dms.DmsSequenceService;
import ispel.integrator.domain.dms.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
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

    private boolean nissanPartsOnly;

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
    private OtherInvoiceLinesBuilder otherInvoiceLinesBuilder;

    @Autowired
    private PartsInvoiceLinesBuilder partsInvoiceLinesBuilder;

    @Autowired
    private RepairOrdersBuilder repairOrderBuilder;

    @Autowired
    private PartsStkBuilder partsStkBuilder;


    @PostConstruct
    private void postConstruct() {
        franchiseCode = dmsDao.getFranchiseCode(franchise);
        logger.debug("franchise: " + franchise);
        dmsVersion = dmsDao.getDmsVersion();
        logger.debug("dmsVersion: " + dmsVersion);
        ico = dmsDao.getIco();
        logger.debug("ico: " + ico);
        nissanPartsOnly = dmsDao.getNissanPartsOnly();
        logger.debug("nissanPartsOnly: " + nissanPartsOnly);
    }


    public DMSextract construct(String documentGroup, String documentNumber) {
        OrderInfo orderInfo = dmsDao.getOrderInfo(documentNumber, documentGroup);
        CustomerInfo customerInfo = dmsDao.getCustomerInfo(orderInfo.getCi_reg());
        EmployeeInfo employeeInfo = dmsDao.getEmployeeInfo(orderInfo.getUserName());
        VehicleInfo vehicleInfo = dmsDao.getVehicleInfo(orderInfo.getCi_auto());
        List<WorkInfo> works = dmsDao.getWorkInfoList(documentNumber, documentGroup);
        List<PartInfo> parts = dmsDao.getPartInfoList(documentNumber, documentGroup);
        List<DescriptionInfo> descriptions = dmsDao.getDescriptionInfoList(documentNumber, documentGroup);

        PartsInvoiceLine[] partsInvoiceLines = partsInvoiceLinesBuilder.newInstance()
                .withParts(parts)
                .build();

        ServiceInvoiceLine[] serviceInvoiceLines = serviceInvoiceLinesBuilder.newInstance()
                .withOrderInfo(orderInfo)
                .withWorks(works)
                .build();

        OtherInvoiceLine[] otherInvoiceLines = otherInvoiceLinesBuilder.newInstance()
                .withOrderInfo(orderInfo)
                .withWorks(works)
                .build();

        Vehicle vehicle = vehicleBuilder.newInstance()
                .withVehicleInfo(vehicleInfo)
                .withCustomerInfo(customerInfo)
                .withOrderInfo(orderInfo)
                .withPartsInvoiceLines(partsInvoiceLines)
                .withServiceInvoiceLines(serviceInvoiceLines)
                .withOtherInvoiceLines(otherInvoiceLines)
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
                .withNissanPartsOnly(nissanPartsOnly)
                .withCustomerInfo(customerInfo)
                .withEmployeeInfo(employeeInfo)
                .withOrderInfo(orderInfo)
                .withWorks(works)
                .withDescriptions(descriptions)
                .build();

        PartsStk[] partsStks = partsStkBuilder.newInstance()
                .withParts(parts)
                .withNissanPartsOnly(nissanPartsOnly)
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

    public DMSextract constructMultiple() {
        DMSextract dms = null;
        List<OrderKey> keys = dmsDao.getOrdersForMultipleProcessing();
        for (OrderKey key : keys) {
            DMSextract d = construct(String.valueOf(key.getSkupina()), String.valueOf(key.getZakazka()));
            if (dms == null) {
                dms = d;
            } else {
                addInvoice(dms, d);
                addRepairOrder(dms, d);
                addPartsStk(dms, d);
            }
            dmsDao.updateOrder(String.valueOf(key.getZakazka()), String.valueOf(key.getSkupina()));
        }
        return dms;
    }

    private void addInvoice(DMSextract dms, DMSextract invDms) {
        dms.getSite().get(0).getTransactions().getInvoice().add(
                invDms.getSite().get(0).getTransactions().getInvoice().get(0));
    }

    private void addRepairOrder(DMSextract dms, DMSextract invDms) {
        dms.getSite().get(0).getRepairOrders().getRepairOrder().addAll(
                invDms.getSite().get(0).getRepairOrders().getRepairOrder());
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
