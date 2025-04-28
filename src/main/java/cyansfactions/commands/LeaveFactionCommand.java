package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveFactionCommand implements CommandExecutor {

    private final FactionManager factionManager;

    public LeaveFactionCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!factionManager.hasFaction(player)) {
            player.sendMessage("§3[CyansFactions]§r You are not in a faction!");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(player);

        if (faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("§3[CyansFactions]§r You are the leader! Disband the faction or transfer leadership first.");
            return true;
        }

        faction.removeMember(player.getUniqueId());
        factionManager.removePlayerFromFaction(player);
        player.sendMessage("§3[CyansFactions]§r You have left your faction.");

        return true;
    }
}
