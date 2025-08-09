package com.iluha168.mcdrive.drive;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteBlock {
    static final Material[] table = new Material[256];
    static final Logger logger = LoggerFactory.getLogger(ByteBlock.class.getSimpleName());

    public static void fillTable() {
        table[128] = Material.AIR;

        int i = 0;
        for (Material material : Material.values()) {
            while (table[i] != null) i++;
            BlockType block = material.asBlockType();
            if (block == null) continue;
            if (
                !block.isOccluding() ||
                block.hasGravity()
            ) continue;
            Class<? extends BlockData> dataClass = block.getBlockDataClass();
            if (
                dataClass != BlockData.class &&
                dataClass != Orientable.class
            ) continue;

            table[i] = material;
            if (++i >= 256) break;
        }

        if (i != 256) {
            logger.error("Generated material table of size {}", i);
            Bukkit.shutdown();
        }
    }

    public static Material byteToMaterial(byte index) {
        return table[index + 128];
    }

    public static byte materialToByte(Material material) {
        for (int i = 0; i < 256; i++)
            if (table[i] == material)
                return (byte)(i - 128);
        return (byte)0;
    }
}
