package org.nanotek.test.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.instancio.Instancio;
import org.nanotek.Base;
import org.nanotek.config.RepositoryClassesMap;
import org.nanotek.repository.data.EntityBaseRepository;
import org.nanotek.test.entity.data.SimpleTableEntity;
import org.nanotek.test.entity.repositories.SimpleTableEntityRepository;
import org.nanotek.test.jpa.repositories.TestJpaRepositoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootConfiguration(proxyBeanMethods = false)
@Import({ MetaClassCustomBean.class,  TestMetaClassDataServiceConfiguration.class})
public class TestMetaClassDataServiceApplication 
implements SpringApplicationRunListener , 
ApplicationRunner, 
ApplicationContextAware{

	
	private static final Logger logger = LoggerFactory.getLogger(TestJpaRepositoryBean.class);

	@Autowired
	@Qualifier("myBf")
	DefaultListableBeanFactory defaultListableBeanFactory;
	
	@Autowired
	@Qualifier("repositoryClassesMap")
	RepositoryClassesMap repositoryClassesMap;
	
	private TransactionTemplate transactionTemplate;

	@Autowired
	PlatformTransactionManager transactionManager;

	
	
	private ApplicationContext applicationContext;
	
	
	public TestMetaClassDataServiceApplication() {
	}
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TestMetaClassDataServiceApplication.class, args);
		TestMetaClassDataServiceApplication bean = context.getBean(TestMetaClassDataServiceApplication.class);
		bean.testRepositoryBeanFactory();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (transactionManager == null)
			throw new RuntimeException();
	}
	
	void testRepositoryBeanFactory(){
		this.transactionTemplate = new TransactionTemplate(transactionManager);
		//Object bean = beanFactory.getBean("SimpleNumericTableRepository");
		repositoryClassesMap
		.forEach((n,y) -> {
			BeanDefinition bd = defaultListableBeanFactory.getBeanDefinition(n+"Repository");
			System.err.println(n + ": " + bd.getBeanClassName());
			try {
				Class<?> beanClass = Class.forName("org.nanotek.data.entity.mb.buddy.repositories."+n+"Repository" , true , defaultListableBeanFactory.getBeanClassLoader());
				Class<Base<?>> entityClass = (Class<Base<?>>) Class.forName("org.nanotek.data."+n, true , defaultListableBeanFactory.getBeanClassLoader());
//				TransactionInterceptor interceptor = applicationContext.getBean(TransactionInterceptor.class);
//				interceptor.setTransactionManager(transactionManager);
				//				defaultListableBeanFactory.createBean(beanClass, 0, false);
				EntityBaseRepository<Base<?>,?> obj = (EntityBaseRepository<Base<?>, ?>) defaultListableBeanFactory.getBean(beanClass);
				assertNotNull(obj);
//				obj.findAll();
				someAnnotatedTransactionalServiceMethod(obj , entityClass);
				obj.deleteAll();
//				Object instance = Instancio.create(entityClass);
//				obj.saveAndFlush(entityClass.cast(instance));
//				obj.deleteAll();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			
		}); 
		;
		JpaRepository<SimpleTableEntity,String> obj = (JpaRepository<SimpleTableEntity,String>) 
															defaultListableBeanFactory.getBean(SimpleTableEntityRepository.class);
		someAnnotatedTransactionalServiceMethod(obj);
		System.err.println(defaultListableBeanFactory.hashCode());
//		assertNotNull(repository);
	}

	@Transactional(readOnly=false)
	public Object someAnnotatedTransactionalServiceMethod(JpaRepository<SimpleTableEntity,String>  obj) {
		SimpleTableEntity instance = Instancio.create(SimpleTableEntity.class);
			obj.saveAndFlush(instance);
//			obj.deleteAll();
		return instance;
	}

	
	public Object someAnnotatedTransactionalServiceMethod(EntityBaseRepository<Base<?>,?> obj , Class<Base<?>> entityClass) {
				Object instance = Instancio.create(entityClass);
					obj.save(entityClass.cast(instance));
//					obj.deleteAll();
				return instance;
			}
	
	public Object someProgramaticServiceMethod(EntityBaseRepository<Base<?>,?> obj , Class<Base<?>> entityClass) {
		return transactionTemplate.execute(new TransactionCallback() {
			// the code in this method runs in a transactional context
			public Object doInTransaction(TransactionStatus status) {
				Object instance = Instancio.create(entityClass);
				try {
					obj.save(entityClass.cast(instance));
					obj.deleteAll();
				} catch (Exception ex) {
					status.setRollbackOnly();
					logger.info("problem on test" , ex);
				}
				return instance;
			}
		});
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
