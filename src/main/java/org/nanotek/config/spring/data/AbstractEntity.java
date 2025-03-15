package org.nanotek.config.spring.data;

import org.nanotek.Base;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;


//A base class for entities with manually assigned identifiers
@MappedSuperclass
public abstract class AbstractEntity<T extends AbstractEntity<T,ID>,ID> 
implements Persistable<ID> , Base<T> {

  @Transient
  private boolean isNew = true; 

  @Override
  public boolean isNew() {
    return isNew; 
  }

  @PrePersist 
  @PostLoad
  void markNotNew() {
    this.isNew = false;
  }

}
