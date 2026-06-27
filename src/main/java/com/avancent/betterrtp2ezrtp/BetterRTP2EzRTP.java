package com.avancent.betterrtp2ezrtp;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterRTP2EzRTP extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginCommand cmd = getCommand("migratertp");
        if (cmd != null) {
            cmd.setExecutor(new MigrationCommand(this));
        }
        getLogger().info("BetterRTP2EzRTP loaded. Run /migratertp to begin migration.");
    }
}
