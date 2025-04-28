package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptInviteCommand implements CommandExecutor {

    private final FactionManager factionManager;

    public AcceptInviteCommand(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!factionManager.hasInvite(player)) {
            player.sendMessage("§3[CyansFactions]§r You have no invites!");
            return true;
        }

        factionManager.acceptInvite(player);
        player.sendMessage("§3[CyansFactions]§r You have joined the faction");
        return true;
    }
}
