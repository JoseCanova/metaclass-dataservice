package org.nanotek.config.spring;

import java.util.function.Consumer;

import javax.sql.DataSource;

import org.nanotek.config.CustomHibernateJpaVendorAdapter;
import org.nanotek.config.MetaClassClassesStore;
import org.nanotek.config.MetaClassLocalContainerEntityManagerFactoryBean;
import org.nanotek.config.MetaClassMergingPersistenceUnitManager;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.config.SpringHibernateJpaPersistenceProvider;
import org.nanotek.repository.data.MetaClassJpaTransactionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.metamodel.Metamodel;

@SpringBootConfiguration
//@EnableTransactionManagement
@EnableAutoConfiguration(exclude= {TransactionAutoConfiguration.class})
@EnableJpaRepositories(
		basePackages = 
	{"org.nanotek.config.spring.repositories"}
		, transactionManagerRef = "transactionManager")
public class MetaClassJpaDataServiceConfiguration implements ApplicationContextAware{

	public MetaClassJpaDataServiceConfiguration() {
	}
	
	ApplicationContext context;
	
	
	@Autowired
	MetaClassClassesStore metaClassClassesStore;
	
	@Bean
	@Lazy(true)
	@Qualifier(value="repositoryClassesMap")
	public MetaClassClassesStore getMetaClassClassesStore() {
		return metaClassClassesStore;
	}
	
	@Bean 
	@Qualifier(value="myBf")
	public DefaultListableBeanFactory defaultListableBeanFactory(@Autowired MetaClassVFSURLClassLoader classLoader )
	{
		DefaultListableBeanFactory v = new DefaultListableBeanFactory();
		v.setParentBeanFactory(context);
		v.setBeanClassLoader(classLoader);
		return v;
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}

//	@Bean
//	@Primary
//	@DependsOn("hikariConfig")
//	public DataSource dataSource() {
//		return new HikariDataSource(hikariConfig());
//	}
//	
//	
	@Bean(value="myPersistenceManager")
	@Qualifier(value="myPersistenceManager")
	@DependsOn("dataSource")
	public MergingPersistenceUnitManager myPersistenceManager(@Autowired DataSource dataSource
						,@Autowired MetaClassClassesStore persistenceUnitClassesMap) {
//		metaClassNumericHundred(injectionClassLoader,persistenceUnitClassesMap);
		MetaClassMergingPersistenceUnitManager pum = new  MetaClassMergingPersistenceUnitManager(persistenceUnitClassesMap);
//		pum.setValidationMode(ValidationMode.NONE);
		pum.setDefaultPersistenceUnitName("buddyPU");
		pum.setPackagesToScan("org.nanotek.config.spring.data");
		pum.setDefaultDataSource(dataSource);
		pum.setPersistenceUnitPostProcessors(myProcessor());
		pum.preparePersistenceUnitInfos();
		return pum;
	}
	
	@Bean(name = "entityManagerFactory")
	@Qualifier(value="entityManagerFactory")
	@DependsOn("myPersistenceManager")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Autowired DataSource dataSource ,
			@Autowired MetaClassVFSURLClassLoader classLoader , 
			@Autowired Initializer initializer, 
			@Autowired MetaClassMergingPersistenceUnitManager myPersistenceManager) {
		
//		MergingPersistenceUnitManager myPersistenceManager = myPersistenceManager( dataSource,
//				classLoader,
//				 persistenceUnitClassesMap);
		
		MetaClassLocalContainerEntityManagerFactoryBean factory = new MetaClassLocalContainerEntityManagerFactoryBean(classLoader);
//		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceUnitManager(myPersistenceManager);
		factory.setPersistenceProviderClass(SpringHibernateJpaPersistenceProvider.class);
		factory.setJpaDialect(new HibernateJpaDialect());
		HibernateJpaVendorAdapter vendorAdapter = new CustomHibernateJpaVendorAdapter(classLoader,myPersistenceManager.getPersistenceUnitClassesMap());
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setEntityManagerInitializer(initializer);
		factory.setConfig(myPersistenceManager.getPersistenceUnitClassesMap());
		factory.setPersistenceUnitName("buddyPU");
		factory.afterPropertiesSet2();
//		factory.afterPropertiesSet();
		return factory;
	}
	
	@Bean
	Initializer initializer(){
		return new Initializer();
	}

	class Initializer implements Consumer<EntityManager>{

		@SuppressWarnings("unused")
		@Override
		public void accept(EntityManager em) {
			Metamodel model = em.getMetamodel();
		}

	}

	@Primary
	@Bean("transactionManager")
	@Qualifier(value="transactionManager")
	@DependsOn("entityManagerFactory")
	public PlatformTransactionManager transactionManager(
			@Autowired DataSource dataSource ,
			@Autowired	@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
			MetaClassJpaTransactionManager transactionManager = new MetaClassJpaTransactionManager();
	      transactionManager.setEntityManagerFactory(factory.getObject());
	      transactionManager.setJpaDialect(new HibernateJpaDialect());
	      transactionManager.setJpaPropertyMap(factory.getObject().getProperties());
	      transactionManager.setDataSource(dataSource);
	      transactionManager.setNestedTransactionAllowed(true);
//		return new HibernateTransactionManager(factory.unwrap(SessionFactory.class));
		return transactionManager;//new DataSourceTransactionManager(dataSource);
	}

//	@Bean 
//	@DependsOn("transactionManager")
//	public TransactionProxyFactoryBean transactionProxyFactoryBean(@Autowired PlatformTransactionManager transactionManager) {
//		TransactionProxyFactoryBean transactionProxyFactoryBean = new TransactionProxyFactoryBean();
//		transactionProxyFactoryBean.setTransactionManager(transactionManager);
//		return transactionProxyFactoryBean;
//	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
	
	@Bean
	public PersistenceUnitPostProcessor myProcessor () {
		return new MyPersistenceUnitPostProcessor();
	}

	class MyPersistenceUnitPostProcessor  implements PersistenceUnitPostProcessor{

		@Autowired
		MetaClassClassesStore repositoryClassesMap;

		@Autowired
		@Qualifier("myBf")
		DefaultListableBeanFactory defaultListableBeanFactory;

		@Autowired
		MetaClassVFSURLClassLoader classLoader;

		@Override
		public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
			defaultListableBeanFactory.setBeanClassLoader(classLoader);
			
			try {
				Class<?> clazz = Class.forName("org.nanotek.config.spring.data.SimpleTable",true , classLoader);
				pui.addManagedClassName(clazz.getName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
			repositoryClassesMap
			.forEach((x,y)->{
				pui.addManagedClassName(y.getName());
			});
			pui.addManagedPackage("org.nanotek.config.spring.data");
			pui.setValidationMode(ValidationMode.NONE);
			//			pui.setExcludeUnlistedClasses(false);
//			Properties p = new Properties(); 
//			pui.setProperties(p);
			pui.setPersistenceUnitName("buddyPU");
		}
	}
}
