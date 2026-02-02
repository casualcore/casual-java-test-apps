package se.laz.casual.test.service.remote;

import se.laz.casual.api.buffer.CasualBuffer;
import se.laz.casual.api.buffer.type.CStringBuffer;
import se.laz.casual.api.buffer.type.ServiceBuffer;
import se.laz.casual.api.buffer.type.fielded.FieldedTypeBuffer;
import se.laz.casual.api.flags.ErrorState;
import se.laz.casual.api.flags.TransactionState;
import se.laz.casual.jca.inbound.handler.InboundResponse;

public class ErrorResponse
{
    private ErrorResponse()
    {}

    public static InboundResponse create(CasualBuffer buffer, Exception exception)
    {
        if(buffer instanceof CStringBuffer cStringBuffer)
        {
            return cstringReturn(exception.getMessage());
        }
        if(buffer instanceof FieldedTypeBuffer fieldedTypeBuffer)
        {
            return fieldedTypeReturn(exception.getMessage());
        }
        return emptyReturn();
    }

    private static InboundResponse cstringReturn(String message)
    {
        CStringBuffer responseBuffer = CStringBuffer.of(message);
        return createReturn(responseBuffer);
    }

    private static InboundResponse fieldedTypeReturn(String message)
    {
        FieldedTypeBuffer responseBuffer = FieldedTypeBuffer.create();
        responseBuffer.write("FLD_STRING1", message);
        return createReturn(responseBuffer);
    }

    private static InboundResponse emptyReturn()
    {
        return createReturn(ServiceBuffer.empty());
    }

    private static InboundResponse createReturn(CasualBuffer buffer)
    {
        return InboundResponse.createBuilder()
                              .errorState(ErrorState.TPESVCFAIL)
                              .buffer(buffer)
                              .transactionState(TransactionState.ROLLBACK_ONLY)
                              .build();
    }

}
