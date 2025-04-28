package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final int homeCooldown; // seconds
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public HomeCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
        this.homeCooldown = CyansFactions.getInstance().getConfig().getInt("home.cooldown-seconds", 10); // fallback 10s
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§3[CyansFactions]§r Only players can use this command.");
            return true;
        }

        if (!factionManager.hasFaction(player)) {
            player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage("§3[CyansFactions]§r Error finding your faction.");
            return true;
        }

        if (faction.getHome() == null) {
            player.sendMessage("§3[CyansFactions]§r Your faction does not have a home set!");
            return true;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid)) {
            long lastUsed = cooldowns.get(uuid);
            long secondsLeft = (lastUsed + (homeCooldown * 1000L) - now) / 1000L;
            if (secondsLeft > 0) {
                player.sendMessage("§3[CyansFactions]§r §cYou must wait " + secondsLeft + " seconds before using /csf home again.");
                return true;
            }
        }
        
        cooldowns.put(uuid, now);


        player.teleport(faction.getHome());
        player.sendMessage("§3[CyansFactions]§r §aTeleported to your faction home!");
        return true;
    }
}
