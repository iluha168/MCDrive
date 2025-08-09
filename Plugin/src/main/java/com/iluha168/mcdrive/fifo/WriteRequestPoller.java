package com.iluha168.mcdrive.fifo;

import java.nio.ByteBuffer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.iluha168.mcdrive.drive.ByteBlock;
import com.iluha168.mcdrive.drive.Space;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class WriteRequestPoller extends FifoRequestPoller {
    @SuppressFBWarnings
    private final Plugin plugin;
    @SuppressFBWarnings
    private final World world;

    private static final long WRITE_BLOCKS_AT_ONCE = Space.BYTES_IN_CHUNK;

    public WriteRequestPoller(Plugin plugin, World world) {
		super("../NBD-FIFO/dist/buse_requests_write");
        this.plugin = plugin;
        this.world = world;
	}

    @Override
	protected void handle(ByteBuffer incoming) {
        final long offset = incoming.getLong();
        Bukkit.getScheduler().runTask(plugin, () -> subHandle(offset, incoming));
    }

    private void subHandle(long startingOffset, ByteBuffer incoming) {
        try {
            for (long offset = 0; offset < WRITE_BLOCKS_AT_ONCE; offset++) {
                if (!incoming.hasRemaining()) {
                    respond(new byte[] { 0 });
                    return;
                }
                Block block = world.getBlockAt(Space.virtualToPhysical(startingOffset + offset));
                block.setType(ByteBlock.byteToMaterial(incoming.get()), false);
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                subHandle(startingOffset + WRITE_BLOCKS_AT_ONCE, incoming);
            }, 2);
        } catch(Throwable e) {
            e.printStackTrace();
            respond(new byte[] { 1 });
            Bukkit.shutdown();
        }
    }
}
