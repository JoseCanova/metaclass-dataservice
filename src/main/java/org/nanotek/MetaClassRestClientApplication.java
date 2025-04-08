package org.nanotek;

import static org.nanotek.ApplicationInitializer.getMetaClasses;
import static org.nanotek.ApplicationInitializer.metaClass;
import static org.nanotek.ApplicationInitializer.prepareRepositoryClass;
import static org.nanotek.ApplicationInitializer.configureMetaClasses;
import java.nio.file.FileSystem;
import java.util.List;

import org.nanotek.config.MetaClassRegistry;
import org.nanotek.config.MetaClassVFSURLClassLoader;
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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;


@SpringBootApplication(proxyBeanMethods = false)
public class MetaClassRestClientApplication {


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
		
		configureMetaClasses("http://localhost:8086/meta-class",byteArrayClassLoader,metaClassRegistry);
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
