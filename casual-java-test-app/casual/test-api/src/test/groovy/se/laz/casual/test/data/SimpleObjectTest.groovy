package se.laz.casual.test.data

import se.laz.casual.api.buffer.type.fielded.FieldedTypeBuffer
import se.laz.casual.api.buffer.type.fielded.marshalling.FieldedTypeBufferProcessor
import spock.lang.Specification

class SimpleObjectTest extends Specification
{

    Long id = 1L
    String name = "myname"
    SimpleObject instance

    def setup()
    {
        instance = new SimpleObject( id, name )
    }

    def "Marshall too and from fielded."()
    {
        when:
        FieldedTypeBuffer buffer = FieldedTypeBufferProcessor.marshall( instance )

        then:
        buffer != null

        when:
        SimpleObject actual = FieldedTypeBufferProcessor.unmarshall( buffer, SimpleObject.class )

        then:
        actual == instance
    }

    def "Empty object."()
    {
        given:
        SimpleObject object = new SimpleObject()

        when:
        FieldedTypeBuffer buffer = FieldedTypeBufferProcessor.marshall( object )

        then:
        buffer != null

        when:
        SimpleObject actual = FieldedTypeBufferProcessor.unmarshall( buffer, SimpleObject.class )

        then:
        actual == object
    }

    def "Partially Empty object."()
    {
        given:
        SimpleObject object = new SimpleObject()
        object.setId( 2L )

        when:
        FieldedTypeBuffer buffer = FieldedTypeBufferProcessor.marshall( object )

        then:
        buffer != null

        when:
        SimpleObject actual = FieldedTypeBufferProcessor.unmarshall( buffer, SimpleObject.class )

        then:
        actual == object
    }
}
