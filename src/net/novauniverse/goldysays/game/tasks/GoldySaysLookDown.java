package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class GoldySaysLookDown extends GoldySaysTask {
    public GoldySaysLookDown(GoldySaysGame game) {
        super(game);
    }

    @EventHandler
    public void LookDownPlayerCheck(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (game.getPlayers().contains(playerId)) {
            if (this.completedPlayers.contains(playerId) == false) {
                if (event.getFrom().getPitch() > 50) {
                    player.sendMessage( ChatColor.LIGHT_PURPLE + "Goldy \uD83D\uDC4D (▰˘◡˘▰)");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);

                    this.taskComplete(player);
                }
            }
        }
    }

    @Override
    public String getCodeName() {
        return "lookDown";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_GREEN + "Look Down!";
    }

    @Override
    public String getDescription() {
        return ChatColor.GOLD + "Goldy Says!";
    }

    @Override
    public int getDuration() {
        return 2;
    }
}
