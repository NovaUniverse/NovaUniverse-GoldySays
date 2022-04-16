package net.novauniverse.goldysays;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.novauniverse.goldysays.game.GoldySaysTask;
import net.novauniverse.goldysays.game.tasks.GoldySaysKillPigs;
import net.novauniverse.goldysays.game.tasks.GoldySaysLookDown;
import net.novauniverse.goldysays.game.tasks.GoldySaysLookUp;
import net.novauniverse.goldysays.game.tasks.GoldySaysTypeUwU;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.mapselector.selectors.guivoteselector.GUIMapVote;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class GoldySays extends JavaPlugin implements Listener {
	private static GoldySays instance;

    protected List<Class<? extends GoldySaysTask>> tasks = new ArrayList();
	
	public static GoldySays getInstance() {
		return instance;
	}
	
	private GoldySaysGame game;
	
	public GoldySaysGame getGame() {
		return game;
	}

    public List<Class<? extends GoldySaysTask>> getTasks() {
        return tasks;
    }

    @Override
	public void onEnable() {
		GoldySays.instance = this;

		saveDefaultConfig();

        tasks.add(GoldySaysKillPigs.class);
        tasks.add(GoldySaysLookUp.class);
        tasks.add(GoldySaysLookDown.class);
        tasks.add(GoldySaysTypeUwU.class);
        
        File mapFolder = new File(this.getDataFolder().getPath() + File.separator + "Maps");
        File worldFolder = new File(this.getDataFolder().getPath() + File.separator + "Worlds");
        
        File mapOverrides = new File(this.getDataFolder().getPath() + File.separator + "map_overrides.json");
        if (mapOverrides.exists()) {
            Log.info("Trying to read map overrides file");
            try {
                JSONObject mapFiles = JSONFileUtils.readJSONObjectFromFile(mapOverrides);

                boolean relative = mapFiles.getBoolean("relative");

                mapFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("maps_folder"));
                worldFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("worlds_folder"));

                Log.info("New paths:");
                Log.info("Map folder: " + mapFolder.getAbsolutePath());
                Log.info("World folder: " + worldFolder.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                Log.error("Failed to read map overrides from file " + mapOverrides.getAbsolutePath());
            }
        }
        
        try {
            FileUtils.forceMkdir(getDataFolder());
            FileUtils.forceMkdir(mapFolder);
            FileUtils.forceMkdir(worldFolder);
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.fatal("Failed to setup data directory");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        ModuleManager.require(GameManager.class);
        ModuleManager.require(GameLobby.class);
        
        this.game = new GoldySaysGame();
        
        GameManager.getInstance().loadGame(game);
        GUIMapVote mapvote = new GUIMapVote();
        
        GameManager.getInstance().setMapSelector(mapvote);
        
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(mapvote, this);
        
        Log.info("GoldySays", "Loading maps from " + mapFolder.getAbsolutePath());
        GameManager.getInstance().readMapsFromFolder(mapFolder, worldFolder);
        Log.info("GoldySays", "Map Loaded!");
        
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);
		
	}

}
