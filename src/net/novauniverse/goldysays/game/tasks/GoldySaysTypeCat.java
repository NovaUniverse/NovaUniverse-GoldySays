package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class GoldySaysTypeCat extends GoldySaysTask {
    public GoldySaysTypeCat(GoldySaysGame game) {
        super(game);
    }

    @EventHandler
    public void PlayerTypeCatCheck(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (game.getPlayers().contains(playerId)) {

            if (this.completedPlayers.contains(playerId) == false) {
                if (event.getMessage().equalsIgnoreCase("cat")) {
                    player.sendMessage( ChatColor.YELLOW + "Meow!");

                    this.taskComplete(player);
                }
            }
        }
    }

    @Override
    public String getCodeName() {
        return "typeCat";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW + "Type Cat in Chat!";
    }

    @Override
    public String getDescription() {
        return ChatColor.GOLD + "Goldy Commands!";
    }

    @Override
    public int getLevel() { return 1;}
}
