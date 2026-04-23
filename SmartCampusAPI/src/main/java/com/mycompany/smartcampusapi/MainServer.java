package com.mycompany.smartcampusapi;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.mycompany.smartcampusapi.resource.DiscoveryResource;
import com.mycompany.smartcampusapi.resource.RoomResource;
import com.mycompany.smartcampusapi.resource.SensorResource;
import com.mycompany.smartcampusapi.mapper.RoomNotEmptyExceptionMapper;
import com.mycompany.smartcampusapi.mapper.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.smartcampusapi.mapper.SensorUnavailableExceptionMapper;
import com.mycompany.smartcampusapi.mapper.GlobalExceptionMapper;
import com.mycompany.smartcampusapi.filter.LoggingFilter;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {
    
    private static final Logger LOGGER = Logger.getLogger(MainServer.class.getName());
    
    public static final String BASE_URI = "http://localhost:8080/";
    
    public static HttpServer startServer() {
        // Register resources explicitly (no package scanning issues)
        final ResourceConfig rc = new ResourceConfig()
            .register(DiscoveryResource.class)
            .register(RoomResource.class)
            .register(SensorResource.class)
            .register(RoomNotEmptyExceptionMapper.class)
            .register(LinkedResourceNotFoundExceptionMapper.class)
            .register(SensorUnavailableExceptionMapper.class)
            .register(GlobalExceptionMapper.class)
            .register(LoggingFilter.class);
        
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
    
    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            
            System.out.println("\n" +
                "========================================\n" +
                "  Smart Campus API Server Started!\n" +
                "========================================\n" +
                "  Server:      http://localhost:8080\n" +
                "  Discovery:   http://localhost:8080/api/v1\n" +
                "  Rooms:       http://localhost:8080/api/v1/rooms\n" +
                "  Sensors:     http://localhost:8080/api/v1/sensors\n" +
                "========================================\n" +
                "  Press Ctrl+C to stop the server...\n");
            
            Thread.currentThread().join();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Server failed to start", e);
        }
    }
}