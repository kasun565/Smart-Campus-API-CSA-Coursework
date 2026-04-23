package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.model.Sensor;
import com.mycompany.smartcampusapi.model.SensorRoom;
import com.mycompany.smartcampusapi.model.SensorReading;
import com.mycompany.smartcampusapi.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URI;

@Path("/api/v1/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    
    private static final Map<Integer, Sensor> sensors = new ConcurrentHashMap<>();
    private static int idCounter = 100;
    
    static {
        // Add sample sensor
        Sensor sensor1 = new Sensor();
        sensor1.setId(idCounter++);
        sensor1.setName("CO2 Sensor 1");
        sensor1.setType("CO2");
        sensor1.setUnit("ppm");
        sensor1.setRoomId(1);
        sensor1.setStatus(Sensor.Status.ACTIVE);
        sensor1.setCurrentValue("415");
        sensors.put(sensor1.getId(), sensor1);
        
        // Add sensor to room
        SensorRoom room1 = RoomResource.getRooms().get(1);
        if (room1 != null) {
            room1.addSensorId(sensor1.getId());
        }
    }
    
    public static Map<Integer, Sensor> getSensors() {
        return sensors;
    }
    
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(sensors.values());
        
        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
        }
        
        return Response.ok(sensorList).build();
    }
    
    @POST
    public Response createSensor(Sensor sensor) {
        Map<Integer, SensorRoom> rooms = RoomResource.getRooms();
        SensorRoom room = rooms.get(sensor.getRoomId());
        
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                "Room with id " + sensor.getRoomId() + " does not exist."
            );
        }
        
        sensor.setId(idCounter++);
        if (sensor.getStatus() == null) {
            sensor.setStatus(Sensor.Status.ACTIVE);
        }
        if (sensor.getReadings() == null) {
            sensor.setReadings(new ArrayList<>());
        }
        
        sensors.put(sensor.getId(), sensor);
        room.addSensorId(sensor.getId());
        
        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId()))
            .entity(sensor)
            .build();
    }
    
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") int sensorId) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Sensor not found with id: " + sensorId))
                .build();
        }
        return Response.ok(sensor).build();
    }
    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") int sensorId) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found with id: " + sensorId);
        }
        return new SensorReadingResource(sensorId);
    }
}