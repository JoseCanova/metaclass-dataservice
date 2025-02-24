package org.nanotek.test.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.instancio.Instancio;
import org.nanotek.Base;
import org.nanotek.config.RepositoryClassesMap;
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	private static final Logger logger = LoggerFactory.getLogger(TestJpaRepositoryBean.class);

	private ApplicationContext applicationContext;
	
	
	public TestMetaClassDataServiceApplication2() {
	}
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) SpringApplication.run(TestMetaClassDataServiceApplication2.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		ClassLoader customClassLoader = applicationContext.getBean(InjectionClassLoader.class);
//		context.setClassLoader(customClassLoader);
        
        TestMetaClassDataServiceApplication2 bean = applicationContext.getBean(TestMetaClassDataServiceApplication2.class);
        bean.runApplicationContext((AnnotationConfigApplicationContext) applicationContext, customClassLoader);
	}
	
	
	public void runApplicationContext (AnnotationConfigApplicationContext context , ClassLoader customClassLoader ) {
        
		AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setClassLoader(customClassLoader);
        childContext.setParent(context);
        childContext.register(MetaClassCustomBean.class);
        childContext.refresh();
        
        AnnotationConfigApplicationContext childContext3 = new AnnotationConfigApplicationContext();
        childContext3.setClassLoader(customClassLoader);
        childContext3.setParent(childContext);
        childContext3.register(MetaClassJpaDataServiceConfiguration.class);
        childContext3.refresh();
        
        AnnotationConfigApplicationContext childContext2 = new AnnotationConfigApplicationContext();
        childContext2.setClassLoader(customClassLoader);
        childContext2.setParent(childContext3);
        childContext2.register(CustomJpaRepositoryConfig.class);
        childContext2.refresh();
        childContext2.getBean("integerValue");
        
        defaultListableBeanFactory = childContext2.getBean(DefaultListableBeanFactory.class);
        run(childContext2);
	}

	
	public void run(ApplicationContext context) {
		RepositoryClassesMap repositoryClassesMap = context.getBean(RepositoryClassesMap.class);
		
		repositoryClassesMap
		.forEach((n,y) -> {
			try {
				Class<?> beanClass = y;
				Class<Base<?>> entityClass = (Class<Base<?>>) Class.forName("org.nanotek.data."+n, true , context.getClassLoader());
//				TransactionInterceptor interceptor = applicationContext.getBean(TransactionInterceptor.class);
//				interceptor.setTransactionManager(transactionManager);
				//				defaultListableBeanFactory.createBean(beanClass, 0, false);
				JpaRepository<Base<?>,?> obj = (JpaRepository<Base<?>, ?>) defaultListableBeanFactory.getBean(beanClass);
				assertNotNull(obj);
//				obj.findAll();
				someAnnotatedTransactionalServiceMethod(obj , entityClass);
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
	public Object someAnnotatedTransactionalServiceMethod(JpaRepository<Base<?>,?> obj , Class<Base<?>> entityClass) {
				Object instance = Instancio.create(entityClass);
					obj.saveAndFlush(entityClass.cast(instance));
//					obj.deleteAll();
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
