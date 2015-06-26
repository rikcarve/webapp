package carve.webapp;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class GreetingCommand extends HystrixCommand<String> {

    private static ServiceProvider<Object> serviceProvider;

    private ServiceInstance<Object> serviceInstance;

    public GreetingCommand() {
        super(Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("greeting"))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withCircuitBreakerRequestVolumeThreshold(5)));
    }

    static {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
                "localhost:2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceDiscovery<Object> serviceDiscovery = ServiceDiscoveryBuilder
                .builder(Object.class).basePath("carve")
                .client(curatorFramework).build();
        try {
            serviceDiscovery.start();
            serviceProvider = serviceDiscovery.serviceProviderBuilder()
                    .serviceName("greeting").build();
            serviceProvider.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Curator init failed");
        }

    }

    @Override
    protected String run() throws Exception {
        serviceInstance = serviceProvider.getInstance();
        String baseUri = serviceInstance.buildUriSpec();
        System.out.println("BaseUri: " + baseUri);
        Client client = ClientBuilder.newClient();
        String greeting = client
                .target(baseUri + "/carve.greeting/v1/greeting/").request()
                .get(String.class);
        return greeting;
    }

    @Override
    protected String getFallback() {
        serviceProvider.noteError(serviceInstance);
        return "Fallback: hello";
    }
}
