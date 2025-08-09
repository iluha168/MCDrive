package com.iluha168.mcdrive.drive;

import org.bukkit.Location;
import org.joml.Vector2L;

public class Space {
    public static final long CHUNK_WIDTH = 16L;
    public static final long CHUNK_HEIGHT = 384L;
    public static final long BYTES_IN_CHUNK = CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_HEIGHT;

    public static Location virtualToPhysical(long virtual) {
        final long chunkIndex = virtual / BYTES_IN_CHUNK;
        final long blockIndex = virtual % BYTES_IN_CHUNK;
        
        final Vector2L chunkPos = spiral1dTo2d(chunkIndex);
        
        final long blockY = blockIndex / (CHUNK_WIDTH * CHUNK_WIDTH);
        final long blockXZ = blockIndex % (CHUNK_WIDTH * CHUNK_WIDTH);
        
        final long blockX = blockXZ / CHUNK_WIDTH;
        final long blockZ = blockXZ % CHUNK_WIDTH;

        return new Location(
            null,
            (int)(CHUNK_WIDTH * chunkPos.x + blockX),
            (int)(blockY - 64L),
            (int)(CHUNK_WIDTH * chunkPos.y + blockZ)
        );
    }

    private static Vector2L spiral1dTo2d(long n) {
        final long k = (long)Math.ceil((Math.sqrt((double)n) - 1D) / 2D);
        long t = 2L * k + 1L;
        long m = t * t;
        t--;
        if (n >= m - t)
            return new Vector2L(k + n - m, -k);
        m -= t;
        if (n >= m - t)
            return new Vector2L(-k, m - n - k);
        m -= t;
        if (n >= m - t)
            return new Vector2L(m - n - k, k);
        return new Vector2L(k, k + n + t - m);
    }
}
