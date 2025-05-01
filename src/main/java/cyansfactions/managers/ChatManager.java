// cyansfactions/managers/ChatManager.java
package cyansfactions.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatManager {
    private final Set<UUID> factionChatToggled = new HashSet<>();

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
}
