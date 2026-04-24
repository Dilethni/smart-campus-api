package com.smartcampus;

import com.smartcampus.filter.LoggingFilter;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import com.smartcampus.exception.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) throws Exception {
        final ResourceConfig rc = new ResourceConfig()

                .register(DiscoveryResource.class)
                .register(RoomResource.class)
                .register(SensorResource.class)

                .register(RoomNotEmptyExceptionMapper.class)
                .register(LinkedResourceNotFoundExceptionMapper.class)
                .register(SensorUnavailableExceptionMapper.class)
                .register(GlobalExceptionMapper.class)

                .register(LoggingFilter.class)

                .register(JacksonJsonProvider.class);

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), rc);

        System.out.println("Smart Campus API running at " + BASE_URI);
        System.out.println("Press ENTER to stop...");
        System.in.read();
        server.stop();
    }
}
