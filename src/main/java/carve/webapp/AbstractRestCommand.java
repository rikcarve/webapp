package carve.webapp;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * Abstarct base class for all "resilient" commands / calls.
 * Uses Curator and Hystrix underneath.
 * @author tkdak
 *
 * @param <T>
 */
public abstract class AbstractRestCommand<T> extends HystrixCommand<T> {

    private String serviceName;
    private ServiceInstance<Object> serviceInstance;
    private ServiceProvider<Object> serviceProvider;

    public AbstractRestCommand(String serviceName) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionTimeoutInMilliseconds(3000)
                        .withCircuitBreakerRequestVolumeThreshold(5)));
        this.serviceName = serviceName;
    }

    protected Client getClient(int timeoutMs) {
        Client client = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();
        return client;
    }

    protected String getUri(String path) throws Exception {
        serviceProvider = CuratorServiceLocator.getServiceProvider(serviceName);
        serviceInstance = serviceProvider.getInstance();
        String baseUri = serviceInstance.buildUriSpec();
        System.out.println("BaseUri: " + baseUri);
        return baseUri + path;
    }

    protected void noteError() {
        serviceProvider.noteError(serviceInstance);
    }
}
