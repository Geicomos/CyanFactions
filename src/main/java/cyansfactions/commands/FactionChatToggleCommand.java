// cyansfactions/commands/FactionChatToggleCommand.java
package cyansfactions.commands;

import cyansfactions.managers.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionChatToggleCommand implements CommandExecutor {

    private final ChatManager chatManager;

    public FactionChatToggleCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        chatManager.toggleFactionChat(player.getUniqueId());
        boolean isOn = chatManager.isInFactionChat(player.getUniqueId());

        player.sendMessage("§3[CyansFactions]§r Faction chat " + (isOn ? "§aenabled" : "§cdisabled") + "§r.");
        return true;
    }
}
