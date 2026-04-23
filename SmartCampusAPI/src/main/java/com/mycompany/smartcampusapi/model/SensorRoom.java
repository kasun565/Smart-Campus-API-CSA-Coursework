package com.mycompany.smartcampusapi.model;

import java.util.ArrayList;
import java.util.List;

public class SensorRoom {
    private int id;
    private String name;
    private String building;
    private int floor;
    private String type;
    private List<Integer> sensorIds;
    
    public SensorRoom() {
        this.sensorIds = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<Integer> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<Integer> sensorIds) { this.sensorIds = sensorIds; }
    
    public void addSensorId(int sensorId) {
        if (this.sensorIds == null) {
            this.sensorIds = new ArrayList<>();
        }
        this.sensorIds.add(sensorId);
    }
}