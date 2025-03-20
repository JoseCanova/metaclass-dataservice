package org.nanotek;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.FileSystem;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nanotek.config.MetaClassRegistry;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.config.RepositoryClassBuilder;
import org.nanotek.config.RepositoryPair;
import org.nanotek.config.spring.CustomJpaRepositoryConfig;
import org.nanotek.config.spring.MetaClassCustomBean;
import org.nanotek.config.spring.MetaClassJpaDataServiceConfiguration;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

@SpringBootApplication(proxyBeanMethods = false)
public class MetaClassRestClientTest {


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
	
	public MetaClassRestClientTest() {
	}
	
	
	public static void main(String[] args) throws Exception {
		
		fileSystem = Jimfs.newFileSystem(Configuration.unix());
    	byteArrayClassLoader = new MetaClassVFSURLClassLoader (MetaClassRestClientTest.class.getClassLoader() , false ,fileSystem);
    	metaClassRegistry = new  MetaClassRegistry<>();
    	List<RdbmsMetaClass> resultList = getMetaClasses();
    	resultList.forEach(r ->{
    		Class<?> theClass = metaClass(r, byteArrayClassLoader);
    		prepareRepositoryClass(theClass, byteArrayClassLoader);
    	});
    	AnnotationConfigServletWebServerApplicationContext context  = 
        		(AnnotationConfigServletWebServerApplicationContext) 
        		new SpringApplication(new PathMatchingResourcePatternResolver(MetaClassRestClientTest.byteArrayClassLoader),
        				MetaClassRestClientTest.class,MetaClassCustomBean.class , MetaClassJpaDataServiceConfiguration.class , CustomJpaRepositoryConfig.class).run(args);
	}


	@SuppressWarnings("rawtypes")
	private static List<RdbmsMetaClass> getMetaClasses() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		URI serverUri = new URI("http://localhost:8086/meta-class");
		serverUri.toURL();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List> response = restTemplate.getForEntity(serverUri, List.class);
		assertNotNull(response.getBody());
		List<?>	responseBody = response.getBody();
		List<RdbmsMetaClass> resultList = responseBody
			.stream()
			.map(e ->{
				assertNotNull(e);
				return mapper.convertValue(e, RdbmsMetaClass.class);
			}).collect(Collectors.toList());
		assertNotNull(resultList);		
		return resultList;
	}
	
	
	public static Class<?> metaClass(RdbmsMetaClass theClass , MetaClassVFSURLClassLoader classLoader) {
    	try {
    		RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
    		Unloaded<?> loaded = eb.getBytesForClassLoader();
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

}
