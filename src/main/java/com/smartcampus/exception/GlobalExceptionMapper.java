package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        // Log the REAL error so we can see it
        logger.severe("Caught exception: " + e.getClass().getName() + " - " + e.getMessage());
        if (e.getCause() != null) {
            logger.severe("Caused by: " + e.getCause().getMessage());
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"Internal Server Error\",\"message\":\"" + e.getMessage() + "\"}")
                .build();
    }
}