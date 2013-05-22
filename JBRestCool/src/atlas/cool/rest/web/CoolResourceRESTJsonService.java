/**
 * 
 */
package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolQueryException;
import atlas.cool.meta.CoolIov;
import atlas.cool.query.tools.QueryTools;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.CoolIovType;
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
 * <b>URL: https://hostname:port/JBRestCool/rest/plsqlcooljson/</b>
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
	private CoolUtilsDAO coolutilsdao;

	@Inject
	private Logger log;

	private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
	
	protected void setSort(String orderByName) {
		atlas.cool.interceptors.WebRestContextHolder.put("OrderBy", orderByName);	
	}

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
	@Path("/{schema}/{db}/{node:.*}/tags")
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
	 * @return A JSON list of iovs per channel.
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
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/run/rangesummary/list
	 * /list
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs in a given range per channel. The time is given
	 * as run number, without the lbs...since we are interested in summary we do not provide here
	 * a better time resolution for selecting the time range.
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
	 *            The COOL since run.
	 * @param until
	 *            The COOL until run.
	 * @return  A JSON page of summary for every channel.
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
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list
	 * </p>
	 * <p>
	 * It retrieves iovs in a given range for every channel. The date format is a number
	 * representing a date: yyyyMMddhhmmss ; it does not take fractions of seconds.
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
	 *            The channel selection, either an ID or a channel name, or <b>all</b> for selecting every channel.
	 * @param chansel
	 * 			  The channel selector: can be either <b>channel</b> or <b>chanid</b> 
	 * @param since
	 *            The COOL since time as a string following the format given by timespan.
	 * @param until
	 *            The COOL until time as a string following the format given by timespan.
	 * @param timespan
	 *            The COOL date format to be used: time, date, runlb.
	 *            
	 * @return A JSON file with iovs for all channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagDateTimeRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since,
			@PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		NodeType selnode = null;
		try {
		
			// Time selection
			BigDecimal _since = null;
			BigDecimal _until = null;
			if (timespan.equals("time")) {
				// Interpret field as BigDecimal
				_since = new BigDecimal(since);
				_until = new BigDecimal(until);
			} else if (timespan.equals("date")) {
				// Interpret fields as dates in the yyyyMMddhhmmss format
				Date st = df.parse(since);
				Date ut = df.parse(until);
				_since = new BigDecimal(st.getTime()*CoolIov.TO_NANOSECONDS);
				_until = new BigDecimal(ut.getTime()*CoolIov.TO_NANOSECONDS);
			} else if (timespan.equals("runlb")) {
				String[] sinceargs = since.split("-");
				String[] untilargs = until.split("-");

				String lbstr = null;
				if (sinceargs.length > 0 && !sinceargs[1].isEmpty()) {
					lbstr = sinceargs[1];
				}
				_since = CoolIov.getCoolRunLumi(sinceargs[0], lbstr);

				lbstr = null;
				if (untilargs.length > 0 && !untilargs[1].isEmpty()) {
					lbstr = untilargs[1];
				}
				_until = CoolIov.getCoolRunLumi(untilargs[0], lbstr);
			}
			
			
			String chan = channel;
			// Channel Selection
			if (chansel.equals("chanid")) {
				// Treat the channel in input as a Long
				Long chanid = new Long(channel);
				selnode = coolutilsdao.listIovsInNodesSchemaTagRangeAsList(
						schema, db, fld, tag, chanid, _since, _until);

			} else if (chansel.equals("channel")) {
				selnode = coolutilsdao.listIovsInNodesSchemaTagRangeAsList(
						schema, db, fld, tag, chan, _since, _until);
			} else {
				throw new CoolIOException("Wrong REST syntax...refer to documentation");
			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{since}/{until}/runlb/iovs/list
	 * </p>
	 * <p>
	 * It retrieves iovs in a given range for every channel, and enabling ad hoc sorting mode.
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
	 * @param sort
	 * 			  The sort list in format name1-mode/name2-mode/ ... , where mode is either ASC or DESC.	
	 * @param since
	 *            The COOL since time as a string run-lb.
	 * @param until
	 *            The COOL until time as a string run-lb.
	 * @return An XML file with iovs for all channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("sort") String sort,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since,
			@PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		NodeType selnode = null;
		try {
			String orderByName = "";
			String[] sortcolumns = sort.split("/");
			if (sortcolumns.length>=0) {
				Map<String, Boolean> colmap = new LinkedHashMap<String,Boolean>();
				for (int i=0; i<sortcolumns.length;i++) {
					String[] colsort = sortcolumns[i].split("-");
					String colname = colsort[0];
					String sortkey = (colsort[1] != null) ? colsort[1] : "ASC";
					Boolean sortflag = true;
					if (sortkey.equals("DESC"))
						sortflag = false;
					colmap.put(colname, sortflag);
				}
				orderByName = QueryTools.getOrderedBy(new CoolIovType(),colmap);
			}
			setSort(orderByName);
			selnode = listIovsInNodesSchemaTagDateTimeRangeAsList(schema, db, fld, tag, channel, chansel, since, until, timespan);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/data/list
	 * </p>
	 * <p>
	 * It retrieves a list of iovs with their payload in a given range per every channel.
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
	 *            The channel selection, either an ID or a channel name, or <b>all</b> for selecting every channel.
	 * @param chansel
	 * 			  The channel selector: can be either <b>channel</b> or <b>chanid</b> 
	 * @param since
	 *            The COOL since time as a string following the format given by timespan.
	 * @param until
	 *            The COOL until time as a string following the format given by timespan.
	 * @param timespan
	 *            The COOL date format to be used: time, date, runlb.
	 *            
	 * @return A JSON file with payloads for selected channels in a given range.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public NodeType listPayloadInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since,
			@PathParam("until") String until,
			@PathParam("timespan") String timespan) {
		NodeType selnode = null;
		try {
			// Time selection
			BigDecimal _since = null;
			BigDecimal _until = null;
			if (timespan.equals("time")) {
				// Interpret field as BigDecimal
				_since = new BigDecimal(since);
				_until = new BigDecimal(until);
			} else if (timespan.equals("date")) {
				// Interpret fields as dates in the yyyyMMddhhmmss format
				Date st = df.parse(since);
				Date ut = df.parse(until);
				_since = new BigDecimal(st.getTime()*CoolIov.TO_NANOSECONDS);
				_until = new BigDecimal(ut.getTime()*CoolIov.TO_NANOSECONDS);
			} else if (timespan.equals("runlb")) {
				String[] sinceargs = since.split("-");
				String[] untilargs = until.split("-");

				String lbstr = null;
				if (sinceargs.length > 0 && !sinceargs[1].isEmpty()) {
					lbstr = sinceargs[1];
				}
				_since = CoolIov.getCoolRunLumi(sinceargs[0], lbstr);

				lbstr = null;
				if (untilargs.length > 0 && !untilargs[1].isEmpty()) {
					lbstr = untilargs[1];
				}
				_until = CoolIov.getCoolRunLumi(untilargs[0], lbstr);
			}
			
			
			String chan = channel;
			// Channel Selection
			if (chansel.equals("chanid")) {
				// Treat the channel in input as a Long
				Long chanid = new Long(channel);
				selnode = coolutilsdao.listPayloadInNodesSchemaTagRangeAsList(
						schema, db, fld, tag, chanid, _since, _until);

			} else if (chansel.equals("channel")) {
				selnode = coolutilsdao.listPayloadInNodesSchemaTagRangeAsList(
						schema, db, fld, tag, chan, _since, _until);
			} else {
				throw new CoolIOException("Wrong REST syntax...refer to documentation");
			}
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
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
	 * @return A JSON file with iovs for selected channels.
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
