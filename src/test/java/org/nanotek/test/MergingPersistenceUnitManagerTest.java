package org.nanotek.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.nanotek.test.config.TestMetaClassDataServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;

@SpringBootTest(classes = {TestMetaClassDataServiceConfiguration.class})
public class MergingPersistenceUnitManagerTest {

	@Autowired
	MergingPersistenceUnitManager entityManager;
	
	@Autowired 
	EntityManagerFactory entityManagerFactory;
	
	public MergingPersistenceUnitManagerTest() {
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
	}

}
