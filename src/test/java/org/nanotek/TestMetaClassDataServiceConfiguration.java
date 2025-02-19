package org.nanotek;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.nanotek.config.CustomHibernateJpaVendorAdapter;
import org.nanotek.config.MetaClassLocalContainerEntityManagerFactoryBean;
import org.nanotek.config.MetaClassMergingPersistenceUnitManager;
import org.nanotek.config.PersistenceUnityClassesMap;
import org.nanotek.config.RepositoryClassesMap;
import org.nanotek.config.SpringHibernateJpaPersistenceProvider;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.nanotek.repository.data.EntityBaseRepositoryImpl;
import org.nanotek.repository.data.MetaClassJpaRepositoryComponentBean;
import org.nanotek.repository.data.SimpleObjectProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(
		basePackages = 
	{"org.nanotek.data.entity.mb.buddy.repositories"}
		, transactionManagerRef = "transactionManager")
public class TestMetaClassDataServiceConfiguration implements ApplicationContextAware{

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
	RepositoryClassesMap repositoryClassesConfig() {
		return new RepositoryClassesMap();
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
	
	@Bean
	@Primary
	@DependsOn("entityManagerFactory")
	RepositoryClassesMap repositoryClassesMap(
			@Autowired InjectionClassLoader classLoader , 
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap,
			@Autowired @Qualifier("myBf") DefaultListableBeanFactory defaultListableBeanFactory) {
		var repositoryClassesMap = new RepositoryClassesMap();
		persistenceUnitClassesMap.forEach((x,y)->{
			Class<?> idClass = getIdClass(y);
			Class <?> repClass = repositoryClassesMap.prepareReppositoryForClass(y, idClass, classLoader);
			configureRepositoryBean(defaultListableBeanFactory , y , y.getSimpleName() , repClass , classLoader);
		});
		return repositoryClassesMap;
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
	
	private void configureRepositoryBean(DefaultListableBeanFactory defaultListableBeanFactory, Class<?> c,
			String sNmae2, Class<?> repClass, InjectionClassLoader classLoader) {
		SimpleEntityPathResolver pr = new SimpleEntityPathResolver(sNmae2);
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setBeanClass(MetaClassJpaRepositoryComponentBean.class);
		bd.setLazyInit(true);
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(new ValueHolder(repClass));
		bd.setConstructorArgumentValues(cav);

		bd.setPropertyValues(new MutablePropertyValues().add("entityPathResolver", 
				new SimpleObjectProvider<>(pr))
				.add("beanClassLoader", classLoader)
				.add("beanFactory", defaultListableBeanFactory)
				.add("repositoryBaseClass", EntityBaseRepositoryImpl.class)
				.add("entityClass", c));

		bd.addQualifier(new AutowireCandidateQualifier(repClass.getSimpleName()));
		defaultListableBeanFactory.registerBeanDefinition(repClass.getSimpleName(), bd);
		
	}
	
	private Class<?> getIdClass(Class<?> y) {
		return Stream.of(y.getDeclaredFields())
		.filter( f -> hasIdAnnotation(f))
		.map(f -> f.getType())
		.findFirst().orElseThrow();
	}

	private Boolean hasIdAnnotation(Field f) {
		return Stream.of(f.getAnnotations()).filter(a ->a.annotationType().equals(jakarta.persistence.Id.class)).count()==1;
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
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Autowired DataSource dataSource ,
			@Autowired  @Qualifier("myPersistenceManager") 
			MetaClassMergingPersistenceUnitManager myPersistenceManager,
			@Autowired InjectionClassLoader classLoader , 
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap,
			@Autowired Initializer initializer) {
		MetaClassLocalContainerEntityManagerFactoryBean factory = new MetaClassLocalContainerEntityManagerFactoryBean(classLoader);
		factory.setDataSource(dataSource);
		factory.setPersistenceUnitManager(myPersistenceManager);
		factory.setPersistenceProviderClass(SpringHibernateJpaPersistenceProvider.class);
		HibernateJpaVendorAdapter vendorAdapter = new CustomHibernateJpaVendorAdapter(classLoader,persistenceUnitClassesMap);
		factory.setJpaVendorAdapter(vendorAdapter);
//		factory.setJpaVendorAdapter(new JpaVendorAdapter() {
//			
//			@Override
//			public PersistenceProvider getPersistenceProvider() {
//				return new SpringHibernateJpaPersistenceProvider(classLoader, persistenceUnitClassesMap);
//			}});
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
