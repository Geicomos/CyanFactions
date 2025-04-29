package cyansfactions.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;
import java.util.UUID;

public class CombatListener implements Listener {

    private final Map<UUID, Long> lastCombat;

    public CombatListener(Map<UUID, Long> lastCombat) {
        this.lastCombat = lastCombat;
    }

    @EventHandler
    public void onCombat(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        long now = System.currentTimeMillis();
        lastCombat.put(victim.getUniqueId(), now);
        lastCombat.put(attacker.getUniqueId(), now);
    }
}
