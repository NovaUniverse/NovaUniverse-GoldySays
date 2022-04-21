package net.novauniverse.goldysays.game;

import net.citizensnpcs.trait.SkinTrait;
import net.novauniverse.goldysays.game.tasks.GoldySaysKillPigs;
import net.novauniverse.goldysays.game.tasks.GoldySaysLookDown;
import net.novauniverse.goldysays.game.tasks.GoldySaysLookUp;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.goldysays.GoldySays;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.FileHandler;

public class GoldySaysGame extends MapGame implements Listener {
	private boolean started;
	private boolean ended;

	private NPC goldy;
	
	public GoldySaysGame() {
		super(GoldySays.getInstance());
		
		this.started = false;
		this.ended = false;
	}

	protected List<GoldySaysTask> goldySaysTasks = new ArrayList();

	@Override
	public String getName() {
		return "goldysays";
	}

	@Override
	public String getDisplayName() {
		return ChatColor.GOLD + "Goldy Says";
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (hasStarted()) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (hasStarted()) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (hasStarted()) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				e.setCancelled(true);
			}
		}
	}

	@Override
	public PlayerQuitEliminationAction getPlayerQuitEliminationAction() {
		return PlayerQuitEliminationAction.NONE;
	}

	@Override
	public boolean eliminatePlayerOnDeath(Player player) {
		return false;
	}

	@Override
	public boolean isPVPEnabled() {
		return false;
	}

	@Override
	public boolean autoEndGame() {
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

	@Override
	public boolean hasEnded() {
		return ended;
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return true;
	}

	@Override
	public boolean canAttack(LivingEntity attacker, LivingEntity target) {
		return true;
	}

	@Override
	public void onStart() {
		if (started) {
			return;
		}

		goldy = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "THEGOLDENPRO");
		goldy.spawn(new Location(this.getWorld(), 35.5, 83, 30.5, 0, 20));

		new BukkitRunnable() {
			@Override
			public void run() {
				SkinTrait skinTrait = goldy.getOrAddTrait(SkinTrait.class);
				String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDQ5MDcxMTkzMCwKICAicHJvZmlsZUlkIiA6ICI1YjY2YzNkZWZhYTI0NWMzYTcwNjM3OTA3NTQ0Yjg3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZWFuX1JhaWNvMDgxNiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YTQ5NGY1Njk3ZTUzMmQ4MmY4MzY4ZmUxNDQ4MWVhZWU2NmE0NWU1NjdkMGQyOGZlMmY4NGE4MGIwMWNlYTM2IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0";
				String signature = "cUMFsqMcnFi/kE+/PkGjYmnPw/2tktQewiLq/ZXlI2BAw2GHNENxC6KgpLheKVQYki0D6cg4CzXhfPgIYElt2Xy55w1KBxg/ZvTY6v4W3bGBdKOWAzgZVKAzgWn07D4Nh+Vtp0xCIlc5QzdEzgk0eNR9+S0QxU8PiXxRiLFJB11FH1KKxMEGVSWX5juEb2xPPk1hckcKkZkBl013Liw/354dAue8H8GF60HMCk53htiHaqhF2Iqc31ilj1cxxfMk7Y0X3NnpUFmZQc2a95KD8pZ/GkUupkNle7BDd+NS4r87I5TS9rPAUL9/zHLlQMKCrKKUpleqonVMfG7pWPd25rDdZgPhxlADEILuLrRNjbt3uKORxpIcs+Am0XTFI3KUgXZYX6u/mbbFk2SHmFVgcQA/1iduQimReW5q9alnGEsU2+sQYG2AvVYWnenRJDICI961vYcfpb902lsxBY4ztIcNbeFPlQ/jHTVAjmLnUV0PSv5SfrevUAdJkc040CcyF+b1kwLVroIFUBmIQKRXqJR319QBHoO9xNPoq1ITy8/tPTViU859In1PjobALdVwahAw+6Fju0TLJD9BerxoeQgmiRa64fb039WTT/H0T6oyvjluCKvZPL8BEIxzBqSAHxo16INHkpMWHtj/h+0ixafAMq7ef21UKI5DPnEszSU=";
				skinTrait.setSkinPersistent("5b66c3defaa245c3a70637907544b870", signature, texture);
				skinTrait.setSkinName("5b66c3defaa245c3a70637907544b870", true);
			}
		}.runTaskLater(getPlugin(), 5L);

		this.getWorld().setDifficulty(Difficulty.PEACEFUL);

		// Adding myself as player.
		players.add(UUID.fromString("3442be05-4211-4a15-a10c-4bdb2b6060fa"));
		//TODO: Remove this.

		// Get players ready and teleport them.
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			PlayerUtils.clearPotionEffects(player);
			PlayerUtils.clearPlayerInventory(player);
			PlayerUtils.resetMaxHealth(player);
			PlayerUtils.fullyHealPlayer(player);
			PlayerUtils.resetPlayerXP(player);

			if (players.contains(player.getUniqueId())) {
				player.setGameMode(GameMode.SURVIVAL);
				player.teleport(this.getActiveMap().getStarterLocations().get(0));
			} else {
				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(this.getActiveMap().getSpectatorLocation());
			}
		}

		// Title Screen
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			VersionIndependantUtils.get().sendTitle(player, ChatColor.GOLD +
					"Goldy Says", ChatColor.YELLOW + "Totally not a Ripoff of simon says!",
					10, 5*20, 10);

			// Cat Meow
			player.playSound(player.getLocation(), Sound.CAT_PURREOW, 50, 50);
		}

		List<Class<? extends GoldySaysTask>> tasks = new ArrayList(GoldySays.getInstance().getTasks());

		Collections.shuffle(tasks);

		for (int i=0; i < 10; i++) {
			if (tasks.size() == 0) {
				break;
			}

			Class<? extends GoldySaysTask> class_ = tasks.remove(0);

			try {
				GoldySaysTask task = (GoldySaysTask) class_.getConstructor(GoldySaysGame.class).newInstance(new Object[] {this});
				goldySaysTasks.add(task);
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("Failed to load class " + class_.getName() + " Reason: " + e.getClass().getName() + " " + e.getMessage());

			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				startNextTask();
			}

		}.runTaskLater(GoldySays.getInstance(), 7*20);

		started = true;
		this.sendBeginEvent();


	}

	private void startNextTask() {
		GoldySaysTask task = goldySaysTasks.remove(0);
		task.startTask();
		Log.debug("Starting next task " + task.getCodeName());
	}

	@EventHandler
	public void onTaskCompleted(GoldyTaskCompletedEvent event) {
		if (goldySaysTasks.size() == 0) {
			this.endGame(GameEndReason.ALL_FINISHED);
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				startNextTask();
			}
		}.runTaskLater(GoldySays.getInstance(), 5*20);
	}

	@Override
	public void onEnd(GameEndReason reason) {
		if (ended) {
			return;
		}

		goldy.destroy();
		
		ended = true;
	}

}
