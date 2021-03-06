package atlas.cool.rest.web.xml;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import atlas.cool.rest.impl.CoolGtagRESTImpl;
import atlas.cool.rest.model.GtagTagDiffType;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.web.ICoolGtagREST;
import atlas.cool.summary.model.CoolCoverage;
import atlas.cool.summary.model.D3TreeMap;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/coolgtag")
@RequestScoped
@Api(value="/coolgtag")
public class CoolGtagRESTService extends CoolGtagRESTImpl implements ICoolGtagREST {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listIovsSummaryInNodesSchemaAsList(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}/{gtag}/iovsummary/list")
	public List<NodeType> listIovsSummaryInNodesSchemaAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {

		return super.listIovsSummaryInNodesSchemaAsList(schema, db, gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolGtagRESTImpl#listGlobalTagsInSchema(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
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
	 * atlas.cool.rest.web.ICoolGtagREST#listIovsSummaryInNodesSchema(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@ApiOperation(produces="text/html", value = "Iov summary list in Cherrypy Cool style.")
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/{type}/summary")
	public String listIovsSummaryInNodesSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("gtag") final String gtag,
			@PathParam("type") final String type) {

		return super.listIovsSummaryInNodesSchema(schema, db, gtag, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listIovsSummaryInNodesSchema(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@ApiOperation(produces="text/html", value = "Iov summary list in Cherrypy Cool style.")
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/{since}/{until}/{timespan}/{type}/summary")
	public String listIovsSummaryInNodesSchemaBetween(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("gtag") final String gtag,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan,
			@PathParam("type") final String type) {

		return super.listIovsSummaryInNodesSchemaBetween(schema, db, gtag, since, until,
				timespan, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listIovsSummaryInNodesSchemaGpl(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@ApiOperation(produces="text/ascii", value = "Iov summary list graphic style.")
	@Produces("text/ascii")
	@Path("/{schema}/{db}/{gtag}/iovsummary/gpl")
	public String listIovsSummaryInNodesSchemaGpl(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		return super.listIovsSummaryInNodesSchemaGpl(schema, db, gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolGtagRESTImpl#listSchemaSummaryInSchemaDb(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}/{gtag}/schemasummary")
	public D3TreeMap listSchemaSummaryInSchemaDb(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		// TODO Auto-generated method stub
		return super.listSchemaSummaryInSchemaDb(schema, db, gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.CoolGtagRESTImpl#findCoverage(java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{gtag}/coverage")
	public CoolCoverage findCoverage(@PathParam("gtag") final String gtag) {
		// TODO Auto-generated method stub
		return super.findCoverage(gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolGtagRESTImpl#listGtagDifferences(java.lang.String
	 * , java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}/{gtag1}/{gtag2}/diff")
	public List<GtagTagDiffType> listGtagDifferences(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag1") final String gtag1, @PathParam("gtag2") final String gtag2) {
		// TODO Auto-generated method stub
		return super.listGtagDifferences(schema, db, gtag1, gtag2);
	}

}
