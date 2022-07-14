package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.VectorArea;
import org.bukkit.*;
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
                    Location location = event.getEntity().getLocation();

                    if (event.getEntity() instanceof Sheep) {
                        this.spawnSheep(location);
                    }

                    if (event.getEntity() instanceof Pig) {
                        this.spawnPig(location);
                    }

                    return;
                }

                if (event.getEntity() instanceof Sheep) {
                    player.sendMessage(ChatColor.RED +
                            "You dumb dumb, I said don't kill the SHEEP!");

                    PlayerUtils.clearPlayerInventory(player);
                    taskFailed(player);
                }

                if (event.getEntity() instanceof Pig) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE +
                            "Good Job!");
                    taskComplete(player);
                }

            }
        }
    }

    public void spawnSheep(Location location) {
        Sheep sheep = (Sheep) location.getWorld().spawnEntity(location, EntityType.SHEEP);
        VersionIndependentUtils.get().setEntityMaxHealth(sheep, 1);
        sheep.setCustomName(ChatColor.LIGHT_PURPLE + "Pig");
        sheep.setColor(DyeColor.PINK);
        sheep.setHealth(1);
    }

    public void spawnPig(Location location) {
        Pig pig = (Pig) location.getWorld().spawnEntity(location, EntityType.PIG);
        pig.setCustomName(ChatColor.LIGHT_PURPLE + "Pig");
    }

    @Override
    public int getDuration() {
        return 6;
    }

    @Override
    public int getLevel() { return 1;}

    @Override
    public void doBeforeTask() {
        // Spawn in Pigs.
        VectorArea area = new VectorArea(28, 81, 47, 42, 81, 33);

        for (int i = 0; i < game.getPlayers().size(); i++) {
            Location location = LocationUtils.getLocation(game.getWorld(), area.getRandomVectorWithin());
            this.spawnPig(location);
        }

        // Spawn in Sheep.
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Location location = LocationUtils.getLocation(game.getWorld(), area.getRandomVectorWithin());
            this.spawnSheep(location);
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
