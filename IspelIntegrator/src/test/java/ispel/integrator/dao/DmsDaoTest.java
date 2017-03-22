package ispel.integrator.dao;

import ispel.integrator.dao.dms.DmsDao;
import ispel.integrator.domain.dms.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@ContextConfiguration(locations = {"classpath:/test_config/system-test-config.xml"})
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class DmsDaoTest {

    @Autowired
    private DmsDao dmsDao;

    @Test
    @Commit
    public void testSourceSequence() {
        BigInteger sourceSequence = dmsDao.getDmsSourceSequenceNextVal();
        System.out.println("sourceSequence: " + sourceSequence);
    }

    @Test
    @Commit
    public void testSiteSequence() {
        BigInteger siteSequence = dmsDao.getDmsSiteSequenceNextVal();
        System.out.println("siteSequence: " + siteSequence);
    }

    @Test
    @Rollback
    public void testGetOrderInfo() {
        OrderInfo orderInfo = dmsDao.getOrderInfo("12140001", "17");
    }

    @Test
    @Rollback
    public void testGetCustomerInfo() {
        CustomerInfo customerInfo = dmsDao.getCustomerInfo("5877");
    }

    @Test
    @Rollback
    public void testGetEmployeeInfo() {
        EmployeeInfo employeeInfo = dmsDao.getEmployeeInfo("felsocx");
    }

    @Test
    @Rollback
    public void testGetVehicleInfo() {
        VehicleInfo vehicleInfo = dmsDao.getVehicleInfo("5868");
    }

    @Test
    @Rollback
    public void testGetWorkInfoList() {
        List<WorkInfo> list = dmsDao.getWorkInfoList("12140001", "17");
    }

    @Test
    @Rollback
    public void testGetPartInfoList() {
        List<PartInfo> list = dmsDao.getPartInfoList("12140001", "17");
    }

    @Test
    @Commit
    public void testGetPartInfoList2() {
        List<PartInfo> list = dmsDao.getPartInfoList("10140148", "15");
    }

    @Test
    @Commit
    public void testGetDescriptionInfoList() {
        List<DescriptionInfo> list = dmsDao.getDescriptionInfoList("10140148", "15");
    }

    @Test
    @Rollback
    public void testGetSlipInfo() {
        SlipInfo slipInfo = dmsDao.getSlipInfo("600001", "16");
    }

}
