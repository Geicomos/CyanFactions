package cyansfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§b=== CyansFactions Commands ===");
        sender.sendMessage("§3/csf createfaction <name> §7- Create a new faction");
        sender.sendMessage("§3/csf invite <player> §7- Invite a player to your faction");
        sender.sendMessage("§3/csf claim §7- Claim the chunk you are standing in");
        sender.sendMessage("§3/csf unclaim §7- Unclaim the chunk you are standing in");
        sender.sendMessage("§3/csf leavefaction §7- Leave your current faction");
        sender.sendMessage("§3/csf sethome §7- Set your faction's home (leader only)");
        sender.sendMessage("§3/csf home §7- Teleport to your faction's home");
        sender.sendMessage("§3/csf deposit §7- Deposit money into your faction bank");
        sender.sendMessage("§3/csf withdraw §7- Withdraw money from your faction bank");
        sender.sendMessage("§3/csf balance §7- Check your faciton balance");
        sender.sendMessage("§3/csf help §7- Show this help page");
        return true;
    }
}
