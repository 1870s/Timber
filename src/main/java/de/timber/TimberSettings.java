package de.timber;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public record TimberSettings(
        boolean requireSneak,
        boolean requireAxe,
        boolean breakLeaves,
        boolean requireNaturalLeaves,
        boolean protectTool,
        int maxLogs,
        int maxLeaves,
        int leafRadius,
        Set<String> disabledWorlds) {

    public static TimberSettings from(FileConfiguration config) {
        return new TimberSettings(
                config.getBoolean("require-sneak", true),
                config.getBoolean("require-axe", true),
                config.getBoolean("break-leaves", true),
                config.getBoolean("require-natural-leaves", true),
                config.getBoolean("protect-tool", true),
                Math.max(1, config.getInt("max-logs", 256)),
                Math.max(0, config.getInt("max-leaves", 1024)),
                Math.clamp(config.getInt("leaf-radius", 6), 1, 10),
                Set.copyOf(config.getStringList("disabled-worlds")));
    }
}
