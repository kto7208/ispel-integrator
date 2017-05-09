package ispel.integrator.service;

import ispel.integrator.dao.dms.DmsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportSzvService {

    @Autowired
    private DmsDao dmsDao;

    @Transactional
    public void updateOrder(String orderNumber, String orderGroup) {
        dmsDao.updateOrder(orderNumber, orderGroup);
    }

}
