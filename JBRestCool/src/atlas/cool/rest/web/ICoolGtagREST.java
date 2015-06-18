/**
 * 
 */
package atlas.cool.rest.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.rest.model.GtagTagDiffType;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.summary.model.CoolCoverage;
import atlas.cool.summary.model.D3TreeMap;

/**
 * @author formica
 * 
 */
public interface ICoolGtagREST {

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{gtag}/iovsummary/list")
	List<NodeType> listIovsSummaryInNodesSchemaAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("gtag") String gtag);

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{gtag}/list")
	List<GtagType> listGlobalTagsInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("gtag") String gtag);

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @param type
	 * @return
	 */
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/{type}/summary")
	String listIovsSummaryInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag, @PathParam("type") String type);

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @param since
	 * @param until
	 * @param timespan
	 * @param type
	 * @return
	 */
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/{since}/{until}/{timespan}/{type}/summary") 
	String listIovsSummaryInNodesSchemaBetween(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag, @PathParam("since") String since,
			@PathParam("until") String until, @PathParam("timespan") String timespan,
			@PathParam("type") String type);

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 */
	@GET
	@Produces("text/ascii")
	@Path("/{schema}/{db}/{gtag}/iovsummary/gpl") 
	String listIovsSummaryInNodesSchemaGpl(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag);

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/schemasummary") 
	D3TreeMap listSchemaSummaryInSchemaDb(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag);

	/**
	 * @param gtag
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/{gtag}/coverage") 
	CoolCoverage findCoverage(@PathParam("gtag") String gtag);
	
	/**
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag1}/{gtag2}/diff") 
	List<GtagTagDiffType> listGtagDifferences(@PathParam("schema") String schema, 
			@PathParam("db") String db,
			@PathParam("gtag1") String gtag1, @PathParam("gtag2") String gtag2);
}