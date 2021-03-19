package com.atlasplugins.atlastemplate;

import com.atlasplugins.atlastemplate.core.AtlasDevelopmentCore;

import lombok.Getter;

public class AtlasTemplate extends AtlasDevelopmentCore{
	
	private static @Getter AtlasTemplate instance;
	
	public AtlasTemplate() {
		super(true, true);
		instance = this;
		setupDatabaseConnection(); // you can remove this
	}

	@Override
	public void load() {
		
	}

	@Override
	public void enable() {
		
	}

	@Override
	public void disable() {
		
	}

}
