package se.laz.casual.test;

import java.io.Serial;

public class MisbehavingCasualException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public MisbehavingCasualException(String message)
    {
        super(message);
    }
}
