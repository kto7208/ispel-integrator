package ispel.integrator.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test_config/system-test-config.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class AbstractSystemTestCase extends AbstractJUnit4SpringContextTests {
}
