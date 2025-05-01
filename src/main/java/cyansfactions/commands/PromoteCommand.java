package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import cyansfactions.models.FactionRole;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PromoteCommand implements CommandExecutor {

    private final FactionManager factionManager;

    public PromoteCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

        @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executer)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            executer.sendMessage("§3[CyansFactions]§r Usage: /csf invite <player>");
            return true;
        }

        if (!factionManager.hasFaction(executer)) {
            executer.sendMessage("§3[CyansFactions]§r You are not in a faction!");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(executer);
        if (faction == null) {
            executer.sendMessage("§3[CyansFactions]§r Error: Could not find your faction.");
            return true;
        }

        // ✅ Role-based permission check
        FactionRole role = faction.getRole(executer.getUniqueId());
        if (role != FactionRole.OWNER && role != FactionRole.COLEADER) {
            executer.sendMessage("§3[CyansFactions]§r Only the owner or co-leaders can invite players.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target.getUniqueId() == null || !target.isOnline()) {
            executer.sendMessage("§3[CyansFactions]§r §cThat player is not online.");
            return true;
        }

        if (faction.getRole(target.getUniqueId()) == FactionRole.COLEADER) {
            executer.sendMessage("§3[CyansFactions]§r That player is already a co-leader.");
            return true;
        }

        faction.setRole(target.getUniqueId(), FactionRole.COLEADER);
        executer.sendMessage("§3[CyansFactions]§r §aYou promoted §b" + target.getName() + " §ato Co-Leader!");
        target.sendMessage("§3[CyansFactions]§r §aYou have been promoted to Co-Leader in §b" + faction.getName() + "§a!");
        return true;
    }
}
