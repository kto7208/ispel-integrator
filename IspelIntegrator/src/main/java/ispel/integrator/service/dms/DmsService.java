package ispel.integrator.service.dms;

import generated.DMSextract;
import ispel.integrator.dao.dms.DmsDao;
import ispel.integrator.domain.dms.OrderKey;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class DmsService {

    private static final Logger logger = Logger
            .getLogger(DmsService.class);

    @Autowired
    private OrderBuilderDirector orderBuilderDirector;

    @Autowired
    private SlipBuilderDirector slipBuilderDirector;

    @Value("${ispel.dms.url}")
    private String sendUrl;

    @Value("${ispel.dms.user}")
    private String user;

    @Value("${ispel.dms.pwd}")
    private String pwd;

    @Autowired
    private DmsDao dmsDao;

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
            dmsExtract = orderBuilderDirector.construct(documentGroup, documentNumber, null, null);
        } else {
            throw new IllegalStateException("wrong documentType: " + documentType);
        }
        return dmsExtract;
    }

    @Transactional
    public DMSextract buildDMSMultiple(String documentType, List<OrderKey> keys) {
        if (documentType == null) {
            throw new IllegalArgumentException("documentType is null");
        }
        if (keys == null) {
            throw new IllegalArgumentException("keys is null");
        }

        DMSextract dmsExtract = null;
        if ("VYD".equals(documentType)) {
            dmsExtract = slipBuilderDirector.constructMultiple();
        } else if ("ZAK".equals(documentType)) {
            dmsExtract = orderBuilderDirector.constructMultiple(keys);
        } else {
            throw new IllegalStateException("wrong documentType: " + documentType);
        }
        return dmsExtract;
    }

    public String sendData(File file) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(sendUrl);
        FileBody fileBody = new FileBody(file, ContentType.APPLICATION_XML);
        StringBody uidBody = new StringBody(user, ContentType.MULTIPART_FORM_DATA);
        StringBody pwdBody = new StringBody(pwd, ContentType.MULTIPART_FORM_DATA);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("uid", uidBody);
        builder.addPart("pwd", pwdBody);
        builder.addPart("uploadfile", fileBody);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPost);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        client.close();
        logger.debug("send result: " + result.toString());
        return result.toString();
    }


    @Transactional
    public void updateOrder(String skupina, String zakazka) {
        dmsDao.updateOrder(zakazka, skupina);
    }

    @Transactional
    public void updateOrders(List<OrderKey> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("keys is null");
        }
        for (OrderKey key : keys) {
            dmsDao.updateOrder(String.valueOf(key.getZakazka()), String.valueOf(key.getSkupina()));
        }
    }

    public boolean emptyRepairOrders(DMSextract dmsExtract) {
        if (dmsExtract == null) {
            throw new IllegalStateException("dmsExtract null");
        }
        return dmsExtract.getSite().get(0).getRepairOrders().getRepairOrder().size() == 0;
    }

    @Transactional(readOnly = true)
    public List<OrderKey> getOrdersForMultipleProcessing() {
        return dmsDao.getOrdersForMultipleProcessing();
    }
}

