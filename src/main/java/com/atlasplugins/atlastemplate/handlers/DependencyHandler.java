package com.atlasplugins.atlastemplate.handlers;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.atlasplugins.atlastemplate.handlers.cash.AtlasCashRegistry;
import com.atlasplugins.atlastemplate.handlers.cash.ICashPlugin;
import com.atlasplugins.atlastemplate.utils.AtlasMessageUtils;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

@Getter
public class DependencyHandler {

	private final Plugin instance;

	private Chat permissionPlugin;
	private Economy economyPlugin;
	private ICashPlugin cashPlugin;

	public DependencyHandler(Plugin instance) {
		this.instance = instance;
	}

	public boolean setupPermissionPlugin() {

		RegisteredServiceProvider<Chat> rsp = instance.getServer().getServicesManager().getRegistration(Chat.class);

		if(rsp != null && rsp.getProvider() != null) {
			permissionPlugin = rsp.getProvider();
		}

		if(permissionPlugin == null) {
			AtlasMessageUtils.error("Nenhum plugin de permissão foi encontrado (Prefixos e sufixos do grupo de jogadores dentro do servidor podem não funcionar como o esperado)");
			return false;
		}

		return true;

	}

	public boolean setupCashPlugin() {
		AtlasCashRegistry[] registry = AtlasCashRegistry.values();
		Stream<AtlasCashRegistry> registries = Arrays.stream(registry).filter(cashRegistry->cashRegistry.isEnabled());

		if(registries.count() == 0) {
			AtlasMessageUtils.error("Nenhum plugin de cash compatível foi encontrado.");
			AtlasMessageUtils.error("Plugins de cash compatíveis nesta versão: "+registry.toString().replace("[", "").replace("]", ""));
			return false;
		}

		Optional<AtlasCashRegistry> selectedCash = registries.findFirst();

		if(!selectedCash.isPresent()) {
			AtlasMessageUtils.error("Ocorreu um erro inesperado durante o stream de cash.");
			return false;
		}

		AtlasCashRegistry registrySelected = selectedCash.get();

		cashPlugin = registrySelected.getAPI();
		AtlasMessageUtils.info("Plugin de cash ("+registrySelected.getPluginName()+") foi sincronizado com sucesso como dependência de cash.");
		return true;

	}

	public boolean setupEconomy() {

		Server server = instance.getServer();

		if (server.getPluginManager().getPlugin("Vault") == null) {
			AtlasMessageUtils.error("Não foi possível encontrar o plugin Vault para registrar o plugin de economia.");
			AtlasMessageUtils.info("Por favor baixe o Vault + um plugin de economia em: https://www.spigotmc.org/resources/vault.34315/");
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);

		if (rsp == null) {
			return false;
		}

		economyPlugin = rsp.getProvider();

		if(economyPlugin == null) {
			AtlasMessageUtils.error("Não foi possível encontrar um plugin de economia para ser hookado com o "+instance.getName());
			AtlasMessageUtils.info("Por favor coloque um plugin de economia na pasta plugins e tente novamente.");
			return false;
		}

		return true;
	}

}
