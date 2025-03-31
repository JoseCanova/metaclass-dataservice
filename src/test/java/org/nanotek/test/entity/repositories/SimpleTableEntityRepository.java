package org.nanotek.test.entity.repositories;

import org.nanotek.test.entity.data.SimpleTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimpleTableEntityRepository 
extends JpaRepository<SimpleTableEntity, String> {
}
