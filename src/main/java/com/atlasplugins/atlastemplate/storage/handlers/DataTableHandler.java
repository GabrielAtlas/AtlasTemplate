package com.atlasplugins.atlastemplate.storage.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlasplugins.atlastemplate.storage.DataTable;
import com.atlasplugins.atlastemplate.storage.datasources.AbstractDataSource;
import com.atlasplugins.atlastemplate.storage.interfaces.Storable;

/*
 * @author Atlas
 * @function represents a object reflected in database table.
 */

public class DataTableHandler {

	private Map<String, Storable> cache;
	private AbstractDataSource dataSource;
	private DataTable dataTable;
	private String tableName;
	private Class<? extends Storable> classz;
	
	public DataTableHandler(AbstractDataSource dataSource, DataTable dataTable, String tableName, Class<? extends Storable> classz) {
		super();
		this.dataSource = dataSource;
		this.dataTable = dataTable;
		this.tableName = tableName;
		this.classz = classz;
		this.cache = new HashMap<String, Storable>();
		dataSource.createTable(this);
	}
	
	public boolean isCached(String key) {
		return cache.containsKey(key);
	}
	
	public Map<String, Storable> getCache() {
		return cache;
	}
	
	public void insert(String key, Storable storable, boolean async) {
		this.dataSource.insert(key, storable, async, tableName);
	}
	
	public void delete(String key, boolean async) {
		this.dataSource.delete(key, async, tableName);
	}
	
	public Storable find(String key) {
		return this.dataSource.find(key, tableName, classz);
	}
	
	public List<Storable> getAll(){
		return this.dataSource.getAll(tableName, classz);
	}
	
	public boolean exists(String key) {
		return this.dataSource.exists(key, tableName);
	}
	
	public DataTable getDataTable() {
		return dataTable;
	}

	public String getTableName() {
		return tableName;
	}

	public Class<? extends Storable> getClassz() {
		return classz;
	}
	
}
