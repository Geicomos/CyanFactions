package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.entity.Player;

import java.util.Set;

public class ListWarpsCommand {

    private final FactionManager factionManager;

    public ListWarpsCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    public void execute(Player player, String[] args) {
        Faction faction = factionManager.getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
            return;
        }

        Set<String> warps = faction.getWarps().keySet();
        if (warps.isEmpty()) {
            player.sendMessage("§3[CyansFactions]§r Your faction has no warps set.");
            return;
        }

        player.sendMessage("§3[CyansFactions]§r Faction Warps:");
        for (String warp : warps) {
            player.sendMessage("§7- §b" + warp);
        }
    }
}
