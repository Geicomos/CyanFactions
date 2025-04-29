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
    private final int warpCooldownSeconds = CyansFactions.getInstance().getConfig().getInt("warp.cooldown-seconds", 30);

    // Track cooldowns: Player UUID -> timestamp of when they can warp again
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public WarpCommand(FactionManager factionManager, Economy economy) {
        this.factionManager = factionManager;
        this.economy = economy;
    }

    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /csf warp <warpName> [password]");
            return;
        }

        Faction faction = factionManager.getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage("§cYou are not in a faction.");
            return;
        }

        String warpName = args[1].toLowerCase();
        String enteredPassword = (args.length >= 3) ? args[2] : null;

        if (!faction.getWarps().containsKey(warpName)) {
            player.sendMessage("§cWarp '" + warpName + "' does not exist.");
            return;
        }

        String requiredPassword = faction.getWarpPassword(warpName);
        if (requiredPassword != null) {
            if (enteredPassword == null || !enteredPassword.equals(requiredPassword)) {
                player.sendMessage("§cIncorrect password for warp '" + warpName + "'.");
                return;
            }
        }

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(playerId)) {
            long nextAvailableTime = cooldowns.get(playerId);
            if (currentTime < nextAvailableTime) {
                long secondsLeft = (nextAvailableTime - currentTime) / 1000;
                player.sendMessage("§cYou must wait " + secondsLeft + " seconds before warping again.");
                return;
            }
        }

        if (!economy.has(player, warpCost)) {
            player.sendMessage("§cYou need $" + warpCost + " to warp.");
            return;
        }

        economy.withdrawPlayer(player, warpCost);

        Location warpLocation = faction.getWarps().get(warpName);
        player.teleport(warpLocation);

        // Set new cooldown
        cooldowns.put(playerId, currentTime + (warpCooldownSeconds * 1000));

        player.sendMessage("§aWarped to '" + warpName + "' successfully!");
    }
}
