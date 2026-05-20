package se.laz.casual.test


import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.core.Response
import se.laz.casual.api.buffer.CasualHeaders
import spock.lang.Shared
import spock.lang.Specification

class HeaderMapperTest extends Specification
{
    @Shared List<String> all = ["a:foo", "b:bar", "c:baz", "c:baa", "type:value"]
    @Shared Map<String,List<String>> v = ["a":["foo"], "b":["bar"], "c": ["baz", "baa"], "type": ["value"]]
    @Shared MultivaluedMap<String,String> httpHeaders = new MultivaluedHashMap<>()

    def setupSpec()
    {
        httpHeaders.putAll( v )
    }

    def "Convert headers filtered."()
    {
        given:
        CasualHeaders expected = CasualHeaders.newBuilder()
                .addAll( result )
                .build(  )

        when:
        CasualHeaders actual = HeaderMapper.applyHeaders( httpHeaders, filter )

        then:
        actual == expected

        where:
        filter      | result
        "all"       | all
        "none"      | []
        "a"         | ["a:foo"]
        "b"         | ["b:bar"]
        "c"         | ["c:baz", "c:baa"]
        "[a-z]"     | ["a:foo", "b:bar", "c:baz", "c:baa"]
        "[a-z]*"    | all
        "typ[a-z]*" | ["type:value"]
        "typ."      | ["type:value"]
        "[0-9]"     | []
    }

    def "Apply all headers to response."()
    {
        given:
        CasualHeaders headers = CasualHeaders.newBuilder().addAll( all ).build()
        Response.ResponseBuilder builder = Response.ok()

        when:
        HeaderMapper.applyHeaders( headers, builder )
        Response actual = builder.build(  )

        then:
        actual.getStringHeaders(  ) == httpHeaders
    }
}
