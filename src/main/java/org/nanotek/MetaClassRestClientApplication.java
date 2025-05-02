package org.nanotek;

import org.nanotek.config.spring.MetaClassJpaDataServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


@SpringBootApplication(proxyBeanMethods = false)
public class MetaClassRestClientApplication{

	public MetaClassRestClientApplication() {
	}
	
	public static void main(String[] args) throws Exception {
    	AnnotationConfigServletWebServerApplicationContext context  = 
        		(AnnotationConfigServletWebServerApplicationContext) 
        		new SpringApplication(new PathMatchingResourcePatternResolver(MetaClassRestClientApplication.class.getClassLoader()),
        				MetaClassRestClientApplication.class,MetaClassJpaDataServiceConfiguration.class ).run(args);
	}

}
