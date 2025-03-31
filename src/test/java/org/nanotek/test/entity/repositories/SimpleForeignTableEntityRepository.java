package org.nanotek.test.entity.repositories;

import org.nanotek.test.entity.data.SimpleForeignTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimpleForeignTableEntityRepository 
extends JpaRepository<SimpleForeignTableEntity, String> {
}
