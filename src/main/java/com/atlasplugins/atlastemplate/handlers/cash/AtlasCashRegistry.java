package com.atlasplugins.atlastemplate.handlers.cash;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import com.atlasplugins.atlastemplate.handlers.cash.implementation.AtlasEconomiaSecundaria;
import com.atlasplugins.atlastemplate.handlers.cash.implementation.PlayerPointsCashPlugin;

import lombok.Getter;

public enum AtlasCashRegistry {

	ATLASECONOMIASECUNDARIA("AtlasEconomiaSecundaria"){

		@Override
		public ICashPlugin getAPI() {
			return new AtlasEconomiaSecundaria();
		}

	},

	PLAYERPOINTS("PlayerPoints") {

		@Override
		public ICashPlugin getAPI() {
			return new PlayerPointsCashPlugin();
		}

	},
	;

	private static final PluginManager manager = Bukkit.getPluginManager();

	private @Getter final String pluginName;

	private AtlasCashRegistry(String pluginName) {
		this.pluginName = pluginName;
	}

	public boolean isEnabled() {
		return manager.getPlugin(this.pluginName) != null;
	}

	public abstract ICashPlugin getAPI();
}
