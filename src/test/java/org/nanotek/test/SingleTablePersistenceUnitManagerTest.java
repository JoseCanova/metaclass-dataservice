package org.nanotek.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.Metamodel;

@SpringBootTest
public class SingleTablePersistenceUnitManagerTest {

	@Autowired
	MergingPersistenceUnitManager entityManager;
	
	@Autowired 
	EntityManagerFactory entityManagerFactory;
	
	public SingleTablePersistenceUnitManagerTest() {
	}
	
	@Test
	void testEntityManagerInitialization() {
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
	}

}
