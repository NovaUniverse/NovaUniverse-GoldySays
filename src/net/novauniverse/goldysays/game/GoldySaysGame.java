package net.novauniverse.goldysays.game;

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

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
		goldy.spawn(new Location(this.getWorld(), 35.5, 83, 30.5, 0, 0));

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
