package org.nanotek.config;

import org.springframework.data.jpa.support.MergingPersistenceUnitManager;

public 	class MetaClassMergingPersistenceUnitManager extends MergingPersistenceUnitManager {
	
	public MetaClassMergingPersistenceUnitManager() {
		super();
	}
	
	@Override
	public void preparePersistenceUnitInfos() {
		super.preparePersistenceUnitInfos();
	}
}