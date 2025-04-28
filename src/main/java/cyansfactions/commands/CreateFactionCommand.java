package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.FactionManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateFactionCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final Economy economy;
    private final double createCost = CyansFactions.getInstance().getConfig().getDouble("faction.create-cost");

    public CreateFactionCommand(FactionManager factionManager, Economy economy) {
        this.factionManager = factionManager;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /createfaction <name>");
            return true;
        }

        if (factionManager.hasFaction(player)) {
            player.sendMessage("§3[CyansFactions]§r You are already in a faction!");
            return true;
        }

        if (economy.getBalance(player) < createCost) {
            player.sendMessage("§3[CyansFactions]§r You don't have enough money to create a faction. It costs $" + createCost + ".");
            return true;
        }

        String factionName = args[0];

        boolean success = factionManager.createFaction(player, factionName);
        if (success) {
            economy.withdrawPlayer(player, createCost);

            player.sendMessage("§3[CyansFactions]§r You created a new faction called: " + factionName);

            Bukkit.broadcastMessage("§3[CyansFactions]§r " + player.getName() + " has founded a new faction called: " + factionName);

        } else {
            player.sendMessage("§3[CyansFactions]§r A faction with that name already exists.");
        }
        return true;
    }
}
