package com.mycompany.smartcampusapi.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.severe("Unhandled exception: " + exception.getMessage());
        exception.printStackTrace();
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "status", 500,
                "message", "An unexpected error occurred"
            ))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}