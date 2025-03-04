package org.nanotek.test.jpa.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.nanotek.Base;
import org.nanotek.config.RepositoryClassesBuilder;
import org.nanotek.config.spring.MetaClassJpaDataServiceConfiguration;
import org.nanotek.repository.data.EntityBaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * SimpleNumericTableRepository
   SimpleNumericHundredTableRepository
   SimpleDateTableRepository
   SimpleTableRepository
 */

@SpringBootTest(classes = {MetaClassJpaDataServiceConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class TestJpaRepositoryBean {

	private static final Logger logger = LoggerFactory.getLogger(TestJpaRepositoryBean.class);
	
	@Autowired
	@Qualifier("myBf")
	DefaultListableBeanFactory defaultListableBeanFactory;
	
	@Autowired 
	ApplicationContext applicationContext; 
	
	@Autowired
	@Qualifier("repositoryClassesMap")
	RepositoryClassesBuilder repositoryClassesMap;
	
	
	private TransactionTemplate transactionTemplate;

	@Autowired
	PlatformTransactionManager transactionManager;
	
//	@Autowired
//	@Qualifier("SimpleTableRepository")
//	EntityBaseRepositoryImpl repository;

	
	public TestJpaRepositoryBean() {
	}
	
	
	@Test
	void testRepositoryBeanFactory(){
		assertNotNull(transactionManager);
		this.transactionTemplate = new TransactionTemplate(transactionManager);
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
				EntityBaseRepository<Base<?>,?> obj = (EntityBaseRepository<Base<?>, ?>) defaultListableBeanFactory.getBean(beanClass);
				assertNotNull(obj);
//				obj.findAll();
				someServiceMethod(obj , entityClass);
//				Object instance = Instancio.create(entityClass);
//				obj.saveAndFlush(entityClass.cast(instance));
//				obj.deleteAll();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			
		}); 
		;
		
		System.err.println(defaultListableBeanFactory.hashCode());
//		assertNotNull(repository);
	}

	public Object someServiceMethod(EntityBaseRepository<Base<?>,?> obj , Class<Base<?>> entityClass) {
		return transactionTemplate.execute(new TransactionCallback() {
			// the code in this method runs in a transactional context
			public Object doInTransaction(TransactionStatus status) {
				Object instance = Instancio.create(entityClass);
				try {
					obj.saveAndFlush(entityClass.cast(instance));
					obj.deleteAll();
				} catch (Exception ex) {
					status.setRollbackOnly();
					logger.info("problem on test" , ex);
				}
				return instance;
			}
		});
	}
	
}
