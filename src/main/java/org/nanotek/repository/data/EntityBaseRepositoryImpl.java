package org.nanotek.repository.data;

import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.metamodel.Metamodel;

@SuppressWarnings({"rawtypes","unchecked"})
public class EntityBaseRepositoryImpl extends SimpleJpaRepository {

	private EntityBaseRepository repository;
	private EntityManager proxy;
	private JpaMetamodelEntityInformation info;
	
	private Class<?> entityClass;
	private JpaMetamodelEntityInformation entityInformation;
	private EntityManager em;
	private PersistenceProvider provider;
	private PersistenceUnitUtil persistenceUnitUtil;
	
	public EntityBaseRepositoryImpl(JpaMetamodelEntityInformation info , EntityManager proxyy) {
		super(processInformation(info , proxyy.getMetamodel() , proxyy.getEntityManagerFactory().getPersistenceUnitUtil()) , proxyy);
		this.entityInformation = info;
		this.em = proxyy;
		this.provider = PersistenceProvider.fromEntityManager(proxyy);
		this.persistenceUnitUtil= proxyy.getEntityManagerFactory().getPersistenceUnitUtil();
	}
	
	
	private static JpaEntityInformation processInformation(JpaMetamodelEntityInformation info2 , Metamodel meta , PersistenceUnitUtil persistenceUnitUtil) {
		Class<?> entityClass = meta.getEntities().stream()
										.filter(e -> e.getName().equals(info2.getEntityName())).map(e -> e.getJavaType())
										.findFirst().get();
		return new JpaMetamodelEntityInformation(entityClass , meta  , persistenceUnitUtil) {
			
			private Class<?> mutableEntity = entityClass;

			@Override
			public String getEntityName() {
				return mutableEntity.getName();
			}
			
			@Override
			public Class getJavaType() {
				return mutableEntity;
			}
			
			public Class<?> getMutableEntity() {
				return mutableEntity;
			}

			public void setMutableEntity(Class<?> mutableEntity) {
				this.mutableEntity = mutableEntity;
			}
			
			 
			
		};
	}


	public void setEntityClass(Class<?> clazz) {
		this.entityClass = clazz;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public EntityBaseRepository getRepository() {
		return repository;
	}

	public void setRepository(EntityBaseRepository repository) {
		this.repository = repository;
	}

	public JpaMetamodelEntityInformation getEntityInformation() {
		return entityInformation;
	}

	public void setEntityInformation(JpaMetamodelEntityInformation entityInformation) {
		this.entityInformation = entityInformation;
	}

	public EntityManager getEm() {
		return em;
	}

	public EntityManager getProxy() {
		return proxy;
	}

	public void setProxy(EntityManager proxy) {
		this.proxy = proxy;
	}

	public JpaMetamodelEntityInformation getInfo() {
		return info;
	}

	public void setInfo(JpaMetamodelEntityInformation info) {
		this.info = info;
	}

	public PersistenceProvider getProvider() {
		return provider;
	}

	public void setProvider(PersistenceProvider provider) {
		this.provider = provider;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}


	
}
