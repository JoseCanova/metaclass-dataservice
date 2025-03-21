package org.nanotek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TimestampTimeJsonDeserialization {

	public TimestampTimeJsonDeserialization() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testJacksonTime() throws JsonProcessingException {
	    ObjectMapper objectMapper = new ObjectMapper();
	    Time time = new Time(Instant.now().toEpochMilli());
	    String serializedTime = objectMapper.writeValueAsString(time);
	    System.err.println("Serialized time format ".concat(serializedTime));
	    Time deserializedTime = objectMapper.readValue(serializedTime, Time.class);
	    System.err.println("deserializedTime time  ".concat(deserializedTime.toString()));
	    assertTrue(time.toString().compareTo(deserializedTime.toString())==0);
	}
	
	@Test
	public void testJacksonTimestamp() throws JsonProcessingException {
	    ObjectMapper objectMapper = new ObjectMapper();
	    Timestamp timestamp = new Timestamp(Instant.now().toEpochMilli());
	    String serializedTimestamp = objectMapper.writeValueAsString(timestamp);
	    System.err.println("serializedTimestamp  format ".concat(serializedTimestamp));
	    Timestamp deserializedTimestamp = objectMapper.readValue(serializedTimestamp, Timestamp.class);
	    assertEquals(timestamp, deserializedTimestamp);
	}
}
