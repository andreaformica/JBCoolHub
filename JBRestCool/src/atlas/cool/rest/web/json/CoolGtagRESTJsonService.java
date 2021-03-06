/**
 * 
 */
package atlas.cool.rest.web.json;

import io.swagger.annotations.Api;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.rest.impl.CoolGtagRESTImpl;
import atlas.cool.rest.model.GtagTagDiffType;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.utils.FilteredResponse;
import atlas.cool.rest.web.ICoolGtagREST;
import atlas.cool.summary.model.CoolCoverage;
import atlas.cool.summary.model.D3TreeMap;

/**
 * @author formica
 * 
 */
@Path("/coolgtagjson")
@RequestScoped
@FilteredResponse
////@Api(value="coolgtagjson")
public class CoolGtagRESTJsonService extends CoolGtagRESTImpl implements ICoolGtagREST {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolGtagRESTImpl#listGlobalTagsInSchema(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/list")
	public List<GtagType> listGlobalTagsInSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		// TODO Auto-generated method stub
		return super.listGlobalTagsInSchema(schema, db, gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolGtagRESTImpl#listIovsSummaryInNodesSchemaAsList
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/iovsummary/list")
	public List<NodeType> listIovsSummaryInNodesSchemaAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		// TODO Auto-generated method stub
		return super.listIovsSummaryInNodesSchemaAsList(schema, db, gtag);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolGtagRESTImpl#listSchemaSummaryInSchemaDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/schemasummary")
	@FilteredResponse
	public D3TreeMap listSchemaSummaryInSchemaDb(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		// TODO Auto-generated method stub
		return super.listSchemaSummaryInSchemaDb(schema, db, gtag);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolGtagRESTImpl#findCoverage(java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{gtag}/coverage")
	public CoolCoverage findCoverage(@PathParam("gtag") String gtag) {
		// TODO Auto-generated method stub
		return super.findCoverage(gtag);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolGtagRESTImpl#listGtagDifferences(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag1}/{gtag2}/diff")
	public List<GtagTagDiffType> listGtagDifferences(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("gtag1") String gtag1,
			@PathParam("gtag2") String gtag2) {
		// TODO Auto-generated method stub
		return super.listGtagDifferences(schema, db, gtag1, gtag2);
	}
	
}
