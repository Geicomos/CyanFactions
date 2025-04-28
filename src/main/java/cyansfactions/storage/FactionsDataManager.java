package cyansfactions.storage;

import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FactionsDataManager {

    private final Plugin plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public FactionsDataManager(Plugin plugin) {
        this.plugin = plugin;
        createDataFile();
    }

    private void createDataFile() {
        dataFile = new File(plugin.getDataFolder(), "factions.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveFactions(FactionManager factionManager, ChunkManager chunkManager) {
        dataConfig.set("factions", null); // clear old data

        for (Faction faction : factionManager.getAllFactions()) {
            String path = "factions." + faction.getName();

            // Save leader
            dataConfig.set(path + ".leader", faction.getLeader().toString());

            // Save members
            List<String> members = new ArrayList<>();
            for (UUID member : faction.getMembers()) {
                members.add(member.toString());
            }
            dataConfig.set(path + ".members", members);

            // Save claimed chunks
            List<String> claimedChunks = new ArrayList<>();
            for (Chunk chunk : chunkManager.getClaimedChunks(faction)) {
                String chunkKey = chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
                claimedChunks.add(chunkKey);
            }
            dataConfig.set(path + ".claimedChunks", claimedChunks);
        }

        saveData();
    }

    public void loadFactions(FactionManager factionManager, ChunkManager chunkManager) {
        if (dataConfig.getConfigurationSection("factions") == null) {
            return;
        }

        for (String factionName : dataConfig.getConfigurationSection("factions").getKeys(false)) {
            String leaderUUID = dataConfig.getString("factions." + factionName + ".leader");
            Faction faction = new Faction(factionName, UUID.fromString(leaderUUID));

            // Load members
            List<String> memberList = dataConfig.getStringList("factions." + factionName + ".members");
            for (String memberUUID : memberList) {
                faction.addMember(UUID.fromString(memberUUID));
            }

            // Save into manager
            factionManager.createFaction(faction);

            // Load claimed chunks
            List<String> claimedChunks = dataConfig.getStringList("factions." + factionName + ".claimedChunks");
            for (String chunkData : claimedChunks) {
                String[] parts = chunkData.split(",");
                if (parts.length == 3) {
                    String worldName = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);
                    Chunk chunk = Bukkit.getWorld(worldName).getChunkAt(x, z);
                    chunkManager.claimChunk(faction, chunk);
                }
            }
        }
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
