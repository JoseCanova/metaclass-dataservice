package org.nanotek.config;


import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.ClassUtils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.spi.PersistenceProvider;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

public class MetaClassLocalContainerEntityManagerFactoryBean extends LocalContainerEntityManagerFactoryBean{

	@Autowired 
	MetaClassVFSURLClassLoader  inkectionClassLoader;
	
	@Autowired
	MetaClassClassesStore persistenceUnityClassesConfig;
	
	String providerClassName;
	
	SpringHibernateJpaPersistenceProvider provider;
	
	public MetaClassLocalContainerEntityManagerFactoryBean(MetaClassVFSURLClassLoader inkectionClassLoader2) {
		this.inkectionClassLoader = inkectionClassLoader2;
	}
	public void afterPropertiesSet2() throws PersistenceException {
		setBeanClassLoader(inkectionClassLoader);
		providerClassName=SpringHibernateJpaPersistenceProvider.class.getName();
	}
	
	@Override
	public ClassLoader getBeanClassLoader() {
		return inkectionClassLoader ;
	}
	
	
	@Override
	protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
//		getPersistenceProvider()
		Class<?> providerClass = ClassUtils.resolveClassName(providerClassName, getBeanClassLoader());
		provider = (SpringHibernateJpaPersistenceProvider) BeanUtils.instantiateClass(providerClass);
		provider.setPersistenceUnityClassesConfig(persistenceUnityClassesConfig);
		provider.setClassLoader(inkectionClassLoader);
		this.setPersistenceProvider(provider);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Building JPA container EntityManagerFactory for persistence unit '" +
					this.getPersistenceUnitInfo().getPersistenceUnitName() + "'");
		}
		EntityManagerFactory emf =
				provider.createContainerEntityManagerFactory(this.getPersistenceUnitInfo(), getJpaPropertyMap() , inkectionClassLoader , persistenceUnityClassesConfig);
		postProcessEntityManagerFactory(emf, this.getPersistenceUnitInfo() );
		return emf;
	}
	public void setConfig(MetaClassClassesStore persistenceUnityClassesConfig2) {
			this.persistenceUnityClassesConfig = persistenceUnityClassesConfig2;
	}
}