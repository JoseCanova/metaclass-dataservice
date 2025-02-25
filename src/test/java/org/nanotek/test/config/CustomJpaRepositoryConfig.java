package org.nanotek.test.config;
import org.nanotek.Base;
import org.nanotek.config.MetaClassClassesStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.persistence.EntityManagerFactory;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

@SpringBootConfiguration
@EnableJpaRepositories(
		basePackages = 
	{"org.nanotek.test.config"}
		, transactionManagerRef = "transactionManager")
public class CustomJpaRepositoryConfig {
	
	@Autowired InjectionClassLoader classLoader;
	
	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@Autowired
	ApplicationContext context;
	
	
	@Bean 
	@Qualifier(value="myBf")
	public DefaultListableBeanFactory defaultListableBeanFactory(@Autowired InjectionClassLoader classLoader )
	{
		DefaultListableBeanFactory v = new DefaultListableBeanFactory();
		v.setParentBeanFactory(context);
		v.setBeanClassLoader(classLoader);
		return v;
	}
	
	
	@Bean("integerValue") 
	@Primary
	@Qualifier(value="integerValue")
	public Integer integerValue(@Autowired
			@Qualifier("repositoryClassesMap")
			MetaClassClassesStore repositoryClassesMap,
			@Autowired @Qualifier("myBf")
			DefaultListableBeanFactory defaultListableBeanFactory,@Autowired InjectionClassLoader classLoader) {
		
		repositoryClassesMap.forEach ((x,y)->{
			try {
				Class<Base<?>> entityClass = (Class<Base<?>>) Class.forName("org.nanotek.data."+x, true , defaultListableBeanFactory.getBeanClassLoader());
				Class<?> beanClass = y;
				
//				configureRepositoryBean(defaultListableBeanFactory , entityClass , x , beanClass , classLoader ,entityManagerFactory );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return Integer.MAX_VALUE;
	}
	
//	private void configureRepositoryBean(DefaultListableBeanFactory defaultListableBeanFactory, Class<?> c,
//			String sNmae2, Class<?> repClass, InjectionClassLoader classLoader, EntityManagerFactory entityManagerFactory) {
//		SimpleEntityPathResolver pr = new SimpleEntityPathResolver(sNmae2);
//		//TODO: verify the need to replace for RootBeanDefinition
//		 GenericBeanDefinition  bd = new  GenericBeanDefinition ();
//		bd.setBeanClass(JpaRepositoryFactoryBean.class);
//		bd.setLazyInit(false);
//		ConstructorArgumentValues cav = new ConstructorArgumentValues();
//		cav.addGenericArgumentValue(new ValueHolder(repClass));
//		bd.setConstructorArgumentValues(cav);
//
//		bd.setPropertyValues(new MutablePropertyValues().add("entityPathResolver", 
//				new SimpleObjectProvider<>(pr))
//				.add("beanClassLoader", classLoader)
//				.add("beanFactory", defaultListableBeanFactory)
//				.add("repositoryBaseClass", SimpleJpaRepository.class)
//				.add("entityManager", entityManagerFactory.createEntityManager()));
//
//		bd.addQualifier(new AutowireCandidateQualifier(repClass.getSimpleName()));
//		bd.setAutowireCandidate(true);
//		bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
//		System.err.println(repClass.getSimpleName());
//		defaultListableBeanFactory.registerBeanDefinition(repClass.getSimpleName(), bd);
//		System.err.println(defaultListableBeanFactory.hashCode());
//	}
}

