package se.laz.casual.test;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import se.laz.casual.api.buffer.CasualHeaders;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class HeaderMapper
{
    public static final String ALL = "all";
    public static final String NONE = "none";

    private HeaderMapper()
    {
    }

    /**
     * Convert map of http headers into CasualHeaders filtering based on the value of includeHeaders:
     * <br/>
     * - all - include all header values.
     * <br/>
     * - none - include no header values.
     * <br/>
     * All other values are treated as a regular expression value to perform pattern matching against
     * the header name.
     *
     * @param headers the http headers as a map.
     * @param filter the filter to use.
     * @return
     */
    public static CasualHeaders applyHeaders( MultivaluedMap<String,String> headers, final String filter )
    {
        CasualHeaders.Builder builder = CasualHeaders.newBuilder();

        Function<String,Boolean> match = switch( filter )
        {
            case NONE ->
                    (s)->false;
            case ALL ->
                    (s)->true;
            default ->
                    (s) -> Pattern.matches( filter, s );
        };

        for( String name : headers.keySet() )
        {
            if( !match.apply( name ) )
            {
                continue;
            }
            List<String> values = headers.get( name );
            for( String value: values )
            {
                builder.add( name, value );
            }
        }
        return builder.build();
    }

    /**
     * Convert CasualHeaders provided into http headers applied to the response builder provided.
     *
     * @param headers the headers to apply.
     * @param builder the target to apply the headers upon.
     */
    public static void applyHeaders( CasualHeaders headers, Response.ResponseBuilder builder )
    {
        for( String name: headers.getNames() )
        {
            List<String> values = headers.get( name );
            for( String value: values )
            {
                builder.header( name, value );
                break;
            }
        }
    }
}
