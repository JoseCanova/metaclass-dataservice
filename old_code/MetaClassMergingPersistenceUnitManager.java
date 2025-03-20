package org.nanotek.config;

import org.springframework.data.jpa.support.MergingPersistenceUnitManager;

public 	class MetaClassMergingPersistenceUnitManager extends MergingPersistenceUnitManager {
	
	
	MetaClassClassesStore persistenceUnitClassesMap;
	
	public MetaClassMergingPersistenceUnitManager() {
		super();
	}
	
	public MetaClassMergingPersistenceUnitManager(MetaClassClassesStore persistenceUnitClassesMap) {
		super();
		this.persistenceUnitClassesMap=persistenceUnitClassesMap;
	}

	@Override
	public void preparePersistenceUnitInfos() {
		super.preparePersistenceUnitInfos();
	}

	public MetaClassClassesStore getPersistenceUnitClassesMap() {
		return persistenceUnitClassesMap;
	}
}