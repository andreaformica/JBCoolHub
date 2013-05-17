/**
 * 
 */
package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
import atlas.cool.dao.CoolPayloadDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table. It is the same interface as @See CoolResourceRESTService but the output is in JSON.
 * 
 * <p>
 * The base URL used by the following methods starts with
 * </p>
 * <p>
 * <b>URL: https://hostname:port/JBRestCool/rest/plsqlcool/</b>
 * </p>
 * <p>
 * Hostname: voatlas135
 * </p>
 * <p>
 * Port: 8443 [8080]
 * </p>
 * <p>
 * The protocol used is https for the moment
 * </p>
 */
@Path("/plsqlcooljson")
@RequestScoped
public class CoolResourceRESTJsonService {

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolPayloadDAO payloaddao;
	@Inject
	private CoolUtilsDAO coolutilsdao;

	@Inject
	private Logger log;

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
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {

		log.info("Calling listNodesInSchema..." + schema + " " + db);
		List<NodeType> results = null;
		try {
			results = coolutilsdao.listNodesInSchema(schema, db);
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

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
	@Path("/{schema}/{db}/{node}/tags")
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node) {

		List<SchemaNodeTagType> results = null;
		try {
			results = coolutilsdao.listTagsInNodesSchema(schema, db, node);
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
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
			@PathParam("gtag") String gtag) {

		log.info("Calling listGlobalTagsTagsInNodesSchema..." + schema + " "
				+ db);
		List<NodeGtagTagType> results = null;
		try {
			results = cooldao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db,
					"%" + gtag + "%");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * <p>
	 * Method : /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs per channel.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @return The XML list of iovs per channel.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan")
	public List<IovType> getIovStatPerChannel(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag) {

		log.info("Calling getIovStatPerChannel..." + schema + " " + db + " "
				+ fld + " " + tag);
		List<IovType> results = null;
		try {
			results = coolutilsdao.getIovStatPerChannel(schema, db, fld, tag);
		} catch (CoolIOException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary
	 * /list
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs in a given range per channel.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param since
	 *            The COOL since time.
	 * @param until
	 *            The COOL until time.
	 * @return An HTML page of summary for every channel.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/run/rangesummary/list")
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRunRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since, @PathParam("until") String until) {

		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..."
				+ schema + " " + db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;
		try {
			summarylist = coolutilsdao
					.listIovsSummaryInNodesSchemaTagRunRangeAsList(schema, db,
							fld, tag, since, until);

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summarylist;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/channel/{since
	 * }/{until}/runlb/iovs/list
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs in a given range per channel.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param channel
	 *            The channel name.
	 * @param since
	 *            The COOL since time as a string run-lb.
	 * @param until
	 *            The COOL until time as a string run-lb.
	 * @return An XML file with iovs for selected channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/channel/{since}/{until}/runlb/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("since") String since, @PathParam("until") String until) {

		NodeType selnode = null;
		try {
			selnode = coolutilsdao.listIovsInNodesSchemaTagRangeAsList(schema,
					db, fld, tag, channel, since, until);
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/runlb/iovs/list
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs in a given range per channel.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param since
	 *            The COOL since time as a string run-lb.
	 * @param until
	 *            The COOL until time as a string run-lb.
	 * @return An XML file with iovs for all channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/runlb/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since, @PathParam("until") String until) {

		NodeType selnode = null;
		try {
			selnode = coolutilsdao.listIovsInNodesSchemaTagRangeAsList(schema,
					db, fld, tag, "all", since, until);
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/channel/{since
	 * }/{until}/time/data/list
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs in a given range per channel.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param channel
	 *            The channel name.
	 * @param since
	 *            The COOL since time as a string run-lb.
	 * @param until
	 *            The COOL until time as a string run-lb.
	 * @return An XML file with iovs for selected channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/channel/{since}/{until}/time/data/list")
	public NodeType listPayloadInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("since") BigDecimal since,
			@PathParam("until") BigDecimal until) {
		NodeType selnode = null;
		try {
			selnode = coolutilsdao.listPayloadInNodesSchemaTagRangeAsList(
					schema, db, fld, tag, channel, since, until);
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary
	 * /list
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs in a given range per channel.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param since
	 *            The COOL since time.
	 * @param until
	 *            The COOL until time.
	 * @return An HTML page of summary for every channel.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary/list")
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") BigDecimal since,
			@PathParam("until") BigDecimal until) {

		Collection<CoolIovSummary> summarylist = null;
		try {
			summarylist = coolutilsdao
					.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
							fld, tag, since, until);

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summarylist;
	}


}
