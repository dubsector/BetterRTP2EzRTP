package com.avancent.betterrtp2ezrtp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class MigrationCommand implements CommandExecutor {

    private final BetterRTP2EzRTP plugin;

    public MigrationCommand(BetterRTP2EzRTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean dryRun = args.length > 0 && args[0].equalsIgnoreCase("--dry-run");

        if (dryRun) {
            sender.sendMessage("[BetterRTP2EzRTP] Running in DRY-RUN mode — no files will be written.");
        } else {
            sender.sendMessage("[BetterRTP2EzRTP] Starting migration...");
        }

        Logger log = plugin.getLogger();
        File serverRoot = plugin.getDataFolder().getParentFile().getParentFile();

        MigrationReport report = new MigrationReport();
        Map<String, String> outputs;

        try {
            outputs = new ConfigMapper(serverRoot, report).map();
        } catch (IllegalStateException e) {
            sender.sendMessage("[BetterRTP2EzRTP] ERROR: " + e.getMessage());
            log.severe("Migration failed: " + e.getMessage());
            return true;
        }

        if (!dryRun) {
            File ezrtpDir = new File(serverRoot, "plugins/EzRTP");
            if (!ezrtpDir.exists() && !ezrtpDir.mkdirs()) {
                sender.sendMessage("[BetterRTP2EzRTP] ERROR: Could not create plugins/EzRTP/ directory.");
                return true;
            }

            for (Map.Entry<String, String> entry : outputs.entrySet()) {
                File out = new File(ezrtpDir, entry.getKey());
                try (FileWriter fw = new FileWriter(out)) {
                    fw.write(entry.getValue());
                    log.info("Wrote " + out.getPath());
                } catch (IOException e) {
                    sender.sendMessage("[BetterRTP2EzRTP] ERROR writing " + entry.getKey() + ": " + e.getMessage());
                    log.severe("Failed to write " + entry.getKey() + ": " + e.getMessage());
                    return true;
                }
            }

            sender.sendMessage("[BetterRTP2EzRTP] Config files written to plugins/EzRTP/");
            tryReloadEzRtp(sender, log);
        } else {
            log.info("=== DRY-RUN: files that WOULD be written to plugins/EzRTP/ ===");
            for (Map.Entry<String, String> entry : outputs.entrySet()) {
                log.info("--- " + entry.getKey() + " ---");
                log.info(entry.getValue());
            }
        }

        report.print(log);
        sender.sendMessage("[BetterRTP2EzRTP] Migration report printed to server console.");
        return true;
    }

    private void tryReloadEzRtp(CommandSender sender, Logger log) {
        Plugin ezrtp = Bukkit.getPluginManager().getPlugin("EzRTP");
        if (ezrtp == null) {
            log.info("[BetterRTP2EzRTP] EzRTP plugin not detected on the server.");
            sender.sendMessage("[BetterRTP2EzRTP] EzRTP is not currently loaded — a server restart is required for the new config to take effect.");
            return;
        }

        // Attempt a reload via EzRTP's own reload command. If EzRTP doesn't register
        // this command the dispatch will return false and we fall back to the restart notice.
        boolean dispatched = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ezrtp reload");
        if (dispatched) {
            sender.sendMessage("[BetterRTP2EzRTP] Sent '/ezrtp reload' — check console to confirm EzRTP accepted it.");
            log.info("[BetterRTP2EzRTP] Dispatched 'ezrtp reload' after writing config.");
        } else {
            sender.sendMessage("[BetterRTP2EzRTP] Could not auto-reload EzRTP (command not recognised). "
                    + "Please restart the server or reload EzRTP manually for new settings to take effect.");
            log.warning("[BetterRTP2EzRTP] 'ezrtp reload' dispatch returned false — manual restart required.");
        }
    }
}
