/*
 * Copyright (c) 2025, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.test.service.remote;

import se.laz.casual.api.buffer.type.fielded.FieldedTypeBuffer;
import se.laz.casual.api.buffer.type.fielded.marshalling.FieldedTypeBufferProcessor;
import se.laz.casual.test.data.SimpleObject;

import java.util.logging.Logger;

public class SimpleObjectTestServiceImpl implements SimpleObjectTestService
{
    private static final Logger LOGGER = Logger.getLogger( SimpleObjectTestServiceImpl.class.getName());
    @Override
    public SimpleObject echoFielded( SimpleObject echoMe )
    {
        FieldedTypeBuffer buffer = FieldedTypeBufferProcessor.marshall( echoMe );
        LOGGER.info( ()-> "buffer: " + buffer.toString() );
        return FieldedTypeBufferProcessor.unmarshall( buffer, SimpleObject.class );
    }
}
