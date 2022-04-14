package net.novauniverse.goldysays.game;

import net.novauniverse.goldysays.game.tasks.GoldySaysLookUp;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.novauniverse.goldysays.GoldySays;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.UUID;

public class GoldySaysGame extends MapGame implements Listener {
	private boolean started;
	private boolean ended;

	public boolean lookUpTask = false;

	public ArrayList completedPlayers = new ArrayList();
	
	public GoldySaysGame() {
		super(GoldySays.getInstance());
		
		this.started = false;
		this.ended = false;
	}

	// NoteBlock Sounds
	//------------------------------------------------------------------------
	public void taskStartSound(Player player) {
		player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));
	}

	public void taskCompleteSound(Player player) {
		player.playSound(player.getLocation(), Sound.CAT_MEOW, 50, 50);
	}

	public void taskFailedSound(Player player) {
		player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.sharp(1, Note.Tone.G));
	}

	// Task Player Checks
	//------------------------------------------------------------------------
	@EventHandler
	public void LookUpPlayerCheck(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		UUID player_id = player.getUniqueId();

		if (this.lookUpTask == true) {
			if (this.completedPlayers.contains(player_id) == false) {
				if (event.getFrom().getPitch() < -10) {
					player.sendMessage( ChatColor.LIGHT_PURPLE + "Goldy \uD83D\uDC4D (▰˘◡˘▰)");
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 10);

					this.completedPlayers.add(player_id);

				}
			}
		}
	}

	@Override
	public String getName() {
		return "goldysays";
	}

	@Override
	public String getDisplayName() {
		return "Goldy Says";
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

		this.getWorld().setDifficulty(Difficulty.PEACEFUL);

		// Adding myself as player.
		players.add(UUID.fromString("3442be05-4211-4a15-a10c-4bdb2b6060fa"));

		// Get players ready and teleport them.
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			PlayerUtils.clearPotionEffects(player);
			PlayerUtils.clearPlayerInventory(player);
			PlayerUtils.resetMaxHealth(player);
			PlayerUtils.fullyHealPlayer(player);
			PlayerUtils.resetPlayerXP(player);

			if (players.contains(player.getUniqueId())) {
				player.setGameMode(GameMode.ADVENTURE);
				player.teleport(this.getActiveMap().getStarterLocations().get(0));
			} else {
				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(this.getActiveMap().getSpectatorLocation());
			}
		}
		
		started = true;
		this.sendBeginEvent();

		// Title Screen
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			VersionIndependantUtils.get().sendTitle(player, ChatColor.YELLOW +
					this.getDisplayName(), ChatColor.GOLD + "Ripoff of simon says! By Goldy!", 10, 5*20, 10);

			// Cat Meow
			player.playSound(player.getLocation(), Sound.CAT_PURREOW, 50, 50);
		}

		// Start Random Game
		int min = 1; int max = 1;
		int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);

		if (random_int == 1) {
			new GoldySaysLookUp(this).startTask();
		}


	}

	@Override
	public void onEnd(GameEndReason reason) {
		if (ended) {
			return;
		}
		
		ended = true;
	}

}
