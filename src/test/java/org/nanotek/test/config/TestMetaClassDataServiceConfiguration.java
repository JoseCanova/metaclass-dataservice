package org.nanotek.test.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.nanotek.repository.data.MetaClassJpaTransactionManager;
import org.nanotek.repository.data.SimpleObjectProvider;
import org.nanotek.test.entity.repositories.SimpleTableEntityRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
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
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration
//@EnableTransactionManagement
@EnableAutoConfiguration(exclude= {JpaRepositoriesAutoConfiguration.class,TransactionAutoConfiguration.class})
//@EnableJpaRepositories(
//		basePackages = 
//	{"org.nanotek.data.entity.mb.buddy.repositories"}
//		, transactionManagerRef = "transactionManager")
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
		PersistenceUnityClassesMap persistenceUnitClassesMap = new PersistenceUnityClassesMap();
		InjectionClassLoader injectionClassLoader = injectionClassLoader();
		Class<?> clazz = metaClass(injectionClassLoader);
		persistenceUnitClassesMap.put(clazz.getTypeName(),clazz);
		Class<?> clazz1 = metaClassNumeric(injectionClassLoader);
		persistenceUnitClassesMap.put(clazz1.getTypeName(),clazz1);
		Class<?> clazz2 = metaClassDate(injectionClassLoader);
		persistenceUnitClassesMap.put(clazz2.getTypeName(),clazz2);
		return persistenceUnitClassesMap;
	}
	
	Class<?> metaClass(InjectionClassLoader injectionClassLoader) {
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
			
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);	
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void saveClazz(Class<?> clazz , ClassLoader classLoader, byte[] bs) throws IOException {
		String fileLocation = "/home/jose/git/metaclass-dataservice/target/classes/org/nanotek/data/";
		String fileName = fileLocation.concat(clazz.getSimpleName().concat(".class"));
		String classPath = clazz.getTypeName().replace('.', '/');// + ".class";
        InputStream classStream = classLoader.getResourceAsStream(classPath);
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(bs);
        }
	}

	Class<?> metaClassNumeric(InjectionClassLoader injectionClassLoader) {
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
		
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);	
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	void metaClassNumericHundred(InjectionClassLoader injectionClassLoader,
//			PersistenceUnityClassesMap persistenceUnitClassesMap) {
//		ObjectMapper objectMapper = new ObjectMapper();
//    	List<JsonNode> list;
//		try {
//			list = objectMapper.readValue
//						(getClass().getResourceAsStream("/metaclass_hundred_numeric.json")
//								, List.class);
//			Object theNode = list.get(0);
//			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
//			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
//			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
//			Class.forName(loaded.getTypeName(), false, injectionClassLoader);
//			persistenceUnitClassesMap.put(loaded.getTypeName(), loaded);
//			} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	Class<?> metaClassDate(InjectionClassLoader injectionClassLoader) {
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
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Bean
	@Primary
	@DependsOn("entityManagerFactory")
	@Qualifier(value="repositoryClassesMap")
	RepositoryClassesMap repositoryClassesMap(
			@Autowired InjectionClassLoader classLoader , 
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap,
			@Autowired @Qualifier("myBf") DefaultListableBeanFactory defaultListableBeanFactory,
			@Autowired EntityManagerFactory entityManagerFactory) {
		var repositoryClassesMap = new RepositoryClassesMap();
		persistenceUnitClassesMap.forEach((x,y)->{
			Class<?> idClass = getIdClass(y);
			Class <?> repClass = repositoryClassesMap.prepareReppositoryForClass(y, idClass, classLoader);
			System.err.println(y.getSimpleName());
			configureRepositoryBean(defaultListableBeanFactory , y , y.getSimpleName().replace("Repository", "") , repClass , classLoader,entityManagerFactory);
		});
		configureEntityRepositoryBean(defaultListableBeanFactory ,classLoader,entityManagerFactory);
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
			String sNmae2, Class<?> repClass, InjectionClassLoader classLoader, EntityManagerFactory entityManagerFactory) {
		SimpleEntityPathResolver pr = new SimpleEntityPathResolver(sNmae2);
		//TODO: verify the need to replace for RootBeanDefinition
		 GenericBeanDefinition  bd = new  GenericBeanDefinition ();
		bd.setBeanClass(MetaClassJpaRepositoryComponentBean.class);
		bd.setLazyInit(false);
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(new ValueHolder(repClass));
		bd.setConstructorArgumentValues(cav);

		bd.setPropertyValues(new MutablePropertyValues().add("entityPathResolver", 
				new SimpleObjectProvider<>(pr))
				.add("beanClassLoader", classLoader)
				.add("classLoader", classLoader)
				.add("beanFactory", defaultListableBeanFactory)
				.add("repositoryBaseClass", EntityBaseRepositoryImpl.class)
				.add("entityClass", c)
				.add("entityManagerFactory", entityManagerFactory));

		bd.addQualifier(new AutowireCandidateQualifier(repClass.getSimpleName()));
		bd.setAutowireCandidate(true);
		bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
		System.err.println(repClass.getSimpleName());
		defaultListableBeanFactory.registerBeanDefinition(repClass.getSimpleName(), bd);
		System.err.println(defaultListableBeanFactory.hashCode());
	}
	
	private void configureEntityRepositoryBean(DefaultListableBeanFactory defaultListableBeanFactory, 
			InjectionClassLoader classLoader, EntityManagerFactory entityManagerFactory) {
		SimpleEntityPathResolver pr = new SimpleEntityPathResolver(SimpleTableEntityRepository.class.getSimpleName());
		//TODO: verify the need to replace for RootBeanDefinition
		 GenericBeanDefinition  bd = new  GenericBeanDefinition ();
		bd.setBeanClass(JpaRepositoryFactoryBean.class);
		bd.setLazyInit(false);
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(new ValueHolder(SimpleTableEntityRepository.class));
		bd.setConstructorArgumentValues(cav);

		bd.setPropertyValues(new MutablePropertyValues().add("entityPathResolver", 
				new SimpleObjectProvider<>(pr))
				.add("beanClassLoader", classLoader)
				.add("beanFactory", defaultListableBeanFactory)
				.add("repositoryBaseClass", SimpleJpaRepository.class)
				.add("entityManager", entityManagerFactory.createEntityManager()));
//				.add("entityClass", SimpleTableEntity.class)
//				.add("entityManagerFactory", entityManagerFactory));

		bd.addQualifier(new AutowireCandidateQualifier(SimpleTableEntityRepository.class));
		bd.setAutowireCandidate(true);
		bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
		System.err.println(SimpleTableEntityRepository.class.getSimpleName());
		defaultListableBeanFactory.registerBeanDefinition(SimpleTableEntityRepository.class.getName(), bd);
		System.err.println(defaultListableBeanFactory.hashCode());
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
	public MetaClassMergingPersistenceUnitManager myPersistenceManager(@Autowired DataSource dataSource,
			@Autowired InjectionClassLoader injectionClassLoader,
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap) {
//		metaClassNumericHundred(injectionClassLoader,persistenceUnitClassesMap);
		MetaClassMergingPersistenceUnitManager pum = new  MetaClassMergingPersistenceUnitManager(persistenceUnitClassesMap);
//		pum.setValidationMode(ValidationMode.NONE);
		pum.setDefaultPersistenceUnitName("buddyPU");
		pum.setPackagesToScan("org.nanotek.data","org.nanotek.test.entity.data");
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
			@Autowired InjectionClassLoader classLoader , 
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

		@Autowired
		PersistenceUnityClassesMap config;

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
//	      SessionFactoryImpl sessionFactory = factory.getObject().unwrap(SessionFactoryImpl.class);
//
//	      EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
//	      registry.getEventListenerGroup(EventType.FLUSH).appendListener(new DefaultFlushEventListener());

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
