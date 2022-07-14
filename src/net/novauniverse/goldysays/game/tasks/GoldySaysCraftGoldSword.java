package net.novauniverse.goldysays.game.tasks;

import net.novauniverse.goldysays.game.GoldySaysGame;
import net.novauniverse.goldysays.game.GoldySaysTask;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.utils.*;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GoldySaysCraftGoldSword extends GoldySaysTask {

    public GoldySaysCraftGoldSword(GoldySaysGame game) {
        super(game);
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW + "Craft Gold Sword!";
    }

    @Override
    public String getCodeName() {
        return "craftGoldSword";
    }

    @Override
    public String getDescription() {
        return ChatColor.GOLD + "Make sure to drop it in the cauldron.";
    }

    @EventHandler
    public void dropSwordInWellCheck(PlayerDropItemEvent event) {
        Vector dropVectorLocation = new Vector(35, 82, 31);
        Location dropLocation = LocationUtils.getLocation(game.getWorld(), dropVectorLocation);

        ItemStack goldSword = new ItemBuilder(Material.GOLD_SWORD).build();

        if (event.getItemDrop().getLocation() == dropLocation) {
            if (event.getItemDrop().getItemStack() == goldSword) {
                taskComplete(event.getPlayer());
            }
        }
    }

    public void givePlayerItems(Player player) {
        ItemBuilder goldIngotBuild = new ItemBuilder(Material.GOLD_INGOT);
        goldIngotBuild.setAmount(3);
        ItemStack goldIngots = goldIngotBuild.build();

        ItemBuilder stickBuilder = new ItemBuilder(Material.STICK);
        stickBuilder.setAmount(2);
        ItemStack stick = stickBuilder.build();

        ItemBuilder blazeRodBuilder = new ItemBuilder(Material.BLAZE_ROD);
        blazeRodBuilder.setAmount(1);
        ItemStack blazeRod = blazeRodBuilder.build();

        player.getInventory().addItem(goldIngots, stick, blazeRod);
    }

    public void spawnCauldron() {
        Vector wellVectorLocation = new Vector(35, 81, 31);
        Location wellLocation = LocationUtils.getLocation(game.getWorld(), wellVectorLocation);
        wellLocation.getBlock().setType(Material.CAULDRON);
    }

    public void spawnCraftingTable() {
        Vector craftingTableVectorLocation = new Vector(35, 81, 40);
        Location craftingTableLocation = LocationUtils.getLocation(game.getWorld(), craftingTableVectorLocation);
        craftingTableLocation.getBlock().setType(Material.WORKBENCH);
    }

    @Override
    public int getDuration() {
        return 12;
    }

    @Override
    public int getLevel() { return 2;}

    @Override
    public void doBeforeTask() {
        this.spawnCauldron();
        this.spawnCraftingTable();

        game.allowDropItem = true;

        Bukkit.getServer().getOnlinePlayers().forEach(player -> this.givePlayerItems(player));
    }

    @Override
    public void doAfterTask() {
        game.getWorld().getBlockAt(35, 81, 31).setType(Material.AIR);
        game.getWorld().getBlockAt(35, 81, 40).setType(Material.AIR);

        game.allowDropItem = false;

        Bukkit.getServer().getOnlinePlayers().forEach(player -> PlayerUtils.clearPlayerInventory(player));
    }
}
