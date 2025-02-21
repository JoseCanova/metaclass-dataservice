package org.nanotek.repository.data;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.EntityManagerFactory;

public class MetaClassJpaTransactionManager extends  JpaTransactionManager{

	public MetaClassJpaTransactionManager() {
	}

	public MetaClassJpaTransactionManager(EntityManagerFactory emf) {
		super(emf);
	}
	@Override
	public void afterPropertiesSet() {
		if (getEntityManagerFactory() == null) {
			throw new IllegalArgumentException("'entityManagerFactory' or 'persistenceUnitName' is required");
		}
		if (getEntityManagerFactory() instanceof EntityManagerFactoryInfo emfInfo) {
			DataSource dataSource = emfInfo.getDataSource();
			if (dataSource != null) {
				setDataSource(dataSource);
			}
			JpaDialect jpaDialect = emfInfo.getJpaDialect();
			if (jpaDialect != null) {
				setJpaDialect(jpaDialect);
			}
		}	}
 
	@Override
	protected Object doGetTransaction() {
		Object txObject = super.doGetTransaction();
		return txObject;
	}
}
