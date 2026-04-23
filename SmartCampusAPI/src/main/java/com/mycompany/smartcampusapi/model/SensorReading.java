package com.mycompany.smartcampusapi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SensorReading {
    private String timestamp;
    private String value;
    private String note;
    
    public SensorReading() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    public SensorReading(String value, String note) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.value = value;
        this.note = note;
    }
    
    // Getters and Setters
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}