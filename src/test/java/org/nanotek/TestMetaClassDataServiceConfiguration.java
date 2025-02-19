package org.nanotek;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.nanotek.config.MyLocalContainerEntityManagerFactoryBean;
import org.nanotek.config.MyMergingPersistenceUnitManager;
import org.nanotek.config.PersistenceUnityClassesMap;
import org.nanotek.config.RepositoryClassesConfig;
import org.nanotek.config.SpringHibernateJpaPersistenceProvider;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.spi.PersistenceProvider;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(
		basePackages = 
	{"org.nanotek.data.entity.mb.buddy.repositories"}
		, transactionManagerRef = "transactionManager")
public class TestMetaClassDataServiceConfiguration {

	public TestMetaClassDataServiceConfiguration() {
	}
	
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
	
	@Bean
	@Primary
	RepositoryClassesConfig repositoryClassesConfig() {
		return new RepositoryClassesConfig();
	}
	
	
	void metaClass(InjectionClassLoader injectionClassLoader,
			PersistenceUnityClassesMap persistenceUnitClassesMap) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(getClass().getResourceAsStream("/metaclass.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			Class.forName(loaded.getTypeName(), false, injectionClassLoader);
			persistenceUnitClassesMap.put(loaded.getTypeName(), loaded);
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void metaClassNumeric(InjectionClassLoader injectionClassLoader,
			PersistenceUnityClassesMap persistenceUnitClassesMap) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(getClass().getResourceAsStream("/metaclass_numeric.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			Class.forName(loaded.getTypeName(), false, injectionClassLoader);
			persistenceUnitClassesMap.put(loaded.getTypeName(), loaded);
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void metaClassNumericHundred(InjectionClassLoader injectionClassLoader,
			PersistenceUnityClassesMap persistenceUnitClassesMap) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(getClass().getResourceAsStream("/metaclass_hundred_numeric.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			Class.forName(loaded.getTypeName(), false, injectionClassLoader);
			persistenceUnitClassesMap.put(loaded.getTypeName(), loaded);
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void metaClassDate(InjectionClassLoader injectionClassLoader,
			PersistenceUnityClassesMap persistenceUnitClassesMap) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(getClass().getResourceAsStream("/meta_class_date.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			Class.forName(loaded.getTypeName(), false, injectionClassLoader);
			persistenceUnitClassesMap.put(loaded.getTypeName(), loaded);
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Bean(value="myPersistenceManager")
	@Qualifier(value="myPersistenceManager")
	public MergingPersistenceUnitManager myPersistenceManager(@Autowired DataSource dataSource,
			@Autowired InjectionClassLoader injectionClassLoader,
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap) {
		metaClass(injectionClassLoader,persistenceUnitClassesMap);
		metaClassNumeric(injectionClassLoader,persistenceUnitClassesMap);
		metaClassDate(injectionClassLoader,persistenceUnitClassesMap);
		metaClassNumericHundred(injectionClassLoader,persistenceUnitClassesMap);
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
