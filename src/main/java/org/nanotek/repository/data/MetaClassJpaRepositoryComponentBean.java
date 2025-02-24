package org.nanotek.repository.data;

import org.nanotek.Base;
import org.nanotek.config.PersistenceUnityClassesMap;
import org.nanotek.config.RepositoryClassesMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


public class MetaClassJpaRepositoryComponentBean<T extends MetaClassBaseEntityRepository<K, ID>, K extends Base<?>, ID> 
extends JpaRepositoryFactoryBean<T,K,ID> {

	@Autowired
	PersistenceUnityClassesMap classesCondig;
	
	@Autowired 
	RepositoryClassesMap repoConfig;
	
	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	private Class<?> entityClass;

	private ObjectProvider<EntityPathResolver> resolver;

	private JpaQueryMethodFactory queryMethodFactory;
	
	protected Class<? extends T> entityRepositoryInterface;
	
	private ClassLoader classLoader;
	
	
	private static Logger logger = LoggerFactory.getLogger(MetaClassJpaRepositoryComponentBean.class);
	
	public MetaClassJpaRepositoryComponentBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
		logger.debug("MetaClassJpaRepositoryComponentBean instantiated");
	}
	
	
	@Override
	public void afterPropertiesSet() {
		setEntityManager(entityManagerFactory.createEntityManager());
		super.afterPropertiesSet();
		logger.debug("MetaClassJpaRepositoryComponentBean afterPropertiesSet");
	}
	
	@Override
	public EntityInformation<K, ID> getEntityInformation() {
		EntityInformation<K, ID>  ei = super.getEntityInformation();
		return ei;
	}

	public PersistenceUnityClassesMap getClassesCondig() {
		return classesCondig;
	}

	public void setClassesCondig(PersistenceUnityClassesMap  classesCondig) {
		this.classesCondig = classesCondig;
	}

	public RepositoryClassesMap getRepoConfig() {
		return repoConfig;
	}

	public void setRepoConfig(RepositoryClassesMap repoConfig) {
		this.repoConfig = repoConfig;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	@Override
	public void setCustomImplementation(Object customImplementation) {
		super.setCustomImplementation(customImplementation);
	}
	
	public void setEntityClass(Class<? extends Base> entityClass) {
		this.entityClass = entityClass;
	}
	
	@Override
	public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
		this.resolver = resolver;
		super.setEntityPathResolver(resolver);
	}
	
	
	@Override
	public void setQueryMethodFactory(JpaQueryMethodFactory factory) {
		this.queryMethodFactory = factory; 
		super.setQueryMethodFactory(factory);
	}
	
	/**
	 * Returns a {@link RepositoryFactorySupport}.
	 */
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

		MyJpaRepositoryFactory jpaRepositoryFactory = new MyJpaRepositoryFactory(entityManager);
		jpaRepositoryFactory.setEntityPathResolver(resolver.getIfAvailable());
		jpaRepositoryFactory.setBeanClassLoader(classLoader);
//		jpaRepositoryFactory.setEscapeCharacter(escapeCharacter);

		EntityInformation<? , ?> ei = jpaRepositoryFactory.getEntityInformation(getEntityClass());
		
		if (queryMethodFactory != null) {
			jpaRepositoryFactory.setQueryMethodFactory(queryMethodFactory);
		}

		return jpaRepositoryFactory;
	}

	public Class<? extends T> getEntityRepositoryInterface() {
		return entityRepositoryInterface;
	}

	public void setEntityRepositoryInterface(Class<? extends T> entityRepositoryInterface) {
		this.entityRepositoryInterface = entityRepositoryInterface;
	}


	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}


	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}


	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	

}
