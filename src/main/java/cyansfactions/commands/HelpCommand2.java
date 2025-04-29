package cyansfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand2 implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§b=== CyansFactions Commands2 ===");
        sender.sendMessage("§3/csf invite <player> §7- Invite a player to your faction");
        sender.sendMessage("§3/csf acceptinvite <faction> §7- Accept invite to a faction");
        sender.sendMessage("§3/csf leavefaction §7- Leave your current faction");
        sender.sendMessage("§3/csf disband §7- Disband your current faction all together");
        sender.sendMessage("§3/csf war §7- Declares war on a faction, allows greifing");
        sender.sendMessage("§3/csf peace §7- Removes war from both factions");
        sender.sendMessage("§3/csf warp <name> <password> §7- Warp to a setwarp");
        sender.sendMessage("§3/csf setwarp <password> §7- Sets a warp");
        sender.sendMessage("§3/csf help3 §7- Shows you the third help page");
        return true;
    }
}
