package com.savoirtech.hecate.load.web;

import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/hello")
@Service("helloBean")
public class HelloResource {

    @Path("/{id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@PathParam("id") final String id) {
        return "Hello, " + id + "!";
    }
}
