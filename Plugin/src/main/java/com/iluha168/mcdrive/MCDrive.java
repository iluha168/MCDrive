package com.iluha168.mcdrive;

import io.papermc.lib.PaperLib;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.iluha168.mcdrive.drive.ByteBlock;
import com.iluha168.mcdrive.fifo.ReadRequestPoller;
import com.iluha168.mcdrive.fifo.WriteRequestPoller;

public class MCDrive extends JavaPlugin {
  @Override
  public void onEnable() {
    PaperLib.suggestPaper(this);
    ByteBlock.fillTable();

    World overworld = getServer().getWorlds().get(0);

    new ReadRequestPoller(overworld).next();
    new WriteRequestPoller(this, overworld).next();
  }
}
