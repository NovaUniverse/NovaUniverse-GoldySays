package net.novauniverse.goldysays.game;

import net.novauniverse.goldysays.GoldySays;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.sql.rowset.spi.SyncResolver;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class GoldySaysTask implements Listener {
    protected GoldySaysGame game;

    protected List<UUID> completedPlayers = new ArrayList();
    protected List<UUID> failedPlayers = new ArrayList();

    public GoldySaysTask(GoldySaysGame game) {
        this.game = game;
    }

    public abstract String getCodeName();

    public abstract String getDisplayName();

    public abstract String getDescription();

    public int getDuration() {
        return 5;
    }

    public List<UUID> getCompletedPlayers() {
        return completedPlayers;
    }

    public List<UUID> getFailedPlayers() {
        return failedPlayers;
    }

    // Task Completion & Fail
    //------------------------------------------------------------------------
    public void taskStart(Player player) {
        player.sendMessage(ChatColor.YELLOW + "[" + game.getDisplayName() + ChatColor.YELLOW + "] " +
                ChatColor.ITALIC + ChatColor.BOLD + this.getDisplayName());

        player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));

        // Clear dropped items.
        World world = Bukkit.getServer().getWorld("world");
        List<Entity> entList = world.getEntities();

        for(Entity current : entList) {
            if (current instanceof Item) {
                current.remove();
            }
        }
    }

    public void taskComplete(Player player) {
        this.completedPlayers.add(player.getUniqueId());

        player.sendMessage(ChatColor.YELLOW + "[" + game.getDisplayName() + ChatColor.YELLOW + "] " +
                ChatColor.GREEN + "Task Completed!");

        // Complete Sound
        player.playSound(player.getLocation(), Sound.CAT_MEOW, 1F, 2F);

        // Complete Title
        VersionIndependantUtils.get().sendTitle(player, ChatColor.GREEN +
                "Task Completed!", "", 5, 3 * 20, 5);

        PlayerUtils.clearPlayerInventory(player);
    }

    public void taskFailed(Player player) {
        this.failedPlayers.add(player.getUniqueId());

        player.sendMessage(ChatColor.YELLOW + "[" + game.getDisplayName() + ChatColor.YELLOW + "] " +
                ChatColor.DARK_GRAY + "Task Failed!");

        // Fail Sound
        player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.sharp(1, Note.Tone.G));

        // Fail Title
        VersionIndependantUtils.get().sendTitle(player, ChatColor.DARK_GRAY +
                "Task Failed!", "", 5, 3 * 20, 5);

        PlayerUtils.clearPlayerInventory(player);
    }

    public void doBeforeTask() {
        /** Override this to prepare things before the actual task starts. **/
    }

    public void doAfterTask() {
        /** Override this to do things after the task is done. **/
    }

    public void startTask() {
        this.doBeforeTask();

        Bukkit.getServer().getPluginManager().registerEvents(this, GoldySays.getInstance());

        // Show Start Text
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            this.taskStart(player);

            VersionIndependantUtils.get().sendTitle(player,
                    this.getDisplayName(), this.getDescription(), 5, 3 * 20, 5);
        }

        Bukkit.getServer().getScheduler().runTaskLater(game.getPlugin(), () -> {
            Log.debug(this.getDisplayName() + " task has ended.");

            HandlerList.unregisterAll(this);

            // Tell players if they have failed or completed.
            //-------------------------------------------------
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (game.getPlayers().contains(player.getUniqueId())) {
                    if (!this.completedPlayers.contains(player.getUniqueId())) {
                        if (!this.failedPlayers.contains(player.getUniqueId())) {
                            this.taskFailed(player);
                        }
                    }

                }
            }

            this.doAfterTask();

            GoldyTaskCompletedEvent event = new GoldyTaskCompletedEvent(this);
            Bukkit.getServer().getPluginManager().callEvent(event);

        }, (this.getDuration())*20);
    }
}
