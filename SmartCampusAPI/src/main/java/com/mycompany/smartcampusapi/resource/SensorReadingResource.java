package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.model.Sensor;
import com.mycompany.smartcampusapi.model.SensorReading;
import com.mycompany.smartcampusapi.exception.SensorUnavailableException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    
    private final int sensorId;
    
    public SensorReadingResource(int sensorId) {
        this.sensorId = sensorId;
    }
    
    @GET
    public Response getReadings() {
        Sensor sensor = SensorResource.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Sensor not found"))
                .build();
        }
        
        List<SensorReading> readings = sensor.getReadings();
        return Response.ok(readings != null ? readings : List.of()).build();
    }
    
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = SensorResource.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Sensor not found"))
                .build();
        }
        
        if (sensor.getStatus() == Sensor.Status.MAINTENANCE) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId + " is in MAINTENANCE mode"
            );
        }
        
        SensorReading newReading = new SensorReading(reading.getValue(), reading.getNote());
        sensor.addReading(newReading);
        sensor.setCurrentValue(reading.getValue());
        
        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}