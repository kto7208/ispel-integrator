package ispel.integrator.dao.dms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class DmsSequenceService {

    @Autowired
    public DmsDao dmsDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BigInteger getDmsSourceSequenceNextVal() {
        return dmsDao.getDmsSourceSequenceNextVal();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BigInteger getDmsSiteSequenceNextVal() {
        return dmsDao.getDmsSiteSequenceNextVal();
    }
}
