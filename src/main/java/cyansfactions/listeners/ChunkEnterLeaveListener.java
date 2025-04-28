package cyansfactions.listeners;

import cyansfactions.managers.ChunkManager;
import cyansfactions.models.Faction;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ChunkEnterLeaveListener implements Listener {

    private final ChunkManager chunkManager;

    public ChunkEnterLeaveListener(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();

        if (fromChunk.equals(toChunk)) {
            return; 
        }

        Faction toFaction = chunkManager.getFactionAt(toChunk);
        Faction fromFaction = chunkManager.getFactionAt(fromChunk);

        if (toFaction != fromFaction) {
            if (toFaction != null) {
                player.sendMessage("§7Now entering §b" + toFaction.getName() + "§7 territory.");
            } else {
                player.sendMessage("§7Now entering §fWilderness.");
            }
        }
    }
}
