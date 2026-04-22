package com.mycompany.smartcampusapi;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // This class registers all JAX-RS resources automatically
    // The @ApplicationPath annotation establishes the API's versioned entry point
}