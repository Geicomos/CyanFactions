package cyansfactions.commands;

import cyansfactions.CyansFactions;
import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class ListCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;

    public ListCommand(FactionManager factionManager, ChunkManager chunkManager) {
        this.factionManager = factionManager;
        this.chunkManager = chunkManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Collection<Faction> factions = factionManager.getAllFactions();

        if (factions.isEmpty()) {
            sender.sendMessage("§3[CyansFactions]§r No factions found.");
            return true;
        }

        sender.sendMessage("§3[CyansFactions]§r §6List of Factions:");

        for (Faction faction : factions) {
            String name = faction.getName();

            int claimedChunks = chunkManager.getClaimedChunks(faction).size();
            double money = faction.getBalance();

            int maxMembers = CyansFactions.getInstance().getConfig().getInt("faction.max-members", 20);
            int totalMembers = faction.getMembers().size();

            int onlineMembers = 0;
            for (UUID uuid : faction.getMembers()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    onlineMembers++;
                }
            }

            sender.sendMessage(String.format("§b%s§r -  %d chunks,  $%.2f,  %d/%d members,  %d online",
                    name, claimedChunks, money, totalMembers, maxMembers, onlineMembers));
        }

        return true;
    }
}
