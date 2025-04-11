package org.nanotek.test.jpa.repositories;

import org.nanotek.test.jpa.data.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, String> {
}
