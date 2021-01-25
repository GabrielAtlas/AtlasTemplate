package com.atlasplugins.atlastemplate.storage;

import com.atlasplugins.atlastemplate.objects.AtlasUser;
import com.atlasplugins.atlastemplate.storage.interfaces.Storable;

public enum DataTable {

	USERS(AtlasUser.class);

	private Class<? extends Storable> storable;
	
	DataTable(Class<? extends Storable> storable) {
		this.storable = storable;
	}
	
	public Class<? extends Storable> getStorableClass() {
		return storable;
	}
	
}
