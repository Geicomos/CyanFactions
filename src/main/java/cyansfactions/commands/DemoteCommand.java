package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import cyansfactions.models.FactionRole;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoteCommand implements CommandExecutor {

    private final FactionManager factionManager;

    public DemoteCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executer)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            executer.sendMessage("§3[CyansFactions]§r Usage: /csf demote <player>");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(executer);
        if (faction == null || !faction.getLeader().equals(executer.getUniqueId())) {
            executer.sendMessage("§3[CyansFactions]§r Only the faction owner can demote co-leaders.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !faction.hasMember(target.getUniqueId())) {
            executer.sendMessage("§3[CyansFactions]§r That player is not in your faction.");
            return true;
        }

        if (faction.getRole(target.getUniqueId()) != FactionRole.COLEADER) {
            executer.sendMessage("§3[CyansFactions]§r That player is not a co-leader.");
            return true;
        }

        if (faction.getRole(executer.getUniqueId()) == FactionRole.OWNER) {
            executer.sendMessage("§3[CyansFactions]§r You cant demote a owner.");
            return true;
        }

        faction.setRole(target.getUniqueId(), FactionRole.MEMBER);
        executer.sendMessage("§3[CyansFactions]§r §a" + target.getName() + " has been demoted to Member.");
        target.sendMessage("§3[CyansFactions]§r §cYou have been demoted to Member in " + faction.getName() + ".");
        return true;
    }
}
