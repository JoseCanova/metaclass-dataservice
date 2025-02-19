package org.nanotek.test.jpa.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.nanotek.TestMetaClassDataServiceConfiguration;
import org.nanotek.config.RepositoryClassesMap;
import org.nanotek.repository.data.EntityBaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SimpleNumericTableRepository
   SimpleNumericHundredTableRepository
   SimpleDateTableRepository
   SimpleTableRepository
 */

@SpringBootTest(classes = {TestMetaClassDataServiceConfiguration.class})
public class TestJpaRepositoryBean {

	@Autowired
	@Qualifier("myBf")
	DefaultListableBeanFactory defaultListableBeanFactory;
	
	@Autowired 
	ApplicationContext applicationContext; 
	
	@Autowired
	@Qualifier("repositoryClassesMap")
	RepositoryClassesMap repositoryClassesMap;
	
	@Autowired
	
	
	
//	@Autowired
//	@Qualifier("SimpleTableRepository")
//	EntityBaseRepositoryImpl repository;

	
	public TestJpaRepositoryBean() {
	}
	
	@Test
	void testRepositoryBeanFactory(){
		assertNotNull(defaultListableBeanFactory);
		//Object bean = beanFactory.getBean("SimpleNumericTableRepository");
		assertNotNull(applicationContext);
		
		Stream.of(defaultListableBeanFactory.getBeanDefinitionNames())
		.forEach(n -> {
			BeanDefinition bd = defaultListableBeanFactory.getBeanDefinition(n);
			System.err.println(n + ": " +bd.getBeanClassName());
			try {
				Class<?> beanClass = Class.forName("org.nanotek.data.entity.mb.buddy.repositories."+n , true , defaultListableBeanFactory.getBeanClassLoader());
//				defaultListableBeanFactory.createBean(beanClass, 0, false);
				JpaRepository<?,?> obj = (JpaRepository) defaultListableBeanFactory.getBean(beanClass);
				assertNotNull(obj);
				obj.findAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}); 
		;
		
		System.err.println(defaultListableBeanFactory.hashCode());
//		assertNotNull(repository);
	}

}
