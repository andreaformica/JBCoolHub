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

import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.CrViewRuninfo;

/**
 * @author formica
 *
 */
@Path("/comajson")
@RequestScoped
public class ComaRESTJsonService extends ComaRESTImpl implements IComaREST {

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.ComaRESTImpl#listRuns(java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{runstart}/{runend}/runs")
	public List<CrViewRuninfo> listRuns(@PathParam("runstart") final BigDecimal runstart, @PathParam("runend") final BigDecimal runend) {
		// TODO Auto-generated method stub
		return super.listRuns(runstart, runend);
	}

	/**
	 * @param state
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/{state}/gtagstate")
	public List<ComaCbGtagStates> listGtagStates(
			@PathParam("state") final String state) {
		return super.listGtagStates(state);
	}

}
