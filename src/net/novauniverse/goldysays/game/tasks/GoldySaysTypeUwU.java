package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class GoldySaysTypeUwU extends GoldySaysTask {
    public GoldySaysTypeUwU(GoldySaysGame game) {
        super(game);
    }

    @EventHandler
    public void PlayerTypeUwUCheck(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (game.getPlayers().contains(playerId)) {

            if (this.completedPlayers.contains(playerId) == false) {
                if (event.getMessage().equalsIgnoreCase("UwU")) {
                    player.sendMessage( ChatColor.LIGHT_PURPLE + "Goldy \uD83D\uDC4D (▰˘◡˘▰)");
                    player.playSound(player.getLocation(), Sound.CAT_PURR, 1F, 1F);

                    this.taskComplete(player);
                }
            }
        }
    }

    @Override
    public String getCodeName() {
        return "typeUwU";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + "Type UwU in Chat!";
    }

    @Override
    public String getDescription() {
        return ChatColor.GOLD + "Goldy Commands!";
    }

    @Override
    public int getLevel() { return 1;}
}
