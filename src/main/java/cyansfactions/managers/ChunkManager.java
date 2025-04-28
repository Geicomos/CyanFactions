package cyansfactions.managers;

import cyansfactions.models.Faction;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkManager {

    private final Map<String, Faction> claimedChunks = new HashMap<>();

    public boolean claimChunk(Faction faction, Chunk chunk) {
        String key = getChunkKey(chunk);
        if (claimedChunks.containsKey(key)) {
            return false;
        }
        claimedChunks.put(key, faction);
        return true;
    }

    public boolean isChunkClaimed(Chunk chunk) {
        String key = getChunkKey(chunk);
        return claimedChunks.containsKey(key);
    }

    public Faction getFactionAt(Chunk chunk) {
        String key = getChunkKey(chunk);
        return claimedChunks.get(key);
    }

    // ✅ Needed for saving which chunks a faction owns
    public List<Chunk> getClaimedChunks(Faction faction) {
        List<Chunk> chunks = new ArrayList<>();
        for (Map.Entry<String, Faction> entry : claimedChunks.entrySet()) {
            if (entry.getValue().equals(faction)) {
                String[] parts = entry.getKey().split(",");
                if (parts.length == 3) {
                    String worldName = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);
                    Chunk chunk = org.bukkit.Bukkit.getWorld(worldName).getChunkAt(x, z);
                    chunks.add(chunk);
                }
            }
        }
        return chunks;
    }

    private String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }
}
