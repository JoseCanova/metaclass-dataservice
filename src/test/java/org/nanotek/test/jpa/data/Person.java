package org.nanotek.test.jpa.data;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="person")
public class Person {

	@Id
	@Column (name="person_key")
	private String personKey;
	
	@Column (name="person_name")
	private String personName;
	
	@OneToOne(mappedBy="person" , targetEntity = Pet.class,fetch = FetchType.EAGER)
	private Pet pet;
	
	public Person() {
	}

	public String getPersonKey() {
		return personKey;
	}

	public void setPersonKey(String personKey) {
		this.personKey = personKey;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public Optional<Pet> getPet() {
		return Optional.ofNullable(pet);
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

}
