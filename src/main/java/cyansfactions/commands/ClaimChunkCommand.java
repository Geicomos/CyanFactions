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

public class ClaimChunkCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;
    private final double baseClaimCost = CyansFactions.getInstance().getConfig().getDouble("claim.cost-to-claim", 600.0);
    private final double multiplyBy = CyansFactions.getInstance().getConfig().getDouble("claim.multiply-by", 2.0);

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

        if (!faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("§3[CyansFactions]§r Only the faction leader can claim land!");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();

        // Check if already claimed
        if (chunkManager.getFactionAt(chunk) != null) {
            player.sendMessage("§3[CyansFactions]§r This chunk is already claimed!");
            return true;
        }

        // Calculate how much this claim costs
        int claimedChunks = chunkManager.getClaimedChunks(faction).size(); // already claimed
        double claimCost = baseClaimCost * Math.pow(multiplyBy, claimedChunks); 

        // Check balance
        if (faction.getBalance() < claimCost) {
            player.sendMessage("§3[CyansFactions]§r Your faction doesn't have enough money! Need $" + String.format("%.2f", claimCost));
            return true;
        }

        // Claim and withdraw
        if (!faction.withdraw(claimCost)) {
            player.sendMessage("§3[CyansFactions]§r §cFailed to withdraw from faction balance!");
            return true;
        }

        chunkManager.claimChunk(faction, chunk);
        player.sendMessage("§3[CyansFactions]§r §aChunk claimed for your faction! (-$" + String.format("%.2f", claimCost) + ")");
        return true;
    }
}
