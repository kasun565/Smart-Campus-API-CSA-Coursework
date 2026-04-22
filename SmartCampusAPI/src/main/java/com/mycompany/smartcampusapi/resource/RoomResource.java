package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.model.SensorRoom;
import com.mycompany.smartcampusapi.exception.RoomNotEmptyException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;

@Path("/api/v1/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    
    private static final Map<Integer, SensorRoom> rooms = new ConcurrentHashMap<>();
    private static int idCounter = 1;
    
    static {
        // Add sample data
        SensorRoom room1 = new SensorRoom();
        room1.setId(idCounter++);
        room1.setName("Engineering Lab 101");
        room1.setBuilding("Engineering");
        room1.setFloor(1);
        room1.setType("Laboratory");
        rooms.put(room1.getId(), room1);
        
        SensorRoom room2 = new SensorRoom();
        room2.setId(idCounter++);
        room2.setName("Lecture Hall A");
        room2.setBuilding("Science");
        room2.setFloor(2);
        room2.setType("Lecture");
        rooms.put(room2.getId(), room2);
    }
    
    public static Map<Integer, SensorRoom> getRooms() {
        return rooms;
    }
    
    @GET
    public Response getAllRooms() {
        List<SensorRoom> roomList = new ArrayList<>(rooms.values());
        return Response.ok(roomList).build();
    }
    
    @POST
    public Response createRoom(SensorRoom room) {
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Room name is required"))
                .build();
        }
        
        room.setId(idCounter++);
        rooms.put(room.getId(), room);
        
        return Response.created(URI.create("/api/v1/rooms/" + room.getId()))
            .entity(room)
            .build();
    }
    
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") int roomId) {
        SensorRoom room = rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Room not found with id: " + roomId))
                .build();
        }
        return Response.ok(room).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") int roomId) {
        SensorRoom room = rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Room not found with id: " + roomId))
                .build();
        }
        
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        }
        
        rooms.remove(roomId);
        return Response.noContent().build();
    }
}