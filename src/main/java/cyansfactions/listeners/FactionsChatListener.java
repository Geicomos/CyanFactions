package cyansfactions.listeners;

import cyansfactions.managers.ChatManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
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

        if (chatManager.isInFactionChat(senderUUID)) {
            if (senderFaction == null) {
                sender.sendMessage("§3[CyansFactions] §cYou are not in a faction.");
                chatManager.setFactionChat(senderUUID, false);
                return;
            }

            String message = "§d[Faction Chat] §7" + sender.getName() + "§f: " + event.getMessage();

            for (UUID memberUUID : senderFaction.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null && member.isOnline()) member.sendMessage(message);
            }

            event.setCancelled(true);
            return;
        }

        if (chatManager.isInAllyChat(senderUUID)) {
            if (senderFaction == null) {
                sender.sendMessage("§3[CyansFactions] §cYou are not in a faction.");
                chatManager.setFactionChat(senderUUID, false);
                return;
            }

            String message = "§d[Ally Chat] §7" + sender.getName() + "§f: " + event.getMessage();
            Set<UUID> recipients = new HashSet<>(senderFaction.getMembers());

            for (String allyName : senderFaction.getAllies()) {
                Faction allyFaction = factionManager.getFactionByName(allyName);
                if (allyFaction != null) recipients.addAll(allyFaction.getMembers());
            }

            for (UUID uuid : recipients) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null && p.isOnline()) p.sendMessage(message);
            }

            event.setCancelled(true);
            return;
        }

        String tag = senderFaction != null ? "§7[" + senderFaction.getName() + "] " : "";
        sender.setDisplayName(tag + "§f" + sender.getName());
    }
}
