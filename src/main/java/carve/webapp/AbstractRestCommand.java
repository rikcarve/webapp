package carve.webapp;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * Abstract base class for all "resilient" commands / calls.
 * Uses Curator and Hystrix underneath.
 * @author tkdak
 *
 * @param <T>
 */
public abstract class AbstractRestCommand<T> extends HystrixCommand<T> {

    public AbstractRestCommand(String serviceName) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withCircuitBreakerRequestVolumeThreshold(5)));
    }

    /**
     * Create a REST {@link Client} with the given socket timeout
     * @param timeoutMs
     * @return
     */
    protected Client createRestClient(int timeoutMs) {
        Client client = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();
        return client;
    }

}
