package com.atlasplugins.atlastemplate.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.atlasplugins.atlastemplate.AtlasTemplate;

import net.md_5.bungee.api.ChatColor;

public class AtlasMessageUtils {

	private static final Plugin instance;
	private static final CommandSender console;

	static {
		console = Bukkit.getConsoleSender();
		instance = AtlasTemplate.getInstance();
	}
	
	public static void info(String message) {
		console.sendMessage(ChatColor.AQUA + "["+instance.getName()+" INFO] " + ChatColor.WHITE + "" + message);
	}
	
	public static void warn(String message) {
		console.sendMessage(ChatColor.YELLOW + "["+instance.getName()+" WARN] " + ChatColor.WHITE + "" + message);
	}
	
	public static void error(String message) {
		console.sendMessage(ChatColor.RED + "["+instance.getName()+" ERROR] " + ChatColor.WHITE + "" + message);
	}


}
