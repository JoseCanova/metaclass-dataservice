package org.nanotek.test.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.nanotek.metaclass.entity.Family;
import org.nanotek.metaclass.entity.FamilyDog;
import org.nanotek.metaclass.repository.FamilyDogRepository;
import org.nanotek.metaclass.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@SpringBootTest(classes = {JpaDataServiceConfiguration.class})
public class JpaDataServiceConfigurationTest {

	@Autowired
	FamilyRepository familyRepository;
	
	@Autowired
	FamilyDogRepository familyDogRepository;
	
	
	public JpaDataServiceConfigurationTest() {
	}

	@Test
	@Transactional
	void testEntityModel() {
		assertNotNull(familyRepository);
		assertNotNull(familyDogRepository);
		Optional<Family> theFamilyOpt = familyRepository.findById("family_key");
		theFamilyOpt.ifPresent(f ->{
			System.err.println("Family is present");
			familyRepository.deleteById("family_key");
			familyDogRepository.deleteById("simplefdkey");
		});
		var ste = createFamilyEntity();
		var savedSte = familyRepository.save(ste);
		familyRepository.flush();
		assertNotNull(savedSte);
		var sfte = createFamilyDogEntity();
		var savedSfte = familyDogRepository.save (sfte);
		familyDogRepository.flush();
		assertNotNull(savedSfte);
		Optional.ofNullable(savedSte.getFamilydog())
		.ifPresentOrElse(fd ->fd.add(savedSfte), ()->{
			savedSte.setFamilydog(new HashSet<>());
			savedSte.getFamilydog().add(savedSfte);
		});
		familyRepository.save(savedSte);
		familyRepository.flush();
		assertTrue(savedSte.getFamilydog().size()==1);
	}
	
	Family createFamilyEntity() {
		Family ste = new Family();
		ste.setFKey("family_key");
		ste.setFName("Family Name");
		return ste;
	}
	
	FamilyDog createFamilyDogEntity() {
		FamilyDog sfte = new FamilyDog();
		sfte.setFdKey("simplefdkey");
		sfte.setFdName("simple fdkey dog");
		return sfte;
	}
	
}
