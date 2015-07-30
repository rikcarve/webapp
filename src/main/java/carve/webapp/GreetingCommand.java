package carve.webapp;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;

public class GreetingCommand extends AbstractRestCommand<String> {

    public GreetingCommand() {
        super("greeting");
    }

    @Override
    protected String run() throws Exception {
        Client client = getClient(3000);
        try {
            return client.target(getUri("/carve.greeting/v1/greeting/"))
                    .request()
                    .get(String.class);
        } catch (ProcessingException e) {
            noteError();
            throw e;
        }
    }

    @Override
    protected String getFallback() {
        return "Fallback: hello";
    }
}
