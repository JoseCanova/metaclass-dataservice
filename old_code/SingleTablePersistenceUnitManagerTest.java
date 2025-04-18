package org.nanotek.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.nanotek.config.spring.MetaClassJpaDataServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.Metamodel;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

@SpringBootTest(classes = {MetaClassJpaDataServiceConfiguration.class})
public class SingleTablePersistenceUnitManagerTest {

	@Autowired
	MergingPersistenceUnitManager entityManager;
	
	@Autowired 
	EntityManagerFactory entityManagerFactory;
	
	@Autowired
	InjectionClassLoader classLoader;
	
	@Autowired
	PlatformTransactionManager defaultTransactionManager;
	
	public SingleTablePersistenceUnitManagerTest() {
	}
	
	@Test
	void testEntityManagerInitialization() throws ClassNotFoundException {
		assertNotNull(entityManager);
		System.out.println(entityManager);
		assertNotNull(entityManagerFactory);
		EntityManager rm = entityManagerFactory.createEntityManager();
		assertNotNull(rm);
		Metamodel model = rm.getMetamodel();
		assertNotNull(model);
		Set<?> entities = model.getEntities();
		assertNotNull(entities);
		Query query = rm.createQuery("select a from org.nanotek.data.SimpleTable a");
		List<?> resultList = query.getResultList();
		assertNotNull(resultList);
		assertNotNull(classLoader);
		executeInsert(rm);
		rm = entityManagerFactory.createEntityManager();
		Query query1 = rm.createQuery("select a from org.nanotek.data.SimpleTable a");
		List<?> resultList1 = query1.getResultList();
		assertTrue(resultList1.size()==1);
		executeDelete(rm);
		rm = entityManagerFactory.createEntityManager();
		Query query11 = rm.createQuery("select a from org.nanotek.data.SimpleTable a");
		List<?> resultList11 = query11.getResultList();
		assertTrue(resultList11.size()==0);
	}

	private void executeDelete(EntityManager rm) {
        EntityTransaction tx = rm.getTransaction();
        Query query11= rm.createQuery("delete from org.nanotek.data.SimpleTable a");
        tx.begin();
		query11.executeUpdate();
        tx.commit();
		
	}

	private void executeInsert(EntityManager rm) throws ClassNotFoundException {
        EntityTransaction tx = rm.getTransaction();
        tx.begin();
		Class<?> clazz = Class.forName("org.nanotek.data.SimpleTable" , false , classLoader);
		Object simpletable = Instancio.create(clazz);
		rm.persist(simpletable);
		rm.flush();
		tx.commit();
	}

}
