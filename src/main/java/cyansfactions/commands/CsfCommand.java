package cyansfactions.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import cyansfactions.CyansFactions;
import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.managers.WarManager;
import cyansfactions.models.Faction;
import cyansfactions.storage.FactionsDataManager;
import net.milkbowl.vault.economy.Economy;

public class CsfCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final FactionsDataManager factionsDataManager;
    private final ChunkManager chunkManager;
    private final Economy economy;
    private final HomeCommand homeCommand;
    private final WarManager warManager; 
    private final WarpCommand warpCommand;
    private final DelWarpCommand delWarpCommand;
    private final ListWarpsCommand listWarpsCommand;

    public CsfCommand(FactionManager factionManager, FactionsDataManager factionsDataManager, ChunkManager chunkManager, Economy economy, HomeCommand homeCommand, WarManager warManager, WarpCommand warpCommand, DelWarpCommand delWarpCommand, ListWarpsCommand listWarpsCommand) {
        this.factionManager = factionManager;
        this.chunkManager = chunkManager;
        this.economy = economy;
        this.homeCommand = homeCommand;
        this.warManager = warManager;
        this.factionsDataManager = factionsDataManager; 
        this.warpCommand = warpCommand;
        this.delWarpCommand = delWarpCommand;
        this.listWarpsCommand = listWarpsCommand;
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

            case "help2":
                new HelpCommand2().onCommand(sender, command, label, args);
                break;

            case "help3":
                new HelpCommand3().onCommand(sender, command, label, args);
                break;

            case "createfaction":
                new CreateFactionCommand(factionManager, CyansFactions.getEconomy()).onCommand(sender, command, label, new String[]{args[1]});
                break;

            case "invite":
                new InviteCommand(factionManager).onCommand(sender, command, label, new String[]{args[1]});
                break;

            case "acceptinvite":
                new AcceptInviteCommand(factionManager).onCommand(sender, command, label, new String[]{});
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

            case "disband":
                new DisbandCommand(factionManager, chunkManager, factionsDataManager).onCommand(sender, command, label, args);
                break;
            
            case "setwarp":
                new SetWarpCommand(factionManager, economy, chunkManager).execute(player, args);
                break;

            case "warp":
                warpCommand.execute(player, args);
                break;
                
            case "delwarp":
                delWarpCommand.execute(player, args);
                break;

            case "listwarps":
                listWarpsCommand.execute(player, args);
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
                    Faction playerFaction = factionManager.getFactionByPlayer(player);
                    if (playerFaction == null) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
                        return true;
                    }                    
                    if (warManager.isAtWar(playerFaction)) {
                        player.sendMessage("§3[CyansFactions]§r You can't deposit money during a war!");
                        return true;
                    }                    
                    if (economy.getBalance(player) < amount) {
                        player.sendMessage("§3[CyansFactions]§r You don't have enough money!");
                        return true;
                    }
                    economy.withdrawPlayer(player, amount);
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
                    Faction playerFaction = factionManager.getFactionByPlayer(player);
                    double amount = Double.parseDouble(args[1]);
                    if (amount <= 0) {
                        player.sendMessage("§3[CyansFactions]§r Amount must be positive!");
                        return true;
                    }
                    if (!factionManager.hasFaction(player)) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
                        return true;
                    }
                    if (warManager.isAtWar(playerFaction)) {
                        player.sendMessage("§3[CyansFactions]§r You can't withdraw money during a war!");
                        return true;
                    }  
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

            case "war":
                    if (args.length != 2) {
                        player.sendMessage("§3[CyansFactions]§r Usage: /csf war <targetFaction>");
                        return true;
                    }

                    if (!factionManager.hasFaction(player)) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction!");
                        return true;
                    }

                    Faction yourFaction = factionManager.getFactionByPlayer(player);
                    if (!yourFaction.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage("§3[CyansFactions]§r Only faction leaders can declare war!");
                        return true;
                    }

                    Faction targetFaction = factionManager.getFactionByName(args[1]);
                    if (targetFaction == null) {
                        player.sendMessage("§3[CyansFactions]§r Target faction not found.");
                        return true;
                    }

                    if (yourFaction.getName().equalsIgnoreCase(targetFaction.getName())) {
                        player.sendMessage("§3[CyansFactions]§r You cannot declare war on your own faction!");
                        return true;
                    }

                    if (warManager.isAtWar(yourFaction) || warManager.isAtWar(targetFaction)) {
                        player.sendMessage("§3[CyansFactions]§r One of the factions is already at war!");
                        return true;
                    }

                    warManager.declareWar(yourFaction, targetFaction);
                    Bukkit.broadcastMessage("§3[CyansFactions] §c" + yourFaction.getName() + " has declared WAR on " + targetFaction.getName() + "!");
                    break;

            case "peace":
                    if (args.length != 2) {
                        player.sendMessage("§3[CyansFactions]§r Usage: /csf peace <targetFaction>");
                        return true;
                    }

                    if (!factionManager.hasFaction(player)) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction!");
                        return true;
                    }

                    Faction yourFaction2 = factionManager.getFactionByPlayer(player);
                    if (!yourFaction2.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage("§3[CyansFactions]§r Only faction leaders can offer peace!");
                        return true;
                    }

                    Faction peaceTargetFaction = factionManager.getFactionByName(args[1]);
                    if (peaceTargetFaction == null) {
                        player.sendMessage("§3[CyansFactions]§r Target faction not found.");
                        return true;
                    }

                    if (!warManager.areFactionsAtWar(yourFaction2, peaceTargetFaction)) {
                        player.sendMessage("§3[CyansFactions]§r You are not at war with that faction!");
                        return true;
                    }

                    UUID enemyLeader = peaceTargetFaction.getLeader();
                    Player enemyPlayer = Bukkit.getPlayer(enemyLeader);
                    
                    // Always add the pending peace offer first
                    warManager.addPendingPeaceOffer(peaceTargetFaction.getName(), yourFaction2.getName());
                    
                    if (enemyPlayer != null && enemyPlayer.isOnline()) {
                        player.sendMessage("§3[CyansFactions]§r §aPeace offer sent! Waiting for the enemy leader to respond");
                        enemyPlayer.sendMessage("§3[CyansFactions]§r §a" + yourFaction2.getName() + " wants peace! Type /csf acceptpeace " + yourFaction2.getName() + " to accept.");
                    } else {
                        // Leader is offline: wait until they join
                        player.sendMessage("§3[CyansFactions]§r §aPeace offer sent! Will notify the enemy leader when they come online.");

                        // Listen for when the leader comes online
                        CyansFactions.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
                            @org.bukkit.event.EventHandler
                            public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                                Player joinedPlayer = event.getPlayer();
                                if (joinedPlayer.getUniqueId().equals(enemyLeader)) {
                                    // When the leader joins, notify them
                                    joinedPlayer.sendMessage("§3[CyansFactions]§r §a" + yourFaction2.getName() + " wants peace! Type /csf acceptpeace " + yourFaction2.getName() + " to accept.");

                                    // Unregister this listener to avoid memory leaks
                                    org.bukkit.event.HandlerList.unregisterAll(this);
                                }
                            }
                        }, CyansFactions.getInstance());                    }
                    break;                    

            case "acceptpeace":
                    if (args.length != 2) {
                        player.sendMessage("§3[CyansFactions]§r Usage: /csf acceptpeace <faction>");
                        return true;
                    }

                    if (!factionManager.hasFaction(player)) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction!");
                        return true;
                    }

                    Faction myFaction = factionManager.getFactionByPlayer(player);
                    if (!myFaction.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage("§3[CyansFactions]§r Only faction leaders can accept peace offers!");
                        return true;
                    }

                    Faction requestedFaction = factionManager.getFactionByName(args[1]);
                    if (requestedFaction == null) {
                        player.sendMessage("§3[CyansFactions]§r That faction doesn't exist.");
                        return true;
                    }

                    if (!warManager.hasPendingPeace(myFaction.getName(), requestedFaction.getName())) {
                        player.sendMessage("§3[CyansFactions]§r There is no pending peace offer from " + requestedFaction.getName() + ".");
                        return true;
                    }
                        
                    warManager.endWar(myFaction, requestedFaction);
                    warManager.removePendingPeace(requestedFaction.getName(), myFaction.getName());                        

                    Bukkit.broadcastMessage("§3[CyansFactions] §a" + myFaction.getName() + " and " + requestedFaction.getName() + " have made peace!");
                    break;

            case "ally":
                    if (args.length != 2) {
                        player.sendMessage("§3[CyansFactions]§r Usage: /csf ally <faction>");
                        return true;
                    }
                
                    if (!factionManager.hasFaction(player)) {
                        player.sendMessage("§3[CyansFactions]§r You are not in a faction!");
                        return true;
                    }
                
                    Faction senderFaction = factionManager.getFactionByPlayer(player);
                    if (!senderFaction.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage("§3[CyansFactions]§r Only faction leaders can form alliances.");
                        return true;
                    }
                
                    Faction targetFaction2 = factionManager.getFactionByName(args[1]);
                    if (targetFaction2 == null) {
                        player.sendMessage("§3[CyansFactions]§r That faction doesn't exist.");
                        return true;
                    }
                
                    if (senderFaction.getName().equalsIgnoreCase(targetFaction2.getName())) {
                        player.sendMessage("§3[CyansFactions]§r You can't ally with your own faction.");
                        return true;
                    }
                
                    if (warManager.areFactionsAtWar(senderFaction, targetFaction2)) {
                        player.sendMessage("§3[CyansFactions]§r You cannot ally with a faction you're at war with.");
                        return true;
                    }
                
                    if (senderFaction.isAlliedWith(targetFaction2.getName())) {
                        player.sendMessage("§3[CyansFactions]§r You are already allied with " + targetFaction2.getName() + ".");
                        return true;
                    }
                                
                    UUID targetLeaderUUID = targetFaction2.getLeader();
                    Player targetLeader = Bukkit.getPlayer(targetLeaderUUID);
                
                    if (targetLeader != null && targetLeader.isOnline()) {
                        targetLeader.sendMessage("§3[CyansFactions]§r §a" + senderFaction.getName() + " wants to ally with your faction.");
                        targetLeader.sendMessage("§3[CyansFactions]§r §aType /csf acceptally " + senderFaction.getName() + " to accept.");
                        player.sendMessage("§3[CyansFactions]§r §aAlly request sent. Waiting for response.");
                        factionManager.addPendingAlly(targetFaction2.getName(), senderFaction.getName());
                    } else {
                        player.sendMessage("§3[CyansFactions]§r §aAlly request sent. Will notify the enemy leader when they come online.");
                        CyansFactions.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
                            @org.bukkit.event.EventHandler
                            public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                                Player joined = event.getPlayer();
                                if (joined.getUniqueId().equals(targetLeaderUUID)) {
                                    joined.sendMessage("§3[CyansFactions]§r §a" + senderFaction.getName() + " wants to ally with your faction.");
                                    joined.sendMessage("§3[CyansFactions]§r §aType /csf acceptally " + senderFaction.getName() + " to accept.");
                                    factionManager.addPendingAlly(targetFaction2.getName(), senderFaction.getName());
                                    org.bukkit.event.HandlerList.unregisterAll(this);
                                }
                            }
                        }, CyansFactions.getInstance());
                    }
                
                    break;

            case "unally":
                if (args.length != 2) {
                    player.sendMessage("§3[CyansFactions]§r Usage: /csf unally <faction>");
                    return true;
                }
            
                if (!factionManager.hasFaction(player)) {
                    player.sendMessage("§3[CyansFactions]§r You are not in a faction!");
                    return true;
                }
            
                Faction playerFactionUnally = factionManager.getFactionByPlayer(player);
                if (!playerFactionUnally.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("§3[CyansFactions]§r Only faction leaders can remove alliances.");
                    return true;
                }
            
                Faction targetFactionUnally = factionManager.getFactionByName(args[1]);
                if (targetFactionUnally == null) {
                    player.sendMessage("§3[CyansFactions]§r That faction doesn't exist.");
                    return true;
                }
            
                if (!playerFactionUnally.isAlliedWith(targetFactionUnally.getName())) {
                    player.sendMessage("§3[CyansFactions]§r You are not allied with " + targetFactionUnally.getName() + ".");
                    return true;
                }
            
                playerFactionUnally.removeAlly(targetFactionUnally.getName());
                targetFactionUnally.removeAlly(playerFactionUnally.getName());
            
                player.sendMessage("§3[CyansFactions]§r You have unallied with " + targetFactionUnally.getName() + ".");
                Player unalliedLeader = Bukkit.getPlayer(targetFactionUnally.getLeader());
                if (unalliedLeader != null) {
                    unalliedLeader.sendMessage("§3[CyansFactions]§r " + playerFactionUnally.getName() + " has unallied your faction.");
                }
                break;
            
        case "acceptally":
            if (args.length != 2) {
                player.sendMessage("§3[CyansFactions]§r Usage: /csf acceptally <faction>");
                return true;
            }

            if (!factionManager.hasFaction(player)) {
                player.sendMessage("§3[CyansFactions]§r You are not in a faction!");
                return true;
            }

            Faction myFaction2 = factionManager.getFactionByPlayer(player);
            if (!myFaction2.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§3[CyansFactions]§r Only faction leaders can accept alliance requests.");
                return true;
            }

            String senderFactionName = args[1];
            Faction senderFaction2 = factionManager.getFactionByName(senderFactionName);
            if (senderFaction2 == null) {
                player.sendMessage("§3[CyansFactions]§r That faction doesn't exist.");
                return true;
            }

            if (!factionManager.hasPendingAlly(myFaction2.getName(), senderFaction2.getName())) {
                player.sendMessage("§3[CyansFactions]§r No pending alliance request from " + senderFaction2.getName() + ".");
                return true;
            }

            senderFaction2.addAlly(myFaction2.getName());
            myFaction2.addAlly(senderFaction2.getName());
            factionManager.removePendingAlly(myFaction2.getName());

            player.sendMessage("§3[CyansFactions]§r §aAlliance formed with " + senderFaction2.getName() + "!");
            Player senderLeader = Bukkit.getPlayer(senderFaction2.getLeader());
            if (senderLeader != null) {
                senderLeader.sendMessage("§3[CyansFactions]§r §a" + myFaction2.getName() + " has accepted your alliance request!");
            }
            break;
    

            default:
                player.sendMessage("§3[CyansFactions]§r Unknown Command, see commands at /csf help");
                break;
        }  

        return true;
    }
}
