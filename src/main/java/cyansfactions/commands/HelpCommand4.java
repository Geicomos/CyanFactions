package cyansfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand4 implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§b=== CyansFactions Commands4 ===");
        sender.sendMessage("§3/csf factionchat §7- Change to faction chat only");
        sender.sendMessage("§3/csf allychat <faction> §7- Chat with a specific ally faction");

        return true;
    }
}
