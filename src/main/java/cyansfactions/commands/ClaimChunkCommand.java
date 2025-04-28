package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.managers.ChunkManager;
import cyansfactions.models.Faction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimChunkCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;

    public ClaimChunkCommand(FactionManager factionManager, ChunkManager chunkManager) {
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
            player.sendMessage("§3[CyansFactions]§r You must be in a faction to claim land!");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(player);

        if (!chunkManager.claimChunk(faction, player.getLocation().getChunk())) {
            player.sendMessage("§3[CyansFactions]§r This chunk is already claimed!");
            return true;
        }

        player.sendMessage("§3[CyansFactions]§r Chunk claimed for your faction!");
        return true;
    }
}
