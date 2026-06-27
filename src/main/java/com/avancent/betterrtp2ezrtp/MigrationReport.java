package com.avancent.betterrtp2ezrtp;

import java.util.ArrayList;
import java.util.List;

public class MigrationReport {

    private final List<String> mapped = new ArrayList<>();
    private final List<String> dropped = new ArrayList<>();
    private final List<String> manual = new ArrayList<>();

    public void addMapped(String entry) {
        mapped.add(entry);
    }

    public void addDropped(String entry) {
        dropped.add(entry);
    }

    public void addManual(String entry) {
        manual.add(entry);
    }

    public List<String> getMapped() { return mapped; }
    public List<String> getDropped() { return dropped; }
    public List<String> getManual() { return manual; }

    public void print(java.util.logging.Logger log) {
        log.info("=== BetterRTP → EzRTP Migration Report ===");

        log.info("--- Mapped (" + mapped.size() + ") ---");
        for (String s : mapped) log.info("  [OK] " + s);

        if (!dropped.isEmpty()) {
            log.info("--- Dropped / No EzRTP Equivalent (" + dropped.size() + ") ---");
            for (String s : dropped) log.info("  [DROPPED] " + s);
        }

        if (!manual.isEmpty()) {
            log.info("--- Needs Manual Attention (" + manual.size() + ") ---");
            for (String s : manual) log.info("  [MANUAL] " + s);
        }

        log.info("--- Notes ---");
        log.info("  BetterRTP files have NOT been removed.");
        log.info("  Once you have tested EzRTP and confirmed it works correctly,");
        log.info("  you may safely delete plugins/BetterRTP/ at your discretion.");
        log.info("===========================================");
    }
}
