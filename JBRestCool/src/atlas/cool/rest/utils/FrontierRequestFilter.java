/**
 * 
 */
package atlas.cool.rest.utils;


import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

/**
 * @author formica
 *
 */
//@Provider
//@FrontierResponse
public class FrontierRequestFilter implements ContainerRequestFilter {


	@Inject
	private Logger log;

	@Override
	public void filter(ContainerRequestContext crctxt) throws IOException {
		// TODO Auto-generated method stub
		log.info("Filtering method "+crctxt.getMethod());
		log.info("          method url "+crctxt.getUriInfo().getPath());
		//Request req = crctxt.getRequest();
	}
	
}
