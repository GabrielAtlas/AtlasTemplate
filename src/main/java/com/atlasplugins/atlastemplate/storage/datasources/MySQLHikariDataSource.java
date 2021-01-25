package com.atlasplugins.atlastemplate.storage.datasources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.atlasplugins.atlastemplate.storage.exceptions.DatabaseException;
import com.atlasplugins.atlastemplate.storage.handlers.DataTableHandler;
import com.atlasplugins.atlastemplate.storage.interfaces.Storable;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.SneakyThrows;

public class MySQLHikariDataSource extends AbstractDataSource{

	private HikariDataSource dataSource;

	public MySQLHikariDataSource(String ip, String database, String user, String password) throws DatabaseException {
		super();
		openConnection(ip, database, user, password);
	}

	@SneakyThrows(Exception.class)
	private void openConnection(String ip, String database, String user, String password) throws DatabaseException {
		String url = "jdbc:mysql://" + ip + "/" + database + "?autoReconnect=true";
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setUsername(user);
		hikariConfig.setPassword(password);
		hikariConfig.setMaximumPoolSize(3);
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		this.dataSource = new HikariDataSource(hikariConfig);
	}

	@Override
	public <V> void insert(String key, Storable value, boolean async, String tableName) {
		Runnable runnable = () -> {
			try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `" + tableName + "`(`key`, `json`) VALUES(?, ?) ON DUPLICATE KEY UPDATE `json` = VALUES(`json`)")) {
				preparedStatement.setString(1, key);
				preparedStatement.setString(2, gson.toJson(value));
				preparedStatement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		if (async) async(runnable);
		else runnable.run();
	}

	@Override
	public void delete(String key, boolean async, String tableName) {
		Runnable runnable = () -> {
			try (Connection connection = dataSource.getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `" + tableName + "` WHERE `key` = ?")) {
				preparedStatement.setString(1, key);
				preparedStatement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		if (async) async(runnable);
		else runnable.run();
	}

	@Override
	public <T extends Storable> T find(String key, String tableName, Class<T> vClass) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?")) {
			preparedStatement.setString(1, key);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return gson.fromJson(resultSet.getString("json"), vClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Storable> getAll(String tableName, Class<? extends Storable> vClass) {
		List<Storable> values = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "`")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				values.add((Storable) gson.fromJson(resultSet.getString("json"), vClass));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public boolean exists(String key, String tableName) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?")) {
			preparedStatement.setString(1, key);
			return preparedStatement.executeQuery().next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void createTable(DataTableHandler dao) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + dao.getTableName() + "`(`key` VARCHAR(64) NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY (`key`))")) {
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws DatabaseException {
		try {
			if (dataSource != null) {
				dataSource.close();
			}
		} catch (Exception e) {
			throw new DatabaseException("não foi possivel fechar conexão com mysql", e);
		}
	}

}
