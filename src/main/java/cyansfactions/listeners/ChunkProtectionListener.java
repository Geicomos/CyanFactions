package cyansfactions.listeners;

import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.WarManager;
import cyansfactions.models.Faction;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.Location;

import java.util.EnumSet;
import java.util.Set;

public class ChunkProtectionListener implements Listener {

    private final ChunkManager chunkManager;
    private WarManager warManager;

    // ✅ Set of protected blocks
    private static final Set<Material> PROTECTED_BLOCKS = EnumSet.of(
        Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR,
        Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.MANGROVE_DOOR, Material.CHERRY_DOOR,
        Material.CRIMSON_DOOR, Material.WARPED_DOOR,

        Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR,
        Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.MANGROVE_TRAPDOOR, Material.CHERRY_TRAPDOOR,
        Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR,

        Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE,
        Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.MANGROVE_FENCE_GATE, Material.CHERRY_FENCE_GATE,
        Material.CRIMSON_FENCE_GATE, Material.WARPED_FENCE_GATE,

        Material.LEVER,

        Material.STONE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON,
        Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON, Material.MANGROVE_BUTTON,
        Material.CHERRY_BUTTON, Material.CRIMSON_BUTTON, Material.WARPED_BUTTON
    );

    public ChunkProtectionListener(ChunkManager chunkManager, WarManager warManager) {
        this.chunkManager = chunkManager;
        this.warManager = warManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Faction faction = chunkManager.getFactionAt(chunk);
    
        if (faction == null) return; // Unclaimed land
    
        boolean isObsidian = block.getType() == Material.OBSIDIAN;
        Faction playerFaction = chunkManager.getFactionAt(player.getLocation().getChunk());
    
        if (!faction.hasMember(player.getUniqueId())) {
            if (playerFaction != null && warManager.isAtWar(playerFaction)) {
                if (isObsidian) {
                    // Prevent mining obsidian even during war
                    event.setCancelled(true);
                } else {
                    return; // Allow breaking normal blocks during war
                }
            }
    
            // Not at war (or obsidian), block
            event.setCancelled(true);
            if (isObsidian) {
                player.sendMessage(faction.getName() + " §cDoes not allow you to mine obsidian.");
            } else {
                player.sendMessage(faction.getName() + " §cDoes not allow you to break blocks.");
            }
        }
    }     

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Faction chunkFaction = chunkManager.getFactionAt(chunk); // Who owns the land
        Material type = block.getType();

        if (chunkFaction == null) return; // Unclaimed land is unprotected
    
        Faction playerFaction = chunkManager.getFactionAt(player.getLocation().getChunk());
    
        // Allow if player is a member of the owning faction
        if (chunkFaction.hasMember(player.getUniqueId())) return;

        // Allow during war (but not lava/water)
        if (playerFaction != null && warManager.isAtWar(playerFaction)) {
            if (type == Material.LAVA || type == Material.WATER) {
                event.setCancelled(true);
                player.sendMessage(chunkFaction.getName() + " §cDoes not allow you to place " + type.name().toLowerCase() + ".");
            }
            return; // Allow placing other blocks during war
        }
    
        // Block placement denied
        event.setCancelled(true);
        player.sendMessage(chunkFaction.getName() + " §cDoes not allow you to place blocks here.");
    }
    

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Material material = block.getType();
        if (!PROTECTED_BLOCKS.contains(material)) {
            return; // Only block interactions with doors/buttons/etc
        }

        Player player = event.getPlayer();
        Chunk chunk = block.getChunk();
        Faction faction = chunkManager.getFactionAt(chunk);

        if (faction == null) {
            return; // Unclaimed
        }

        if (!faction.hasMember(player.getUniqueId()) && !warManager.isAtWar(faction)) {
            event.setCancelled(true);
            player.sendMessage(faction.getName() + " §cDoes not allow you to interact with " + material.name().replace("_", " ").toLowerCase() + ".");
        }   
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Chunk chunk = block.getChunk();
        Faction chunkFaction = chunkManager.getFactionAt(chunk);

        if (chunkFaction == null) return;
        if (chunkFaction.hasMember(player.getUniqueId())) return;

        Faction playerFaction = chunkManager.getFactionAt(player.getLocation().getChunk());

        // Block bucket use unless in war and not placing water/lava
        if (event.getBucket() == Material.WATER_BUCKET || event.getBucket() == Material.LAVA_BUCKET) {
            event.setCancelled(true);
            player.sendMessage(chunkFaction.getName() + " §cDoes not allow you to pour liquids here.");
            return;
        }

        if (playerFaction == null || !warManager.isAtWar(playerFaction)) {
            event.setCancelled(true);
            player.sendMessage(chunkFaction.getName() + " §cDoes not allow you to place blocks here.");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onLiquidFlow(BlockFromToEvent event) {
            Block fromBlock = event.getBlock();
            Block toBlock = event.getToBlock();

            // Only care about actual liquid moving into air blocks
            if (!fromBlock.isLiquid() || !toBlock.getType().isAir()) return;

            Chunk fromChunk = fromBlock.getChunk();
            Chunk toChunk = toBlock.getChunk();

            Faction fromFaction = chunkManager.getFactionAt(fromChunk);
            Faction toFaction = chunkManager.getFactionAt(toChunk);

            // Cancel if liquid is flowing from unclaimed land into claimed land
            if (fromFaction == null && toFaction != null) {
                event.setCancelled(true);
            }
        }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntityType() != EntityType.PRIMED_TNT && event.getEntityType() != EntityType.MINECART_TNT) {
            return;
        }

        Location explosionCenter = event.getLocation();
        World world = explosionCenter.getWorld();
        int radius = 1; // 3 blocks in every direction

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location checkLoc = explosionCenter.clone().add(x, y, z);
                    Block block = world.getBlockAt(checkLoc);

                    if (block.getType() != Material.OBSIDIAN) continue;

                    Chunk chunk = block.getChunk();
                    Faction faction = chunkManager.getFactionAt(chunk);

                    if (warManager.isAtWar(faction)) {
                        // ✅ Faction at war — allow obsidian to explode
                        block.setType(Material.AIR);
                        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.OBSIDIAN));
                        continue;
                    }
                    // Protect obsidian from tnt
                }
            }
        }
        
    }
}