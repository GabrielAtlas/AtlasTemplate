package com.atlasplugins.atlastemplate.objects;

import com.atlasplugins.atlastemplate.storage.interfaces.Storable;

public class AtlasUser implements Storable{
	
	private String username;
	private double coins;
	
	public AtlasUser() {
		this.username = "TheAtlas_";
		this.coins = 999999;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}

	@Override
	public String getKey() {
		return username;
	}

}
