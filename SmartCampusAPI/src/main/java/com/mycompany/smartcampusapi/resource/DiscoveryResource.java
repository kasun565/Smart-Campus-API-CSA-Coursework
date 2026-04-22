package com.mycompany.smartcampusapi.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1")
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        Map<String, Object> response = new HashMap<>();
        
        // Version info
        Map<String, String> version = new HashMap<>();
        version.put("version", "1.0.0");
        version.put("api", "Smart Campus API");
        version.put("status", "active");
        response.put("version", version);
        
        // Contact info
        Map<String, String> contact = new HashMap<>();
        contact.put("name", "Smart Campus Support Team");
        contact.put("email", "campus-support@university.edu");
        contact.put("department", "Facilities Management");
        response.put("contact", contact);
        
        // Resources (HATEOAS links)
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "http://localhost:8080/api/v1/rooms");
        resources.put("sensors", "http://localhost:8080/api/v1/sensors");
        response.put("resources", resources);
        
        // Documentation links
        Map<String, String> links = new HashMap<>();
        links.put("self", "http://localhost:8080/api/v1");
        links.put("documentation", "https://github.com/smart-campus/api-docs");
        response.put("links", links);
        
        // Message
        response.put("message", "Welcome to Smart Campus API");
        response.put("basePath", "/api/v1");
        
        return Response.ok(response).build();
    }
}