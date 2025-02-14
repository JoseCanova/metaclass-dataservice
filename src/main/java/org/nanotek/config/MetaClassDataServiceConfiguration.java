package org.nanotek.config;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManagerFactory;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration
public class MetaClassDataServiceConfiguration {

	public MetaClassDataServiceConfiguration() {
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
	
	void metaClass(InjectionClassLoader injectionClassLoader) {
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
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Bean(value="myPersistenceManager")
	@Qualifier(value="myPersistenceManager")
	public MergingPersistenceUnitManager myPersistenceManager(@Autowired DataSource dataSource,
			@Autowired InjectionClassLoader injectionClassLoader) {
		metaClass(injectionClassLoader);
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
			@Autowired InjectionClassLoader classLoader) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceUnitManager(myPersistenceManager);
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setBeanClassLoader(classLoader);
		return factory;
	}
	
	@Bean("transactionManager")
	@Qualifier(value="transactionManager")
	public PlatformTransactionManager defaultTransactionManager(
			@Autowired	@Qualifier("entityManagerFactory") EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}

}
