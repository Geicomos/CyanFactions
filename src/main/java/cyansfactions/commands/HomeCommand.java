package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> lastCombat = new HashMap<>(); 
    private final int pvpCooldown = CyansFactions.getInstance().getConfig().getInt("warp.pvp-cooldown", 30);
    private final int homeCooldown = CyansFactions.getInstance().getConfig().getInt("home.cooldown-seconds", 10); // fallback 10s

    public Map<UUID, Long> getLastCombatMap() {
        return lastCombat;
    }

    public HomeCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
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
            long secondsLeft = (lastUsed + (homeCooldown * 1000L) - now) / 1000;
            if (secondsLeft > 0) {
                player.sendMessage("§3[CyansFactions]§r §cYou must wait " + secondsLeft + "s before using /csf home again.");
                return true;
            }
        }

        if (lastCombat.containsKey(uuid)) {
            long lastHit = lastCombat.get(uuid);
            long timeSinceCombat = now - lastHit;
            long combatCooldownMillis = pvpCooldown * 1000L;

            if (timeSinceCombat < combatCooldownMillis) {
                long secondsLeft = (combatCooldownMillis - timeSinceCombat) / 1000;
                player.sendMessage("§3[CyansFactions]§r §cYou are in combat! Wait " + secondsLeft + "s before warping.");
                return true;
            }
        }
        cooldowns.put(uuid, now);

        player.teleport(faction.getHome());
        player.sendMessage("§3[CyansFactions]§r §aTeleported to your faction home!");
        return true;
    }
}
