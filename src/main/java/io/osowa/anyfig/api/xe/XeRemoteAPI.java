package io.osowa.anyfig.api.xe;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.ConfigurationException;
import io.osowa.anyfig.api.RemoteAPI;
import sun.misc.Signal;

import java.util.Date;
import java.util.Map;

public class XeRemoteAPI implements RemoteAPI {

    private Host host = null;

    @Override
    public void start(RemoteAPI.Config config, Anyfig anyfig) {
        if (config.token == null) {
            throw new ConfigurationException("Refusing to start Remote API without a token");
        }
        host = new Host(config.level, config.bindAddress, config.port, config.peers, config.dataDir, config.token, anyfig);
        try {
            host.initialize();
            host.start();
        } catch (Throwable exception) {
            host = null; // TODO: host.stop() ??!
            throw new ConfigurationException("Unable to start Xe API host", exception);
        }
        Runnable stop = () -> {
            System.err.println(new Date() + " " + getClass().getName() + " shutting down...");
            stop();
        };
        Runtime.getRuntime().addShutdownHook(new Thread(stop));
        Signal.handle(new Signal("TERM"), (ignored) -> stop.run());
    }

    @Override
    public void stop() {
        if (host != null) {
            host.stop();
            host = null;
        }
    }

}
