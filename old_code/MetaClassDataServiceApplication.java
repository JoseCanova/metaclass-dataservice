package org.nanotek.config.spring;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Arrays;
import java.util.List;

import org.instancio.Instancio;
import org.nanotek.Base;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
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
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

@SpringBootConfiguration(proxyBeanMethods = false)
//@Import({ CustomBeanFactoryPostProcessor.class,  TestMetaClassDataServiceConfiguration.class})
public class MetaClassDataServiceApplication 
implements SpringApplicationRunListener , 
ApplicationRunner, 
ApplicationContextAware{

	@Autowired
	@Qualifier("myBf")
	public DefaultListableBeanFactory defaultListableBeanFactory;
	
	@Bean
	@Qualifier(value="injectionClassLoader")
	InjectionClassLoader injectionClassLoader() {
		InjectionClassLoader ic = new  MultipleParentClassLoader(Thread.currentThread().getContextClassLoader() 
				, Arrays.asList(getClass().getClassLoader())  , 
				false);
		return ic;
	}
	
	@Bean
	@Qualifier (value="jimfs")
	FileSystem jimsFileSystem () {
		return Jimfs.newFileSystem(Configuration.unix());
	}
	
	@Bean
	@Primary
	MetaClassVFSURLClassLoader vfsClassLoader(@Autowired @Qualifier("injectionClassLoader") InjectionClassLoader injectionClassLoader
			,@Autowired @Qualifier("jimfs") FileSystem fileSystem) throws Exception{
		MetaClassVFSURLClassLoader vfsClassLoader = new MetaClassVFSURLClassLoader(Thread.currentThread().getContextClassLoader() 
				,  false , fileSystem);
		return vfsClassLoader;
	}
	

	private ApplicationContext applicationContext;
	
	
	public MetaClassDataServiceApplication() {
	}
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) SpringApplication.run(MetaClassDataServiceApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		MetaClassVFSURLClassLoader injectionClassLoader = applicationContext.getBean(MetaClassVFSURLClassLoader.class);
//		context.setClassLoader(customClassLoader);

	        // Use the new layer
        MetaClassDataServiceApplication bean = applicationContext.getBean(MetaClassDataServiceApplication.class);
        bean.runApplicationContext((AnnotationConfigApplicationContext) applicationContext, injectionClassLoader);
	}
	
	
	public void runApplicationContext (AnnotationConfigApplicationContext context , 
			MetaClassVFSURLClassLoader injectionClassLoader  ) {

    	try {   
		AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setClassLoader(getClass().getClassLoader());
        childContext.setParent(context);
        childContext.register(MetaClassCustomBean.class);
        childContext.refresh();
       
		
		AnnotationConfigApplicationContext childContext3 = new AnnotationConfigApplicationContext();
        childContext3.setClassLoader(getClass().getClassLoader());
        childContext3.setParent(childContext);
        childContext3.register(MetaClassJpaDataServiceConfiguration.class);
        childContext3.refresh();
        
   
        AnnotationConfigApplicationContext childContext2 = new AnnotationConfigApplicationContext();
        childContext2.setClassLoader(getClass().getClassLoader());
        childContext2.setParent(childContext3);
        childContext2.register(CustomJpaRepositoryConfig.class);
        childContext2.refresh();
        childContext2.getBean("configureJpaRepository");
//        
        veriyLoadedClassesByResource(childContext2);
        defaultListableBeanFactory = childContext3.getBean(DefaultListableBeanFactory.class);
        //run(childContext3,defaultListableBeanFactory);
        
    	} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
	private void veriyLoadedClassesByResource(AnnotationConfigApplicationContext childContext3) {
		  try {
			    Resource[] urls = childContext3.getResources("org/nanotek/config/spring/repositories");
			    Resource[] resources = childContext3.getResources("org/nanotek/config/spring/**/*.class");
				Assert.isTrue(resources !=null &&  resources.length > 0, "resource is null");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@Transactional
	private void saveAndFlush(JpaRepository<Base<?>, ?> obj, Class<Base<?>> entityClass) {
		Base<?> instance = Instancio.create(entityClass);
		obj.saveAndFlush(instance);
	}



	private void simpleFlush(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		em.flush();
		et.commit();
	}

	@Transactional
	private void saveAndFlush(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		em.flush();
		et.commit();
	}


	@Transactional(readOnly=false)
	public Object someProgramaticTransactionalServiceMethod(EntityManagerFactory entityManagerFactory , Class<Base<?>> entityClass) {
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
