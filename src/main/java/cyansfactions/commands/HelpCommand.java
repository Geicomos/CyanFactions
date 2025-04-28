package cyansfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§b=== CyansFactions Commands ===");
        sender.sendMessage("§3/csr createfaction <name> §7- Create a new faction");
        sender.sendMessage("§3/csr invite <player> §7- Invite a player to your faction");
        sender.sendMessage("§3/csr claimchunk §7- Claim the chunk you are standing in");
        sender.sendMessage("§3/csr leavefaction §7- Leave your current faction");
        sender.sendMessage("§3/csr sethome §7- Set your faction's home (leader only)");
        sender.sendMessage("§3/csr home §7- Teleport to your faction's home");
        sender.sendMessage("§3/csr help §7- Show this help page");
        return true;
    }
}
