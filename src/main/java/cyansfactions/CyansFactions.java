package cyansfactions;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import cyansfactions.commands.CreateFactionCommand;
import cyansfactions.commands.CsfCommand;
import cyansfactions.commands.FactionChatCommand;
import cyansfactions.commands.HomeCommand;
import cyansfactions.commands.InviteCommand;
import cyansfactions.commands.LeaveFactionCommand;
import cyansfactions.commands.SetHomeCommand;
import cyansfactions.listeners.ChunkEnterLeaveListener;
import cyansfactions.listeners.ChunkProtectionListener;
import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.storage.FactionsDataManager;
import cyansfactions.commands.ClaimChunkCommand;
import cyansfactions.commands.AcceptInviteCommand;

public class CyansFactions extends JavaPlugin {

    private static CyansFactions instance;
    private static Economy economy; 
    private FactionManager factionManager;
    private ChunkManager chunkManager;
    private FactionsDataManager factionsDataManager;
    private HomeCommand homeCommand;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        
        if (!setupEconomy()) {
            getLogger().severe("[CyansFactions] Vault not found or no economy provider found! Download Vault!");
            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        factionsDataManager = new FactionsDataManager(this);

        this.factionManager = new FactionManager();
        this.chunkManager = new ChunkManager();
        this.homeCommand = new HomeCommand(factionManager);

        FactionChatCommand factionChatCommand = new FactionChatCommand(factionManager);
        getCommand("fchat").setExecutor(factionChatCommand);
        getServer().getPluginManager().registerEvents(factionChatCommand, this);

        factionsDataManager.loadFactions(factionManager, chunkManager);
        // Register commands
        if (getCommand("createfaction") != null) {
            getCommand("createfaction").setExecutor(new CreateFactionCommand(factionManager, economy));
        }
        if (getCommand("invite") != null) {
            getCommand("invite").setExecutor(new InviteCommand(factionManager));
        }
        if (getCommand("claimchunk") != null) {
            getCommand("claimchunk").setExecutor(new ClaimChunkCommand(factionManager, chunkManager));
        }
        if (getCommand("acceptinvite") != null) {
            getCommand("acceptinvite").setExecutor(new AcceptInviteCommand(factionManager));
        }

        getLogger().info("[CyansFactions] enabled and Vault hooked successfully!");
        getServer().getPluginManager().registerEvents(new ChunkEnterLeaveListener(chunkManager), this);
        getServer().getPluginManager().registerEvents(new ChunkProtectionListener(factionManager, chunkManager), this);

        if (getCommand("leavefaction") != null) {
            getCommand("leavefaction").setExecutor(new LeaveFactionCommand(factionManager));
        }

        if (getCommand("sethome") != null) {
            getCommand("sethome").setExecutor(new SetHomeCommand(factionManager, chunkManager));
        }

        if (getCommand("home") != null) {
            getCommand("home").setExecutor(new SetHomeCommand(factionManager, chunkManager));

        }

        if (getCommand("csf") != null) {
            getCommand("csf").setExecutor(new CsfCommand(factionManager, chunkManager, economy, homeCommand));
        }

    }

    @Override
    public void onDisable() {
        if (factionsDataManager != null) {
            factionsDataManager.saveFactions(factionManager, chunkManager);
        }

        getLogger().info("[CyansFactions] disabled successfully!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static CyansFactions getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return economy;
    }

}
