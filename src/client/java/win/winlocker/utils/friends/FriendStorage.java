package win.winlocker.utils.friends;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FriendStorage {
    private static FriendStorage instance;
    private final Set<Friend> friends = new HashSet<>();
    private final Set<String> friendNames = new HashSet<>();
    private final Set<UUID> friendUUIDs = new HashSet<>();
    private static final String FRIENDS_FILE = "winlocker_friends.txt";
    
    public static FriendStorage getInstance() {
        if (instance == null) {
            instance = new FriendStorage();
        }
        return instance;
    }
    
    public FriendStorage() {
        loadFriends();
    }
    
    public void addFriend(String name, UUID uuid) {
        Friend friend = new Friend(name, uuid);
        friends.add(friend);
        friendNames.add(name.toLowerCase());
        friendUUIDs.add(uuid);
        saveFriends();
        
        System.out.println("[FriendStorage] Added friend: " + name);
    }
    
    public void removeFriend(String name) {
        friends.removeIf(friend -> friend.getName().equalsIgnoreCase(name));
        friendNames.remove(name.toLowerCase());
        saveFriends();
        
        System.out.println("[FriendStorage] Removed friend: " + name);
    }
    
    public void removeFriend(UUID uuid) {
        friends.removeIf(friend -> friend.getUUID().equals(uuid));
        friendUUIDs.remove(uuid);
        saveFriends();
    }
    
    public boolean isFriend(String name) {
        return friendNames.contains(name.toLowerCase());
    }
    
    public boolean isFriend(UUID uuid) {
        return friendUUIDs.contains(uuid);
    }
    
    public boolean isFriend(Player player) {
        if (player == null) return false;
        return isFriend(player.getName().getString()) || isFriend(player.getUUID());
    }
    
    public Friend getFriend(String name) {
        return friends.stream()
                .filter(friend -> friend.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    public Friend getFriend(UUID uuid) {
        return friends.stream()
                .filter(friend -> friend.getUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }
    
    public Set<Friend> getAllFriends() {
        return new HashSet<>(friends);
    }
    
    public Set<String> getFriendNames() {
        return new HashSet<>(friendNames);
    }
    
    public int getFriendCount() {
        return friends.size();
    }
    
    public void clearFriends() {
        friends.clear();
        friendNames.clear();
        friendUUIDs.clear();
        saveFriends();
    }
    
    public void addFriendFromPlayer(Player player) {
        if (player != null) {
            addFriend(player.getName().getString(), player.getUUID());
        }
    }
    
    public void removeFriendFromPlayer(Player player) {
        if (player != null) {
            removeFriend(player.getName().getString());
        }
    }
    
    private void saveFriends() {
        try {
            Path configDir = Paths.get("config");
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            Path filePath = configDir.resolve(FRIENDS_FILE);
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Friend friend : friends) {
                    writer.write(friend.getName() + ":" + friend.getUUID().toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("[FriendStorage] Failed to save friends: " + e.getMessage());
        }
    }
    
    private void loadFriends() {
        try {
            Path filePath = Paths.get("config").resolve(FRIENDS_FILE);
            if (!Files.exists(filePath)) {
                return;
            }
            
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String name = parts[0];
                        try {
                            UUID uuid = UUID.fromString(parts[1]);
                            Friend friend = new Friend(name, uuid);
                            friends.add(friend);
                            friendNames.add(name.toLowerCase());
                            friendUUIDs.add(uuid);
                        } catch (IllegalArgumentException e) {
                            System.err.println("[FriendStorage] Invalid UUID: " + parts[1]);
                        }
                    }
                }
            }
            
            System.out.println("[FriendStorage] Loaded " + friends.size() + " friends");
        } catch (IOException e) {
            System.err.println("[FriendStorage] Failed to load friends: " + e.getMessage());
        }
    }
    
    public void toggleFriend(Player player) {
        if (isFriend(player)) {
            removeFriendFromPlayer(player);
        } else {
            addFriendFromPlayer(player);
        }
    }
    
    public String getFriendStatus(Player player) {
        if (isFriend(player)) {
            return "§a[Friend]";
        }
        return "";
    }
    
    public static class Friend {
        private final String name;
        private final UUID uuid;
        private final long addedTime;
        
        public Friend(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
            this.addedTime = System.currentTimeMillis();
        }
        
        public String getName() {
            return name;
        }
        
        public UUID getUUID() {
            return uuid;
        }
        
        public long getAddedTime() {
            return addedTime;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Friend friend = (Friend) obj;
            return uuid.equals(friend.uuid);
        }
        
        @Override
        public int hashCode() {
            return uuid.hashCode();
        }
        
        @Override
        public String toString() {
            return name + " (" + uuid.toString().substring(0, 8) + "...)";
        }
    }
}
