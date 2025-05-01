package cyansfactions.storage;

import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.managers.WarManager;
import cyansfactions.models.Faction;
import cyansfactions.models.FactionRole;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
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
    Map<String, String> roleMap = new HashMap<>();

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

            dataConfig.set(path + ".leader", faction.getLeader().toString());
            dataConfig.set(path + ".balance", faction.getBalance());
            dataConfig.set(path + ".allies", new ArrayList<>(faction.getAllies()));

            List<String> members = new ArrayList<>();
            for (UUID member : faction.getMembers()) {
                members.add(member.toString());
            }
            dataConfig.set(path + ".members", members);

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

            for (UUID uuid : faction.getMembers()) {
                FactionRole role = faction.getRole(uuid);
                roleMap.put(uuid.toString(), role.name());
            }
                dataConfig.set(path + ".roles", roleMap);

            for (Map.Entry<String, Location> entry : faction.getWarps().entrySet()) {
                String warpName = entry.getKey();
                Location loc = entry.getValue();
            
                String warpPath = path + ".warps." + warpName;
                dataConfig.set(warpPath + ".world", loc.getWorld().getName());
                dataConfig.set(warpPath + ".x", loc.getX());
                dataConfig.set(warpPath + ".y", loc.getY());
                dataConfig.set(warpPath + ".z", loc.getZ());
                dataConfig.set(warpPath + ".yaw", loc.getYaw());
                dataConfig.set(warpPath + ".pitch", loc.getPitch());
            
                // 🔥 Save the warp password if it exists
                String password = faction.getWarpPassword(warpName);
                if (password != null) {
                    dataConfig.set(warpPath + ".password", password);
                }
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

            // Load roles
            if (dataConfig.contains(path + ".roles")) {
                ConfigurationSection section = dataConfig.getConfigurationSection(path + ".roles");
                for (String uuidStr : section.getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        FactionRole role = FactionRole.valueOf(section.getString(uuidStr));
                        faction.setRole(uuid, role);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (dataConfig.contains(path + ".warps")) {
                Set<String> warpKeys = dataConfig.getConfigurationSection(path + ".warps").getKeys(false);
            
                for (String warpName : warpKeys) {
                    String warpPath = path + ".warps." + warpName;
            
                    String worldName = dataConfig.getString(warpPath + ".world");
                    World world = Bukkit.getWorld(worldName);
            
                    if (world != null) {
                        double x = dataConfig.getDouble(warpPath + ".x");
                        double y = dataConfig.getDouble(warpPath + ".y");
                        double z = dataConfig.getDouble(warpPath + ".z");
                        float yaw = (float) dataConfig.getDouble(warpPath + ".yaw");
                        float pitch = (float) dataConfig.getDouble(warpPath + ".pitch");
            
                        Location warpLoc = new Location(world, x, y, z, yaw, pitch);
                        faction.setWarp(warpName, warpLoc);
            
                        // 🔥 Load the password if it exists
                        if (dataConfig.contains(warpPath + ".password")) {
                            String password = dataConfig.getString(warpPath + ".password");
                            faction.setWarpPassword(warpName, password);
                        }
                    }
                }
            }
            
            List<String> allies = dataConfig.getStringList(path + ".allies");
            for (String allyName : allies) {
                faction.addAlly(allyName);
            }

        }
    }
    public void deleteFaction(String factionName) {
        dataConfig.set("factions." + factionName, null);
        saveData();
    }
    
    public void saveWars(WarManager warManager) {
        dataConfig.set("wars", null); // Clear old war data

        for (Map.Entry<String, String> entry : warManager.getActiveWars().entrySet()) {
            String factionName = entry.getKey();
            String enemyName = entry.getValue();
            dataConfig.set("wars." + factionName, enemyName);
        }

        saveData();
    }

    public void loadWars(WarManager warManager, FactionManager factionManager) {
        if (!dataConfig.isConfigurationSection("wars")) {
            return;
        }

        for (String factionName : dataConfig.getConfigurationSection("wars").getKeys(false)) {
            String enemyName = dataConfig.getString("wars." + factionName);

            Faction faction = factionManager.getFactionByName(factionName);
            Faction enemy = factionManager.getFactionByName(enemyName);

            if (faction != null && enemy != null) {
                warManager.declareWar(faction, enemy);
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
