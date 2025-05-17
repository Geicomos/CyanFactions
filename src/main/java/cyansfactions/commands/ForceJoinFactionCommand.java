package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceJoinFactionCommand implements CommandExecutor {

    private final FactionManager factionManager;

    public ForceJoinFactionCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executer)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!executer.isOp() && !executer.hasPermission("cyansfactions.forcejoin")) {
            executer.sendMessage("§3[CyansFactions]§r You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            executer.sendMessage("§3[CyansFactions]§r Usage: /csf forcejoin <faction>");
            return true;
        }

        Faction targetFaction = factionManager.getFactionByName(args[0]);
        if (targetFaction == null) {
            for (Faction f : factionManager.getAllFactions()) {
                if (f.getName().equalsIgnoreCase(args[0])) {
                    targetFaction = f;
                    break;
                }
            }
        }
        if (targetFaction == null) {
            executer.sendMessage("§3[CyansFactions]§r That faction doesn't exist.");
            return true;
        }

        if (factionManager.hasFaction(executer)) {
            Faction current = factionManager.getFactionByPlayer(executer);
            if (current != null) current.removeMember(executer.getUniqueId());
        }

        targetFaction.addMember(executer.getUniqueId());
        factionManager.setFactionForPlayer(executer, targetFaction);

        executer.sendMessage("§3[CyansFactions]§r You force‑joined " + targetFaction.getName() + ".");
        String announce = "§3[CyansFactions]§r §d" + executer.getName() + " has joined the faction.";
        for (UUID uuid : targetFaction.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline() && !p.equals(executer)) p.sendMessage(announce);
        }
        return true;
    }
}
