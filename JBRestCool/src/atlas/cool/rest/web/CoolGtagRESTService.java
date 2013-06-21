package atlas.cool.rest.web;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeType;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/coolgtag")
@RequestScoped
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
	@Produces("application/xml")
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
	@Produces("application/xml")
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
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/{since}/{until}/{timespan}/{type}/summary")
	public String listIovsSummaryInNodesSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("gtag") final String gtag,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan,
			@PathParam("type") final String type) {

		return super.listIovsSummaryInNodesSchema(schema, db, gtag, since, until,
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
	@Produces("text/ascii")
	@Path("/{schema}/{db}/{gtag}/iovsummary/gpl")
	public String listIovsSummaryInNodesSchemaGpl(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		return super.listIovsSummaryInNodesSchemaGpl(schema, db, gtag);
	}

}
