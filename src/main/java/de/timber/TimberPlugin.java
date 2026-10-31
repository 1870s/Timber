package de.timber;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class TimberPlugin extends JavaPlugin {

    private TimberSettings settings;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        settings = TimberSettings.from(getConfig());

        getServer().getPluginManager().registerEvents(new TreeListener(this), this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(
                        Commands.literal("timber")
                                .requires(source -> source.getSender().hasPermission("timber.reload"))
                                .then(Commands.literal("reload")
                                        .executes(ctx -> {
                                            reloadConfig();
                                            settings = TimberSettings.from(getConfig());
                                            ctx.getSource().getSender()
                                                    .sendRichMessage("<green>Timber-Konfiguration neu geladen.");
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .build(),
                        "Timber verwalten",
                        List.of()));
    }

    public TimberSettings settings() {
        return settings;
    }
}
