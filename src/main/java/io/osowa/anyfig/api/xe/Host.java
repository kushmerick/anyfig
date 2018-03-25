package io.osowa.anyfig.api.xe;

import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceHost;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.api.RemoteAPI;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static io.osowa.anyfig.api.RemoteAPI.AUTHORIZATION_HEADER;

public class Host extends ServiceHost {

    private final Level level;
    private final String bindAddress;
    private final int port;
    private final String[] peerNodes;
    private final String sandbox;
    private final String token;
    private final Anyfig anyfig;

    public Host(Level level, String bindAddress, int port, Collection<RemoteAPI.Config.Peer> peers, String dataDir, String token, Anyfig anyfig) {
        this.level = level;
        this.bindAddress = bindAddress;
        this.port= port;
        this.peerNodes = peers.stream().map(RemoteAPI.Config.Peer::toString).collect(Collectors.toList()).toArray(new String[0]);
        this.sandbox = dataDir;
        this.token = token;
        this.anyfig = anyfig;
    }

    public void initialize() throws Throwable {
        setLoggingLevel(level);
        Arguments arguments = new Arguments();
        arguments.bindAddress = bindAddress;
        arguments.port = port;
        arguments.peerNodes = peerNodes;
        arguments.sandbox = Paths.get(sandbox);
        this.initialize(arguments);
    }

    @Override
    public Host start() throws Throwable {
        super.start();
        startService(new AnyfigService(anyfig));
        return this;
    }

    public boolean isAuthorized(Operation op) {
        return token != null && token.equals(op.getRequestHeader(AUTHORIZATION_HEADER));
    }

}
