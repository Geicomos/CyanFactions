package cyansfactions.commands;

import cyansfactions.managers.FactionManager;
import cyansfactions.CyansFactions;
import cyansfactions.managers.ChunkManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClaimChunkCommand implements CommandExecutor {

    private final FactionManager factionManager;
    private final ChunkManager chunkManager;
    private final double baseClaimCost = CyansFactions.getInstance().getConfig().getDouble("claim.cost-to-claim", 600.0);
    private final double multiplyBy = CyansFactions.getInstance().getConfig().getDouble("claim.multiply-by", 2.0);
    private final double chunkMax = CyansFactions.getInstance().getConfig().getDouble("claim.max-claims", 20.0);

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

        if (!faction.isOwner(player.getUniqueId()) && !faction.isCoLeader(player.getUniqueId())) {
            player.sendMessage("§3[CyansFactions]§r Only the owner or co-leaders can do that!");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        int claimedChunks = chunkManager.getClaimedChunks(faction).size();

        if (claimedChunks > chunkMax) {
            player.sendMessage("§3[CyansFactions]§r You have hit the claim limit!");
            return true;
        }

        if (chunkManager.getFactionAt(chunk) != null) {
            player.sendMessage("§3[CyansFactions]§r This chunk is already claimed!");
            return true;
        }

        double claimCost = baseClaimCost * Math.pow(multiplyBy, claimedChunks);

        if (faction.getBalance() < claimCost) {
            player.sendMessage("§3[CyansFactions]§r Your faction doesn't have enough money! Need $" + String.format("%.2f", claimCost));
            return true;
        }

        if (!faction.withdraw(claimCost)) {
            player.sendMessage("§3[CyansFactions]§r §cFailed to withdraw from faction balance!");
            return true;
        }

        chunkManager.claimChunk(faction, chunk);

        String personal = "§3[CyansFactions]§r §aChunk claimed for your faction! (-$" + String.format("%.2f", claimCost) + ")";
        String announce = "§3[CyansFactions]§r §e" + player.getName() + " claimed a chunk for the faction (-$" + String.format("%.2f", claimCost) + ").";

        player.sendMessage(personal);
        for (UUID uuid : faction.getMembers()) {
            Player m = Bukkit.getPlayer(uuid);
            if (m != null && m.isOnline() && !m.equals(player)) m.sendMessage(announce);
        }
        return true;
    }
}
