package com.atlasplugins.atlastemplate.handlers.cash;

import org.bukkit.entity.Player;

public interface ICashPlugin {

	double getCash(Player player);
	double getCash(String player);
	void setCash(Player player, double cash);
	void setCash(String player, double cash);
	
}
