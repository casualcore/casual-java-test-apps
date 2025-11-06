package se.laz.casual.test.data;

import se.laz.casual.api.buffer.type.fielded.annotation.CasualFieldElement;

import java.io.Serializable;
import java.util.Objects;

public class SimpleObject implements Serializable
{
    private static final long serialVersionUID = 1L;

    @CasualFieldElement( name = "FLD_LONG1" )
    private Long id;
    @CasualFieldElement( name = "FLD_STRING1" )
    private String name;

    public SimpleObject()
    {

    }

    public SimpleObject( long id, String name )
    {
        this.id = id;
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        SimpleObject that = (SimpleObject) o;
        return id == that.id && Objects.equals( name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, name );
    }

    @Override
    public String toString()
    {
        return "SimpleObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
