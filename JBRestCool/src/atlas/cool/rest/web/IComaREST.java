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
	public abstract List<CrViewRuninfo> listRuns(
			@PathParam("runstart") BigDecimal runstart,
			@PathParam("runend") BigDecimal runend);

	/**
	 * @param state
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{state}/gtagstate")
	public abstract List<ComaCbGtagStates> listGtagStates(
			@PathParam("state") String state);

	
}