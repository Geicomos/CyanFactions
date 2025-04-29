package cyansfactions.models;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Faction {

    private final String name;
    private final UUID leader;
    private final Set<UUID> members = new HashSet<>();
    private Map<String, Location> warps = new HashMap<>();
    private Map<String, String> warpPasswords = new HashMap<>();
    private final Set<String> allies = new HashSet<>();
    private final Map<String, String> pendingAllyRequests = new HashMap<>();
    private Location home;
    private double balance = 0.0; 

    public Faction(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        members.add(leader);
    }

    public String getName() {
        return name;
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }    

    public boolean hasMember(UUID playerUUID) {
        return members.contains(playerUUID);
    }
    

    public Location getHome() {
        return home;
    }

    public int getMemberCount() {
        return members.size();
    }
    
    public void setHome(Location home) {
        this.home = home;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }    

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public Map<String, Location> getWarps() {
        return warps;
    }
    
    public void setWarp(String name, Location location) {
        warps.put(name.toLowerCase(), location);
    }
    
    public void removeWarp(String name) {
        warps.remove(name.toLowerCase());
    }

    public Map<String, String> getWarpPasswords() {
        return warpPasswords;
    }
    
    public void setWarpPassword(String warpName, String password) {
        warpPasswords.put(warpName.toLowerCase(), password);
    }
    
    public String getWarpPassword(String warpName) {
        return warpPasswords.get(warpName.toLowerCase());
    }
    
    public void removeWarpPassword(String warpName) {
        warpPasswords.remove(warpName.toLowerCase());
    }
    public void addAlly(String factionName) {
    allies.add(factionName.toLowerCase());
    }

    public void removeAlly(String factionName) {
        allies.remove(factionName.toLowerCase());
    }

    public boolean isAlliedWith(String factionName) {
        return allies.contains(factionName.toLowerCase());
    }

    public Set<String> getAllies() {
        return Collections.unmodifiableSet(allies);
    }

    public void addPendingAlly(String targetFaction, String senderFaction) {
        pendingAllyRequests.put(targetFaction.toLowerCase(), senderFaction.toLowerCase());
    }
    
    public boolean hasPendingAlly(String targetFaction, String senderFaction) {
        return pendingAllyRequests.getOrDefault(targetFaction.toLowerCase(), "").equalsIgnoreCase(senderFaction);
    }
    
    public void removePendingAlly(String targetFaction) {
        pendingAllyRequests.remove(targetFaction.toLowerCase());
    }
    
}
