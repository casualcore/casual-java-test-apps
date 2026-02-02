/*
 * Copyright (c) 2022 - 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test.service.remote;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import se.laz.casual.api.buffer.CasualBuffer;
import se.laz.casual.api.concurrency.Concurrent;
import se.laz.casual.api.flags.AtmiFlags;
import se.laz.casual.api.flags.ErrorState;
import se.laz.casual.api.flags.Flag;
import se.laz.casual.api.service.CasualService;
import se.laz.casual.jca.inbound.handler.InboundRequest;
import se.laz.casual.jca.inbound.handler.InboundResponse;

import jakarta.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Stateless
@Remote(TestService.class)
public class TestServiceImpl implements TestService
{
    public static final String JAVA_FORWARD_ENV_NAME = "JAVA_FORWARD_SERVICE_NAME";
    private static final Delayer delayer = Delayer.of();
    private TpCaller tpCaller;

    @Resource
    ManagedExecutorService executorService;

    // wls
    public TestServiceImpl()
    {}

    @Inject
    public TestServiceImpl(TpCaller tpCaller)
    {
        this.tpCaller = tpCaller;
    }

    @CasualService(name="casual/example/java/executor/forward")
    @Override
    public InboundResponse executorForward(InboundRequest request)
    {
        Callable<InboundResponse> callable = Concurrent.wrap(() -> forward(request));
        var future = executorService.submit(callable);
        try
        {
            return future.get(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e)
        {
            return ErrorResponse.create(request.getBuffer(), e);
        }
    }

    @CasualService(name="casual/example/java/echo")
    @Override
    public InboundResponse casualEcho(InboundRequest buffer)
    {
        return InboundResponse.createBuilder()
                              .buffer( buffer.getBuffer() )
                              .build();
    }

    @CasualService(name="casual/example/java/forward")
    @Override
    public InboundResponse forward(InboundRequest buffer)
    {
        String forwardName = Optional.ofNullable(getForwardNameIfAny()).orElseThrow(() -> new ForwardDefinitionMissingException("env var JAVA_FORWARD_SERVICE_NAME undefined, forward will not work ( does not know where to forward to)"));
        CasualBuffer returnBuffer = tpCaller.makeTpCall(forwardName, buffer.getBuffer(), Flag.of(AtmiFlags.NOFLAG));
        return InboundResponse.createBuilder().buffer( returnBuffer).build();
    }

    @CasualService(name="casual/example/java/commit")
    @Override
    public InboundResponse commit(InboundRequest buffer)
    {
        return InboundResponse.createBuilder()
                              .buffer( buffer.getBuffer() )
                              .build();
    }

    @CasualService(name="casual/example/java/rollback")
    @Override
    public InboundResponse rollback(InboundRequest buffer)
    {
        return InboundResponse.createBuilder()
                              .buffer( buffer.getBuffer() )
                              .errorState(ErrorState.TPESVCFAIL)
                              .build();
    }

    @CasualService(name="casual/example/java/work")
    @Override
    public InboundResponse work(InboundRequest buffer)
    {
        delayer.spinUntilDone();
        return InboundResponse.createBuilder()
                              .buffer( buffer.getBuffer() )
                              .build();
    }

    @CasualService(name="casual/example/java/sleep")
    @Override
    public InboundResponse sleep(InboundRequest buffer)
    {
        delayer.sleep();
        return InboundResponse.createBuilder()
                              .buffer( buffer.getBuffer() )
                              .build();
    }

    /**
     * Note, should be called with TPNORETURN
     * @param buffer - the buffer to be ignored
     */
    @CasualService(name="casual/example/java/sink")
    @Override
    public void sink(InboundRequest buffer)
    {
        // NOP
    }

    private String getForwardNameIfAny()
    {
        return System.getenv(JAVA_FORWARD_ENV_NAME);
    }
}
