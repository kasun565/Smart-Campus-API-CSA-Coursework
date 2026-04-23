package com.mycompany.smartcampusapi.model;

import java.util.ArrayList;
import java.util.List;

public class Sensor {
    public enum Status {
        ACTIVE, INACTIVE, MAINTENANCE
    }
    
    private int id;
    private String name;
    private String type;
    private String unit;
    private Status status;
    private int roomId;
    private String currentValue;
    private List<SensorReading> readings;
    
    public Sensor() {
        this.readings = new ArrayList<>();
        this.status = Status.ACTIVE;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public String getCurrentValue() { return currentValue; }
    public void setCurrentValue(String currentValue) { this.currentValue = currentValue; }
    
    public List<SensorReading> getReadings() { return readings; }
    public void setReadings(List<SensorReading> readings) { this.readings = readings; }
    
    public void addReading(SensorReading reading) {
        if (this.readings == null) {
            this.readings = new ArrayList<>();
        }
        this.readings.add(reading);
    }
}