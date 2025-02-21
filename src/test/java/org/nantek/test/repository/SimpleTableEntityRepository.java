package org.nantek.test.repository;

import org.nantek.test.entity.SimpleTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimpleTableEntityRepository extends JpaRepository<SimpleTableEntity,String>{
}
