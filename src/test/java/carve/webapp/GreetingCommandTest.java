package carve.webapp;

import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(CdiRunner.class)
@AdditionalClasses(CuratorServiceLocator.class)
public class GreetingCommandTest {

    @Inject
    Instance<GreetingCommand> commandInstance;

    @Test
    public void testRun() throws Exception {
        assertTrue(!commandInstance.isUnsatisfied());
    }
}