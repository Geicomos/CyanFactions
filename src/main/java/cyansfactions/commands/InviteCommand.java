package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import cyansfactions.models.FactionRole;
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
            inviter.sendMessage("§3[CyansFactions]§r Usage: /csf invite <player>");
            return true;
        }

        if (!factionManager.hasFaction(inviter)) {
            inviter.sendMessage("§3[CyansFactions]§r You are not in a faction!");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(inviter);
        if (faction == null) {
            inviter.sendMessage("§3[CyansFactions]§r Error: Could not find your faction.");
            return true;
        }

        // ✅ Role-based permission check
        FactionRole role = faction.getRole(inviter.getUniqueId());
        if (role != FactionRole.OWNER && role != FactionRole.COLEADER) {
            inviter.sendMessage("§3[CyansFactions]§r Only the owner or co-leaders can invite players.");
            return true;
        }

        // ✅ Check member limit
        int maxMembers = CyansFactions.getInstance().getConfig().getInt("faction.max-members", 20);
        if (faction.getMembers().size() >= maxMembers) {
            inviter.sendMessage("§3[CyansFactions]§r §cYour faction is full! (Max " + maxMembers + " members)");
            return true;
        }

        // ✅ Get target player
        Player invitee = Bukkit.getPlayer(args[0]);
        if (invitee == null || !invitee.isOnline()) {
            inviter.sendMessage("§3[CyansFactions]§r §cThat player is not online.");
            return true;
        }

        if (factionManager.hasFaction(invitee)) {
            inviter.sendMessage("§3[CyansFactions]§r §cThat player is already in a faction.");
            return true;
        }

        // ✅ Send invite
        factionManager.invitePlayer(inviter, invitee);
        inviter.sendMessage("§3[CyansFactions]§r §aYou invited §b" + invitee.getName() + "§a to your faction.");
        invitee.sendMessage("§aYou have been invited to join §b" + faction.getName() + "§a! Use §e/csf acceptinvite §ato join.");
        return true;
    }
}
