package cyansfactions.commands;

import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import cyansfactions.storage.FactionsDataManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.UUID;

public class DisbandCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final FactionsDataManager factionsDataManager;
    private final ChunkManager chunkManager;

    public DisbandCommand(FactionManager factionManager, ChunkManager chunkManager, FactionsDataManager factionsDataManager) {
        this.factionManager = factionManager;
        this.chunkManager = chunkManager;
        this.factionsDataManager = factionsDataManager;
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

        Faction faction = factionManager.getFactionByPlayer(player);

        if (!faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("§3[CyansFactions]§r Only the faction leader can disband the faction.");
            return true;
        }

        String factionName = faction.getName();

        // Notify all faction members
        for (UUID memberUUID : faction.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage("§3[CyansFactions]§r §cYour faction has been disbanded by the leader.");
            }
        }

        factionManager.deleteFaction(faction, chunkManager, factionsDataManager);
        Bukkit.broadcastMessage("§3[CyansFactions]§r §cThe faction §f" + factionName + "§c has been disbanded!");

        return true;
    }
}
