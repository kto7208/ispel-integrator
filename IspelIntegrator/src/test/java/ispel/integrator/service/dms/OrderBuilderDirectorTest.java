package ispel.integrator.service.dms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = {"classpath:/test_config/system-test-config.xml"})
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class OrderBuilderDirectorTest {

    @Autowired
    private OrderBuilderDirector orderBuilderDirector;

    @Test
    @Commit
    public void testConstruct() {
        orderBuilderDirector.construct("17","12140001");
    }

    @Test
    @Commit
    public void testConstructMultiple() {
        orderBuilderDirector.constructMultiple();
    }

}
