package org.nanotek.config.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.util.List;
import java.util.stream.Stream;

import org.instancio.Instancio;
import org.nanotek.Base;
import org.nanotek.config.MetaClassRegistry;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.config.RepositoryClassBuilder;
import org.nanotek.config.RepositoryPair;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.nanotek.repository.data.EntityBaseRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import jakarta.persistence.EntityManagerFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;


//WebMvcAutoConfiguration.class, 
@SpringBootApplication(proxyBeanMethods = false)
//@SpringBootConfiguration(proxyBeanMethods = false)
@ComponentScan(useDefaultFilters=false)
//@EnableAutoConfiguration(exclude = {WebServicesAutoConfiguration.class,  JpaRepositoriesAutoConfiguration.class ,  HibernateJpaAutoConfiguration.class , RepositoryRestMvcAutoConfiguration.class})
public class ApplicationAgent 
implements SpringApplicationRunListener , 
ApplicationContextAware{
	
	public static FileSystem fileSystem;
	
	public static MetaClassVFSURLClassLoader byteArrayClassLoader;

	public static MetaClassRegistry<?> metaClassRegistry;
	
	
	@Bean
	@Qualifier(value="injectionClassLoader")
	InjectionClassLoader injectionClassLoader() {
		return byteArrayClassLoader;
	}
	
	@Bean
	@Primary
	MetaClassVFSURLClassLoader vfsClassLoader() {
		return byteArrayClassLoader;
	}
	
//	
//	@Bean 
//	ServletWebServerFactory servletWebServerFactory() {
//		return new TomcatServletWebServerFactory();
//	}
	
	private ApplicationContext applicationContext;
	
    public static void main(String[] args) throws Exception {
    	Instrumentation instrumentation = ByteBuddyAgent.install();
        premain(null, instrumentation);
        var urlConnection = new Clazze();
        urlConnection.printValue("hello");
        Class<?> clazz = Class.forName("org.nanotek.config.spring.data.SimpleTable", true , byteArrayClassLoader);
        System.out.println(clazz.getName());
        clazz = Class.forName("jimfs.org.nanotek.config.spring.repositories.SimpleTableRepository" , true , byteArrayClassLoader);
        System.out.println(clazz.getName());
        AnnotationConfigServletWebServerApplicationContext context  = 
        		(AnnotationConfigServletWebServerApplicationContext) 
        		new SpringApplication(new PathMatchingResourcePatternResolver(ApplicationAgent.byteArrayClassLoader),
        				ApplicationAgent.class,MetaClassCustomBean.class , MetaClassJpaDataServiceConfiguration.class , CustomJpaRepositoryConfig.class).run(args);
    }

    public static void premain(String arg, Instrumentation inst) throws Exception {
    	fileSystem = Jimfs.newFileSystem(Configuration.unix());
    	byteArrayClassLoader = new MetaClassVFSURLClassLoader (ApplicationAgent.class.getClassLoader() , false ,fileSystem);
    	metaClassRegistry = new  MetaClassRegistry<>();
    	Class<?> entityClass = metaClass(byteArrayClassLoader);
    	Class<?> entityNumericClass = metaClassNumeric(byteArrayClassLoader);
    	Class<?> repClass4 = prepareRepositoryClass(entityClass,byteArrayClassLoader);
    	Class<?> repClassN = prepareRepositoryClass(entityNumericClass,byteArrayClassLoader);
    	Class<?> clazzR = prepareRepositoryConfigClass(repClass4,repClassN,byteArrayClassLoader);
    
    }
    
    public static Class<?> metaClass(MetaClassVFSURLClassLoader classLoader) {
    	ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
    	try {
    		FileInputStream stream = new FileInputStream(new File("/home/jose/git/metaclass-dataservice/src/main/resources/metaclass.json"));
    		list = objectMapper.readValue
    					(stream
    							, List.class);
    		Object theNode = list.get(0);
    		RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
    		RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
    		Unloaded<?> loaded = eb.getBytesForClassLoader();
    		//injectionClassLoader.saveClassFile(loaded.getTypeName(), eb.getBytes());
    		String className = loaded.getTypeDescription().getActualName();
    		Class<?> clazz = classLoader.defineClass(className, loaded.getBytes());
    		metaClassRegistry.registryEntityClass(Class.class.<Class<Base<?>>>cast(clazz));
    		return  clazz;	
    	
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    public static Class<?> metaClassNumeric(MetaClassVFSURLClassLoader classLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			InputStream is = classLoader.getResourceAsStream("metaclass_numeric.json");
    		FileInputStream stream = new FileInputStream(new File("/home/jose/git/metaclass-dataservice/src/main/resources/metaclass_numeric.json"));
    		list = objectMapper.readValue
					(is
							, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
    		RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Unloaded<?> loaded = eb.getBytesForClassLoader();
    		//injectionClassLoader.saveClassFile(loaded.getTypeName(), eb.getBytes());
    		String className = loaded.getTypeDescription().getActualName();
    		Class<?> clazz = classLoader.defineClass(className, loaded.getBytes());
    		metaClassRegistry.registryEntityClass(Class.class.<Class<Base<?>>>cast(clazz));
    		return  clazz;	
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Class<?> prepareRepositoryClass(Class<?> entityClass , MetaClassVFSURLClassLoader classLoader){
		Class<?> idClass = getIdClass(entityClass);
		RepositoryPair pair = RepositoryClassBuilder.prepareReppositoryForClass(entityClass, idClass);
		try {
			Class<?> repClass =  classLoader.defineClass(pair.repositoryName(), pair.unloaded().getBytes());
			Class<Base<?>> repBaseClass = Class.class.<Class<Base<?>>>cast(repClass);
			metaClassRegistry.registryRepositoryClass(Class.class.<Class<Base<?>>>cast(entityClass), repBaseClass);
			return repClass;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
    
    private static Class<?> getIdClass(Class<?> y) {
		return Stream.of(y.getDeclaredFields())
		.filter( f -> hasIdAnnotation(f))
		.map(f -> f.getType())
		.findFirst().orElseThrow();
	}
    private static Boolean hasIdAnnotation(Field f) {
		return Stream.of(f.getAnnotations()).filter(a ->a.annotationType().equals(jakarta.persistence.Id.class)).count()==1;
	}
    
    public static class Clazze  {
    	public void printValue(String value) {
    		System.err.println(value);
    	}
    }
    
	public void run(ApplicationArguments args) throws Exception {
		MetaClassVFSURLClassLoader injectionClassLoader = applicationContext.getBean(MetaClassVFSURLClassLoader.class);
//		context.setClassLoader(customClassLoader);

	        // Use the new layer
		ApplicationAgent bean = applicationContext.getBean(ApplicationAgent.class);
        bean.runApplicationContext((AnnotationConfigServletWebServerApplicationContext) applicationContext, injectionClassLoader);
	}
	
	
	public void runApplicationContext
	(AnnotationConfigServletWebServerApplicationContext context , 
			MetaClassVFSURLClassLoader injectionClassLoader  ) {
		
		
		try {   
		AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setClassLoader(byteArrayClassLoader);
        childContext.setParent(context);
        childContext.register(MetaClassCustomBean.class);
        childContext.refresh();
       
		
		AnnotationConfigApplicationContext childContext3 = new AnnotationConfigApplicationContext();
        childContext3.setClassLoader(byteArrayClassLoader);
        childContext3.setParent(childContext);
        childContext3.setResourceLoader(new PathMatchingResourcePatternResolver(byteArrayClassLoader));
        childContext3.register(MetaClassJpaDataServiceConfiguration.class);
        childContext3.refresh();
        
        Class<?> claz = Class.forName("org.nanotek.config.spring.RepoConfig",true , byteArrayClassLoader);
        AnnotationConfigApplicationContext childContext2 = new AnnotationConfigApplicationContext();
        childContext2.setClassLoader(byteArrayClassLoader);
        childContext2.setParent(childContext3);
        childContext2.register(CustomJpaRepositoryConfig.class);
        childContext2.refresh();
        veriyLoadedClassesByResource(childContext2);
        

        
        veriyLoadedClassesByResource(childContext2);
        
    	} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
	private void veriyLoadedClassesByResource(AnnotationConfigApplicationContext childContext3) {
		  try {
			    MetaPathMatchingResourcePatternResolver metResolver = new MetaPathMatchingResourcePatternResolver(childContext3);
			    Resource[] urls = childContext3.getResources("org/nanotek/config/spring/repositories");
			    Resource resource = childContext3.getResource("org/nanotek/config/spring/repositories/SimpleTableRepository.class");
			    Class<?> clazze = Class.forName("org.nanotek.config.spring.repositories.SimpleTableRepository",true,byteArrayClassLoader);
			    ClassPathResource cpr = new ClassPathResource("org/nanotek/config/spring/repositories/SimpleTableRepository.class",byteArrayClassLoader);
//				Assert.isTrue(resources !=null &&  resources.length > 0, "resource is null");
			    JpaRepository<?,?> theRepositoryBean = JpaRepository.class.cast( childContext3.getBean(clazze));
			    Resource[] resources1 = metResolver.getResources("classpath*:org/nanotek/config/spring/repositories/**/*.class");
			    CachingMetadataReaderFactory cfn = new CachingMetadataReaderFactory(byteArrayClassLoader);
			    MetadataReader mdr = cfn.getMetadataReader(cpr);
			    EntityManagerFactory entityManagerFactory = childContext3.getBean(EntityManagerFactory.class);
//				someProgramaticTransactionalServiceMethod(entityManagerFactory , entityClass);
//				simpleFlush(entityManagerFactory);
				
				metaClassRegistry
					.getEntityClasses()
					.stream()
					.map(c -> org.nanotek.Base.withUUID(c))
					.map(uuid ->  Pair.of(uuid, metaClassRegistry.getRepositoryClass(uuid)))
					.forEach(rc ->{
						 EntityBaseRepository<Base<?>,?> ebr= EntityBaseRepository
								 										.class.
								 										<EntityBaseRepository<Base<?>,?>>cast(childContext3.getBean(rc.getSecond()));
						 Assert.isTrue(ebr !=null, "Non existent repository in registry");
						 Class<Base<?>> ec = metaClassRegistry.getEntityClass(rc.getFirst());
						 saveAndFlush(ebr, ec);
					});
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	private void saveAndFlush(JpaRepository<Base<?>, ?> obj, Class<Base<?>> entityClass) {
		Base<?> instance = Instancio.create(entityClass);
		obj.saveAndFlush(instance);
	}

	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public static Class<?> prepareRepositoryConfigClass(Class<?> clazz , Class<?> clazzn , ClassLoader classLoader){
		var av = new Class[] {clazz,clazzn};
		DynamicType.Unloaded<?> unloaded =   new ByteBuddy(ClassFileVersion.JAVA_V22)
				.subclass(Object.class)
				.name("org.nanotek.config.spring.RepoConfig")
				.annotateType( AnnotationDescription.Builder.ofType(EnableJpaRepositories.class)
						.defineTypeArray("basePackageClasses",  av)
						.define("transactionManagerRef",  "transactionManager")
						.build())
				.make();
				//,ClassLoadingStrategy.Default.WRAPPER_PERSISTENT
		Class<?> cd =unloaded.load(classLoader,ClassLoadingStrategy.Default.INJECTION).getLoaded();
		try {
			Class<?> theclazz = Class.forName(cd.getTypeName(),true , classLoader);
			System.err.println(theclazz.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return cd;
	}
	
}