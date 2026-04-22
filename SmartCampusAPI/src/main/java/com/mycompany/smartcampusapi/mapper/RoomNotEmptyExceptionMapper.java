package com.mycompany.smartcampusapi.mapper;

import com.mycompany.smartcampusapi.exception.RoomNotEmptyException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    
    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        return Response.status(Response.Status.CONFLICT)
            .entity(Map.of(
                "error", "CONFLICT",
                "status", 409,
                "message", exception.getMessage(),
                "roomId", exception.getRoomId(),
                "activeSensors", exception.getSensorCount()
            ))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}