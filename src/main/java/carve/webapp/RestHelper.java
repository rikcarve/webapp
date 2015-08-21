package carve.webapp;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class RestHelper {
    /**
     * Create a REST {@link Client} with the given socket timeout
     * @param timeoutMs
     * @return
     */
    public static Client createRestClient(int timeoutMs) {
        Client client = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();
        return client;
    }

}
