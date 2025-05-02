package org.nanotek.test.entity;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.nanotek.repository.data.MetaClassJpaTransactionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;

import jakarta.persistence.ValidationMode;

@SpringBootConfiguration
@EnableJpaRepositories(basePackages = "org.nanotek.metaclass.repository")
@EnableAutoConfiguration(exclude= {TransactionAutoConfiguration.class})
public class JpaDataServiceConfiguration implements ApplicationContextAware{

	public JpaDataServiceConfiguration() {
	}
	
	ApplicationContext context;
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	@Bean(value="myPersistenceManager")
	@Qualifier(value="myPersistenceManager")
	@DependsOn("dataSource")
	public MergingPersistenceUnitManager myPersistenceManager(@Autowired DataSource dataSource) {
		MergingPersistenceUnitManager pum = new  MergingPersistenceUnitManager();
		pum.setValidationMode(ValidationMode.NONE);
		pum.setDefaultPersistenceUnitName("buddyPU");
		pum.setPackagesToScan("org.nanotek.metaclass.entity");
		pum.setDefaultDataSource(dataSource);
		return pum;
	}
	
	@Bean(name = "entityManagerFactory")
	@Qualifier(value="entityManagerFactory")
	@DependsOn("myPersistenceManager")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Autowired DataSource dataSource ,
			@Autowired @Qualifier("myPersistenceManager") MergingPersistenceUnitManager myPersistenceManager) {
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceUnitManager(myPersistenceManager);
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		factory.setJpaDialect(new HibernateJpaDialect());
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		factory.setJpaVendorAdapter(vendorAdapter);
		return factory;
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
	

}
