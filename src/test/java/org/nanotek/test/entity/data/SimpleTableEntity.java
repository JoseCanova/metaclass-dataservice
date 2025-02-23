package org.nanotek.test.entity.data;

import org.nanotek.Base;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name="SimpleTableEntity")
@Table(name="simple_table")
public class SimpleTableEntity implements Base<SimpleTableEntity> {

	public SimpleTableEntity() {
	}

}
