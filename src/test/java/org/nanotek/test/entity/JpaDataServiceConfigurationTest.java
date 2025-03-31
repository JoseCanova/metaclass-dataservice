package org.nanotek.test.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.nanotek.test.entity.data.SimpleForeignTableEntity;
import org.nanotek.test.entity.data.SimpleTableEntity;
import org.nanotek.test.entity.repositories.SimpleForeignTableEntityRepository;
import org.nanotek.test.entity.repositories.SimpleTableEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JpaDataServiceConfiguration.class})
public class JpaDataServiceConfigurationTest {

	@Autowired
	SimpleTableEntityRepository simpleTableEntityRepository;
	
	@Autowired
	SimpleForeignTableEntityRepository simpleForeignTableEntityRepository;
	
	
	public JpaDataServiceConfigurationTest() {
	}

	@Test
	void testEntityModel() {
		assertNotNull(simpleTableEntityRepository);
		assertNotNull(simpleForeignTableEntityRepository);
		simpleTableEntityRepository.deleteAll();
		var ste = createSimpleTableEntity();
		var savedSte = simpleTableEntityRepository.save(ste);
		assertNotNull(savedSte);
		var sfte = createSimpleForeignTableEntity(savedSte);
		var savedSfte = simpleForeignTableEntityRepository.save (sfte);
		assertNotNull(savedSfte);
		simpleTableEntityRepository.deleteAll();
		List<?> list = simpleTableEntityRepository.findAll();
		assertTrue(list.size()==0);
	}
	
	SimpleTableEntity createSimpleTableEntity() {
		SimpleTableEntity ste = new SimpleTableEntity();
		ste.setSimpleKey("simple_key");
		Date dt = new Date();
		ste.setSimpleDate(dt);
		Timestamp ts = new Timestamp(dt.getTime());
		ste.setSimpleTimestamp(ts);
		ste.setSimpleColumn("simple_column");
		return ste;
	}
	
	SimpleForeignTableEntity createSimpleForeignTableEntity(SimpleTableEntity ste) {
		SimpleForeignTableEntity sfte = new SimpleForeignTableEntity();
		sfte.setSimpleForeignKey("simplefkey");
		sfte.setSimpleForeignColumn("simple column");
		sfte.setSimpleTableEntity(ste);
		return sfte;
	}
	
}
