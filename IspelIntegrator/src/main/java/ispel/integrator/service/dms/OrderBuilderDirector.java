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
import java.math.BigInteger;
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


    public DMSextract construct(String documentGroup, String documentNumber, BigInteger siteSequence, BigInteger sourceSequence) {
        OrderInfo orderInfo = dmsDao.getOrderInfo(documentNumber, documentGroup);
        CustomerInfo customerInfo = dmsDao.getCustomerInfo(orderInfo.getCi_reg());
        EmployeeInfo employeeInfo = dmsDao.getEmployeeInfo(orderInfo.getUserName());
        List<WorkInfo> works = dmsDao.getWorkInfoList(documentNumber, documentGroup);
        List<PartInfo> parts = dmsDao.getPartInfoList(documentNumber, documentGroup);
        List<DescriptionInfo> descriptions = dmsDao.getDescriptionInfoList(documentNumber, documentGroup);
        VehicleInfo vehicleInfo = null;
        if (!"0".equals(orderInfo.getCi_auto())) {
            vehicleInfo = dmsDao.getVehicleInfo(orderInfo.getCi_auto());
        }

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
                   .withRepairOrders(repairOrders)
                   .build();
    }

    public DMSextract constructMultiple(List<OrderKey> keys) {
        DMSextract dms = null;
        if (keys != null && keys.size() > 0) {
            BigInteger siteSequence = this.dmsSequenceService.getDmsSiteSequenceNextVal();
            BigInteger sourceSequence = this.dmsSequenceService.getDmsSourceSequenceNextVal();
            for (OrderKey key : keys) {
                DMSextract d = construct(String.valueOf(key.getSkupina()),
                        String.valueOf(key.getZakazka()),
                        siteSequence, sourceSequence);
                if (dms == null) {
                    dms = d;
                } else {
                    addInvoice(dms, d);
                    addRepairOrder(dms, d);
                    addPartsStk(dms, d);
                }
            }
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
            PtStk p1 = findPtStk(p, ptStk);
            if (p1 != null) {
                p1.setQtyOnOrder(p1.getQtyOnOrder().add(ptStk.getQtyOnOrder()));
            } else {
                p.getPtStk().add(ptStk);
            }
        }
    }

    private PtStk findPtStk(PartsStk partsStk, PtStk ptStk) {
        for (PtStk p : partsStk.getPtStk()) {
            if (p.getNum().equals(ptStk.getNum())) {
                return p;
            }
        }
        return null;
    }
}
