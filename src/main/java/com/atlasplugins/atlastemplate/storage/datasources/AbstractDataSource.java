package com.atlasplugins.atlastemplate.storage.datasources;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.atlasplugins.atlastemplate.AtlasTemplate;
import com.atlasplugins.atlastemplate.storage.adapters.ItemStackAdapter;
import com.atlasplugins.atlastemplate.storage.adapters.LocationAdapter;
import com.atlasplugins.atlastemplate.storage.exceptions.DatabaseException;
import com.atlasplugins.atlastemplate.storage.handlers.DataTableHandler;
import com.atlasplugins.atlastemplate.storage.interfaces.Storable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractDataSource {

	protected Gson gson;

	public AbstractDataSource() throws DatabaseException {
		this.gson = buildGson();
	}

	public void async(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(AtlasTemplate.getInstance(), runnable);
	}

	private Gson buildGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.enableComplexMapKeySerialization();
		gsonBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
		gsonBuilder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
		return gsonBuilder.create();
	}

	public abstract <V> void insert(String key, Storable value, boolean async, String tableName);

	public abstract void delete(String key, boolean async, String tableName);

	public abstract <T extends Storable> T find(String key, String tableName, Class<T> vClass);

	public abstract List<Storable> getAll(String tableName, Class<? extends Storable> vClass);

	public abstract boolean exists(String key, String tableName);

	public abstract void createTable(DataTableHandler tableHandler);

	public abstract void close() throws DatabaseException;

}