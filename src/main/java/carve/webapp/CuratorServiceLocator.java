package carve.webapp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

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
@Startup
@Singleton
public class CuratorServiceLocator {
    private ServiceDiscovery<Object> serviceDiscovery;
    private Map<String, ServiceProvider<Object>> serviceProviders = new HashMap<>();

    @PostConstruct
    public void initCurator() {
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
        System.out.println("Curator init complete");
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
    public ServiceProvider<Object> myServiceProvider(String serviceName) throws Exception {
        ServiceProvider<Object> serviceProvider = serviceProviders.get(serviceName);
        if (serviceProvider == null) {
            serviceProvider = serviceDiscovery.serviceProviderBuilder()
                    .serviceName(serviceName).build();
            serviceProvider.start();
            serviceProviders.put(serviceName, serviceProvider);
        }
        return serviceProvider;
    }

    @Produces
    public ServiceProvider<Object> getServiceProvider(InjectionPoint ip) throws Exception {
        String serviceName = ip.getAnnotated().getAnnotation(CuratorServiceName.class).value();
        return myServiceProvider(serviceName);
    }
}
