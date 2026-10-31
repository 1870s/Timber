package de.timber;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

final class TreeListener implements Listener {

    private final TimberPlugin plugin;

    /** Spieler, deren Baum gerade gefällt wird – verhindert, dass unsere eigenen Abbau-Events erneut auslösen. */
    private final Set<UUID> chopping = new HashSet<>();

    TreeListener(TimberPlugin plugin) {
        this.plugin = plugin;
    }

    // MONITOR + ignoreCancelled: nur reagieren, wenn der Abbau wirklich durchgeht (z. B. nicht von WorldGuard blockiert)
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block origin = event.getBlock();
        TimberSettings settings = plugin.settings();

        if (chopping.contains(player.getUniqueId())) return;
        if (!TreeScanner.isLog(origin.getType())) return;
        if (settings.disabledWorlds().contains(origin.getWorld().getName())) return;
        if (!player.hasPermission("timber.use")) return;
        if (settings.requireSneak() && !player.isSneaking()) return;
        if (settings.requireAxe() && !isAxe(player.getInventory().getItemInMainHand())) return;

        TreeScanner.Tree tree = TreeScanner.scan(origin, settings);
        if (tree == null) return;

        // Einen Tick später fällen, damit der ursprüngliche Abbau komplett abgeschlossen ist
        Material logType = origin.getType();
        plugin.getServer().getScheduler().runTask(plugin, () -> chop(player, logType, tree, settings));
    }

    private void chop(Player player, Material logType, TreeScanner.Tree tree, TimberSettings settings) {
        if (!player.isOnline()) return;

        chopping.add(player.getUniqueId());
        try {
            for (Block log : tree.logs()) {
                // Ursprünglicher Block ist schon weg; andere können sich inzwischen geändert haben
                if (log.getType() != logType) continue;

                ItemStack tool = player.getInventory().getItemInMainHand();
                if (settings.requireAxe() && !isAxe(tool)) return;
                if (settings.protectTool() && isAboutToBreak(tool)) {
                    player.sendActionBar(Component.text(
                            "Deine Axt ist fast kaputt – Timber hat angehalten.", NamedTextColor.RED));
                    return;
                }

                // Wie ein normaler Abbau: Drops, Verzauberungen, Haltbarkeit, Statistiken, Schutz-Plugins
                player.breakBlock(log);
            }

            if (settings.breakLeaves()) {
                for (Block leaf : tree.leaves()) {
                    breakLeaf(player, leaf);
                }
            }
        } finally {
            chopping.remove(player.getUniqueId());
        }
    }

    /** Blätter droppen normal, kosten aber keine Haltbarkeit der Axt. */
    private void breakLeaf(Player player, Block leaf) {
        if (!TreeScanner.isNaturalLeaf(leaf)) return;

        BlockBreakEvent event = new BlockBreakEvent(leaf, player);
        if (!event.callEvent()) return;

        if (player.getGameMode() == GameMode.CREATIVE || !event.isDropItems()) {
            leaf.setType(Material.AIR);
        } else {
            leaf.breakNaturally(player.getInventory().getItemInMainHand(), true);
        }
    }

    private static boolean isAxe(ItemStack item) {
        return Tag.ITEMS_AXES.isTagged(item.getType());
    }

    private static boolean isAboutToBreak(ItemStack tool) {
        Integer maxDamage = tool.getData(DataComponentTypes.MAX_DAMAGE);
        if (maxDamage == null || tool.hasData(DataComponentTypes.UNBREAKABLE)) return false;
        Integer damage = tool.getData(DataComponentTypes.DAMAGE);
        return maxDamage - (damage == null ? 0 : damage) <= 1;
    }
}
