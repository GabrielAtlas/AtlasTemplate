package com.atlasplugins.atlastemplate.storage;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.atlasplugins.atlastemplate.storage.datasources.AbstractDataSource;
import com.atlasplugins.atlastemplate.storage.exceptions.DatabaseException;
import com.atlasplugins.atlastemplate.storage.handlers.DataTableHandler;
import com.atlasplugins.atlastemplate.storage.interfaces.Storable;

import lombok.SneakyThrows;

public class AtlasDatabaseHandler {
	
	private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

	private AbstractDataSource dataSource;
	private Map<DataTable, DataTableHandler> handlers;

	public AtlasDatabaseHandler(FileConfiguration config) {
		if(DatabaseConnectionType.isValid(config.getString("Database.Tipo"))) {
			DatabaseConnectionType dataType = DatabaseConnectionType.valueOf(config.getString("Database.Tipo"));
			this.dataSource = dataType.getDataSource(config.getString("Database.IP"), config.getString("Database.DB"), config.getString("Database.User"), config.getString("Database.Pass"));
			for(DataTable table : DataTable.values()) {
				Class<? extends Storable> classz = table.getStorableClass();
				String tableName = table.toString().toLowerCase();
				handlers.put(table, new DataTableHandler(this.dataSource, table, tableName, classz));
			}
		}else {
			console.sendMessage("§c[AtlasStorager] Não foi possível identificar o tipo do banco de dados: "+config.getString("Database.Tipo"));
		}
	}
	
	public AbstractDataSource getDataSource() {
		return dataSource;
	}

	public DataTableHandler getDataTable(DataTable dataTable){
		return handlers.get(dataTable);
	}
	
	@SneakyThrows(DatabaseException.class)
	public void close() {
		for(DataTableHandler handler : this.handlers.values()) {
			for(Entry<String, Storable> e : handler.getCache().entrySet()) {
				handler.insert(e.getKey(), e.getValue(), false);
			}
		}
		this.dataSource.close();
	}
}
