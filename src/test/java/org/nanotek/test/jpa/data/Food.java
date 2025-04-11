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
   name = "Food"
)
@Table(
   name = "food",
   catalog = "",
   schema = "",
   uniqueConstraints = {},
   indexes = {}
)
public class Food implements Base<Base> {
   @Max(
      payload = {},
      value = 30L,
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
      name = "food_key",
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
   private String foodKey;
   @Max(
      payload = {},
      value = 200L,
      message = "{jakarta.validation.constraints.Max.message}",
      groups = {}
   )
   @Column(
      unique = false,
      name = "food_name",
      length = 255,
      scale = 0,
      table = "",
      precision = 0,
      nullable = true,
      insertable = true,
      updatable = true,
      columnDefinition = ""
   )
   private String foodName;
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
      name = "food_pet_key",
      table = "",
      referencedColumnName = "pet_key",
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
   private Pet petKey;

   public void setFoodKey(String var1) {
      this.foodKey = var1;
   }

   public String getFoodKey() {
      return this.foodKey;
   }

   public void setFoodName(String var1) {
      this.foodName = var1;
   }

   public String getFoodName() {
      return this.foodName;
   }

   public void setPetKey(Pet var1) {
      this.petKey = var1;
   }

   public Pet getPetKey() {
      return this.petKey;
   }
}

