package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.managers.WarManager;
import cyansfactions.models.Faction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PeaceCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final WarManager warManager;

    public PeaceCommand(FactionManager factionManager, WarManager warManager) {
        this.factionManager = factionManager;
        this.warManager = warManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!factionManager.hasFaction(player)) {
            player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§3[CyansFactions]§r Usage: /csf peace <faction>");
            return true;
        }

        Faction playerFaction = factionManager.getFactionByPlayer(player);

        String targetFactionName = args[0];
        Faction targetFaction = factionManager.getFactionByName(targetFactionName);

        if (targetFaction == null) {
            player.sendMessage("§3[CyansFactions]§r That faction does not exist.");
            return true;
        }

        if (!warManager.areFactionsAtWar(playerFaction, targetFaction)) {
            player.sendMessage("§3[CyansFactions]§r You are not at war with that faction.");
            return true;
        }

        warManager.endWar(playerFaction, targetFaction);
        player.sendMessage("§3[CyansFactions]§r §aYou have made peace with " + targetFaction.getName() + "!");
        return true;
    }
}
