package com.atlasplugins.atlastemplate.storage.datasources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.atlasplugins.atlastemplate.AtlasTemplate;
import com.atlasplugins.atlastemplate.storage.exceptions.DatabaseException;
import com.atlasplugins.atlastemplate.storage.handlers.DataTableHandler;
import com.atlasplugins.atlastemplate.storage.interfaces.Storable;

import lombok.SneakyThrows;

public class SQLLiteDataSource extends AbstractDataSource{

	private Connection connection;

	public SQLLiteDataSource() throws DatabaseException {
		super();
		openConnection();
	}

	@SneakyThrows(Exception.class)
	private void openConnection() throws DatabaseException {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + AtlasTemplate.getInstance().getDataFolder().getPath() + "/database.db");
	}


	@Override
	public <V> void insert(String key, Storable value, boolean async, String tableName) {
		Runnable runnable = () -> {
			try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO `" + tableName + "`(`key`, `json`) VALUES(?, ?)")) {
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
			try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `" + tableName + "` WHERE `key` = ?")) {
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
		try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?")) {
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
		try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "`")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				values.add(gson.fromJson(resultSet.getString("json"), vClass));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;	
	}

	@Override
	public boolean exists(String key, String tableName) {
		try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?")) {
			preparedStatement.setString(1, key);
			return preparedStatement.executeQuery().next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void createTable(DataTableHandler tableHandler) {
		try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableHandler.getTableName() + "`(`key` VARCHAR(64) NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY (`key`))")) {
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SneakyThrows(SQLException.class)
	@Override
	public void close() throws DatabaseException {
		if (connection != null) {
			connection.close();
		}
	}

}
