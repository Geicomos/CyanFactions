package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.entity.Player;

public class DelWarpCommand {

    private final FactionManager factionManager;

    public DelWarpCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /csf delwarp <warpName>");
            return;
        }

        Faction faction = factionManager.getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage("§cYou are not in a faction.");
            return;
        }

        String warpName = args[1].toLowerCase();

        if (!faction.getWarps().containsKey(warpName)) {
            player.sendMessage("§cWarp '" + warpName + "' does not exist.");
            return;
        }

        // Remove warp location and password if exists
        faction.getWarps().remove(warpName);
        faction.getWarpPasswords().remove(warpName);

        player.sendMessage("§aWarp '" + warpName + "' has been deleted!");
    }
}
