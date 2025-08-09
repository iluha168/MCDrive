package com.iluha168.mcdrive.fifo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;

import org.bukkit.Bukkit;

public abstract class FifoRequestPoller {
    private final Path filePath;

    public FifoRequestPoller(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Awaits next request and calls `handle` */
    public final void next() {
        new Thread(this::nextRequestThread, "FIFO Poller at "+filePath.toString()).start();
    }

    private final void nextRequestThread() {
        try {
            byte[] request = Files.readAllBytes(filePath);
            ByteBuffer requestBuffer = ByteBuffer.wrap(request);
            requestBuffer.order(ByteOrder.nativeOrder());
            handle(requestBuffer);
        } catch(IOException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }
    }

    final void respond(byte[] response) {
        try {
            Files.write(filePath, response, StandardOpenOption.WRITE);
        } catch(IOException e) {
            if(!e.getMessage().equals("Broken pipe")) {
                e.printStackTrace();
                Bukkit.shutdown();
            }
            return;
        }
        next();
    }

    /** The `next` callback. Must call `respond` eventually.
     * @throws IOException */
    protected abstract void handle(ByteBuffer requestBuffer) throws IOException;
}
