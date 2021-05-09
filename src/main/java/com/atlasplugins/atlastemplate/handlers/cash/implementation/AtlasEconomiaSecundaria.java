package com.atlasplugins.atlastemplate.handlers.cash.implementation;

import org.bukkit.entity.Player;

import com.atlasplugins.atlastemplate.handlers.cash.ICashPlugin;
import com.dont.economiasecundaria.Terminal;
import com.dont.economiasecundaria.database.DataManager;
import com.dont.economiasecundaria.models.database.User;

public class AtlasEconomiaSecundaria implements ICashPlugin {

	private DataManager dontDataManager;

	public AtlasEconomiaSecundaria() {
		this.dontDataManager = Terminal.getPlugin(Terminal.class).getDataManager();
	}

	@Override
	public double getCash(Player player) {
		User user = (User)dontDataManager.get(player.getName());
		return user.getMoney();
	}

	@Override
	public double getCash(String player) {
		User user = (User)dontDataManager.get(player);
		return user.getMoney();
	}

	@Override
	public void setCash(Player player, double cash) {
		User user = (User)dontDataManager.get(player.getName());
		user.setMoney(cash, "Servidor");
	}

	@Override
	public void setCash(String player, double cash) {
		User user = (User)dontDataManager.get(player);
		user.setMoney(cash, "Servidor");
	}

}
