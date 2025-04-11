package org.nanotek.test.jpa.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="pet")
public class Pet {

	@Id
	@Column(name="pet_key")
	private String petKey;
	
	@Column(name="pet_name")
	private String petName;
	
	@OneToOne
	@JoinColumn(referencedColumnName = "person_key",name = "pet_person_key")
	private Person person;
	
	public Pet() {
	}


	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}


	public String getPetKey() {
		return petKey;
	}


	public void setPetKey(String petKey) {
		this.petKey = petKey;
	}
	
	

}
