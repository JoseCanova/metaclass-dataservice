package org.nanotek.config.spring;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.persistence.EntityManagerFactory;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(
		basePackages = 
	{"jimfs.org.nanotek.config.spring.repositories"}
		, transactionManagerRef = "transactionManager")
public class CustomJpaRepositoryConfig {
	
	@Autowired MetaClassVFSURLClassLoader classLoader;
	
	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@Autowired
	ApplicationContext context;
	
}

