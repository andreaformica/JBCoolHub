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

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.ComaRESTImpl#listRuns(java.math.BigDecimal,
	 * java.math.BigDecimal)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{runstart}/{runend}/runs")
	public List<CrViewRuninfo> listRuns(@PathParam("runstart") final BigDecimal runstart,
			@PathParam("runend") final BigDecimal runend) {
		// TODO Auto-generated method stub
		return super.listRuns(runstart, runend);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.ComaRESTImpl#listRuns(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{since}/{until}/{timespan}/runs")
	public List<CrViewRuninfo> listRuns(@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {
		// TODO Auto-generated method stub
		return super.listRuns(since, until, timespan);
	}

	/**
	 * @param state
	 * @return
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{state}/gtagstate")
	public List<ComaCbGtagStates> listGtagStates(@PathParam("state") final String state) {
		return super.listGtagStates(state);
	}

	/**
	 * @param state
	 * @return
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{state}/{since}/gtagstate")
	public List<ComaCbGtagStates> listGtagStatesAtTime(
			@PathParam("state") final String state, @PathParam("since") final String since) {
		return super.listGtagStatesAtTime(state, since);
	}

}
