package org.nanotek;

import java.net.URI;
import java.nio.file.FileSystem;
import java.util.List;
import java.util.stream.Collectors;

import org.nanotek.config.spring.CustomJpaRepositoryConfig;
import org.nanotek.config.spring.MetaClassCustomBean;
import org.nanotek.config.spring.MetaClassJpaDataServiceConfiguration;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
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

import net.bytebuddy.dynamic.loading.InjectionClassLoader;


@SpringBootApplication(proxyBeanMethods = false)
public class MetaClassRestClientApplication{


	public static final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
	
	public static final MetaClassVFSURLClassLoader byteArrayClassLoader  = new MetaClassVFSURLClassLoader 
																			(MetaClassRestClientApplication.class.getClassLoader() , 
																					false ,fileSystem);

	public static final MetaClassRegistry<?> metaClassRegistry  = new  MetaClassRegistry<>();
	
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
	
	@Bean 
	@Primary
	 MetaClassRegistry<?> metaClassRegistry(){
		return metaClassRegistry;
	}
	
	public MetaClassRestClientApplication() {
	}
	
	
	public static void main(String[] args) throws Exception {
		ApplicationInitializer initializer = new ApplicationInitializer() {
			@Override
			public List<RdbmsMetaClass> getMetaClasses(String uriEndpont) {
				List<RdbmsMetaClass> resultList = null;
				try {
						ObjectMapper mapper = new ObjectMapper();
						URI serverUri = new URI(uriEndpont);
						RestTemplate restTemplate = new RestTemplate();
						ResponseEntity<List> response = restTemplate.getForEntity(serverUri, List.class);
						List<?>	responseBody = response.getBody();
						resultList = responseBody
							.stream()
							.map(e ->{
								return mapper.convertValue(e, RdbmsMetaClass.class);
							}).collect(Collectors.toList());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return resultList;
			}
		};
		List<Class<?>> theList =	initializer.configureMetaClasses("http://localhost:8086/meta-class",byteArrayClassLoader,metaClassRegistry);
		initializer.configureRepositoryClasses(theList, byteArrayClassLoader, metaClassRegistry); 
		
		//    	List<RdbmsMetaClass> resultList = getMetaClasses("http://localhost:8086/meta-class");
//    	resultList.forEach(r ->{
//    		Class<?> theClass = metaClass(r, byteArrayClassLoader,metaClassRegistry);
//    		prepareRepositoryClass(theClass, byteArrayClassLoader,metaClassRegistry);
//    	});
    	AnnotationConfigServletWebServerApplicationContext context  = 
        		(AnnotationConfigServletWebServerApplicationContext) 
        		new SpringApplication(new PathMatchingResourcePatternResolver(MetaClassRestClientApplication.byteArrayClassLoader),
        				MetaClassRestClientApplication.class,MetaClassCustomBean.class , MetaClassJpaDataServiceConfiguration.class , CustomJpaRepositoryConfig.class).run(args);
	}




}
