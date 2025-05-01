package cyansfactions.listeners;

import cyansfactions.managers.ChatManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class FactionsChatListener implements Listener {

    private final FactionManager factionManager;
    private final ChatManager chatManager;

    public FactionsChatListener(FactionManager factionManager, ChatManager chatManager) {
        this.factionManager = factionManager;
        this.chatManager = chatManager;
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
}
