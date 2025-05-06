package cyansfactions.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatManager {
    private final Set<UUID> factionChatToggled = new HashSet<>();
    private final Set<UUID> allyChatToggled = new HashSet<>();

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
        return allyChatToggled.contains(uuid);
    }

    public void toggleAllyChat(UUID uuid) {
        if (!allyChatToggled.add(uuid)) {
            allyChatToggled.remove(uuid);
        }
    }

    public void setAllyChat(UUID uuid, boolean enabled) {
        if (enabled) {
            allyChatToggled.add(uuid);
        } else {
            allyChatToggled.remove(uuid);
        }
    }
}
