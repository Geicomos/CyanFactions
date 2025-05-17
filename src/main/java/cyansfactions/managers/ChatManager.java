package cyansfactions.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class ChatManager {
    private final Set<UUID> factionChatToggled = new HashSet<>();
    private final Map<UUID, String> allyChatTargets = new HashMap<>();

    public boolean isInFactionChat(UUID uuid) {
        return factionChatToggled.contains(uuid);
    }

    public void toggleFactionChat(UUID uuid) {
        if (!factionChatToggled.add(uuid)) {
            factionChatToggled.remove(uuid);
        }
    }

    public void setFactionChat(UUID uuid, boolean enabled) {
        if (enabled) {
            factionChatToggled.add(uuid);
        } else {
            factionChatToggled.remove(uuid);
        }
    }

    public boolean isInAllyChat(UUID uuid) {
        return allyChatTargets.containsKey(uuid);
    }

    public String getAllyChatTarget(UUID uuid) {
        return allyChatTargets.get(uuid);
    }

    public void setAllyChat(UUID uuid, String targetFaction) {
        if (targetFaction == null) {
            allyChatTargets.remove(uuid);
        } else {
            allyChatTargets.put(uuid, targetFaction.toLowerCase());
        }
    }
}
