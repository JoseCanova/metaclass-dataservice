package org.nanotek.config;

import org.springframework.data.jpa.support.MergingPersistenceUnitManager;

public 	class MetaClassMergingPersistenceUnitManager extends MergingPersistenceUnitManager {
	
	
	PersistenceUnityClassesMap persistenceUnitClassesMap;
	
	public MetaClassMergingPersistenceUnitManager() {
		super();
	}
	
	public MetaClassMergingPersistenceUnitManager(PersistenceUnityClassesMap persistenceUnitClassesMap) {
		super();
		this.persistenceUnitClassesMap=persistenceUnitClassesMap;
	}

	@Override
	public void preparePersistenceUnitInfos() {
		super.preparePersistenceUnitInfos();
	}

	public PersistenceUnityClassesMap getPersistenceUnitClassesMap() {
		return persistenceUnitClassesMap;
	}
}