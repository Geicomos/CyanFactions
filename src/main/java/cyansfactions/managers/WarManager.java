package cyansfactions.managers;

import cyansfactions.models.Faction;
import java.util.HashMap;
import java.util.Map;

public class WarManager {

    private final HashMap<String, String> activeWars = new HashMap<>();
    private final Map<String, String> pendingPeaceOffers = new HashMap<>();

    public WarManager(FactionManager factionManager) {
    }

    public Map<String, String> getActiveWars() {
        return activeWars;
    }
    public void declareWar(Faction attacker, Faction defender) {
        activeWars.put(attacker.getName().toLowerCase(), defender.getName().toLowerCase());
        activeWars.put(defender.getName().toLowerCase(), attacker.getName().toLowerCase());
    }

    public void endWar(Faction faction1, Faction faction2) {
        activeWars.remove(faction1.getName().toLowerCase());
        activeWars.remove(faction2.getName().toLowerCase());
    }


    public void addPendingPeaceOffer(String receiverFaction, String requesterFaction) {
        pendingPeaceOffers.put(receiverFaction.toLowerCase(), requesterFaction.toLowerCase());
    }
    
    public boolean hasPendingPeace(String receiverFaction, String requesterFaction) {
        return pendingPeaceOffers.containsKey(receiverFaction.toLowerCase())
            && pendingPeaceOffers.get(receiverFaction.toLowerCase()).equalsIgnoreCase(requesterFaction.toLowerCase());
    }
    
    public void removePendingPeace(String receiverFaction, String requesterFaction) {
        pendingPeaceOffers.remove(receiverFaction.toLowerCase());
    }
    

    public boolean isAtWar(Faction faction) {
        return activeWars.containsKey(faction.getName().toLowerCase());
    }

    public String getEnemyName(Faction faction) {
        return activeWars.get(faction.getName().toLowerCase());
    }

    public boolean areFactionsAtWar(Faction faction1, Faction faction2) {
        String enemy1 = activeWars.get(faction1.getName().toLowerCase());
        String enemy2 = activeWars.get(faction2.getName().toLowerCase());
        return (enemy1 != null && enemy1.equalsIgnoreCase(faction2.getName()))
            || (enemy2 != null && enemy2.equalsIgnoreCase(faction1.getName()));
    }
}
