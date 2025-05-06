package cyansfactions;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import cyansfactions.commands.CreateFactionCommand;
import cyansfactions.commands.CsfCommand;
import cyansfactions.commands.DelWarpCommand;
import cyansfactions.commands.HomeCommand;
import cyansfactions.commands.InviteCommand;
import cyansfactions.commands.LeaveFactionCommand;
import cyansfactions.listeners.FactionsChatListener;
import cyansfactions.commands.ListWarpsCommand;
import cyansfactions.commands.SetHomeCommand;
import cyansfactions.listeners.ChunkEnterLeaveListener;
import cyansfactions.listeners.ChunkProtectionListener;
import cyansfactions.listeners.CombatListener;
import cyansfactions.managers.ChatManager;
import cyansfactions.managers.ChunkManager;
import cyansfactions.managers.FactionManager;
import cyansfactions.managers.WarManager;
import cyansfactions.storage.FactionsDataManager;
import cyansfactions.commands.ClaimChunkCommand;
import cyansfactions.commands.AcceptInviteCommand;
import cyansfactions.commands.WarpCommand;

public class CyansFactions extends JavaPlugin {

    private static CyansFactions instance;
    private static Economy economy; 
    private FactionManager factionManager;
    private ChunkManager chunkManager;
    private FactionsDataManager factionsDataManager;
    private HomeCommand homeCommand;
    private WarManager warManager;


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

        this.factionManager = new FactionManager(this); // 🔥 passing CyansFactions plugin instance
        this.chunkManager = new ChunkManager(this, factionManager); // 🔥 passing plugin + factionManager        
        this.homeCommand = new HomeCommand(factionManager);
        this.warManager = new WarManager(factionManager);

        ChatManager chatManager = new ChatManager();
        WarpCommand warpCommand = new WarpCommand(factionManager, economy);
        DelWarpCommand delWarpCommand = new DelWarpCommand(factionManager);
        ListWarpsCommand listWarpsCommand = new ListWarpsCommand(factionManager);
        CsfCommand csfCommand = new CsfCommand(factionManager, factionsDataManager, chunkManager, economy, homeCommand, warManager, warpCommand, delWarpCommand, listWarpsCommand, chatManager);
        getCommand("csf").setExecutor(csfCommand);
        getServer().getPluginManager().registerEvents(new CombatListener(warpCommand.getLastCombatMap()), this);
        getServer().getPluginManager().registerEvents(new CombatListener(homeCommand.getLastCombatMap()), this);
        getServer().getPluginManager().registerEvents(new FactionsChatListener(factionManager, chatManager), this);

        factionsDataManager.loadFactions(factionManager, chunkManager);
        factionsDataManager.loadWars(warManager, factionManager);

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
        getServer().getPluginManager().registerEvents(new ChunkProtectionListener(chunkManager, warManager), this);

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
            getCommand("csf").setExecutor(new CsfCommand(factionManager, factionsDataManager, chunkManager, economy, homeCommand, warManager, warpCommand, delWarpCommand, listWarpsCommand, chatManager));
        }
    }

    @Override
    public void onDisable() {
        if (factionsDataManager != null) {
            factionsDataManager.saveFactions(factionManager, chunkManager);
            factionsDataManager.saveWars(warManager);
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
