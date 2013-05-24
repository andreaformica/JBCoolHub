package atlas.cool.rest.web;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import oracle.sql.BLOB;
import oracle.sql.CLOB;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.dao.CoolResultSetDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.payload.model.CoolPayload;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.utils.SvgRestUtils;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the Cool tables
 * via PL/SQL.
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
 * Port: 8443
 * </p>
 * <p>
 * The protocol used is https for the moment
 * </p>
 */
@Path("/plsqlcool")
@RequestScoped
public class CoolResourceRESTService extends CoolRESTImpl implements ICoolREST {

	@Inject
	protected CoolResultSetDAO payloaddao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.CoolRESTImpl#setSort(java.lang.String)
	 */
	@Override
	protected void setSort(String orderByName) {
		// TODO Auto-generated method stub
		super.setSort(orderByName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.CoolRESTImpl#listNodesInSchema(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/nodes")
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {
		// TODO Auto-generated method stub
		return super.listNodesInSchema(schema, db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listTagsInNodesSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{node:.*}/tags")
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node) {
		// TODO Auto-generated method stub
		return super.listTagsInNodesSchema(schema, db, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listGlobalTagsTagsInNodesSchema(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{gtag}/trace")
	public List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		// TODO Auto-generated method stub
		return super.listGlobalTagsTagsInNodesSchema(schema, db, gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#getIovStatPerChannel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan")
	public List<IovType> getIovStatPerChannel(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag) {
		// TODO Auto-generated method stub
		return super.getIovStatPerChannel(schema, db, fld, tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listIovsInNodesSchemaTagRangeAsList(
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		return super.listIovsInNodesSchemaTagRangeAsList(schema, db, fld, tag,
				channel, chansel, since, until, timespan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listIovsInNodesSchemaTagRangeSortedAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("sort") String sort,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		return super.listIovsInNodesSchemaTagRangeSortedAsList(schema, db, fld,
				tag, sort, channel, chansel, since, until, timespan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listPayloadInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public NodeType listPayloadInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {
		return super.listPayloadInNodesSchemaTagRangeAsList(schema, db, fld,
				tag, channel, chansel, since, until, timespan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listPayloadInNodesSchemaTagRangeSortedAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public NodeType listPayloadInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("sort") String sort,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		return super.listPayloadInNodesSchemaTagRangeSortedAsList(schema, db,
				fld, tag, sort, channel, chansel, since, until, timespan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listIovsSummaryInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/{timespan}/rangesummary/list")
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		return super.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
				fld, tag, since, until, timespan);
	}

	/**
	 * <p>
	 * Method : /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{time}/{chan}/payload
	 * </p>
	 * <p>
	 * It retrieves the payload inside the iov containing time, for the given
	 * folder and tag in TEXT format.
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
	 * @param time
	 *            A time in COOL format.
	 * @param channel
	 *            A channel number.
	 * @return
	 */
	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{time}/{chan}/payload")
	public String getPayload(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld,
			@PathParam("tag") String tag, @PathParam("time") BigDecimal time,
			@PathParam("chan") Long channel) {
		log.info("Calling getPayload..." + schema + " " + db + " " + fld + " "
				+ channel);
		try {
			Long chid = channel;
			if (channel < 0)
				chid = null;
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			String output = "";
			ResultSet pyld = payloaddao.getPayload(schema, db, node, tag, time,
					chid);
			if (pyld != null) {
				output = dumpResultSet(pyld);
			}
			payloaddao.closeConnection();
			return output;
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// should remove the sfsb
		}
		return "done";
	}

	/**
	 * <p>
	 * Method : /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{stime}/{etime}/{chan}/
	 * payloads
	 * </p>
	 * <p>
	 * It retrieves the payload inside the iov range stime - etime, for the
	 * given folder and tag in TEXT format.
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
	 * @param stime
	 *            A start time in COOL format.
	 * @param etime
	 *            An end time in COOL format.
	 * @param channel
	 *            A channel number.
	 * @return Returns a text file with the payloads.
	 */
	@GET
	@Produces("text/plain")
	// @Path("/payload/{id}")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{stime}/{etime}/{chan}/payloads")
	// public String getPayload(@PathParam("id") PathSegment id) {
	public String getPayloads(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld,
			@PathParam("tag") String tag, @PathParam("stime") BigDecimal stime,
			@PathParam("etime") BigDecimal etime,
			@PathParam("chan") Long channel) {
		/*
		 * MultivaluedMap<String,String> params = id.getMatrixParameters();
		 * String schema = params.get("schema").get(0); String db =
		 * params.get("db").get(0); String fld = params.get("fld").get(0);
		 * String tag = params.get("tag").get(0); BigDecimal time = new
		 * BigDecimal(params.get("time").get(0));
		 */log.info("Calling getPayload..." + schema + " " + db + " " + fld);
		try {
			Long chid = channel;
			if (channel < 0)
				chid = null;
			String fldname = fld.replaceAll("/", "_");
			String output = "";
			ResultSet pyld = payloaddao.getPayloads(schema, db, fld, tag,
					stime, etime, chid);
			if (pyld != null) {
				// output = dumpResultSet(pyld);
				output = dump2FileResultSet(pyld, "/tmp/" + schema + "_"
						+ fldname + "_" + tag);
			}
			return output;
			// payloaddao.remove();
		} catch (CoolIOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// should remove the sfsb
		}
		return "done";
	}

	/**
	 * <p>
	 * Method : /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/holes
	 * </p>
	 * <p>
	 * It retrieves a summary of holes per channel.
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
	 *            The COOL start time.
	 * @param until
	 *            The COOL until time.
	 * @return An XML list of holes.
	 */
	@GET
	@Produces("text/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/holes")
	public List<IovType> getIovHolesPerChannel(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") BigDecimal since,
			@PathParam("until") BigDecimal until) {

		log.info("Calling getIovHolesPerChannel..." + schema + " " + db + " "
				+ fld + " " + tag + " " + since + " " + until);
		List<IovType> results = null;
		try {
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
					db, node);
			NodeType selnode = null;
			if (nodes != null && nodes.size() > 0) {
				for (NodeType anode : nodes) {
					log.info("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			results = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(
							schema, db, node, seltag, since, until);
			for (IovType aniov : results) {
				aniov.setIovBase(selnode.getNodeIovBase());
			}
		} catch (CoolIOException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * <p>
	 * Method : /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsummary
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
	 * @return An HTML page of summary for every channel.
	 */
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsummary")
	public String listIovsSummaryInNodesSchemaTag(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag) {

		log.info("Calling listIovsSummaryInNodesSchemaTag..." + schema + " "
				+ db + " folder " + fld + " tag " + tag);
		StringBuffer results = new StringBuffer();
		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");
		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ tag + " in folder " + fld + "</h1><hr>");

			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
					db, node);
			NodeType selnode = null;
			if (nodes != null && nodes.size() > 0) {
				for (NodeType anode : nodes) {
					log.info("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}

			results.append("<h2>" + colorseptagstart + schema + " > " + " "
					+ selnode.getNodeFullpath() + " ; " + seltag + colortagend
					+ "</h2>" + "<br>");

			results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

			Map<Long, CoolIovSummary> iovsummary = coolutilsdao
					.computeIovSummaryMap(schema, db, node, seltag,
							selnode.getNodeIovBase());
			List<ChannelType> chanList = cooldao
					.retrieveChannelsFromNodeSchemaAndDb(schema, db, node, "%");
			Set<Long> channelList = iovsummary.keySet();

			results.append("<p>Number of channels used " + channelList.size()
					+ " over a total of " + chanList.size());

			for (Long chanid : channelList) {
				CoolIovSummary coolsumm = iovsummary.get(chanid);
				results.append("<p class=\"small\">" + coolsumm.getChanId()
						+ " " + coolsumm.getChannelName() + " - "
						+ coolsumm.getIovbase() + " : ");
				Map<Long, IovRange> timeranges = coolsumm.getIovRanges();
				if (timeranges != null) {
					Set<Long> sincetimes = timeranges.keySet();
					String colortagstart = colorgoodtagstart;
					String iovDump = "";
					long minsince = coolsumm.getMinsince();
					int iiov = 0;
					for (Long asince : sincetimes) {
						IovRange ivr = timeranges.get(asince);
						colortagstart = colorgoodtagstart;
						if ((iiov == 0)
								&& (ivr.getSince().compareTo(minsince) != 0)) {
							colortagstart = colorwarntagstart;
						}
						String holedump = "";
						if (ivr.getIshole()) {
							colortagstart = colorbadtagstart;
							long timespan = ivr.getUntil() - ivr.getSince();
							if (coolsumm.getIovbase().equals("time")) {
								timespan = timespan / 1000L;
							}
							holedump = "[" + timespan + "] ";
						}
						iovDump = colortagstart
								+ ivr.getNiovs()
								+ " ["
								+ CoolIov.getCoolTimeString(ivr.getSince(),
										coolsumm.getIovbase())
								+ "] ["
								+ CoolIov.getCoolTimeString(ivr.getUntil(),
										coolsumm.getIovbase()) + "] "
								+ holedump + colortagend;

						results.append(" | " + iovDump);
						iiov++;
					}
					results.append("</p>");
				}
			}
			results.append("</body>");

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary
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
	@Produces("text/html")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary")
	public String listIovsSummaryInNodesSchemaTagRange(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") BigDecimal since,
			@PathParam("until") BigDecimal until) {

		log.info("Calling listIovsSummaryInNodesSchemaTag..." + schema + " "
				+ db + " folder " + fld + " tag " + tag);
		StringBuffer results = new StringBuffer();
		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");
		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ tag + " in folder " + fld + "</h1><hr>");

			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
					db, node);
			NodeType selnode = null;
			if (nodes != null && nodes.size() > 0) {
				for (NodeType anode : nodes) {
					log.info("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}

			results.append("<h2>" + colorseptagstart + schema + " > " + " "
					+ selnode.getNodeFullpath() + " ; " + seltag + colortagend
					+ "</h2>" + "<br>");

			results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

			Map<Long, CoolIovSummary> iovsummary = coolutilsdao
					.computeIovSummaryRangeMap(schema, db, node, seltag,
							selnode.getNodeIovBase(), since, until);
			List<ChannelType> chanList = cooldao
					.retrieveChannelsFromNodeSchemaAndDb(schema, db, node, "%");
			Set<Long> channelList = iovsummary.keySet();

			results.append("<p>Number of channels used " + channelList.size()
					+ " over a total of " + chanList.size());

			for (Long chanid : channelList) {
				CoolIovSummary coolsumm = iovsummary.get(chanid);
				results.append("<p class=\"small\">" + coolsumm.getChanId()
						+ " " + coolsumm.getChannelName() + " - "
						+ coolsumm.getIovbase() + " : ");
				Map<Long, IovRange> timeranges = coolsumm.getIovRanges();
				if (timeranges != null) {
					Set<Long> sincetimes = timeranges.keySet();
					String colortagstart = colorgoodtagstart;
					String iovDump = "";
					long minsince = coolsumm.getMinsince();
					int iiov = 0;
					for (Long asince : sincetimes) {
						IovRange ivr = timeranges.get(asince);
						colortagstart = colorgoodtagstart;
						if ((iiov == 0)
								&& (ivr.getSince().compareTo(minsince) != 0)) {
							colortagstart = colorwarntagstart;
						}
						String holedump = "";
						if (ivr.getIshole()) {
							colortagstart = colorbadtagstart;
							long timespan = ivr.getUntil() - ivr.getSince();
							if (coolsumm.getIovbase().equals("time")) {
								timespan = timespan / 1000L;
							}
							holedump = "[" + timespan + "] ";
						}
						iovDump = colortagstart
								+ ivr.getNiovs()
								+ " ["
								+ CoolIov.getCoolTimeString(ivr.getSince(),
										coolsumm.getIovbase())
								+ "] ["
								+ CoolIov.getCoolTimeString(ivr.getUntil(),
										coolsumm.getIovbase()) + "] "
								+ holedump + colortagend;

						results.append(" | " + iovDump);
						iiov++;
					}
					results.append("</p>");
				}
			}
			results.append("</body>");

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary/svg
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
	 * @return An html page with svg plot of summary for every channel.
	 */
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary/svg")
	public String listIovsSummaryInNodesSchemaTagRangeSvg(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") BigDecimal since,
			@PathParam("until") BigDecimal until) {

		log.info("Calling listIovsSummaryInNodesSchemaTagRangeSvg..." + schema
				+ " " + db + " folder " + fld + " tag " + tag);

		StringBuffer results = new StringBuffer();
		StringBuffer svg = new StringBuffer();

		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");

		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ tag + " in folder " + fld + "</h1><hr>");

			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
					db, node);
			NodeType selnode = null;
			if (nodes != null && nodes.size() > 0) {
				for (NodeType anode : nodes) {
					log.info("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}

			results.append("<h2>" + colorseptagstart + schema + " > " + " "
					+ selnode.getNodeFullpath() + " ; " + seltag + colortagend
					+ "</h2>" + "<br>");

			results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

			Map<Long, CoolIovSummary> iovsummary = coolutilsdao
					.computeIovSummaryRangeMap(schema, db, node, seltag,
							selnode.getNodeIovBase(), since, until);
			List<ChannelType> chanList = cooldao
					.retrieveChannelsFromNodeSchemaAndDb(schema, db, node, "%");
			Set<Long> channelList = iovsummary.keySet();

			results.append("<p>Number of channels used " + channelList.size()
					+ " over a total of " + chanList.size());

			SvgRestUtils svgutil = new SvgRestUtils();
			svgutil.setSvgabsmin(0L);
			svgutil.setSvgabsmax(CoolIov.COOL_MAX_DATE);
			if (channelList.size() < 20) {
				svgutil.setLinewidth(10);
			} else if (channelList.size() < 100) {
				svgutil.setLinewidth(6);
			} else if (channelList.size() < 200) {
				svgutil.setLinewidth(3);
			} else {
				svgutil.setLinewidth(1);
			}
			results.append("<p>Number of channels used " + channelList.size()
					+ " over a total of " + chanList.size());
			String svgcanvas = "<svg width=\""
					+ svgutil.getSvglinewidth()
					+ "px\" height=\""
					+ (channelList.size() * svgutil.getLinewidth() + svgutil
							.getSvgheight())
					+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
			svg.append(svgcanvas);
			Long ichan = 0L;
			for (Long chanid : channelList) {
				CoolIovSummary coolsumm = iovsummary.get(chanid);
				if (ichan == 0) {
					results.append(" | Info in ichan 0: niovs="
							+ coolsumm.getTotalIovs() + " from "
							+ coolsumm.getMinsince() + " / "
							+ coolsumm.getMinuntil() + " to "
							+ coolsumm.getMaxsince() + " / "
							+ coolsumm.getMaxuntil() + "</p><br>");

					svgutil.setSvgabsmin(coolsumm.getMinsince());
					svgutil.setSvgabsmax(coolsumm.getMaxuntil());
					if (coolsumm.getMinuntil() < CoolIov.COOL_MAX_DATE) {
						Long iovspan = coolsumm.getMinuntil()
								- coolsumm.getMinsince();
						if (iovspan < 1000L)
							svgutil.setSvgabsmin(coolsumm.getMinuntil()
									- (Long) (iovspan / 10L));
						else
							svgutil.setSvgabsmin(coolsumm.getMinuntil() - 1000L);
					}
					if (coolsumm.getMaxuntil() >= CoolIov.COOL_MAX_RUN) {
						svgutil.setSvgabsmax(coolsumm.getMaxsince() + 1000L);
					}
				}
				log.finer("Node " + node + " tag " + seltag + ": Chan "
						+ chanid + " is using svgmin " + svgutil.getSvgabsmin()
						+ " and svgmax " + svgutil.getSvgabsmax() + " from "
						+ coolsumm.getMinsince() + " " + coolsumm.getMinuntil()
						+ " " + coolsumm.getMaxsince());
				Map<Long, IovRange> timeranges = coolsumm.getIovRanges();
				if (timeranges != null) {
					Set<Long> sincetimes = timeranges.keySet();
					for (Long asince : sincetimes) {
						IovRange ivr = timeranges.get(asince);
						svg.append(svgutil.getSvgLine(ivr.getSince(),
								ivr.getUntil(), ichan, coolsumm.getIovbase(),
								ivr.getIshole()));
					}
				}
				ichan++;
			}
			results.append(svg.toString() + "</svg><br>");
			svg.delete(0, svg.length());
			results.append("</body>");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsummary/{type}/dump")
	public String dumpIovsSummaryInNodesSchemaTag(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag, 
			@PathParam("type") String type) {

		log.info("Calling dumpIovsSummaryInNodesSchemaTag..." + schema + " "
				+ db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> iovsummaryColl = super.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, fld, tag, "0", "Inf", "time");
		Integer channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();
		}
		String results = "Empty result string...retrieved list of "+channels;
		if (type.equals("text")) {
			results = "";
			log.info("Dumping list as text html");
			results = dumpIovSummaryAsText(iovsummaryColl);
		} else if (type.equals("svg")){
			results = "";
			log.info("Dumping list as text html");
			results = dumpIovSummaryAsSvg(iovsummaryColl);			
		}
		return results;
	}
	
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/{timespan}/rangesummary/{type}/dump")
	public String dumpIovsSummaryInNodesSchemaTagRange(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan,
			@PathParam("type") String type) {

		log.info("Calling dumpIovsSummaryInNodesSchemaTagRange..." + schema + " "
				+ db + " folder " + fld + " tag " + tag+" "+since+" "+until);
		Collection<CoolIovSummary> iovsummaryColl = super.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, fld, tag, since, until, timespan);
		Integer channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();
		}		
		StringBuffer results = new StringBuffer();
		String resultsDefault = "Empty result string...retrieved list of "+channels+" channels ";
		if (type.equals("text")) {
			log.info("Dumping list as text html");
			results.append(dumpIovSummaryAsText(iovsummaryColl));
		} else if (type.equals("svg")){
			log.info("Dumping list as text html");
			results.append(dumpIovSummaryAsSvg(iovsummaryColl));			
		} else {
			results.append(resultsDefault);
		}
		String coverage = "<p>All important runs are covered</p>";
		try {
			coverage = checkHoles(iovsummaryColl);
		} catch (ComaQueryException e) {
			e.printStackTrace();
			coverage = "<p>Error in coverage checking...</p>";
		}
		results.append(coverage);
		return results.toString();
	}

	protected String checkHoles(Collection<CoolIovSummary> iovsummaryColl) throws ComaQueryException {
		StringBuffer results = new StringBuffer();

		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");
		results.append("<body>");

		results.append("<h1>Coverage verification.... </h1>");

		CoolIovSummary firstsumm = iovsummaryColl.iterator().next();
		results.append("<h2>" + colorseptagstart + firstsumm.getSchema()
				+ " > " + " " + firstsumm.getNode() + " ; "
				+ firstsumm.getTag() + colortagend + "</h2>" + "<br>");
		Boolean ishole = false;
		Boolean coverageerror = true;
		for (CoolIovSummary iovsummary : iovsummaryColl) {
			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				int iiov = 0;
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					String holedump = "";
					if (ivr.getIshole()) {
						if (iiov==0) {
						results.append("<p class=\"small\">" + iovsummary.getChanId() + " "
								+ iovsummary.getChannelName() + " - "
								+ iovsummary.getIovbase() + " : ");
						}
						iiov++;
						ishole = true;
						colortagstart = colorbadtagstart;
						List<CrViewRuninfo> runlist = null;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
							holedump = "[" + timespan + "] ";
							Timestamp _since = new Timestamp(ivr.getSince()/CoolIov.TO_NANOSECONDS);
							Timestamp _until = new Timestamp(ivr.getUntil()/CoolIov.TO_NANOSECONDS);
							runlist = super.comadao.findRunsInRange(_since, _until);

						} else if (iovsummary.getIovbase().equals("run-lumi")) {
							Long runsince = CoolIov.getRun(ivr.getSince());
							Long rununtil = CoolIov.getRun(ivr.getUntil());
							timespan = rununtil - runsince;
							holedump = "[" + timespan +"]";
							BigDecimal _since = new BigDecimal(runsince);
							BigDecimal _until = new BigDecimal(rununtil);
							// Verify holes with run ranges
							runlist = super.comadao.findRunsInRange(_since, _until);
						}
						for (CrViewRuninfo arun : runlist) {
							if (arun.getPPeriod() != null && arun.getPProject() != null) {
								if (arun.getPProject().startsWith("data")) {
									coverageerror = false;
									iovDump = colortagstart
											+ ivr.getNiovs()
											+ " ["
											+ arun.getRunNumber()+" "+arun.getPProject()
											+ "] " + holedump
											+ colortagend;

									results.append(" | " + iovDump);									
								}
							}
						}
					}
				}
				if (ishole)
					results.append("</p>");
			}
		}
		if (coverageerror) {
			results.append("All relevant runs are covered...");
		}
		results.append("</body>");
		return results.toString();
	}

	protected String dumpIovSummaryAsText(
			Collection<CoolIovSummary> iovsummaryColl) {

		StringBuffer results = new StringBuffer();

		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");
		results.append("<body>");

		results.append("<h1>Iovs statistics.... </h1>");

		int channels = iovsummaryColl.size();
		CoolIovSummary firstsumm = iovsummaryColl.iterator().next();
		results.append("<h2>" + colorseptagstart + firstsumm.getSchema()
				+ " > " + " " + firstsumm.getNode() + " ; "
				+ firstsumm.getTag() + colortagend + "</h2>" + "<br>");

		results.append("<h3>Total of used channels is "+channels+" </h3>");
		results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

		for (CoolIovSummary iovsummary : iovsummaryColl) {
			results.append("<p class=\"small\">" + iovsummary.getChanId() + " "
					+ iovsummary.getChannelName() + " - "
					+ iovsummary.getIovbase() + " : ");
			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				long minsince = iovsummary.getMinsince();
				int iiov = 0;
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					if ((iiov == 0)
							&& (ivr.getSince().compareTo(minsince) != 0)) {
						colortagstart = colorwarntagstart;
					}
					String holedump = "";
					if (ivr.getIshole()) {
						colortagstart = colorbadtagstart;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
						}
						holedump = "[" + timespan + "] ";
					}
					iovDump = colortagstart
							+ ivr.getNiovs()
							+ " ["
							+ ivr.getSinceCoolStr()
							+ "] ["
							+ ivr.getUntilCoolStr() + "] " + holedump
							+ colortagend;

					results.append(" | " + iovDump);
					iiov++;
				}
				results.append("</p>");
			}
		}
		results.append("</body>");
		return results.toString();
	}

	protected String dumpIovSummaryAsSvg(
			Collection<CoolIovSummary> iovsummaryColl) {

		StringBuffer results = new StringBuffer();
		StringBuffer svg = new StringBuffer();

		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");

		results.append("<body>");
		results.append("<h1>Iovs statistics.... </h1>");
		int channels = iovsummaryColl.size();
		
		results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");
		CoolIovSummary firstsumm = iovsummaryColl.iterator().next();
		results.append("<h2>" + colorseptagstart + firstsumm.getSchema()
				+ " > " + " " + firstsumm.getNode() + " ; "
				+ firstsumm.getTag() + colortagend + "</h2>" + "<br>");
		
			SvgRestUtils svgutil = new SvgRestUtils();
		svgutil.setSvgabsmin(0L);
		svgutil.setSvgabsmax(CoolIov.COOL_MAX_DATE);
		if (channels < 20) {
			svgutil.setLinewidth(10);
		} else if (channels < 100) {
			svgutil.setLinewidth(6);
		} else if (channels < 200) {
			svgutil.setLinewidth(3);
		} else {
			svgutil.setLinewidth(1);
		}
		results.append("<p>Number of channels used " + channels);
		String svgcanvas = "<svg width=\""
				+ svgutil.getSvglinewidth()
				+ "px\" height=\""
				+ (channels * svgutil.getLinewidth() + svgutil
						.getSvgheight())
				+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
		svg.append(svgcanvas);
		Long ichan = 0L;
		for (CoolIovSummary iovsummary : iovsummaryColl) {
			
			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (ichan == 0) {
				results.append(" | Info in ichan 0: niovs="
						+ iovsummary.getTotalIovs() + " from "
						+ iovsummary.getMinsince() + " / "
						+ iovsummary.getMinuntil() + " to "
						+ iovsummary.getMaxsince() + " / "
						+ iovsummary.getMaxuntil() + "</p><br>");

				svgutil.setSvgabsmin(iovsummary.getMinsince());
				svgutil.setSvgabsmax(iovsummary.getMaxuntil());
				if (iovsummary.getMinuntil() < CoolIov.COOL_MAX_DATE) {
					Long iovspan = iovsummary.getMinuntil()
							- iovsummary.getMinsince();
					if (iovspan < 1000L)
						svgutil.setSvgabsmin(iovsummary.getMinuntil()
								- (Long) (iovspan / 10L));
					else
						svgutil.setSvgabsmin(iovsummary.getMinuntil() - 1000L);
				}
				if (iovsummary.getMaxuntil() >= CoolIov.COOL_MAX_RUN) {
					svgutil.setSvgabsmax(iovsummary.getMaxsince() + 1000L);
				}
			}
			log.finer("Node " + iovsummary.getNode() + " tag " + iovsummary.getTag() + ": Chan "
					+ iovsummary.getChanId() + " is using svgmin " + svgutil.getSvgabsmin()
					+ " and svgmax " + svgutil.getSvgabsmax() + " from "
					+ iovsummary.getMinsince() + " " + iovsummary.getMinuntil()
					+ " " + iovsummary.getMaxsince());
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					svg.append(svgutil.getSvgLine(ivr.getSince(),
							ivr.getUntil(), ichan, iovsummary.getIovbase(),
							ivr.getIshole()));
				}
			}
			ichan++;
		}
		results.append(svg.toString() + "</svg><br>");
		svg.delete(0, svg.length());
		results.append("</body>");
		return results.toString();
	}

	protected String dumpResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd_rs = rs.getMetaData();
		for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
			log.info("col " + i + " name = " + rsmd_rs.getColumnName(i));
		}
		log.info(" rs is on first row " + rs.isFirst());
		StringBuffer buf = new StringBuffer();
		int ncol = rsmd_rs.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= ncol; i++) {
				String colname = rsmd_rs.getColumnName(i);
				Object colval = rs.getObject(i);
				// payload.addColumn(i, colname);
				// payload.addData(i, colval);
				// log.info("Retrieved " + colname + " = " + dumpObject(colval)
				// );
				buf.append("[" + colname + "]=" + dumpObject(colval) + " ; \n");
			}
		}
		return buf.toString();
	}

	protected String dump2FileResultSet(ResultSet rs, String fname)
			throws SQLException {
		FileWriter fw = null;
		CoolPayload pyld = new CoolPayload();
		List<String> masked = pyld.getMasked();
		try {
			fw = new FileWriter(fname);
			ResultSetMetaData rsmd_rs = rs.getMetaData();
			StringBuffer bufheader = new StringBuffer();
			for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
				String colname = rsmd_rs.getColumnName(i);
				log.info("col " + i + " name = " + colname);
				if (masked.contains(colname)) {
					log.info("Ignore column " + colname
							+ " in the output file ");
				} else
					bufheader.append(colname + "  ");
			}
			fw.write(bufheader.toString() + "\n");
			log.info(" rs is on first row " + rs.isFirst());
			int ncol = rsmd_rs.getColumnCount();
			while (rs.next()) {
				StringBuffer bufline = new StringBuffer();
				for (int i = 1; i <= ncol; i++) {
					String colname = rsmd_rs.getColumnName(i);
					Object colval = rs.getObject(i);
					if (masked.contains(colname)) {
						log.info("Ignore column " + colname
								+ " in the output file ");
					} else
						bufline.append(dumpObject(colval) + " ; ");

				}
				fw.write(bufline.toString() + "\n");
				fw.flush();
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fname;
	}

	protected String dumpObject(Object val) throws SQLException {
		String buf = "null";
		if (val == null)
			return buf;
		if (val instanceof oracle.sql.CLOB) {
			CLOB clob = (CLOB) val;
			buf = clob.stringValue();
		} else if (val instanceof oracle.sql.BLOB) {
			BLOB blob = (BLOB) val;
			try {
				buf = lobtoString(blob);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			buf = val.toString();
		}
		return buf;
	}

	protected String lobtoString(BLOB dat) throws IOException, SQLException {
		StringBuffer strOut = new StringBuffer();
		String aux;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				dat.asciiStreamValue()));
		while ((aux = br.readLine()) != null)
			strOut.append(aux);
		return strOut.toString();
	}

}
