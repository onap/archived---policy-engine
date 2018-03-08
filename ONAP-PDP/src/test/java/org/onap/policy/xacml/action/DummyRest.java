package org.onap.policy.xacml.action;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class DummyRest {
	
	@GET
	@Path("/foobar")
	public String subscribe() {

		return "{\"Foo\":\"bar\"}";
	}
	

}
