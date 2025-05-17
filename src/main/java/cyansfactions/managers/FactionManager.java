package cyansfactions.managers;

import cyansfactions.CyansFactions;
import cyansfactions.models.Faction;
import cyansfactions.storage.FactionsDataManager;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionManager {

    private final Map<String, Faction> factions = new HashMap<>();
    private final Map<UUID, String> playerFactions = new HashMap<>();
    private final Map<UUID, String> invites = new HashMap<>();
    private final Map<String, String> pendingAllyRequests = new HashMap<>();
    @SuppressWarnings("unused")
    private final CyansFactions plugin;

    public FactionManager(CyansFactions plugin) {
        this.plugin = plugin;
    }
    
    public boolean createFaction(Player creator, String name) {
        if (factions.containsKey(name.toLowerCase())) {
            return false;
        }
        Faction faction = new Faction(name, creator.getUniqueId());
        factions.put(name.toLowerCase(), faction);
        playerFactions.put(creator.getUniqueId(), name.toLowerCase());
        return true;
    }

    public boolean hasFaction(Player player) {
        return playerFactions.containsKey(player.getUniqueId());
    }

    public void invitePlayer(Player inviter, Player invitee) {
        invites.put(invitee.getUniqueId(), getFactionName(inviter));
    }

    public boolean hasInvite(Player player) {
        return invites.containsKey(player.getUniqueId());
    }

    public String getFactionName(Player player) {
        return playerFactions.get(player.getUniqueId());
    }

    public boolean acceptInvite(Player player) {
        String factionName = invites.remove(player.getUniqueId());
        if (factionName == null) {
            return false;
        }
        Faction faction = factions.get(factionName);
        if (faction == null) {
            return false;
        }
        faction.addMember(player.getUniqueId());
        playerFactions.put(player.getUniqueId(), factionName);
        return true;
    }

    public Faction getFaction(String name) {
        return factions.get(name.toLowerCase());
    }

    public void removePlayerFromFaction(Player player) {
        playerFactions.remove(player.getUniqueId());
    }
    

    public Faction getFactionByPlayer(Player player) {
        String factionName = playerFactions.get(player.getUniqueId());
        return factionName != null ? factions.get(factionName) : null;
    }
 
    public Collection<Faction> getAllFactions() {
        return factions.values();
    }
    
    public void createFaction(Faction faction) {
        factions.put(faction.getName().toLowerCase(), faction);
        for (UUID member : faction.getMembers()) {
            playerFactions.put(member, faction.getName().toLowerCase());
        }
    }
    
    public void deleteFaction(Faction faction, ChunkManager chunkManager, FactionsDataManager factionsDataManager) {
        // Unclaim all chunks
        for (Chunk chunk : chunkManager.getClaimedChunks(faction)) {
            chunkManager.unclaimChunk(chunk);
        }
    
        // Remove faction from memory
        factions.remove(faction.getName().toLowerCase());
    
        // Remove players from memory
        for (UUID member : faction.getMembers()) {
            playerFactions.remove(member);
        }
    
        // Remove from file
        if (factionsDataManager != null) {
            factionsDataManager.deleteFaction(faction.getName());
        }
    }    
        
    public Faction getFactionByName(String name) {
        for (Faction faction : getAllFactions()) {
            if (faction.getName().equalsIgnoreCase(name)) {
                return faction;
            }
        }
        return null;
    }    
    
    public Faction getFactionByUUID(UUID uuid) {
        for (Faction faction : factions.values()) {
            if (faction.hasMember(uuid)) {
                return faction;
            }
        }
        return null;
    }
    
    public void addPendingAlly(String targetFaction, String senderFaction) {
        pendingAllyRequests.put(targetFaction.toLowerCase(), senderFaction.toLowerCase());
    }
    
    public boolean hasPendingAlly(String targetFaction, String senderFaction) {
        return senderFaction.equalsIgnoreCase(pendingAllyRequests.getOrDefault(targetFaction.toLowerCase(), ""));
    }
    
    public void removePendingAlly(String targetFaction) {
        pendingAllyRequests.remove(targetFaction.toLowerCase());
    }
    
    public void setFactionForPlayer(Player player, Faction faction) {
        playerFactions.put(player.getUniqueId(), faction.getName().toLowerCase());
    }
}
