package cyansfactions.managers;

import cyansfactions.models.Faction;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionManager {

    private final Map<String, Faction> factions = new HashMap<>();
    private final Map<UUID, String> playerFactions = new HashMap<>();
    private final Map<UUID, String> invites = new HashMap<>();

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
    
}
