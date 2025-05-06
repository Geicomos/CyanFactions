package cyansfactions.commands;

import cyansfactions.managers.ChatManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AllyChatToggleCommand implements CommandExecutor {

    private final ChatManager chatManager;

    public AllyChatToggleCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        chatManager.toggleAllyChat(player.getUniqueId());
        boolean isOn = chatManager.isInAllyChat(player.getUniqueId());

        player.sendMessage("§3[CyansFactions]§r Ally chat " + (isOn ? "§aenabled" : "§cdisabled") + "§r.");
        return true;
    }
}
