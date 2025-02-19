package org.nanotek.test.jpa.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.nanotek.Base;
import org.nanotek.TestMetaClassDataServiceConfiguration;
import org.nanotek.config.RepositoryClassesMap;
import org.nanotek.repository.data.EntityBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

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
		
		repositoryClassesMap
		.forEach((n,y) -> {
			BeanDefinition bd = defaultListableBeanFactory.getBeanDefinition(n+"Repository");
			System.err.println(n + ": " + bd.getBeanClassName());
			try {
				Class<?> beanClass = Class.forName("org.nanotek.data.entity.mb.buddy.repositories."+n+"Repository" , true , defaultListableBeanFactory.getBeanClassLoader());
				Class<Base<?>> entityClass = (Class<Base<?>>) Class.forName("org.nanotek.data."+n, true , defaultListableBeanFactory.getBeanClassLoader());

				//				defaultListableBeanFactory.createBean(beanClass, 0, false);
				EntityBaseRepository<Base<?>,?> obj = (EntityBaseRepository) defaultListableBeanFactory.getBean(beanClass);
				assertNotNull(obj);
				obj.findAll();
				Object instance = Instancio.create(entityClass);
				obj.saveAndFlush(entityClass.cast(instance));
				obj.deleteAll();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			
		}); 
		;
		
		System.err.println(defaultListableBeanFactory.hashCode());
//		assertNotNull(repository);
	}


}
