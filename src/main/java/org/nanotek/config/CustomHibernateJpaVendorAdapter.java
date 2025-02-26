package org.nanotek.config;



import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.spi.PersistenceProvider;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

public class   CustomHibernateJpaVendorAdapter extends HibernateJpaVendorAdapter{



	private InjectionClassLoader internalClassLoader;
	private MetaClassClassesStore persistenceUnitClassesMap;

	public CustomHibernateJpaVendorAdapter(InjectionClassLoader classLoader , MetaClassClassesStore persistenceUnitClassesMap) {
		super();
		this.internalClassLoader = classLoader;
		this.persistenceUnitClassesMap = persistenceUnitClassesMap;
	}

	@Override
	public PersistenceProvider getPersistenceProvider() {
		return new SpringHibernateJpaPersistenceProvider(internalClassLoader, persistenceUnitClassesMap);
	}
}
