package org.nanotek.test.jpa;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.nanotek.config.spring.data.Person;
import org.nanotek.config.spring.data.Pet;
import org.nanotek.test.jpa.repositories.PersonRepository;
import org.nanotek.test.jpa.repositories.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JpaDataServiceConfiguration.class})
public class JpaDataServiceConfigurationTest {

	@Autowired
	PetRepository petRepository;

	@Autowired
	PersonRepository personRepository;
	
	public JpaDataServiceConfigurationTest() {
	}
	
	@Test
	void test() {
		
		assertNotNull(petRepository);
		assertNotNull(personRepository);
		
		Optional<Person> casperPerson = personRepository.findById("p_key");
		
		casperPerson.ifPresentOrElse(
				person ->{
					assertNotNull(person);
					Optional<Person> casperPersonAgain = personRepository.findById("p_key");
					Pet optionalPirineusKiller = casperPersonAgain.get().getPet();
					assertNotNull(optionalPirineusKiller);
					System.out.println("Pirineus Killer is present");
				}, 
				() -> new RuntimeException());
		
	}

	
}
