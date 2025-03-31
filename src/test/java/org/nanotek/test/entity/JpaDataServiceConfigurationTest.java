package org.nanotek.test.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JpaDataServiceConfiguration.class})
public class JpaDataServiceConfigurationTest {

	public JpaDataServiceConfigurationTest() {
	}

	@Test
	void testEntityMetaMode() {
		assertTrue(true);
	}
	
}
