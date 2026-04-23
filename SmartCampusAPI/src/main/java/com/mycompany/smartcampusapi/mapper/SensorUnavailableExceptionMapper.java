package com.mycompany.smartcampusapi.mapper;

import com.mycompany.smartcampusapi.exception.SensorUnavailableException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        return Response.status(Response.Status.FORBIDDEN)
            .entity(Map.of(
                "error", "FORBIDDEN",
                "status", 403,
                "message", exception.getMessage()
            ))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}