package org.nanotek.config;

import org.springframework.data.jpa.support.MergingPersistenceUnitManager;

public 	class MyMergingPersistenceUnitManager extends MergingPersistenceUnitManager {
	
	public MyMergingPersistenceUnitManager() {
		super();
	}
	
	@Override
	public void preparePersistenceUnitInfos() {
		super.preparePersistenceUnitInfos();
	}
}