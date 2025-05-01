package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.managers.ChunkManager;
import cyansfactions.models.Faction;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnclaimChunkCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;

    public UnclaimChunkCommand(FactionManager factionManager, ChunkManager chunkManager) {
        this.factionManager = factionManager;
        this.chunkManager = chunkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!factionManager.hasFaction(player)) {
            player.sendMessage("§3[CyansFactions]§r You must be in a faction to unclaim land!");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(player);

        if (!faction.isOwner(player.getUniqueId()) && !faction.isCoLeader(player.getUniqueId())) {
            player.sendMessage("§3[CyansFactions]§r Only the owner or co-leaders can do that!");
            return true;
        }    

        Chunk chunk = player.getLocation().getChunk();

        Faction ownerFaction = chunkManager.getFactionAt(chunk);

        if (ownerFaction == null) {
            player.sendMessage("§3[CyansFactions]§r This chunk is not claimed by any faction.");
            return true;
        }

        if (!ownerFaction.getName().equalsIgnoreCase(faction.getName())) {
            player.sendMessage("§3[CyansFactions]§r You can only unclaim land owned by your own faction.");
            return true;
        }

        chunkManager.unclaimChunk(chunk);
        player.sendMessage("§3[CyansFactions]§r §aSuccessfully unclaimed this chunk.");
        return true;
    }
}
