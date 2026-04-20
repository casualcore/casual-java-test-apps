package se.laz.casual.test;

import se.laz.casual.api.buffer.CasualBuffer;

import java.io.Serial;
import java.util.List;

public class MisbehavingCasualBuffer implements CasualBuffer
{
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String getType()
    {
        throw new MisbehavingCasualException("MisbehavingCasualBuffer::getType()");
    }

    @Override
    public List<byte[]> getBytes()
    {
        throw new MisbehavingCasualException("MisbehavingCasualBuffer::getBytes()");
    }
}
