package net.novauniverse.goldysays.game;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public void toggleTask(String taskName) throws IllegalAccessException, NoSuchFieldException {
        // Toggles the task on and off.
        Class<?> class_ = game.getClass();

        Field field_ = class_.getDeclaredField(taskName + "Task");

        if (field_.get(game).equals(false)) {
            field_.set(game, true);
        }else {
            field_.set(game, false);
        }
    }

    public void startTask() {
        Log.debug(getDisplayName() + " task has started.");
        System.out.println(getDisplayName() + " task has started.");

        Bukkit.getServer().getScheduler().runTaskLater(game.getPlugin(), () -> {
            // Start Task
            try {
                this.toggleTask(getCodeName());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            // Show Start Text
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                VersionIndependantUtils.get().sendTitle(player,
                        getDisplayName(), getDescription(), 5, 3 * 20, 5);
            }
        }, 5*20);

        Bukkit.getServer().getScheduler().runTaskLater(game.getPlugin(), () -> {
            // Complete Task
            try {
                this.toggleTask(getCodeName());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            // Show Completed/Failed Text
            for (Player player_2 : Bukkit.getServer().getOnlinePlayers()) {
                if (game.completedPlayers.contains(player_2.getUniqueId())) {
                    game.taskCompleteSound(player_2);

                    VersionIndependantUtils.get().sendTitle(player_2, ChatColor.GREEN +
                            "Task Completed!", "", 5, 3 * 20, 5);
                } else {
                    game.taskFailedSound(player_2);

                    VersionIndependantUtils.get().sendTitle(player_2, ChatColor.DARK_GRAY +
                            "Task Failed!", "", 5, 3 * 20, 5);
                }
            }

            // Clear completed players list.
            game.completedPlayers.clear();
        }, 10*20);
    }
}
