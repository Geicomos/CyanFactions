package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpCommand {

    private final FactionManager factionManager;
    private final Economy economy;
    private final double warpCost = CyansFactions.getInstance().getConfig().getDouble("warp.cost-to-use", 50);
    private final int warpCooldownSeconds = CyansFactions.getInstance().getConfig().getInt("warp.cooldown-seconds", 120);
    private final int pvpCooldown = CyansFactions.getInstance().getConfig().getInt("warp.pvp-cooldown", 30);

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> lastCombat = new HashMap<>(); 
    public Map<UUID, Long> getLastCombatMap() {
        return lastCombat;
    }
    
    public WarpCommand(FactionManager factionManager, Economy economy) {
        this.factionManager = factionManager;
        this.economy = economy;
    }

    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /csf warp <warpName> or /csf warp <faction:warp>");
            return;
        }
    
        Faction playerFaction = factionManager.getFactionByPlayer(player);
        if (playerFaction == null) {
            player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
            return;
        }
    
        String input = args[1].toLowerCase();
        String enteredPassword = (args.length >= 3) ? args[2] : null;
    
        String targetFactionName;
        String warpName;
    
        if (input.contains(":")) {
            // Ally warp format
            String[] parts = input.split(":");
            if (parts.length != 2) {
                player.sendMessage("§3[CyansFactions]§r Invalid warp format. Use /csf warp faction:warp.");
                return;
            }
            targetFactionName = parts[0];
            warpName = parts[1];
        } else {
            // Own faction warp
            targetFactionName = playerFaction.getName();
            warpName = input;
        }
    
        Faction targetFaction = factionManager.getFactionByName(targetFactionName);
        if (targetFaction == null) {
            player.sendMessage("§3[CyansFactions]§r Faction '" + targetFactionName + "' does not exist.");
            return;
        }
    
        // Check if player has access
        boolean isOwnFaction = targetFaction.getName().equalsIgnoreCase(playerFaction.getName());
        boolean isAlly = targetFaction.isAlliedWith(playerFaction.getName());
    
        if (!isOwnFaction && !isAlly) {
            player.sendMessage("§3[CyansFactions]§r You can only warp to your own or allied faction warps.");
            return;
        }
    
        // Get the warp location
        Map<String, Location> warps = targetFaction.getWarps();
        if (!warps.containsKey(warpName)) {
            player.sendMessage("§3[CyansFactions]§r Warp '" + warpName + "' does not exist in faction " + targetFaction.getName() + ".");
            return;
        }
    
        String requiredPassword = targetFaction.getWarpPassword(warpName);
        if (requiredPassword != null && !isOwnFaction) {
            if (enteredPassword == null || !enteredPassword.equals(requiredPassword)) {
                player.sendMessage("§3[CyansFactions]§r Warp '" + warpName + "' requires a password.");
                return;
            }
        }        
    
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(playerId)) {
            long nextAvailableTime = cooldowns.get(playerId);
            if (currentTime < nextAvailableTime) {
                long secondsLeft = (nextAvailableTime - currentTime) / 1000;
                player.sendMessage("§3[CyansFactions]§r You must wait " + secondsLeft + " seconds before warping again.");
                return;
            }
        }
    
        if (!economy.has(player, warpCost)) {
            player.sendMessage("§3[CyansFactions]§r You need $" + warpCost + " to warp.");
            return;
        }
    
        if (lastCombat.containsKey(playerId)) {
            long lastHit = lastCombat.get(playerId);
            long msSinceCombat = currentTime - lastHit;
            long msCooldown = (long) (pvpCooldown* 1000);

            if (msSinceCombat < msCooldown) {
                long secondsLeft = (msCooldown - msSinceCombat) / 1000;
                player.sendMessage("§3[CyansFactions]§r You are in combat! Wait " + secondsLeft + "s before warping.");
                return;
            }
        }

        economy.withdrawPlayer(player, warpCost);
        player.teleport(warps.get(warpName));
        cooldowns.put(playerId, currentTime + (warpCooldownSeconds * 1000));
    
        player.sendMessage("§3[CyansFactions]§r §aWarped to '" + warpName + "' in " + targetFaction.getName() + "!");
    }    
    
}
