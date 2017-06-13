package ispel.integrator.test;

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
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestSend {

    @Test
    public void whenPostRequestUsingHttpClient_thenCorrect()
            throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://prismcentral.prism-services.com/prismcentral/servlet/com.prismservices.prism.ptrcentral.Submit");

        File file = new File("c:/temp/dms/orders-20175106-225111.xml");
        FileBody fileBody = new FileBody(file, ContentType.APPLICATION_XML);
        StringBody uidBody = new StringBody("CLIENTZLIN_DMS", ContentType.MULTIPART_FORM_DATA);
        StringBody pwdBody = new StringBody("wXmHeDTw", ContentType.MULTIPART_FORM_DATA);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("uid", uidBody);
        builder.addPart("pwd", pwdBody);
        builder.addPart("uploadfile", fileBody);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println("result: " + result);
        client.close();
    }
}
