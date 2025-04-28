package cyansfactions.listeners;

import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChunkProtectionListener implements Listener {

    private final ChunkManager chunkManager;

    public ChunkProtectionListener(FactionManager factionManager, ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        Faction faction = chunkManager.getFactionAt(chunk);

        if (faction == null) {
            return; // Chunk is unclaimed -> allow
        }

        if (!faction.hasMember(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(faction.getName() + " §cDoes not allow you to break blocks.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        Faction faction = chunkManager.getFactionAt(chunk);

        if (faction == null) {
            return; // Chunk is unclaimed -> allow
        }

        if (!faction.hasMember(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(faction.getName() + " §cDoes not allow you to place blocks.");
        }
    }
}
