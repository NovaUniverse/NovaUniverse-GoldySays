package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import org.bukkit.ChatColor;

public class GoldySaysKillPigs extends GoldySaysTask {

    public GoldySaysKillPigs(GoldySaysGame game) {
        super(game);
    }

    @Override
    public String getDisplayName() {
        return ChatColor.LIGHT_PURPLE + "Kill Pigs!";
    }

    @Override
    public String getCodeName() {
        return "killPigs";
    }

    @Override
    public String getDescription() {
        return ChatColor.RED + "Just leave the sheep alone, alright!";
    }

    @Override
    public void doBeforeTask() {
        // Spawn in Pigs and Cows.

    }
}
