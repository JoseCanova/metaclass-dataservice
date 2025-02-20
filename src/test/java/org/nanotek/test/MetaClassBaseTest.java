package org.nanotek.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.nanotek.test.config.TestMetaClassDataServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest(classes = {TestMetaClassDataServiceConfiguration.class})
public class MetaClassBaseTest {
    
	RdbmsMetaClass theClass ;
	
	
	@BeforeEach
	void testJsonReader() throws Exception{
		
        ObjectMapper objectMapper = new ObjectMapper();
        	List<JsonNode> list = objectMapper.readValue
            			(getClass().getResourceAsStream("/metaclass.json")
            					, List.class);
        	assertTrue (list.size()==1);
        	Object theNode = list.get(0);
        	theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
        	assertTrue(theClass.getTableName().equals("simple_table"));
    }
	
	
	//TODO: implement the class builder with attributes and validation.
	@Test
	void testClassBuilder() {
	
		RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
		Class<?> loaded = eb.getLoadedClassInDefaultClassLoader();
		assertNotNull(loaded);
		assertTrue( loaded.getDeclaredFields().length == theClass.getMetaAttributes().size());
	}

	
}
