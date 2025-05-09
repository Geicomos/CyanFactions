package cyansfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand3 implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§b=== CyansFactions Commands3 ===");
        sender.sendMessage("§3/csf delwarp §7- Deletes a warp");
        sender.sendMessage("§3/csf listwarps §7- Lists all faction warps");
        sender.sendMessage("§3/csf ally <faction> §7- Ally with another faction");
        sender.sendMessage("§3/csf acceptally <faction> §7- Accept invite to ally");
        sender.sendMessage("§3/csf unally <faction> §7- Unally with a faction");
        sender.sendMessage("§3/csf promote <faction> §7- Promote a member to co-leader");
        sender.sendMessage("§3/csf demote <faction> §7- Demote a co-leader");
        sender.sendMessage("§3/csf list <faction> §7- List all factions and data");
        sender.sendMessage("§3/csf help4 §7- Shows you the fourth help page");

        return true;
    }
}
