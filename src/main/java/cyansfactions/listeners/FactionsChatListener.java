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
                chatManager.setAllyChat(senderUUID, null);
                return;
            }

            String targetAllyName = chatManager.getAllyChatTarget(senderUUID);
            if (targetAllyName == null) {
                sender.sendMessage("§3[CyansFactions] §cNo ally faction selected for chat.");
                chatManager.setAllyChat(senderUUID, null);
                return;
            }

            if (!senderFaction.getAllies().contains(targetAllyName)) {
                sender.sendMessage("§3[CyansFactions] §cYou are not allied with " + targetAllyName + ".");
                chatManager.setAllyChat(senderUUID, null);
                return;
            }

            Faction allyFaction = factionManager.getFactionByName(targetAllyName);
            if (allyFaction == null) {
                sender.sendMessage("§3[CyansFactions] §cThe ally faction no longer exists.");
                chatManager.setAllyChat(senderUUID, null);
                return;
            }

            String message = "§d[Ally Chat - " + targetAllyName + "] §7" + sender.getName() + "§f: " + event.getMessage();
            Set<UUID> recipients = new HashSet<>(senderFaction.getMembers());
            recipients.addAll(allyFaction.getMembers());

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
