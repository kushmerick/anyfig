package io.osowa.anyfig.tests;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.api.RemoteAPI;

import java.net.ServerSocket;
import java.util.Collections;

import static io.osowa.anyfig.api.RemoteAPI.APPLICATION_JSON;
import static io.osowa.anyfig.api.RemoteAPI.AUTHORIZATION_HEADER;
import static io.osowa.anyfig.api.RemoteAPI.CONTENT_TYPE_HEADER;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static junit.framework.TestCase.assertEquals;

public class RemoteAPITest {

    private static final String TOKEN = "XYZ";
    private static final Gson GSON = new Gson();

    private RemoteAPI.Config makeConfig() {
        RemoteAPI.Config config = new RemoteAPI.Config();
        Anyfig anyfig = new Anyfig();
        // use the defaults configuration
        anyfig.configure(config);
        // but override a couple...
        config.port = pickPort();
        config.token = TOKEN;
        return config;
    }

    @Test
    public void testAuthorizationIsEnforced() throws Exception {
        RemoteAPI.Config config = makeConfig();
        try (Anyfig anyfig = new Anyfig()) {
            anyfig.enableRemoteAPI(config);
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                String url = "http://localhost:" + config.port + "/anyfig";
                HttpGet get = new HttpGet(url);
                get.setHeader(AUTHORIZATION_HEADER, "INVALID!");
                try (CloseableHttpResponse response = httpclient.execute(get)) {
                    assertEquals(HTTP_UNAUTHORIZED, response.getStatusLine().getStatusCode());
                }
            }
        }
    }

    @Test
    public void testCanGet() throws Exception {
        RemoteAPI.Config config = makeConfig();
        try (Anyfig anyfig = new Anyfig()) {
            anyfig.configure(TestRemoteAPI.class);
            anyfig.enableRemoteAPI(config);
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                String url = "http://localhost:" + config.port + "/anyfig/" + TestRemoteAPI.class.getName() + ".field";
                HttpGet get = new HttpGet(url);
                get.setHeader(AUTHORIZATION_HEADER, TOKEN);
                try (CloseableHttpResponse response = httpclient.execute(get)) {
                    assertEquals(HTTP_OK, response.getStatusLine().getStatusCode());
                    RemoteAPI.GetResponse getResponse = getBody(response, RemoteAPI.GetResponse.class);
                    assertEquals(
                        (double) TestRemoteAPI.DEFAULT_FIELD, // GSON converts response's "1" into double 1.0
                        getResponse.value);
                }
            }
        }
    }

    @Test
    public void testCanEnumerate() throws Exception {
        RemoteAPI.Config config = makeConfig();
        try (Anyfig anyfig = new Anyfig()) {
            anyfig.configure(TestRemoteAPI.class);
            anyfig.enableRemoteAPI(config);
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                String url = "http://localhost:" + config.port + "/anyfig";
                HttpGet get = new HttpGet(url);
                get.setHeader(AUTHORIZATION_HEADER, TOKEN);
                try (CloseableHttpResponse response = httpclient.execute(get)) {
                    assertEquals(HTTP_OK, response.getStatusLine().getStatusCode());
                    RemoteAPI.GetResponse getResponse = getBody(response, RemoteAPI.GetResponse.class);
                    assertEquals(
                        (double) TestRemoteAPI.DEFAULT_FIELD, // GSON converts response's "1" into double 1.0
                        getResponse.values.get(TestRemoteAPI.class.getName() + ".field"));
                }
            }
        }
    }

    @Test
    public void testCanSet() throws Exception {
        RemoteAPI.Config config = makeConfig();
        try (Anyfig anyfig = new Anyfig()) {
            anyfig.configure(TestRemoteAPI.class);
            anyfig.enableRemoteAPI(config);
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                String url = "http://localhost:" + config.port + "/anyfig/" + TestRemoteAPI.class.getName() + ".field";
                HttpPatch patch = new HttpPatch(url);
                patch.setHeader(AUTHORIZATION_HEADER, TOKEN);
                patch.setHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
                RemoteAPI.PatchRequest request = new RemoteAPI.PatchRequest();
                request.value = 2;
                patch.setEntity(new StringEntity(GSON.toJson(request)));
                try (CloseableHttpResponse response = httpclient.execute(patch)) {
                    assertEquals(HTTP_OK, response.getStatusLine().getStatusCode());
                    assertEquals(request.value, TestRemoteAPI.field);
                }
            }
        }
    }

    @Test
    public void testCanPatch() throws Exception {
        RemoteAPI.Config config = makeConfig();
        try (Anyfig anyfig = new Anyfig()) {
            anyfig.configure(TestRemoteAPI.class);
            anyfig.enableRemoteAPI(config);
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                String url = "http://localhost:" + config.port + "/anyfig";
                HttpPatch patch = new HttpPatch(url);
                patch.setHeader(AUTHORIZATION_HEADER, TOKEN);
                patch.setHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
                RemoteAPI.PatchRequest request = new RemoteAPI.PatchRequest();
                int value = 2;
                request.values = Collections.singletonMap(TestRemoteAPI.class.getName() + ".field", value);
                patch.setEntity(new StringEntity(GSON.toJson(request)));
                try (CloseableHttpResponse response = httpclient.execute(patch)) {
                    assertEquals(HTTP_OK, response.getStatusLine().getStatusCode());
                    assertEquals(value, TestRemoteAPI.field);
                }
            }
        }
    }

    // pick an available port; inspired by Xe's UriUtils.findAvailablePort.
    private static int pickPort() {
        try (ServerSocket socket = new ServerSocket(0)) { // 0 means 'pick an available port'
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception exception) {
            throw new RuntimeException("Could not pick a port", exception);
        }
    }

    private <T> T getBody(HttpResponse response, Class<T> clazz) throws Exception {
        String str = EntityUtils.toString(response.getEntity());
        return GSON.fromJson(str, clazz);
    }

    public static class TestRemoteAPI {
        private static final int DEFAULT_FIELD = 1;
        public static int field;
    }

}
