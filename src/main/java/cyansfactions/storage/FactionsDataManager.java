package cyansfactions.storage;

import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.models.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
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
        dataConfig.set("factions", null); // Clear old data

        for (Faction faction : factionManager.getAllFactions()) {
            String path = "factions." + faction.getName();

            // Save leader
            dataConfig.set(path + ".leader", faction.getLeader().toString());

            // Save balance
            dataConfig.set(path + ".balance", faction.getBalance());

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

            // Save home location if exists
            if (faction.getHome() != null) {
                Location home = faction.getHome();
                dataConfig.set(path + ".home.world", home.getWorld().getName());
                dataConfig.set(path + ".home.x", home.getX());
                dataConfig.set(path + ".home.y", home.getY());
                dataConfig.set(path + ".home.z", home.getZ());
                dataConfig.set(path + ".home.yaw", home.getYaw());
                dataConfig.set(path + ".home.pitch", home.getPitch());
            }
        }

        saveData();
    }

    public void loadFactions(FactionManager factionManager, ChunkManager chunkManager) {
        if (dataConfig.getConfigurationSection("factions") == null) {
            return;
        }

        for (String factionName : dataConfig.getConfigurationSection("factions").getKeys(false)) {
            String path = "factions." + factionName;
            String leaderUUID = dataConfig.getString(path + ".leader");
            double balance = dataConfig.getDouble(path + ".balance", 0.0);

            Faction faction = new Faction(factionName, UUID.fromString(leaderUUID));
            faction.setBalance(balance);

            // Load members
            List<String> memberList = dataConfig.getStringList(path + ".members");
            for (String memberUUID : memberList) {
                faction.addMember(UUID.fromString(memberUUID));
            }

            factionManager.createFaction(faction);

            // Load claimed chunks
            List<String> claimedChunks = dataConfig.getStringList(path + ".claimedChunks");
            for (String chunkData : claimedChunks) {
                String[] parts = chunkData.split(",");
                if (parts.length == 3) {
                    World world = Bukkit.getWorld(parts[0]);
                    if (world != null) {
                        int x = Integer.parseInt(parts[1]);
                        int z = Integer.parseInt(parts[2]);
                        Chunk chunk = world.getChunkAt(x, z);
                        chunkManager.claimChunk(faction, chunk);
                    }
                }
            }

            // Load home if exists
            if (dataConfig.contains(path + ".home.world")) {
                World world = Bukkit.getWorld(dataConfig.getString(path + ".home.world"));
                if (world != null) {
                    double x = dataConfig.getDouble(path + ".home.x");
                    double y = dataConfig.getDouble(path + ".home.y");
                    double z = dataConfig.getDouble(path + ".home.z");
                    float yaw = (float) dataConfig.getDouble(path + ".home.yaw");
                    float pitch = (float) dataConfig.getDouble(path + ".home.pitch");

                    Location home = new Location(world, x, y, z, yaw, pitch);
                    faction.setHome(home);
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
