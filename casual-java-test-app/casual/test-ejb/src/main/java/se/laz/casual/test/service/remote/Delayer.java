/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test.service.remote;

import se.laz.casual.api.CasualRuntimeException;

import java.time.Instant;

public class Delayer
{
    private static final EnvTime envTime = EnvTime.of();
    private Delayer()
    {}
    public static Delayer of()
    {
        return new Delayer();
    }
    public void spinUntilDone()
    {
        spinUntilDone(envTime.getWorkTime());
    }
    private void spinUntilDone(long milliseconds)
    {
        Instant now = Instant.now();
        Instant end = now.plusMillis(milliseconds);
        while(now.isBefore(end))
        {
            now = Instant.now();
        }
    }
    public void sleep()
    {
        sleep(envTime.getSleepTime());
    }
    public void sleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new CasualRuntimeException(e);
        }
    }
}
