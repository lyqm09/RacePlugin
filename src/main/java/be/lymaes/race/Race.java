package be.lymaes.race;

import be.lymaes.race.command.*;
import be.lymaes.race.item.IRaceItem;
import be.lymaes.race.listener.*;
import be.lymaes.race.manager.GUIManager;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public final class Race extends JavaPlugin {

    private static Race instance;
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private Messager messager;

    private RaceManager raceManager;
    private GUIManager guiManager;
    private ItemManager itemManager;

    private BukkitRunnable mainRunnable;

    @Override
    public void onEnable() {
        // singleton
        instance = this;

        // Messager
        this.messager = new Messager(this);

        // managers
        this.raceManager = new RaceManager();
        this.guiManager = new GUIManager();
        this.itemManager = new ItemManager();

        // listeners
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new StaticItemListener(this), this);

        for(IRace instance : raceManager.getRegisterValues()) {
            getServer().getPluginManager().registerEvents(instance, this);
        }
        for(IRaceItem instance : itemManager.getRegisterValues()) {
            getServer().getPluginManager().registerEvents(instance, this);
        }

        // command
        MutsuharaCMD mutsuhara = new MutsuharaCMD(this);
        getCommand("mutsuhara").setExecutor(mutsuhara);

        GiveCMD give = new GiveCMD(this);
        getCommand("giver").setExecutor(give);
        getCommand("giver").setTabCompleter(give);

        MexpCMD mexp = new MexpCMD(this);
        getCommand("mexp").setExecutor(mexp);

        HomeCMD home = new HomeCMD(this);
        getCommand("home").setExecutor(home);

        BlessCMD bless = new BlessCMD(this);
        getCommand("bless").setExecutor(bless);

        FortuneCMD fortune = new FortuneCMD();
        getCommand("fortune").setExecutor(fortune);

        SharpnessCMD sharpness = new SharpnessCMD();
        getCommand("sharpness").setExecutor(sharpness);

        VillagerCMD villager = new VillagerCMD(this);
        getCommand("villager").setExecutor(villager);

        // Runnable
        this.mainRunnable = new MainRunnable(this);
    }

    @Override
    public void onDisable() {
        if(mainRunnable != null && !mainRunnable.isCancelled()) {
            mainRunnable.cancel();
            this.mainRunnable = null;
        }

        itemManager.terminate();
        guiManager.terminate();
        raceManager.terminate();

        messager.terminate();
    }

    public static Race getInstance() {
        return instance;
    }

    public Messager getMessager() {
        return messager;
    }

    public RaceManager getRaceManager() {
        return raceManager;
    }
    public GUIManager getGuiManager() {
        return guiManager;
    }
    public ItemManager getItemManager() {
        return itemManager;
    }
}
