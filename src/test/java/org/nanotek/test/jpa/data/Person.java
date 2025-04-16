package org.nanotek.test.jpa.data;

import java.util.Optional;

import org.nanotek.Base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

@Entity(
   name = "Person"
)
@Table(
   name = "person",
   catalog = "",
   schema = "",
   uniqueConstraints = {},
   indexes = {}
)
public class Person implements Base<Base> {
   @Max(
      payload = {},
      value = 50L,
      message = "{jakarta.validation.constraints.Max.message}",
      groups = {}
   )
   @NotBlank(
      payload = {},
      message = "{jakarta.validation.constraints.NotBlank.message}",
      groups = {}
   )
   @Column(
      unique = false,
      name = "person_key",
      length = 255,
      scale = 0,
      table = "",
      precision = 0,
      nullable = false,
      insertable = true,
      updatable = true,
      columnDefinition = ""
   )
   @Id
   private String personKey;
   @Max(
      payload = {},
      value = 200L,
      message = "{jakarta.validation.constraints.Max.message}",
      groups = {}
   )
   @NotBlank(
      payload = {},
      message = "{jakarta.validation.constraints.NotBlank.message}",
      groups = {}
   )
   @Column(
      unique = false,
      name = "person_name",
      length = 255,
      scale = 0,
      table = "",
      precision = 0,
      nullable = false,
      insertable = true,
      updatable = true,
      columnDefinition = ""
   )
   private String personName;
   @OneToOne(
      optional = true,
      cascade = {},
      fetch = FetchType.EAGER,
      targetEntity = void.class,
      mappedBy = "personKey",
      orphanRemoval = false
   )
   private Pet pet;

   public void personKey(String var1) {
      this.personKey = var1;
   }

   public String personKey() {
      return this.personKey;
   }

   public void personName(String var1) {
      this.personName = var1;
   }

   public String getPersonName() {
      return this.personName;
   }

   public void pet(Pet var1) {
      this.pet = var1;
   }

   public Optional<Pet> pet() {
      return Optional.ofNullable(this.pet);
   }
}

