package org.nanotek.config.hibernate;

import java.util.List;

import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.nanotek.config.SpringHibernateJpaPersistenceProvider;

import jakarta.persistence.spi.PersistenceUnitInfo;

public class MetaClassPersistenceUnitInfoDescriptor extends PersistenceUnitInfoDescriptor {

	private SpringHibernateJpaPersistenceProvider springHibernateJpaPersistenceProvider;
	private List<String> mergedClassesAndPackages;

	public MetaClassPersistenceUnitInfoDescriptor(PersistenceUnitInfo persistenceUnitInfo) {
		super(persistenceUnitInfo);
	}

	public MetaClassPersistenceUnitInfoDescriptor(PersistenceUnitInfo persistenceUnitInfo,
			SpringHibernateJpaPersistenceProvider springHibernateJpaPersistenceProvider,
			List<String> mergedClassesAndPackages) {
		super(persistenceUnitInfo);
		this.springHibernateJpaPersistenceProvider=springHibernateJpaPersistenceProvider;
		this.mergedClassesAndPackages=mergedClassesAndPackages;
	}

	@Override
	public String getProviderClassName() {
		return springHibernateJpaPersistenceProvider.getProvideClassName();
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return springHibernateJpaPersistenceProvider.getInjectedClassLoader();
	}
	
	@Override
	public boolean isExcludeUnlistedClasses() {
		return false;
	}
	
	@Override
	public List<String> getManagedClassNames() {
		return mergedClassesAndPackages;
	}
}
