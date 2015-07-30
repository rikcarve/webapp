package carve.webapp;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/hello")
public class HelloResource {

    @Inject
    Instance<GreetingCommand> greetingCommand;

    @GET
    @Path("world")
    @Produces("text/plain")
    public String world() {
        return greetingCommand.get().execute();
    }
}
