package org.nanotek.config.spring;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import jakarta.persistence.EntityManagerFactory;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(
		basePackages = 
	{"org.nanotek.config.spring.repositories"}
		, transactionManagerRef = "transactionManager")
public class CustomJpaRepositoryConfig {
	
	@Autowired MetaClassVFSURLClassLoader classLoader;
	
	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@Autowired
	ApplicationContext context;
	
	
	@Autowired
	@Qualifier(value="myBf")
	public DefaultListableBeanFactory defaultListableBeanFactory;
	
}

