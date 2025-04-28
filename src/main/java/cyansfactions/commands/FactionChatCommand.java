package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FactionChatCommand implements CommandExecutor, Listener {

    private static final Set<UUID> factionChatEnabled = new HashSet<>();
    private final FactionManager factionManager;

    public FactionChatCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can toggle faction chat!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (factionChatEnabled.contains(uuid)) {
            factionChatEnabled.remove(uuid);
            player.sendMessage("§3[CyansFactions]§r You are now talking in §apublic chat§r.");
        } else {
            factionChatEnabled.add(uuid);
            player.sendMessage("§3[CyansFactions]§r You are now talking in §bFaction Chat§r.");
        }
        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!factionChatEnabled.contains(uuid)) {
            return; // Public chat, do not modify
        }

        // Player is using faction chat
        Faction faction = factionManager.getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage("§3[CyansFactions]§r You are not in a faction. Switching you back to public chat.");
            factionChatEnabled.remove(uuid);
            return;
        }

        event.setCancelled(true); // Cancel normal chat

        String message = "§b[Faction] §f" + player.getName() + "§7: §f" + event.getMessage();

        for (UUID memberUUID : faction.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }
}
