package carve.webapp;

import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceProvider;

/**
 * Locates services (ServiceProvider) in Zookeeper.
 * Helper class to ease the use of Curator/Zookeeper
 */
public class CuratorServiceLocator {
    private static ServiceDiscovery<Object> serviceDiscovery;
    private static Map<String, ServiceProvider<Object>> serviceProviders = new HashMap<>();

    static {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
                "localhost:2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(Object.class).basePath("carve")
                .client(curatorFramework).build();
        try {
            serviceDiscovery.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a {@link ServiceProvider} for a given service name, which can then be used to get an instance
     * of this service.
     * <pre>
     * {@code
     * ServiceProvider<Object> serviceProvider = CuratorServiceLocator.getServiceProvider("greeting");
     * ServiceInstance<Object> serviceInstance = serviceProvider.getInstance();
     * }
     * </pre>
     * @param serviceName
     * @return
     * @throws Exception
     */
    public synchronized static ServiceProvider<Object> getServiceProvider(String serviceName) throws Exception {
        ServiceProvider<Object> serviceProvider = serviceProviders.get(serviceName);
        if (serviceProvider == null) {
            serviceProvider = serviceDiscovery.serviceProviderBuilder()
                    .serviceName(serviceName).build();
            serviceProvider.start();
            serviceProviders.put(serviceName, serviceProvider);
        }
        return serviceProvider;
    }

}
