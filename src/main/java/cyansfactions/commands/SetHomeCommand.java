package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.CyansFactions;
import cyansfactions.managers.ChunkManager;
import cyansfactions.models.Faction;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;
    private final double createCost = CyansFactions.getInstance().getConfig().getDouble("home.cost-to-set", 300);

    public SetHomeCommand(FactionManager factionManager, ChunkManager chunkManager) {
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
            player.sendMessage("§3[CyansFactions]§r You are not in a faction.");
            return true;
        }

        Faction faction = factionManager.getFactionByPlayer(player);

        if (!faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("§3[CyansFactions]§r Only the faction leader can set the home!");
            return true;
        }

        Chunk currentChunk = player.getLocation().getChunk();
        Faction ownerFaction = chunkManager.getFactionAt(currentChunk);

        if (ownerFaction == null || !ownerFaction.getName().equalsIgnoreCase(faction.getName())) {
            player.sendMessage("§3[CyansFactions]§r You must be standing in your faction's claimed land to set the home!");
            return true;
        }

        if (!faction.withdraw(createCost)) {
            player.sendMessage("§3[CyansFactions]§r Your faction doesn't have enough money to set a home. Need " + createCost + "$.");
            return true;
        }

        faction.setHome(player.getLocation());
        player.sendMessage("§3[CyansFactions]§r §aFaction home set successfully!");
        return true;
    }
}
