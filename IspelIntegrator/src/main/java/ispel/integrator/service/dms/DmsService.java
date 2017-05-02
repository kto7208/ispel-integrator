package ispel.integrator.service.dms;

import generated.DMSextract;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DmsService {

    private static final Logger logger = Logger
            .getLogger(DmsService.class);

    @Autowired
    private OrderBuilderDirector orderBuilderDirector;

    @Autowired
    private SlipBuilderDirector slipBuilderDirector;


    @Transactional(readOnly = true)
    public DMSextract buildDMS(String documentType, String documentNumber, String documentGroup) {
        if (documentType == null) {
            throw new IllegalArgumentException("documentType is null");
        }
        if (documentNumber == null) {
            throw new IllegalArgumentException("documentNumber is null");
        }
        if (documentGroup == null) {
            throw new IllegalArgumentException("documentGroup is null");
        }

        DMSextract dmsExtract = null;
        if ("VYD".equals(documentType)) {
            dmsExtract = slipBuilderDirector.construct(documentGroup, documentNumber);
        } else if ("ZAK".equals(documentType)) {
            dmsExtract = orderBuilderDirector.construct(documentGroup, documentNumber);
        } else {
            throw new IllegalStateException("wrong documentType: " + documentType);
        }
        return dmsExtract;
    }
}

