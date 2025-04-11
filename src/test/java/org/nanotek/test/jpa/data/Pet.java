package org.nanotek.test.jpa.data;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import org.nanotek.Base;

@Entity(
   name = "Pet"
)
@Table(
   name = "pet",
   catalog = "",
   schema = "",
   uniqueConstraints = {},
   indexes = {}
)
public class Pet implements Base<Base> {
   @Max(
      payload = {},
      value = 25L,
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
      name = "pet_key",
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
   private String petKey;
   @Max(
      payload = {},
      value = 250L,
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
      name = "pet_name",
      length = 255,
      scale = 0,
      table = "",
      precision = 0,
      nullable = false,
      insertable = true,
      updatable = true,
      columnDefinition = ""
   )
   private String petName;
   @OneToOne(
      optional = true,
      cascade = {},
      fetch = FetchType.EAGER,
      targetEntity = void.class,
      mappedBy = "",
      orphanRemoval = false
   )
   @JoinColumn(
      unique = false,
      name = "pet_person_key",
      table = "",
      referencedColumnName = "person_key",
      nullable = true,
      insertable = true,
      updatable = true,
      columnDefinition = "",
      foreignKey = @ForeignKey(
         name = "",
         value = ConstraintMode.PROVIDER_DEFAULT,
         foreignKeyDefinition = ""
      )
   )
   private Person personKey;
   @OneToOne(
      optional = true,
      cascade = {},
      fetch = FetchType.EAGER,
      targetEntity = void.class,
      mappedBy = "petKey",
      orphanRemoval = false
   )
   private Food food;

   public void setPetKey(String var1) {
      this.petKey = var1;
   }

   public String getPetKey() {
      return this.petKey;
   }

   public void setPetName(String var1) {
      this.petName = var1;
   }

   public String getPetName() {
      return this.petName;
   }

   public void setPersonKey(Person var1) {
      this.personKey = var1;
   }

   public Person getPersonKey() {
      return this.personKey;
   }

   public void setFood(Food var1) {
      this.food = var1;
   }

   public Food getFood() {
      return this.food;
   }
}

