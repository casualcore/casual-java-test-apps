package se.laz.casual.test;

import se.laz.casual.api.flags.ErrorState;
import se.laz.casual.api.service.ServiceInfo;
import se.laz.casual.jca.inbound.handler.InboundRequest;
import se.laz.casual.jca.inbound.handler.InboundResponse;
import se.laz.casual.jca.inbound.handler.service.ServiceHandler;
import se.laz.casual.network.messages.domain.TransactionType;
import se.laz.casual.spi.Priority;

import static java.lang.System.Logger.Level.WARNING;

/**
 * Misbehaving service handler for test. Used to verify error handling if a service handler acts in ways it's not
 * supposed to.
 * <p>
 * This class will handle all services prefixed as <code>casual/example/java/misbehaving/</code> and has a few specific names that cause
 * errors in various lifecycles. For used in testing error handling in casual-jca.
 * <ul>
 *     <li><code>casual/example/java/misbehaving/can-handle</code>, throws on canHandleService -> <code>TPENOENT</code></li>
 *     <li><code>casual/example/java/misbehaving/is-available</code>, throws on isServiceAvailable -> <code>TPESYSTEM</code> </li>
 *     <li><code>casual/example/java/misbehaving/discovery</code>, throws on getServiceInfo (discovery) -> <code>TPENOENT</code></li>
 *     <li><code>casual/example/java/misbehaving/crashing-buffer</code>, returns a bad CasualBuffer in a service call that throws an exception if interacted with -> <code>TPESYSTEM</code></li>
 *     <li><code>casual/example/java/misbehaving/*</code>, throws when service is called (default behavior) -> <code>TPESYSTEM</code></li>
 * </ul>
 *
 * Otherwise all attempts to call a service with this handler will result in a thrown exception.
 * <p>
 * All different error states produced by this handler need to be properly handled by casual-jca.
 */
public class MisbehavingServiceHandler implements ServiceHandler
{
    private static final System.Logger logger = System.getLogger(MisbehavingServiceHandler.class.getName());
    private static final String SERVICE_PREFIX = "casual/example/java/misbehaving/";
    private static final String SERVICE_THROWS_ON_CAN_HANDLE = SERVICE_PREFIX + "can-handle";
    private static final String SERVICE_THROWS_ON_IS_AVAILABLE = SERVICE_PREFIX + "is-available";
    private static final String SERVICE_THROWS_ON_INFO = SERVICE_PREFIX + "discovery";
    private static final String SERVICE_RESPONDS_WITH_CRASHING_BUFFER = SERVICE_PREFIX + "crashing-buffer";

    @Override
    public boolean canHandleService(String serviceName)
    {
        if (SERVICE_THROWS_ON_CAN_HANDLE.equals(serviceName))
        {
            throw new MisbehavingCasualException("MisbehavingServiceHandler::canHandleService throws for service '%s'".formatted(serviceName));
        }
        return serviceName.startsWith(SERVICE_PREFIX);
    }

    @Override
    public boolean isServiceAvailable(String serviceName)
    {
        if (SERVICE_THROWS_ON_IS_AVAILABLE.equals(serviceName))
        {
            throw new MisbehavingCasualException("MisbehavingServiceHandler::isServiceAvailable throws for service '%s'".formatted(serviceName));
        }
        return serviceName.startsWith(SERVICE_PREFIX);
    }

    @Override
    public InboundResponse invokeService(InboundRequest request)
    {
        if (SERVICE_RESPONDS_WITH_CRASHING_BUFFER.equals(request.getServiceName()))
        {
            logger.log(WARNING, "Invoking %s".formatted(request.getServiceName()));
            return InboundResponse.createBuilder()
                    .errorState(ErrorState.OK)
                    .userSuppliedErrorCode(Long.MIN_VALUE)
                    .buffer(new MisbehavingCasualBuffer())
                    .build();
        }
        throw new MisbehavingCasualException("MisbehavingServiceHandler::invokeService does no special handling for service '%s'".formatted(request.getServiceName()));
    }

    @Override
    public ServiceInfo getServiceInfo(String serviceName)
    {
        if (SERVICE_THROWS_ON_INFO.equals(serviceName))
        {
            throw new MisbehavingCasualException("MisbehavingServiceHandler::getServiceInfo throws for service '%s'".formatted(serviceName));
        }
        return ServiceInfo.of(serviceName, "java-test-services", TransactionType.NONE);
    }

    @Override
    public Priority getPriority()
    {
        return Priority.LEVEL_4;
    }
}
