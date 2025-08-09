package com.iluha168.mcdrive.fifo;

import java.nio.ByteBuffer;

import org.bukkit.Material;
import org.bukkit.World;

import com.iluha168.mcdrive.drive.ByteBlock;
import com.iluha168.mcdrive.drive.Space;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ReadRequestPoller extends FifoRequestPoller {
    @SuppressFBWarnings
    private final World world;

    public ReadRequestPoller(World world) {
		super("../NBD-FIFO/dist/buse_requests_read");
        this.world = world;
	}

    @Override
	protected void handle(ByteBuffer incoming) {
        final long offset = incoming.getLong();
        final int len = incoming.getInt();
        final byte[] outputBytes = new byte[len];
        for (int i = 0; i < len; i++) {
            Material material = world.getBlockAt(
                Space.virtualToPhysical(i + offset)
            ).getType();
            outputBytes[i] = ByteBlock.materialToByte(material);
        }
        respond(outputBytes);
    }
}
