package carve.webapp;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.client.Client;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
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

    private String serviceName;
    private ServiceInstance<Object> serviceInstance;
    private ServiceProvider<Object> serviceProvider;

    @Inject
    private CuratorServiceLocator serviceLocator;

    public AbstractRestCommand(String serviceName) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withCircuitBreakerRequestVolumeThreshold(5)));
        this.serviceName = serviceName;
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

    /**
     * Creates the URI string by getting an service instance (baseUri)
     * and adding the given path
     * @param path
     * @return
     * @throws Exception
     */
    protected String createUri(String path) throws Exception {
        serviceProvider = serviceLocator.getServiceProvider(serviceName);
        serviceInstance = serviceProvider.getInstance();
        String baseUri = serviceInstance.buildUriSpec();
        System.out.println("BaseUri: " + baseUri);
        return baseUri + path;
    }

    /**
     * When a service fails (in your catch clause) report it here, so that this service instance
     * will be marked as down.
     */
    protected void noteError() {
        serviceProvider.noteError(serviceInstance);
    }
}
