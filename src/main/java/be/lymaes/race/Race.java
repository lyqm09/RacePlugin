package be.lymaes.race;

import be.lymaes.race.command.*;
import be.lymaes.race.listener.*;
import be.lymaes.race.manager.AbilityManager;
import be.lymaes.race.manager.GUIManager;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.plugin.java.JavaPlugin;


public final class Race extends JavaPlugin {

    private static Race instance;
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private Messager messager;

    private RaceManager raceManager;
    private GUIManager guiManager;
    private ItemManager itemManager;
    private AbilityManager abilityManager;

    private MainRunnable mainRunnable;

    @Override
    public void onEnable() {
        // singleton
        instance = this;

        // managers
        this.raceManager = new RaceManager();
        this.guiManager = new GUIManager();
        this.itemManager = new ItemManager();
        this.abilityManager = new AbilityManager(raceManager.getModels());

        // Messager
        this.messager = new Messager(this);

        // listeners
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new ConsumeListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new TargetListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new RaidListener(this), this);
        getServer().getPluginManager().registerEvents(new SneakListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PotionListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftListener(this), this);

        // command
        MutsuharaCMD mutsuhara = new MutsuharaCMD(this);
        getCommand("mutsuhara").setExecutor(mutsuhara);

        GiveCMD give = new GiveCMD(this);
        getCommand("giver").setExecutor(give);
        getCommand("giver").setTabCompleter(give);

        MexpCMD mexp = new MexpCMD(this);
        getCommand("mexp").setExecutor(mexp);

        MtailCMD mtail = new MtailCMD(this);
        getCommand("mtail").setExecutor(mtail);

        HomeCMD home = new HomeCMD(this);
        getCommand("foyer").setExecutor(home);

        BlessCMD bless = new BlessCMD(this);
        getCommand("bless").setExecutor(bless);

        FortuneCMD fortune = new FortuneCMD();
        getCommand("fortune").setExecutor(fortune);

        SharpnessCMD sharpness = new SharpnessCMD();
        getCommand("sharpness").setExecutor(sharpness);

        VillagerCMD villager = new VillagerCMD(this);
        getCommand("villager").setExecutor(villager);

        SetKamiCMD setKami = new SetKamiCMD(this);
        getCommand("setkami").setExecutor(setKami);

        // Runnable
        this.mainRunnable = new MainRunnable(this);
    }

    @Override
    public void onDisable() {
        mainRunnable.terminate();

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
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }
}
