package io.osowa.anyfig.api;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.Configurable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

public interface RemoteAPI {

    public static class Config {
        // logging level
        public static Level DEFAULT_LEVEL = Level.WARNING; // most clients won't know/care about Xe happy-path logging
        @Configurable(property="anyfig.remote-api.level", envvar="ANYFIG_REMOTE_API_LEVEL", argument="anyfigRemoteAPILevel")
        public Level level;
        // bind address
        public static final String DEFAULT_BIND_ADDRESS = java.net.Inet4Address.getLoopbackAddress().getHostAddress();
        @Configurable(property="anyfig.remote-api.bindAddress", envvar="ANYFIG_REMOTE_API_BIND_ADDRESS", argument="anyfigRemoteAPIBindAddress")
        public String bindAddress;
        // port
        public static final int DEFAULT_PORT = 9111;
        @Configurable(property="anyfig.remote-api.port", envvar="ANYFIG_REMOTE_API_PORT", argument="anyfigRemoteAPIPort")
        public int port;
        // peers
        public static class Peer {
            public String host;
            public int port = DEFAULT_PORT;
            public String toString() { return host + ":" + port; }
        }
        @Configurable(property="anyfig.remote-api.peers", envvar="ANYFIG_REMOTE_API_PEERS", argument="anyfigRemoteAPIPeers")
        public List<Peer> peers = Collections.emptyList();
        // data directory
        public final static String DEFAULT_DATA_DIR = System.getProperty("java.io.tmpdir") + File.separator + Anyfig.class.getName();
        @Configurable(property="anyfig.remote-api.dataDir", envvar="ANYFIG_REMOTE_API_DATA_DIR", argument="anyfigRemoteAPIDataDir")
        public String dataDir;
        // auth token
        @Configurable(property="anyfig.remote-api.token", envvar="ANYFIG_REMOTE_API_TOKEN", argument="anyfigRemoteAPIToken", blockremote=true, redact=true)
        public String token = null; // by default there is _no_ auth token!
    }

    public static final String AUTHORIZATION_HEADER = AUTHORIZATION;
    public static final String CONTENT_TYPE_HEADER = CONTENT_TYPE;
    public static final String APPLICATION_JSON = "application/json";

    public void start(Config config, Anyfig anyfig);

    public void stop();

    public static class Response {
        public String error;
    }

    public static class InvalidToken extends Response {
        {
            error = "Invalid token";
        }
        public static final InvalidToken SINGLETON = new InvalidToken();
    }

    public static class GetResponse extends Response {
        public Object value;              // GET /anyfig/foo
        public Map<String,Object> values; // GET /anyfig
    }

    public static class PatchRequest {
        public Object value;              // PATCH /anyfig/foo
        public Map<String,Object> values; // PATCH /anyfig
    }

    public static class PatchResponse extends Response {
        // this space intentionally left blank
    }

}
