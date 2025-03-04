package org.nanotek.config.spring;
import org.nanotek.Base;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.config.RepositoryClassesBuilder;
import org.nanotek.repository.data.EntityBaseRepositoryImpl;
import org.nanotek.repository.data.MetaClassJpaRepositoryComponentBean;
import org.nanotek.repository.data.SimpleObjectProvider;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import jakarta.persistence.EntityManagerFactory;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude= {TransactionAutoConfiguration.class})
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
	
	@Bean("integerValue") 
	@Primary
	@Qualifier(value="integerValue")
	public Integer integerValue(
			@Autowired 
			@Qualifier("repositoryClassesMap") RepositoryClassesBuilder repositoryClassesBuilder) {
		
		repositoryClassesBuilder.forEach ((x,y)->{
			try {
				Class<Base<?>> entityClass = (Class<Base<?>>) Class.forName("org.nanotek.config.spring.data."+x, true , defaultListableBeanFactory.getBeanClassLoader());
				Class<?> beanClass = y;
				
				configureRepositoryBean(defaultListableBeanFactory , entityClass , x , beanClass , classLoader ,entityManagerFactory );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return Integer.MAX_VALUE;
	}
	
	private void configureRepositoryBean(DefaultListableBeanFactory defaultListableBeanFactory, Class<?> c,
			String sNmae2, Class<?> repClass, MetaClassVFSURLClassLoader classLoader, EntityManagerFactory entityManagerFactory) {
		SimpleEntityPathResolver pr = new SimpleEntityPathResolver(sNmae2);
		//TODO: verify the need to replace for RootBeanDefinition
		 GenericBeanDefinition  bd = new  GenericBeanDefinition ();
		bd.setBeanClass(MetaClassJpaRepositoryComponentBean.class);
		bd.setLazyInit(false);
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(new ValueHolder(repClass));
		bd.setConstructorArgumentValues(cav);

		bd.setPropertyValues(new MutablePropertyValues().add("entityPathResolver", 
				new SimpleObjectProvider<>(pr))
				.add("beanClassLoader", classLoader)
				.add("beanFactory", defaultListableBeanFactory)
				.add("repositoryBaseClass", EntityBaseRepositoryImpl.class)
				.add("entityManagerFactory", entityManagerFactory)
				.add("entityManager", entityManagerFactory.createEntityManager())
				.add("entityClass",c));

		bd.addQualifier(new AutowireCandidateQualifier(repClass.getSimpleName()));
		bd.setAutowireCandidate(true);
		bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
		System.err.println(repClass.getSimpleName());
		defaultListableBeanFactory.registerBeanDefinition(repClass.getSimpleName(), bd);
		System.err.println(defaultListableBeanFactory.hashCode());
	}
}

