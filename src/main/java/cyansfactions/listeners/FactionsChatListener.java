package cyansfactions.listeners;

import cyansfactions.managers.ChatManager;
import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.managers.WarManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.UUID;

public class FactionsChatListener implements Listener {

    private final FactionManager factionManager;
    private final ChatManager chatManager;
    private final ChunkManager chunkManager;
    private final WarManager warManager;

    public FactionsChatListener(FactionManager factionManager, ChatManager chatManager, ChunkManager chunkManager, WarManager warManager) {
        this.factionManager = factionManager;
        this.chatManager = chatManager;
        this.chunkManager = chunkManager;
        this.warManager = warManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        UUID senderUUID = sender.getUniqueId();
        Faction senderFaction = factionManager.getFactionByPlayer(sender);

        // If faction chat is toggled for this player
        if (chatManager.isInFactionChat(senderUUID)) {
            if (senderFaction == null) {
                sender.sendMessage("§3[CyansFactions] §cYou are not in a faction.");
                chatManager.setFactionChat(senderUUID, false);
                return;
            }

            String message = "§d[Faction Chat] §7" + sender.getName() + "§f: " + event.getMessage();

            for (UUID memberUUID : senderFaction.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null && member.isOnline()) {
                    member.sendMessage(message);
                }
            }

            event.setCancelled(true); // Don't send to global chat
            return;
        }

        // Otherwise, modify their global chat name with faction tag
        String tag = "";
        if (senderFaction != null) {
            tag = "§7[" + senderFaction.getName() + "] ";
        }

        sender.setDisplayName(tag + "§f" + sender.getName()); // for EssentialsChat etc.
        
    }

    @EventHandler
        public void onBucketEmpty(PlayerBucketEmptyEvent event) {
            Player player = event.getPlayer();
            Block block = event.getBlockClicked().getRelative(event.getBlockFace());
            Chunk chunk = block.getChunk();
            Faction chunkFaction = chunkManager.getFactionAt(chunk);

            if (chunkFaction == null) return;
            if (chunkFaction.hasMember(player.getUniqueId())) return;

            Faction playerFaction = chunkManager.getFactionAt(player.getLocation().getChunk());

            // Block bucket use unless in war and not placing water/lava
            if (event.getBucket() == Material.WATER_BUCKET || event.getBucket() == Material.LAVA_BUCKET) {
                event.setCancelled(true);
                player.sendMessage(chunkFaction.getName() + " §cDoes not allow you to pour liquids here.");
                return;
            }

            if (playerFaction == null || !warManager.isAtWar(playerFaction)) {
                event.setCancelled(true);
                player.sendMessage(chunkFaction.getName() + " §cDoes not allow you to place blocks here.");
            }
        }    
}
