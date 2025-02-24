package org.nanotek.test.config.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.instancio.Instancio;
import org.nanotek.test.entity.data.SimpleTableEntity;
import org.nanotek.test.entity.repositories.SimpleTableEntityRepository;
import org.nanotek.test.jpa.repositories.TestJpaRepositoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootConfiguration(proxyBeanMethods = false)
@Import({ TestJpaDataServiceConfiguration.class})
public class TestJpaDataServiceApplication 
implements SpringApplicationRunListener , 
ApplicationRunner, 
ApplicationContextAware{

	
	private static final Logger logger = LoggerFactory.getLogger(TestJpaRepositoryBean.class);

	@Autowired
	@Qualifier("myBf")
	DefaultListableBeanFactory defaultListableBeanFactory;
	
	
	private TransactionTemplate transactionTemplate;

	@Autowired
	PlatformTransactionManager transactionManager;

	private ApplicationContext applicationContext;
	
	
	public TestJpaDataServiceApplication() {
	}
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TestJpaDataServiceApplication.class, args);
		TestJpaDataServiceApplication bean = context.getBean(TestJpaDataServiceApplication.class);
		bean.testRepositoryBeanFactory();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (transactionManager == null)
			throw new RuntimeException();
	}
	
	void testRepositoryBeanFactory(){
		this.transactionTemplate = new TransactionTemplate(transactionManager);
			try {
//				interceptor.setTransactionManager(transactionManager);
				//				defaultListableBeanFactory.createBean(beanClass, 0, false);

				JpaRepository obj = (JpaRepository) applicationContext.getBean(SimpleTableEntityRepository.class);
				assertNotNull(obj);
//				obj.findAll();
				someAnnotatedTransactionalServiceMethod(obj , SimpleTableEntity.class);
//				Object instance = Instancio.create(entityClass);
//				obj.saveAndFlush(entityClass.cast(instance));
//				obj.deleteAll();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		;
		
		System.err.println(defaultListableBeanFactory.hashCode());
//		assertNotNull(repository);
	}

	
	@Transactional
	public Object someAnnotatedTransactionalServiceMethod(JpaRepository obj , Class<?> class1) {
				Object instance = Instancio.create(class1);
					obj.saveAndFlush(class1.cast(instance));
//					obj.deleteAll();
				return instance;
			}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
