package org.nanotek.test.entity.data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import org.nanotek.Base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name="SimpleTableEntity")
@Table(name="simple_table")
public class SimpleTableEntity 
implements Base<SimpleTableEntity> {

	@Id
	@Column(name="simple_key" , nullable = false)
	private String simpleKey;

	@Column(name="simple_column" , nullable = false)
	private String simpleColumn;

	@Column(name="simple_date" , nullable = false)
	private Date simpleDate;

	@Column(name="simple_timestamp" , nullable = false)
	private Timestamp simpleTimestamp;
	
	@OneToMany(mappedBy = "simpleTableEntity")
	private Set<SimpleForeignTableEntity> simpleForeignTableEntitySet;
	
	public SimpleTableEntity() {
	}

	public String getSimpleKey() {
		return simpleKey;
	}

	public void setSimpleKey(String simpleKey) {
		this.simpleKey = simpleKey;
	}


	public Date getSimpleDate() {
		return simpleDate;
	}

	public void setSimpleDate(Date simpleDate) {
		this.simpleDate = simpleDate;
	}

	public String getSimpleColumn() {
		return simpleColumn;
	}

	public void setSimpleColumn(String simpleColumn) {
		this.simpleColumn = simpleColumn;
	}

	public Timestamp getSimpleTimestamp() {
		return simpleTimestamp;
	}

	public void setSimpleTimestamp(Timestamp simpleTimestamp) {
		this.simpleTimestamp = simpleTimestamp;
	}

	public Set<SimpleForeignTableEntity> getSimpleForeignTableEntitySet() {
		return simpleForeignTableEntitySet;
	}

	public void setSimpleForeignTableEntitySet(Set<SimpleForeignTableEntity> simpleForeignTableEntitySet) {
		this.simpleForeignTableEntitySet = simpleForeignTableEntitySet;
	}
	
}
