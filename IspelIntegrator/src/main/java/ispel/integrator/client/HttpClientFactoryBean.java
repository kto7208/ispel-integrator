package ispel.integrator.client;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.FactoryBean;

public class HttpClientFactoryBean implements FactoryBean<HttpClient> {

	private String proxyHost;
	private int proxyPort;
	private String proxyUser;
	private String proxyPassword;
	private String ispelUser;
	private String ispelPassword;

	private HttpClientBuilder builder;

	public HttpClientFactoryBean(String proxyHost, int proxyPort,
			String proxyUser, String proxyPassword, String ispelUser,
			String ispelPassword) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPassword = proxyPassword;
		this.ispelUser = ispelUser;
		this.ispelPassword = ispelPassword;
	}

	@PostConstruct
	public void init() {
		builder = HttpClients.custom();
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(proxyHost,
				AuthScope.ANY_PORT), new UsernamePasswordCredentials(proxyUser,
				proxyPassword));
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(ispelUser, ispelPassword));

		builder.setDefaultCredentialsProvider(credentialsProvider);
		builder.setProxy(new HttpHost(proxyHost, proxyPort));
		builder.addInterceptorFirst(new HttpRequestInterceptor() {
			public void process(final HttpRequest request,
					final HttpContext context) throws HttpException,
					IOException {
				if (request instanceof HttpEntityEnclosingRequest) {
					if (request.containsHeader(HTTP.TRANSFER_ENCODING)) {
						request.removeHeaders(HTTP.TRANSFER_ENCODING);
					}
					if (request.containsHeader(HTTP.CONTENT_LEN)) {
						request.removeHeaders(HTTP.CONTENT_LEN);
					}
				}
			}
		});
	}

	public HttpClient getObject() throws Exception {
		return builder.build();
	}

	public Class<?> getObjectType() {
		return HttpClient.class;
	}

	public boolean isSingleton() {
		return false;
	}
}
