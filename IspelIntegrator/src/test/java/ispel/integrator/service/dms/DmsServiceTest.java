package ispel.integrator.service.dms;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import generated.DMSextract;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;

@ContextConfiguration(locations = {"classpath:/test_config/system-test-config.xml"})
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class DmsServiceTest {

    @Autowired
    private DmsService dmsService;

    @Autowired
    private Jaxb2Marshaller dmsExtractMarshaller;


    @Test
    @Commit
    public void testDmService() throws Exception {
        DMSextract dmsextract = dmsService.buildDMS("ZAK", "12140001", "17");
        StringWriter stringWriter = new StringWriter();
        dmsExtractMarshaller.marshal(dmsextract,new StreamResult(stringWriter));
        Files.write(stringWriter.toString(), new File("c:/temp/nissan/order.xml"), Charsets.UTF_8);
    }

    @Test
    @Commit
    public void testDmService2() throws Exception {
        DMSextract dmsextract = dmsService.buildDMS("ZAK", "11150131", "16");
        StringWriter stringWriter = new StringWriter();
        dmsExtractMarshaller.marshal(dmsextract, new StreamResult(stringWriter));
        Files.write(stringWriter.toString(), new File("c:/temp/nissan/order.xml"), Charsets.UTF_8);
    }

    @Test
    @Commit
    public void testDmService3() throws Exception {
        DMSextract dmsextract = dmsService.buildDMS("ZAK", "10140148", "15");
        StringWriter stringWriter = new StringWriter();
        dmsExtractMarshaller.marshal(dmsextract, new StreamResult(stringWriter));
        Files.write(stringWriter.toString(), new File("c:/temp/nissan/order.xml"), Charsets.UTF_8);
    }
}
