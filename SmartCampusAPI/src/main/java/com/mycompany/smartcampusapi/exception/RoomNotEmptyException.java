package com.mycompany.smartcampusapi.exception;

public class RoomNotEmptyException extends RuntimeException {
    private final int roomId;
    private final int sensorCount;
    
    public RoomNotEmptyException(int roomId, int sensorCount) {
        super("Room " + roomId + " has " + sensorCount + " active sensors");
        this.roomId = roomId;
        this.sensorCount = sensorCount;
    }
    
    public int getRoomId() { return roomId; }
    public int getSensorCount() { return sensorCount; }
}