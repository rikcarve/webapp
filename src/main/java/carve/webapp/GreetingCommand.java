package carve.webapp;

import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;

public class GreetingCommand extends AbstractRestCommand<String> {

    @Inject
    @CuratorServiceProvider("greeting")
    private ServiceProvider<Object> serviceProvider;

    public GreetingCommand() {
        super("greeting");
    }

    @Override
    protected String run() throws Exception {
        Client client = createRestClient(3000);
        ServiceInstance<Object> serviceInstance = serviceProvider.getInstance();
        try {
            return client.target(serviceInstance.buildUriSpec() + "/carve.greeting/v1/greeting/")
                    .request()
                    .get(String.class);
        } catch (ProcessingException e) {
            serviceProvider.noteError(serviceInstance);
            throw e;
        }
    }

    @Override
    protected String getFallback() {
        return "Fallback: hello";
    }
}
