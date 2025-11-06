/*
 * Copyright (c) 2022 - 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.laz.casual.test.data.SimpleObject;
import se.laz.casual.test.service.remote.SimpleObjectTestService;

import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Path("/simple")
public class SimpleService
{
    private static final Logger LOGGER = Logger.getLogger( SimpleService.class.getName());
    private SimpleObjectTestService simpleObjectTestService;

    public SimpleService()
    {
        // NOP ctor needed for CDI
    }

    @Inject
    public SimpleService( SimpleObjectTestService simpleObjectTestService )
    {
        this.simpleObjectTestService = simpleObjectTestService;
    }

    @POST
    @Produces( MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("echoFielded")
    public Response serviceRequest( SimpleObject echoMe )
    {
        try
        {
            LOGGER.info( "Received: " + echoMe );
            SimpleObject reply = simpleObjectTestService.echoFielded( echoMe );
            LOGGER.info( "Replied: " + reply );
            return Response.ok( reply ).build();
        }
        catch (Exception e)
        {
            LOGGER.log( Level.SEVERE, e, ()-> "Failed to echo." );
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}
