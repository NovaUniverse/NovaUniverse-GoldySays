package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.VectorArea;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

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

    @EventHandler
    public void KillPigsPlayerCheck(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            if (event.getEntity().getKiller() instanceof Player) {
                Player player = (Player) event.getEntity().getKiller();
                if (failedPlayers.contains(player.getUniqueId()) || completedPlayers.contains(player.getUniqueId())) {
                    return;
                }

                if (event.getEntity() instanceof Sheep) {
                    player.sendMessage(ChatColor.RED + TextUtils.ICON_SKULL_AND_CROSSBONES +
                            " You dumb dumb, I said don't kill the SHEEP!");
                    taskFailed(player);
                }

                if (event.getEntity() instanceof Pig) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + TextUtils.ICON_SWORDS +
                            " Nice!");
                    taskComplete(player);
                }

            }
        }
    }

    @Override
    public int getDuration() {
        return 6;
    }

    @Override
    public void doBeforeTask() {
        // Spawn in Pigs.
        VectorArea area = new VectorArea(28, 81, 47, 42, 81, 33);

        for (int i = 0; i < game.getPlayers().size(); i++) {
            Location location = LocationUtils.getLocation(game.getWorld(), area.getRandomVectorWithin());
            location.getWorld().spawnEntity(location, EntityType.PIG);
        }

        // Spawn in Sheep.
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Location location = LocationUtils.getLocation(game.getWorld(), area.getRandomVectorWithin());
            Sheep sheep = (Sheep) location.getWorld().spawnEntity(location, EntityType.SHEEP);
            VersionIndependantUtils.get().setEntityMaxHealth(sheep, 1);
            sheep.setHealth(1);
        }

        // Give players sword.
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_SWORD)
                    .setUnbreakable(true).setName(ChatColor.RED + "Goldy's Pig Destroyer").build());
        });


    }

    @Override
    public void doAfterTask() {
        game.getWorld().getEntitiesByClass(Sheep.class).forEach(sheep -> sheep.remove());
        game.getWorld().getEntitiesByClass(Pig.class).forEach(pig -> pig.remove());
        Bukkit.getServer().getOnlinePlayers().forEach(player -> PlayerUtils.clearPlayerInventory(player));
    }
}
