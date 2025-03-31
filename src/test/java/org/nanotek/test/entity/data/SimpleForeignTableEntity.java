package org.nanotek.test.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name="SimpleForeignTableEntity")
@Table(name="simple_foreign_table")
public class SimpleForeignTableEntity {

	@Id
	@Column(name="simple_foreign_key",nullable=false)
	private String simpleForeignKey;
	
	@Column(name="simple_foreign_column",nullable=false)
	private String simpleForeignColumn;
	
	@ManyToOne
	@JoinColumn(name = "simple_key",nullable = false)
	private SimpleTableEntity simpleTableEntity;
	
	
	public SimpleForeignTableEntity() {
	}


	public String getSimpleForeignKey() {
		return simpleForeignKey;
	}


	public void setSimpleForeignKey(String simpleForeignKey) {
		this.simpleForeignKey = simpleForeignKey;
	}


	public String getSimpleForeignColumn() {
		return simpleForeignColumn;
	}


	public void setSimpleForeignColumn(String simpleForeignColumn) {
		this.simpleForeignColumn = simpleForeignColumn;
	}


	public SimpleTableEntity getSimpleTableEntity() {
		return simpleTableEntity;
	}


	public void setSimpleTableEntity(SimpleTableEntity simpleTableEntity) {
		this.simpleTableEntity = simpleTableEntity;
	}

}
