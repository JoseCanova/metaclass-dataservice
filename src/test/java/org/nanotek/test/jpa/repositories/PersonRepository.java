package org.nanotek.test.jpa.repositories;

import java.util.Optional;

import org.nanotek.test.jpa.data.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, String> {

}
