/**
 * 
 */
package atlas.cool.rest.web;


import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.coma.model.CrViewRuninfo;


/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the COMA
 * tables.
 * 
 * <p>
 * The base URL used by the following methods starts with
 * </p>
 * <p>
 * <b>URL: https://hostname:port/JBRestCool/rest/coma/</b>
 * </p>
 * <p>
 * Hostname: voatlas135
 * </p>
 * <p>
 * Port: 8443 [8080]
 * </p>
 * <p>
 * The protocol used is https for the moment
 * </p>
 * 
 * @author formica
 */
@Path("/coma")
@RequestScoped
public class ComaRESTService extends ComaRESTImpl implements IComaREST {

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.ComaRESTImpl#listRuns(java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{runstart}/{runend}/runs")
	public List<CrViewRuninfo> listRuns(@PathParam("runstart") BigDecimal runstart, @PathParam("runend") BigDecimal runend) {
		// TODO Auto-generated method stub
		return super.listRuns(runstart, runend);
	}


}