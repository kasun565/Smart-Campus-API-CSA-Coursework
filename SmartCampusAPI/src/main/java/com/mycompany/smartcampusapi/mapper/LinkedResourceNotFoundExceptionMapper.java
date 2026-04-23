package com.mycompany.smartcampusapi.mapper;

import com.mycompany.smartcampusapi.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        return Response.status(422)
            .entity(Map.of(
                "error", "UNPROCESSABLE_ENTITY",
                "status", 422,
                "message", exception.getMessage()
            ))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}