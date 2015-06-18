package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.coma.model.ComaCbClass;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.CrViewRuninfo;
import atlas.coma.model.NemoRun;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

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
	@Path("/{start}/{end}/{tspan}/nemoruns")
	List<NemoRun> listNemoRuns(@PathParam("start") String start,
			@PathParam("end") String end, @PathParam("tspan") String tspan);

	/**
	 * @param start
	 * @param end
	 * @param timespan: time, date, run
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{start}/{end}/{tspan}/nemotimerange")
	Map<String,Object> getNemoTimeRangeConversion(@PathParam("start") String start,
			@PathParam("end") String end, @PathParam("tspan") String tspan);

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
	 * @param runstart
	 * @param runend
	 * @param rtype
	 * @param period
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{runstart}/{runend}/{rtype}/{period}/runs")
	List<CrViewRuninfo> listRunsBetween(@PathParam("runstart") BigDecimal runstart,
			@PathParam("runend") BigDecimal runend,
			@PathParam("period") String rtype, 
			@PathParam("period") String period);

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{since}/{until}/{timespan}/runsbyiov")
	List<CrViewRuninfo> listRunsByIov(@PathParam("since") String since,
			@PathParam("until") String until, @PathParam("timespan") String timespan);

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	@GET
	@Produces("application/xml")
	@Path("/{since}/{until}/{timespan}/{rtype}/{period}/runsbyiov")
	List<CrViewRuninfo> listRunsByIovBetween(@PathParam("since") String since,
			@PathParam("until") String until, @PathParam("timespan") String timespan, 
			@PathParam("period") String rtype, @PathParam("period") String period);


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

	/**
	 * <p>
	 * Method : /{schema}/{db}/nodes
	 * </p>
	 * <p>
	 * It retrieves a list of nodes in XML format.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/nodes")
	public List<NodeType> listNodesInSchema(
			@PathParam("schema") String schema, @PathParam("db") String db);

	/**
	 * <p>
	 * Method : /{schema}/{db}/{node}/tags
	 * </p>
	 * <p>
	 * It retrieves a list of tags in XML format.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param node
	 *            The node name : a search string like MDT, in this case we do
	 *            not use full folder name
	 * @return An XML list of tags for the given node.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{node:.*}/tags")
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node);

	/**
	 * <p>
	 * Method : /{schema}/{db}/{gtag}/trace
	 * </p>
	 * <p>
	 * It retrieves a list of tags associated to the given global tag in XML
	 * format.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param gtag
	 *            The Cool global tag : COMCOND-BLKPA-006-09
	 * @return An XML list of schemas and folders and tags which are associated
	 *         to the global tag.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/trace")
	public List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag);


	/**
	 * @param schema
	 * @param db
	 * @param tag
	 * 		The leaf tag which you want to backtrace
	 * @return
	 * 		A list of global tags in which the given tag appears
	 * @throws CoolIOException
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{tag}/backtrace")
	List<NodeGtagTagType> retrieveTagGtagsFromSchemaAndDb(
			@PathParam("schema") String schema, 
			@PathParam("db") String db,
			@PathParam("tag") String tag) throws CoolIOException;
	

	/**
	 * @param schema
	 * @param db
	 * @param tag
	 * 		The leaf tag which you want to backtrace
	 * @return
	 * 		A list of global tags in which the given tag appears
	 * @throws CoolIOException
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{node:.*}/classification")
	List<ComaCbClass> getClassificationForSchemaAndNode(
			@PathParam("schema") String schema, 
			@PathParam("node") String node) throws CoolIOException;


}