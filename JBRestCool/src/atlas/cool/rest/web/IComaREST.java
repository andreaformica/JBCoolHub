package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.coma.model.CrViewRuninfo;

public interface IComaREST {

	@GET
	@Produces("application/xml")
	@Path("/{runstart}/{runend}/runs")
	public abstract List<CrViewRuninfo> listRuns(
			@PathParam("runstart") BigDecimal runstart,
			@PathParam("runend") BigDecimal runend);

}