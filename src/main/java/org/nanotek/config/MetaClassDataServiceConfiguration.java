package org.nanotek.config;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.spi.PersistenceProvider;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration
public class MetaClassDataServiceConfiguration {

	public MetaClassDataServiceConfiguration() {
	}
	
	//TODO: Builder a Stream of classloader to avoid duplicity classloader on MultipleParentClassLoader
	@Bean
	@Primary
	InjectionClassLoader injectionClassLoader() {
		InjectionClassLoader ic = new  MultipleParentClassLoader(Thread.currentThread().getContextClassLoader() 
				, Arrays.asList(getClass().getClassLoader() , 
						CrudMethodMetadata.class.getClassLoader() , 
						AbstractEntityManagerFactoryBean.class.getClassLoader())  , 
				false);
		return ic;
	}
	
	@Bean
	@Primary
	PersistenceUnityClassesMap persistenceUnitClassesMap() {
		return new PersistenceUnityClassesMap();
	}
	
	
	@Bean(value="myPersistenceManager")
	@Qualifier(value="myPersistenceManager")
	public MergingPersistenceUnitManager myPersistenceManager(@Autowired DataSource dataSource,
			@Autowired InjectionClassLoader injectionClassLoader,
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap) {
		MergingPersistenceUnitManager pum = new  MyMergingPersistenceUnitManager();
//		pum.setValidationMode(ValidationMode.NONE);
		pum.setDefaultPersistenceUnitName("buddyPU");
		pum.setPackagesToScan("org.nanotek.data");
		pum.setDefaultDataSource(dataSource);
//		pum.setPersistenceUnitPostProcessors(myProcessor());
		pum.preparePersistenceUnitInfos();
		return pum;
	}
	
	@Bean(name = "entityManagerFactory")
	@Qualifier(value="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Autowired DataSource dataSource ,
			@Autowired  @Qualifier("myPersistenceManager") 
			MyMergingPersistenceUnitManager myPersistenceManager,
			@Autowired InjectionClassLoader classLoader , 
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap,
			@Autowired Initializer initializer) {
		MyLocalContainerEntityManagerFactoryBean factory = new MyLocalContainerEntityManagerFactoryBean(classLoader);
		factory.setDataSource(dataSource);
		factory.setPersistenceUnitManager(myPersistenceManager);
		factory.setPersistenceProviderClass(SpringHibernateJpaPersistenceProvider.class);
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setJpaVendorAdapter(new JpaVendorAdapter() {
			@Override
			public PersistenceProvider getPersistenceProvider() {
				return new SpringHibernateJpaPersistenceProvider(classLoader, persistenceUnitClassesMap);
			}});
		factory.setEntityManagerInitializer(initializer);
		factory.setConfig(persistenceUnitClassesMap);
//		factory.setJpaPropertyMap(buddyJpaPropertie());
		factory.setPersistenceUnitName("buddyPU");
		factory.afterPropertiesSet2();
		return factory;
	}
	
	@Bean
	Initializer initializer(){
		return new Initializer();
	}

	class Initializer implements Consumer<EntityManager>{

		@Autowired
		PersistenceUnityClassesMap config;

		@SuppressWarnings("unused")
		@Override
		public void accept(EntityManager em) {
			Metamodel model = em.getMetamodel();
		}

	}
	@Bean("transactionManager")
	@Qualifier(value="transactionManager")
	public PlatformTransactionManager defaultTransactionManager(
			@Autowired	@Qualifier("entityManagerFactory") EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}

}
