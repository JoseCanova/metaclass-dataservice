package org.nanotek.test.config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.nanotek.config.MetaClassMergingPersistenceUnitManager;
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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Metamodel;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration
//@EnableTransactionManagement
@EnableAutoConfiguration(exclude= {JpaRepositoriesAutoConfiguration.class , TransactionAutoConfiguration.class})
public class MetaClassJpaDataServiceConfiguration implements ApplicationContextAware{

	public MetaClassJpaDataServiceConfiguration() {
	}
	
	ApplicationContext context;
	
	@Bean 
	@Qualifier(value="myBf")
	public DefaultListableBeanFactory defaultListableBeanFactory(@Autowired InjectionClassLoader classLoader )
	{
		DefaultListableBeanFactory v = new DefaultListableBeanFactory();
		v.setParentBeanFactory(context);
		v.setBeanClassLoader(classLoader);
		return v;
	}

	private Boolean hasIdAnnotation(Field f) {
		return Stream.of(f.getAnnotations()).filter(a ->a.annotationType().equals(jakarta.persistence.Id.class)).count()==1;
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
	public MergingPersistenceUnitManager myPersistenceManager(@Autowired DataSource dataSource) {
//		metaClassNumericHundred(injectionClassLoader,persistenceUnitClassesMap);
		MergingPersistenceUnitManager pum = new  MetaClassMergingPersistenceUnitManager();
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
	@DependsOn("myPersistenceManager")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Autowired DataSource dataSource ,
			@Autowired Initializer initializer, 
			@Autowired MergingPersistenceUnitManager myPersistenceManager) {
		
//		MergingPersistenceUnitManager myPersistenceManager = myPersistenceManager( dataSource,
//				classLoader,
//				 persistenceUnitClassesMap);
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceUnitManager(myPersistenceManager);
		factory.setPersistenceProviderClass(SpringHibernateJpaPersistenceProvider.class);
		factory.setJpaDialect(new HibernateJpaDialect());
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setEntityManagerInitializer(initializer);
//		factory.setJpaPropertyMap(buddyJpaPropertie());
		factory.setPersistenceUnitName("buddyPU");
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
}
