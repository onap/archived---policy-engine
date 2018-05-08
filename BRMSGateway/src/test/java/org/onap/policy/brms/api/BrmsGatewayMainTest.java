package org.onap.policy.brms.api;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.policy.api.PolicyException;

public class BrmsGatewayMainTest {

    @Test
    public void testTooManyArguments() {
        try {
            String[] args = {"aaa", "bbb"};
            BrmsGateway.main(args);
            fail("test should throw an exception");
        }
        catch (PolicyException e) {
            assertEquals("usage: org.onap.policy.brms.api.BrmsGateway [configFile]", e.getMessage());
        }
        catch (Exception e) {
            fail("test should throw a PolicyException");
        }

        try {
            String[] args = {"aaa"};
            BrmsGateway.main(args);
            fail("test should throw an exception");
        }
        catch (PolicyException e) {
            assertEquals("Check your property file: PE300 - Data Issue: "
                            + "Config File doesn't Exist in the specified Path aaa", e.getMessage());
        }
        catch (Exception e) {
            fail("test should throw a PolicyException");
        }
    }

}
