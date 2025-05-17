package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetWarpCommand {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;
    private final double baseWarpCost = CyansFactions.getInstance().getConfig().getDouble("warp.cost-to-set", 100);
        private final double multiplyBy = CyansFactions.getInstance().getConfig().getDouble("warp.multiply-by", 3.0);
    private final int warpMax = CyansFactions.getInstance().getConfig().getInt("warp.max-warps", 10);

    public SetWarpCommand(FactionManager factionManager, ChunkManager chunkManager) {
        this.factionManager = factionManager;
        this.chunkManager = chunkManager;
    }

    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§3[CyansFactions]§r Usage: /csf setwarp <name> [password]");
            return;
        }

        Faction faction = factionManager.getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
            return;
        }

        
        String warpName = args[1].toLowerCase();
        String password = args.length >= 3 ? args[2] : null; // Optional password
        int currentWarps = faction.getWarps().size();

        if (faction.getWarps().size() >= warpMax) {
            player.sendMessage("§3[CyansFactions]§r Your faction has reached the max number of warps (" + warpMax + ").");
            return;
        }

        double warpCost = baseWarpCost * Math.pow(multiplyBy, currentWarps);

        if (!faction.withdraw(warpCost)) {
            player.sendMessage("§3[CyansFactions]§r Your Faction needs $" + warpCost + " to set a warp.");
            return;
        }

        Chunk currentChunk = player.getLocation().getChunk();
        Faction ownerFaction = chunkManager.getFactionAt(currentChunk);

        if (ownerFaction == null || !ownerFaction.getName().equalsIgnoreCase(faction.getName())) {
            player.sendMessage("§3[CyansFactions]§r You must be standing in your faction's claimed land to set warps");
            return;
        }

        Location loc = player.getLocation();
        faction.setWarp(warpName, loc);

        if (password != null) {
            faction.setWarpPassword(warpName, password);
        }

        player.sendMessage("§3[CyansFactions]§r Warp '" + warpName + "' has been set!" + (password != null ? " §7(Password protected)" : ""));
    }
}
