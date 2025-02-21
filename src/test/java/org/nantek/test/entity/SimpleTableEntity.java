package org.nantek.test.entity;

import java.sql.Timestamp;
import java.util.Date;

import org.nanotek.Base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity(name = "SimpleTableEntity")
@Table(name="simple_table")
public class SimpleTableEntity implements Base<SimpleTableEntity> {

	@Id
	@Column(name="simple_key")
	private String simpleKey;
	
	@Column(name="simple_column")
	private String simpleColumn;
	
	@Column(name="simple_date")
	private Date simpleDate;
	
	@Column(name="simple_timestamp")
	private Timestamp simplesimpleTimestamp;
	
	
	
	public SimpleTableEntity() {
	}



	public String getSimpleKey() {
		return simpleKey;
	}



	public void setSimpleKey(String simpleKey) {
		this.simpleKey = simpleKey;
	}



	public String getSimpleColumn() {
		return simpleColumn;
	}



	public void setSimpleColumn(String simpleColumn) {
		this.simpleColumn = simpleColumn;
	}



	public Date getSimpleDate() {
		return simpleDate;
	}



	public void setSimpleDate(Date simpleDate) {
		this.simpleDate = simpleDate;
	}



	public Timestamp getSimplesimpleTimestamp() {
		return simplesimpleTimestamp;
	}



	public void setSimplesimpleTimestamp(Timestamp simplesimpleTimestamp) {
		this.simplesimpleTimestamp = simplesimpleTimestamp;
	}

}
