package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.util.List;

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
public interface IComaREST {

	/**
	 * @param runstart
	 * @param runend
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{runstart}/{runend}/runs")
	List<CrViewRuninfo> listRuns(@PathParam("runstart") BigDecimal runstart,
			@PathParam("runend") BigDecimal runend);

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{since}/{until}/{timespan}/runs")
	List<CrViewRuninfo> listRuns(@PathParam("since") String since,
			@PathParam("until") String until, @PathParam("timespan") String timespan);

	/**
	 * @param state
	 * @return List of global tag states.
	 */
	@GET
	@Produces("application/xml")
	@Path("/{state}/gtagstate")
	List<ComaCbGtagStates> listGtagStates(@PathParam("state") String state);

	/**
	 * @param state
	 * @param since
	 * @return List of global tag states in a given range in time.
	 */
	@GET
	@Produces("application/xml")
	@Path("/{state}/{since}/{until}/gtagstate")
	List<ComaCbGtagStates> listGtagStatesAtTime(@PathParam("state") final String state,
			@PathParam("since") final String since);

}