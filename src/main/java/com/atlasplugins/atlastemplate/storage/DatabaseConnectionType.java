package com.atlasplugins.atlastemplate.storage;

import com.atlasplugins.atlastemplate.storage.datasources.AbstractDataSource;
import com.atlasplugins.atlastemplate.storage.datasources.MySQLDataSource;
import com.atlasplugins.atlastemplate.storage.datasources.MySQLHikariDataSource;
import com.atlasplugins.atlastemplate.storage.datasources.SQLLiteDataSource;

import lombok.SneakyThrows;

public enum DatabaseConnectionType {

	MYSQL_HIKARI{

		@SneakyThrows(Exception.class)
		@Override
		public AbstractDataSource getDataSource(String ip, String database, String user, String password) {
			return new MySQLHikariDataSource(ip, database, user, password);
		}
		
	}, 
	MYSQL{

		@SneakyThrows(Exception.class)
		@Override
		public AbstractDataSource getDataSource(String ip, String database, String user, String password) {
			return new MySQLDataSource(ip, database, user, password);
		}
		
	},
	SQLLITE{

		@SneakyThrows(Exception.class)
		@Override
		public AbstractDataSource getDataSource(String ip, String database, String user, String password) {
			return new SQLLiteDataSource();
		}
		
	};
	
	public abstract AbstractDataSource getDataSource(String ip, String database, String user, String password);
	
	public static boolean isValid(String a) {
		try {
			DatabaseConnectionType.valueOf(a);
			return true;	
		} catch (Exception e) {
			return false;
		}
	}
	

}
