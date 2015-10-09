package de.qabel.core.accounting;

import com.amazonaws.auth.BasicSessionCredentials;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import de.qabel.core.config.AccountingServer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class AccountingHTTP {

	private final static Logger logger = LoggerFactory.getLogger(AccountingHTTP.class.getName());

	private AccountingServer server;
	private final CloseableHttpClient httpclient;
	private Gson gson;

	public AccountingHTTP(AccountingServer server) {
		this.server = server;
		httpclient = HttpClients.createMinimal();
		gson = new Gson();
	}

	public boolean login() throws IOException {
		URI uri;
		try {
			uri = this.buildUri("api/v0/auth/login").build();
		} catch (URISyntaxException e) {
			logger.error("Login url building failed", e);
			throw new RuntimeException("Login url building failed", e);
		}
		HttpPost httpPost = new HttpPost(uri);
		Map<String, String> params = new HashMap<>();
		params.put("username", server.getUsername());
		params.put("password", server.getPassword());
		String json = gson.toJson(params);
		StringEntity input = new StringEntity(json);
		input.setContentType("application/json");
		httpPost.setEntity(input);
		try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No answer from login");
			}
			String responseString = EntityUtils.toString(entity);
			try {
				Map<String, String> answer = gson.fromJson(responseString, HashMap.class);
				if (answer.containsKey("key")) {
					server.setAuthToken(answer.get("key"));
					return true;
				} else {
					throw new IOException(answer.toString());
				}
			} catch (JsonSyntaxException e) {
				logger.error("Illegal response: {}", responseString);
				throw e;
			}
		}
	}

	public int getQuota() throws IOException {
		if (server.getAuthToken() == null) {
			login();
		}
		URI uri;
		try {
			uri = this.buildUri("api/v0/profile").build();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Url building failed", e);
		}
		HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("Authorization", "Token " + server.getAuthToken());
		try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No answer from login");
			}
			String responseString = EntityUtils.toString(entity);
			try {
				Map<String, Double> answer = gson.fromJson(responseString, HashMap.class);
				return answer.get("quota").intValue();
			} catch (JsonSyntaxException|NumberFormatException|NullPointerException e) {
				logger.error("Illegal response: {}", responseString);
				throw e;
			}

		}
	}

	public BasicSessionCredentials getCredentials() throws IOException {
		if (server.getAuthToken() == null) {
			login();
		}
		URI uri;
		try {
			uri = this.buildUri("api/v0/token").build();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Token url building failed", e);
		}
		HttpPost httpPost = new HttpPost(uri);
		httpPost.addHeader("Authorization", "Token " + server.getAuthToken());
		try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No answer from login");
			}
			String responseString = EntityUtils.toString(entity);
			try {
				LinkedTreeMap<String, Object> answer = gson.fromJson(responseString, LinkedTreeMap.class);
				LinkedTreeMap<String, String> credentials =
						(LinkedTreeMap<String, String>) answer.get("Credentials");
				return new BasicSessionCredentials(
						credentials.get("AccessKeyId"),
						credentials.get("SecretAccessKey"),
						credentials.get("SessionToken"));
			} catch (JsonSyntaxException|NullPointerException e) {
				// NullPointerException also means that our response was not ok because on of the
				// .get() failed
				logger.error("Illegal response: {}", responseString);
				throw e;
			}
		}

	}

	public URIBuilder buildUri(String resource) {
		if (resource.endsWith("/") || resource.startsWith("/")) {
			logger.error("Resource {} starts or ends with /", resource);
			throw new RuntimeException("Illegal resource");
		}
		return new URIBuilder(this.server.getUri())
					.setPath("/" + resource + "/");
	}

}
