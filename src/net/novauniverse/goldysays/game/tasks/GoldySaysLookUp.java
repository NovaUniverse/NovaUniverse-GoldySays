package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import org.bukkit.ChatColor;

public class GoldySaysLookUp extends GoldySaysTask {
    public GoldySaysLookUp(GoldySaysGame game) {
        super(game);
    }

    @Override
    public String getCodeName() {
        return "lookUp";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + "Look Up!";
    }

    @Override
    public String getDescription() {
        return ChatColor.GOLD + "Goldy Says!";
    }
}
