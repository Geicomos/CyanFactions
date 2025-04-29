package cyansfactions.listeners;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatFormatListener implements Listener {

    private final FactionManager factionManager;

    public ChatFormatListener(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Faction faction = factionManager.getFactionByPlayer(player); 

        String factionTag = "";
        if (faction != null) {
            factionTag = "§7[" + faction.getName() + "§7] ";
        }

        event.setFormat(factionTag + "§f" + player.getName() + "§7: §f" + event.getMessage());
    }
}
