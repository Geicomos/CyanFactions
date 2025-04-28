package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import net.milkbowl.vault.economy.Economy;
import cyansfactions.managers.ChunkManager;
import cyansfactions.CyansFactions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CsfCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;
    private final Economy economy;
    private final HomeCommand homeCommand;

    public CsfCommand(FactionManager factionManager, ChunkManager chunkManager, Economy economy, HomeCommand homeCommand) {
        this.factionManager = factionManager;
        this.chunkManager = chunkManager;
        this.economy = economy;
        this.homeCommand = homeCommand;
    }
    

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /csf <subcommand>");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use faction commands.");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "help":
                new HelpCommand().onCommand(sender, command, label, args);
            break;

            case "createfaction":
                if (args.length != 2) {
                    player.sendMessage("Usage: /csf createfaction <name>");
                    return true;
                }
                new CreateFactionCommand(factionManager, CyansFactions.getEconomy()).onCommand(sender, command, label, new String[]{args[1]});
                break;

            case "invite":
                if (args.length != 2) {
                    player.sendMessage("Usage: /csf invite <player>");
                    return true;
                }
                new InviteCommand(factionManager).onCommand(sender, command, label, new String[]{args[1]});
                break;

            case "claim":
                new ClaimChunkCommand(factionManager, chunkManager).onCommand(sender, command, label, new String[]{});
                break;

            case "unclaim":
                new UnclaimChunkCommand(factionManager, chunkManager).onCommand(sender, command, label, args);
                break;
            
            case "leavefaction":
                new LeaveFactionCommand(factionManager).onCommand(sender, command, label, new String[]{});
                break;

            case "sethome":
                new SetHomeCommand(factionManager, chunkManager).onCommand(sender, command, label, args);
                break;
            

            case "home":
                homeCommand.onCommand(sender, command, label, args);
                break;

            case "deposit":
                if (args.length != 2) {
                    player.sendMessage("Usage: /csf deposit <amount>");
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    if (amount <= 0) {
                        player.sendMessage("§3[CyansFactions]§r Amount must be positive!");
                        return true;
                    }
                    if (!factionManager.hasFaction(player)) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
                        return true;
                    }
                    if (economy.getBalance(player) < amount) {
                        player.sendMessage("§3[CyansFactions]§r You don't have enough money!");
                        return true;
                    }
                    economy.withdrawPlayer(player, amount);
                    Faction playerFaction = factionManager.getFactionByPlayer(player);
                    playerFaction.deposit(amount);
                    player.sendMessage("§3[CyansFactions]§r Deposited $" + amount + " into your faction bank!");
                } catch (NumberFormatException e) {
                    player.sendMessage("§3[CyansFactions]§r Invalid number.");
                }
            break;

            case "balance":
                if (!factionManager.hasFaction(player)) {
                    player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
                    return true;
                }
                Faction faction = factionManager.getFactionByPlayer(player);
                player.sendMessage("§3[CyansFactions]§r Your faction balance is: §a$" + faction.getBalance());
            break;

            case "withdraw":
                if (args.length != 2) {
                    player.sendMessage("Usage: /csf withdraw <amount>");
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    if (amount <= 0) {
                        player.sendMessage("§3[CyansFactions]§r Amount must be positive!");
                        return true;
                    }
                    if (!factionManager.hasFaction(player)) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
                        return true;
                    }
                    Faction playerFaction = factionManager.getFactionByPlayer(player);
                    if (playerFaction.getBalance() < amount) {
                        player.sendMessage("§3[CyansFactions]§r Your faction doesn't have enough money!");
                        return true;
                    }
                    playerFaction.withdraw(amount);
                    economy.depositPlayer(player, amount);
                    player.sendMessage("§3[CyansFactions]§r Withdrew " + amount + "$ from your faction bank!");
                } catch (NumberFormatException e) {
                    player.sendMessage("§3[CyansFactions]§r Invalid number.");
                }
                break;

            default:
                player.sendMessage("Unknown subcommand. Try: createfaction, invite, claimchunk, leavefaction, sethome, home");
                break;
        }

        return true;
    }
}
