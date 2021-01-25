package com.atlasplugins.atlastemplate.storage.exceptions;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

/**
 * @author Atlas
 */

public class DatabaseException extends Exception {

	private static final ConsoleCommandSender console = Bukkit.getConsoleSender();
	private static final long serialVersionUID = 1L;

	public DatabaseException(String message) {
        super(message);
        console.sendMessage("§c[AtlasDatabase] [DataSource Exception] Ocorreu um erro ao executar uma ação envolvendo um banco de dados.");
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        console.sendMessage("§c[AtlasDatabase] [DataSource Exception] Ocorreu um erro ao executar uma ação envolvendo um banco de dados.");
    }
}