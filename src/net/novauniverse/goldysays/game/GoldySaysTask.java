package net.novauniverse.goldysays.game;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.sql.rowset.spi.SyncResolver;
import java.lang.reflect.Field;

public class GoldySaysTask {
    protected GoldySaysGame game;

    public GoldySaysTask(GoldySaysGame game) {
        this.game = game;
    }

    public String getCodeName() {
        return "task";
    }

    public String getDisplayName() {
        return "Task";
    }

    public String getDescription() {
        return "Goldy Says!";
    }

    public int getDuration() {
        return 5;
    }

    // Task Completion & Fail
    //------------------------------------------------------------------------
    public void taskStart(Player player) {
        player.sendMessage(ChatColor.YELLOW + "[" + game.getDisplayName() + ChatColor.YELLOW + "] " +
                ChatColor.ITALIC + ChatColor.BOLD + this.getDisplayName());

        player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));
    }

    public void taskComplete(Player player) {
        player.sendMessage(ChatColor.YELLOW + "[" + game.getDisplayName() + ChatColor.YELLOW + "] " +
                ChatColor.GREEN + "Task Completed!");

        // Complete Sound
        player.playSound(player.getLocation(), Sound.CAT_MEOW, 50, 50);

        // Complete Title
        VersionIndependantUtils.get().sendTitle(player, ChatColor.GREEN +
                "Task Completed!", "", 5, 3 * 20, 5);
    }

    public void taskFailed(Player player) {
        player.sendMessage(ChatColor.YELLOW + "[" + game.getDisplayName() + ChatColor.YELLOW + "] " +
                ChatColor.DARK_GRAY + "Task Failed!");

        // Fail Sound
        player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.sharp(1, Note.Tone.G));

        // Fail Title
        VersionIndependantUtils.get().sendTitle(player, ChatColor.DARK_GRAY +
                "Task Failed!", "", 5, 3 * 20, 5);
    }

    public void toggleTaskEvent(String taskName) throws IllegalAccessException, NoSuchFieldException {
        // Toggles the task on and off.
        Class<?> class_ = game.getClass();

        Field field_ = class_.getDeclaredField(taskName + "Task");

        if (field_.get(game).equals(false)) {
            field_.set(game, true);
        }else {
            field_.set(game, false);
        }
    }

    public void doBeforeTask() {
        /** Override this to prepare things before the actual task starts. **/
    }

    public void doAfterTask() {
        /** Override this to do things after the task is done. **/
    }

    public void startTask() {
        Bukkit.getServer().getScheduler().runTaskLater(game.getPlugin(), this::doBeforeTask, 5*20);

        Bukkit.getServer().getScheduler().runTaskLater(game.getPlugin(), () -> {
            Log.debug(this.getDisplayName() + " task has started.");

            // Start Task Event Handler
            try {
                this.toggleTaskEvent(getCodeName());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            // Show Start Text
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                this.taskStart(player);

                VersionIndependantUtils.get().sendTitle(player,
                        this.getDisplayName(), this.getDescription(), 5, 3 * 20, 5);
            }
        }, 6*20);

        Bukkit.getServer().getScheduler().runTaskLater(game.getPlugin(), () -> {
            Log.debug(this.getDisplayName() + " task has ended.");

            // Toggle Task Event Handler off.
            //------------------------
            try {
                this.toggleTaskEvent(getCodeName());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            // Tell players if they have failed or completed.
            //-------------------------------------------------
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (game.completedPlayers.contains(player.getUniqueId())) {
                    this.taskComplete(player);
                } else {
                    this.taskFailed(player);

                }
            }

            // Clear completed players list.
            game.completedPlayers.clear();

            this.doAfterTask();
        }, (6 + this.getDuration())*20);
    }
}
