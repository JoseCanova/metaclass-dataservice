package org.nanotek.test.entity.data;

import org.nanotek.Base;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name="SimpleDateTableEntity")
@Table(name="simple_date_entity")
public class SimpleDateTableEntity 
implements Base<SimpleDateTableEntity>{

	public SimpleDateTableEntity() {
	}

}
