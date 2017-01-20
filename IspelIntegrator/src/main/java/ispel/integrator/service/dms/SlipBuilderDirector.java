package ispel.integrator.service.dms;

import generated.DMSextract;
import ispel.integrator.dao.dms.DmsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SlipBuilderDirector {

    @Autowired
    private DmsDao dmsDao;

    @Value("${ispel.dms.franchise}")
    private String franchise;

    @Value("${ispel.dms.vendor}")
    private String vendor;

    @Value("${ispel.dms.product.name}")
    private String productName;

    private String franchiseCode;

    private String dmsVersion;

    @Autowired
    private DmsBuilder dmsBuilder;

    public DMSextract construct(String documentGroup, String documentNumber) {
        return null;
    }
}
