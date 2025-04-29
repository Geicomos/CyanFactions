package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand implements CommandExecutor {

    private final FactionManager factionManager;

    public InviteCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player inviter)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            inviter.sendMessage("Usage: /csr invite <player>");
            return true;
        }

        if (!factionManager.hasFaction(inviter)) {
            inviter.sendMessage("§3[CyansFactions]§r You are not in a faction!");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(inviter);
        if (faction == null) {
            inviter.sendMessage("§3[CyansFactions]§r Error finding your faction.");
            return true;
        }

        // ✅ Grab max members from config
        int maxMembers = CyansFactions.getInstance().getConfig().getInt("faction.max-members", 20);

        if (faction.getMembers().size() >= maxMembers) {
            inviter.sendMessage("§3[CyansFactions]§r §cYour faction is full! (Max " + maxMembers + " members)");
            return true;
        }

        Player invitee = Bukkit.getPlayer(args[0]);
        if (invitee == null) {
            inviter.sendMessage("§3[CyansFactions]§r §cPlayer not found.");
            return true;
        }

        factionManager.invitePlayer(inviter, invitee);
        inviter.sendMessage("§3[CyansFactions]§r §aInvited " + invitee.getName() + " to your faction!");
        invitee.sendMessage("§aYou have been invited to join " + faction.getName() + "! Use §b/csf acceptinvite§a to join.");
        return true;
    }
}
