/**
 * 
 */
package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.dao.CoolIOException;
import atlas.cool.dao.CoolResultSetDAO;
import atlas.cool.rest.model.NodeType;

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
public class ComaRESTService {

	@Inject
	protected ComaCbDAO comadao;

	@Inject
	protected Logger log;

	@GET
	@Produces("application/xml")
	@Path("/{runstart}/{runend}/runs")
	public List<CrViewRuninfo> listRuns(@PathParam("runstart") BigDecimal runstart, @PathParam("runend") BigDecimal runend) {
		log.info("Calling listRuns..." + runstart + " " + runend);
		List<CrViewRuninfo> results = null;
		try {
			results = comadao.findRunsInRange(runstart, runend);
		} catch (ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}


}
