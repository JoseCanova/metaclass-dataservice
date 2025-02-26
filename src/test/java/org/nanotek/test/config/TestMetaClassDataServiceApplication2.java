package org.nanotek.test.config;

import java.util.Arrays;
import java.util.List;

import org.instancio.Instancio;
import org.nanotek.Base;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.config.RepositoryClassesBuilder;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.nanotek.test.jpa.repositories.TestJpaRepositoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration(proxyBeanMethods = false)
//@Import({ CustomBeanFactoryPostProcessor.class,  TestMetaClassDataServiceConfiguration.class})
public class TestMetaClassDataServiceApplication2 
implements SpringApplicationRunListener , 
ApplicationRunner, 
ApplicationContextAware{

	@Autowired
	@Qualifier("myBf")
	public DefaultListableBeanFactory defaultListableBeanFactory;
	
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
	MetaClassVFSURLClassLoader vfsClassLoader(@Autowired InjectionClassLoader injectionClassLoader) throws Exception{
		MetaClassVFSURLClassLoader vfsClassLoader = MetaClassVFSURLClassLoader.createVFSClassLoader("ram://", injectionClassLoader);
		return vfsClassLoader;
	}
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(TestJpaRepositoryBean.class);

	private ApplicationContext applicationContext;
	
	
	public TestMetaClassDataServiceApplication2() {
	}
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) SpringApplication.run(TestMetaClassDataServiceApplication2.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InjectionClassLoader injectionClassLoader = applicationContext.getBean(InjectionClassLoader.class);
		MetaClassVFSURLClassLoader vfsClassLoader = applicationContext.getBean(MetaClassVFSURLClassLoader.class);
//		context.setClassLoader(customClassLoader);
        
        TestMetaClassDataServiceApplication2 bean = applicationContext.getBean(TestMetaClassDataServiceApplication2.class);
        bean.runApplicationContext((AnnotationConfigApplicationContext) applicationContext, injectionClassLoader , vfsClassLoader);
	}
	
	
	public void runApplicationContext (AnnotationConfigApplicationContext context , 
			InjectionClassLoader injectionClassLoader , MetaClassVFSURLClassLoader vfsClassLoader ) {
        
		AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setClassLoader(vfsClassLoader);
        childContext.setParent(context);
        childContext.register(MetaClassCustomBean.class);
        childContext.refresh();
        
        AnnotationConfigApplicationContext childContext3 = new AnnotationConfigApplicationContext();
        childContext3.setClassLoader(vfsClassLoader);
        childContext3.setParent(childContext);
        childContext3.register(MetaClassJpaDataServiceConfiguration.class);
        childContext3.refresh();
        
//        AnnotationConfigApplicationContext childContext2 = new AnnotationConfigApplicationContext();
//        childContext2.setClassLoader(vfsClassLoader);
//        childContext2.setParent(childContext3);
//        childContext2.register(CustomJpaRepositoryConfig.class);
//        childContext2.refresh();
//        childContext2.getBean("integerValue");
        
        defaultListableBeanFactory = childContext3.getBean(DefaultListableBeanFactory.class);
        run(childContext3);
	}

	
	public void run(ApplicationContext context) {
		RepositoryClassesBuilder repositoryClassesMap = context.getBean(RepositoryClassesBuilder.class);
		EntityManagerFactory entityManagerFactory = context.getBean(EntityManagerFactory.class);
		repositoryClassesMap
		.forEach((n,y) -> {
			try {
				Class<?> beanClass = y;
				Class<Base<?>> entityClass = (Class<Base<?>>) Class.forName("org.nanotek.test.config.spring.data."+n, true , context.getClassLoader());
//				TransactionInterceptor interceptor = applicationContext.getBean(TransactionInterceptor.class);
//				interceptor.setTransactionManager(transactionManager);
				//				defaultListableBeanFactory.createBean(beanClass, 0, false);
//				JpaRepository<Base<?>,?> obj = (JpaRepository<Base<?>, ?>) defaultListableBeanFactory.getBean(beanClass);
//				assertNotNull(obj);
//				obj.findAll();
				someAnnotatedTransactionalServiceMethod(entityManagerFactory , entityClass);
//				obj.deleteAll();
//				Object instance = Instancio.create(entityClass);
//				obj.saveAndFlush(entityClass.cast(instance));
//				obj.deleteAll();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	@Transactional(readOnly=false)
	public Object someAnnotatedTransactionalServiceMethod(EntityManagerFactory entityManagerFactory , Class<Base<?>> entityClass) {
		EntityManager em = entityManagerFactory.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		Object instance = Instancio.create(entityClass);
				em.persist(instance);
//					obj.deleteAll();
		et.commit();
		return instance;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	static Class<?> metaClass(ClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(ObjectMapper.class.getResourceAsStream("/metaclass.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			
			return loaded;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static Class<?> metaClassNumeric(ClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(ObjectMapper.class.getResourceAsStream("/metaclass_numeric.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
		
			return loaded;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static Class<?> metaClassDate(ClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(ObjectMapper.class.getResourceAsStream("/meta_class_date.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			return loaded;		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
