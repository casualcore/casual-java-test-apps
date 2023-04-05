/*
 * Copyright (c) 2022, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test;

import org.apache.commons.io.IOUtils;
import se.laz.casual.api.buffer.CasualBuffer;
import se.laz.casual.api.buffer.ServiceReturn;
import se.laz.casual.api.buffer.type.OctetBuffer;
import se.laz.casual.api.flags.AtmiFlags;
import se.laz.casual.api.flags.Flag;
import se.laz.casual.api.flags.ServiceReturnState;
import se.laz.casual.connection.caller.CasualCaller;
import se.laz.casual.test.service.remote.ServiceCallFailedException;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
@Stateless
@Path("/casual")
public class CasualService
{
    @Inject
    CasualCaller casualCaller;

    @Resource
    private EJBContext ctx;

    @POST
    @Consumes("application/casual-x-octet")
    @Path("{serviceName}")
    public Response serviceRequest(@PathParam("serviceName") String serviceName, InputStream inputStream)
    {
        try
        {
            byte[] data = IOUtils.toByteArray(inputStream);
            Flag<AtmiFlags> flags = Flag.of(AtmiFlags.NOFLAG);
            OctetBuffer buffer = OctetBuffer.of(data);
            return Response.ok().entity(makeServiceCall(buffer, serviceName, flags).getBytes().get(0)).build();
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            if (!ctx.getRollbackOnly())
            {
                ctx.setRollbackOnly();
            }
            return Response.serverError().entity(sw.toString()).build();
        }
    }

    private CasualBuffer makeServiceCall(CasualBuffer msg, String serviceName, Flag<AtmiFlags> flags)
    {
        ServiceReturn<CasualBuffer> reply = casualCaller.tpcall(serviceName, msg, flags);
        if(reply.getServiceReturnState() == ServiceReturnState.TPSUCCESS)
        {
            return reply.getReplyBuffer();
        }
        throw new ServiceCallFailedException("tpcall failed: " + reply.getErrorState());
    }

}
