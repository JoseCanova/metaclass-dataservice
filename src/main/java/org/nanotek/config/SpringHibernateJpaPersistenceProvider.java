package org.nanotek.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.nanotek.config.hibernate.MetaClassPersistenceUnitInfoDescriptor;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.ProviderUtil;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

public class SpringHibernateJpaPersistenceProvider extends HibernatePersistenceProvider {

	private MetaClassVFSURLClassLoader classLoader = null;
	private MetaClassClassesStore persistenceUnityClassesConfig;

	public SpringHibernateJpaPersistenceProvider() {
		super();
	}

	public SpringHibernateJpaPersistenceProvider(MetaClassVFSURLClassLoader classLoader,
			MetaClassClassesStore persistenceUnityClassesConfig) {
		super();
		this.classLoader = classLoader;
		this.persistenceUnityClassesConfig = persistenceUnityClassesConfig;
	}


	@Override
	public ProviderUtil getProviderUtil() {
		return super.getProviderUtil();
	}
	
	
	
	@Override
	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
		final List<String> mergedClassesAndPackages = new ArrayList<>(info.getManagedClassNames());
		if (info instanceof SmartPersistenceUnitInfo) {
			mergedClassesAndPackages.addAll(((SmartPersistenceUnitInfo) info).getManagedPackages());
		}
		if (persistenceUnityClassesConfig !=null) {
			persistenceUnityClassesConfig.forEach((x , y)->{
				mergedClassesAndPackages.add(y.getName());
			});
		}
		
		return new EntityManagerFactoryBuilderImpl(
				new MetaClassPersistenceUnitInfoDescriptor(info , this,mergedClassesAndPackages), weavingProperties(properties) , classLoader !=null ? classLoader : new MultipleParentClassLoader(getClass().getClassLoader() , new ArrayList<>(), false))
				.build();
	}

	private Map<String,Object> weavingProperties(Map properties) {
		Map<String,Object> map = new HashMap<>();
		
//		map.put("hibernate.default_entity_mode", "pojo");
		map.put("hibernate.use_sql_comments", true);
		map.put("hibernate.jpa.compliance.query", false);
		map.put("hibernate.id.sequence.increment_size_mismatch_strategy", "true");
		map.put("hibernate.bytecode.provider", "bytebuddy");
		map.put("hibernate.max_fetch_depth", "1");
		map.put("hibernate.flushMode",org.hibernate.FlushMode.MANUAL);
		map.put("hibernate.jpa.compliance.transaction" , true);
		map.put("hibernate.transaction.flush_before_completion"  , true);
		map.put("hibernate.transaction.auto_close_session" , false);
		map.put("hibernate.current_session_context_class" , "thread" );
		map.put("hibernate.enable_lazy_load_no_trans" , true);
		
//		map.put("hibernate.enable_lazy_load_no_trans", "true");
		properties
			.keySet()
			.stream()
			.forEach(k -> {
				map.put(k.toString(), properties.get(k));
			});
		return map;
	}


	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo persistenceUnitInfo,
			Map<String, Object> jpaPropertyMap, MetaClassVFSURLClassLoader inkectionClassLoader) {
		this.classLoader  = inkectionClassLoader;
		return createContainerEntityManagerFactory( persistenceUnitInfo,  jpaPropertyMap);
	}


	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo persistenceUnitInfo,
			Map<String, Object> jpaPropertyMap, MetaClassVFSURLClassLoader inkectionClassLoader,
			MetaClassClassesStore persistenceUnityClassesConfig2) {
		// TODO Auto-generated method stub
		this.persistenceUnityClassesConfig = persistenceUnityClassesConfig2;
		return createContainerEntityManagerFactory( persistenceUnitInfo,
				 jpaPropertyMap,  inkectionClassLoader);
	}


	public MetaClassVFSURLClassLoader getInjectedClassLoader() {
		return classLoader;
	}
	
	public String getProvideClassName() {
		return SpringHibernateJpaPersistenceProvider.class.getName();
	}


	public MetaClassVFSURLClassLoader getClassLoader() {
		return classLoader;
	}


	public void setClassLoader(MetaClassVFSURLClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	public MetaClassClassesStore getPersistenceUnityClassesConfig() {
		return persistenceUnityClassesConfig;
	}


	public void setPersistenceUnityClassesConfig(MetaClassClassesStore persistenceUnityClassesConfig) {
		this.persistenceUnityClassesConfig = persistenceUnityClassesConfig;
	}

	
}