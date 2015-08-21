package carve.webapp;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * Abstract base class for all "resilient" commands / calls.
 * Uses Curator and Hystrix underneath.
 *
 * @param <T>
 */
public abstract class AbstractHystrixCommand<T> extends HystrixCommand<T> {

    public AbstractHystrixCommand(String serviceName) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withCircuitBreakerRequestVolumeThreshold(5)));
        System.out.println("New Command: " + this);
    }

}
