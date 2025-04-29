package cyansfactions.models;

import org.bukkit.Location;

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
}
