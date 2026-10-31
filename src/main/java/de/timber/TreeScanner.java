package de.timber;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Finds all connected logs of a tree and the leaves that belong to it.
 */
final class TreeScanner {

    private static final BlockFace[] FACES = {
            BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    record Tree(List<Block> logs, List<Block> leaves) {}

    private TreeScanner() {}

    static boolean isLog(Material type) {
        return Tag.LOGS.isTagged(type);
    }

    /** Natural leaves, plus the wart blocks and shroomlights of Nether fungi. */
    static boolean isNaturalLeaf(Block block) {
        Material type = block.getType();
        if (Tag.LEAVES.isTagged(type)) {
            return block.getBlockData() instanceof Leaves leaves && !leaves.isPersistent();
        }
        return Tag.WART_BLOCKS.isTagged(type) || type == Material.SHROOMLIGHT;
    }

    /**
     * @return the tree, or {@code null} if it doesn't count as a real tree according to the settings
     */
    static Tree scan(Block origin, TimberSettings settings) {
        List<Block> logs = findLogs(origin, settings.maxLogs());
        List<Block> leaves = findLeaves(logs, settings);

        if (settings.requireNaturalLeaves() && leaves.isEmpty()) {
            return null;
        }
        return new Tree(logs, leaves);
    }

    /**
     * Breadth-first search over all 26 neighbours (for diagonal branches, e.g. acacia),
     * but only the same wood type and never below the broken block.
     */
    private static List<Block> findLogs(Block origin, int maxLogs) {
        Material logType = origin.getType();
        int minY = origin.getY();

        List<Block> logs = new ArrayList<>();
        Set<Block> visited = new HashSet<>();
        ArrayDeque<Block> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && logs.size() < maxLogs) {
            Block current = queue.poll();
            logs.add(current);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        Block next = current.getRelative(dx, dy, dz);
                        if (next.getY() < minY || next.getType() != logType) continue;
                        if (visited.add(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
        }
        return logs;
    }

    /** Breadth-first search from all logs through connected natural leaves. */
    private static List<Block> findLeaves(List<Block> logs, TimberSettings settings) {
        List<Block> leaves = new ArrayList<>();
        Map<Block, Integer> distance = new HashMap<>();
        ArrayDeque<Block> queue = new ArrayDeque<>();
        for (Block log : logs) {
            distance.put(log, 0);
            queue.add(log);
        }

        while (!queue.isEmpty() && leaves.size() < settings.maxLeaves()) {
            Block current = queue.poll();
            int nextDistance = distance.get(current) + 1;
            if (nextDistance > settings.leafRadius()) continue;

            for (BlockFace face : FACES) {
                Block next = current.getRelative(face);
                if (distance.containsKey(next) || !isNaturalLeaf(next)) continue;
                distance.put(next, nextDistance);
                leaves.add(next);
                queue.add(next);
            }
        }
        return leaves;
    }
}
