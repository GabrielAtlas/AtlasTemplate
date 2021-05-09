package com.atlasplugins.atlastemplate.handlers.cash.implementation;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.atlasplugins.atlastemplate.handlers.cash.ICashPlugin;

public class PlayerPointsCashPlugin implements ICashPlugin {

	private PlayerPointsAPI playerPointsAPI;

	public PlayerPointsCashPlugin() {
		final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
		PlayerPoints playerPoints = (PlayerPoints) plugin;
		playerPointsAPI = playerPoints.getAPI();
	}

	@Override
	public double getCash(Player player) {
		return playerPointsAPI.look(player.getUniqueId());
	}

	@SuppressWarnings("deprecation")
	@Override
	public double getCash(String player) {
		return playerPointsAPI.look(player);
	}

	@Override
	public void setCash(Player player, double cash) {
		playerPointsAPI.set(player.getUniqueId(), (int) cash);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setCash(String player, double cash) {
		playerPointsAPI.set(player, (int) cash);
	}

}
