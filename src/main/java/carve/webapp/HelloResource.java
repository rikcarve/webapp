package carve.webapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/hello")
public class HelloResource {

    @GET
    @Path("world")
    @Produces("text/plain")
    public String world() {
        return new GreetingCommand().execute();
    }
}
