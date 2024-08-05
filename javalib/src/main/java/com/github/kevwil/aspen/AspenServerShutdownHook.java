package com.github.kevwil.aspen;

public class AspenServerShutdownHook extends Thread {
    private final AspenServer server;

    public AspenServerShutdownHook(final AspenServer server) {
        super();
        this.server = server;
    }

    @Override
    public void run() {
        server.stop();
    }
}
