package atlas.cool.rest.web;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import oracle.sql.BLOB;
import oracle.sql.CLOB;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
import atlas.cool.dao.CoolPayloadDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.meta.CoolIov;
import atlas.cool.meta.CoolPayload;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.utils.SvgRestUtils;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
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
public class CoolResourceRESTService {

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
	@Produces("text/xml")
	@Path("/{schema}/{db}/nodes")
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {

		log.info("Calling listNodesInSchema..." + schema + " " + db);
		List<NodeType> results = null;
		try {
			results = cooldao.retrieveNodesFromSchemaAndDb(schema + "%", db,
					"%");
			if (results == null) {
				// create a fake entry
				NodeType nt = new NodeType();
				nt.setNodeId(1L);
				nt.setNodeFullpath("this is a fake node");
				nt.setNodeTinstime(new Timestamp(new Date().getTime()));
				List<NodeType> _fakes = new ArrayList<NodeType>();
				_fakes.add(nt);
				results = _fakes;
			}
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
	@Produces("text/xml")
	@Path("/{schema}/{db}/{node}/tags")
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node) {

		log.info("Calling listTagsInNodeSchema..." + schema + " " + db + " "
				+ node);
		List<SchemaNodeTagType> results = null;
		try {
			if (node.equals("all")) {
				node = "%";
			} else {
				node = "%" + node + "%";
			}
			results = cooldao.retrieveTagsFromNodesSchemaAndDb(schema + "%",
					db, node, null);
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
	@Produces("text/xml")
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
			@PathParam("chan") Integer channel) {
		log.info("Calling getPayload..." + schema + " " + db + " " + fld + " "
				+ channel);
		try {
			Integer chid = channel;
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
			// payloaddao.remove();
			return output;
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				payloaddao.remove();
			} catch (CoolIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			@PathParam("chan") Integer channel) {
		/*
		 * MultivaluedMap<String,String> params = id.getMatrixParameters();
		 * String schema = params.get("schema").get(0); String db =
		 * params.get("db").get(0); String fld = params.get("fld").get(0);
		 * String tag = params.get("tag").get(0); BigDecimal time = new
		 * BigDecimal(params.get("time").get(0));
		 */log.info("Calling getPayload..." + schema + " " + db + " " + fld);
		try {
			Integer chid = channel;
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
			try {
				payloaddao.remove();
			} catch (CoolIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "done";
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
	@Produces("text/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan")
	public List<IovType> getIovStatPerChannel(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag) {

		log.info("Calling getIovStatPerChannel..." + schema + " " + db + " "
				+ fld + " " + tag);
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
			results = cooldao.retrieveIovStatPerChannelFromNodeSchemaAndDb(
					schema, db, node, seltag);
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
						if ((iiov == 0) && (ivr.getSince().compareTo(minsince) != 0)) {
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
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary/list
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
	@Produces("text/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/run/rangesummary/list")
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRunRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since,
			@PathParam("until") String until) {

		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..." + schema + " "
				+ db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;
		try {
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

			Map<Long, CoolIovSummary> iovsummary = coolutilsdao
					.computeIovSummaryRangeMap(schema, db, node, seltag,
							selnode.getNodeIovBase(), CoolIov.getCoolRun(since), CoolIov.getCoolRun(until));

			summarylist = iovsummary.values();
			
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summarylist;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/channel/{since}/{until}/runlb/iovs/list
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
	@Produces("text/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/channel/{since}/{until}/runlb/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeAsList(@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("since") String since,
			@PathParam("until") String until) {

		log.info("Calling listIovsInNodesSchemaTagRangeAsList..." + schema + " "
				+ db + " folder " + fld + " tag " + tag+" "+channel+" "+since+" "+until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;
		try {
			String chan = "%"+channel+"%";
			if (channel.equals("all")) {
				chan = "%";
			}
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
					db, node);
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

			if (since.contains("-")) {
				String[] sinceargs = since.split("-");
				String[] untilargs = until.split("-");
				String lbstr = null;
				if (sinceargs.length>0 && !sinceargs[1].isEmpty()) {
					lbstr = sinceargs[1];
				}
				BigDecimal _since = CoolIov.getCoolRunLumi(sinceargs[0], lbstr);
				lbstr = null;
				if (untilargs.length>0 && !untilargs[1].isEmpty()) {
					lbstr = untilargs[1];
				}
				BigDecimal _until = CoolIov.getCoolRunLumi(untilargs[0], sinceargs[1]);
				iovlist = cooldao.retrieveIovsFromNodeSchemaAndDbInRangeByChanName(
						schema, db, node, seltag, chan, _since, _until);
				selnode.setIovList(iovlist);
			} else {
				return null;
			}

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
	@Produces("text/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/runlb/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeAsList(@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since,
			@PathParam("until") String until) {

		NodeType selnode = listIovsInNodesSchemaTagRangeAsList(schema, db, fld, tag, "all", since, until);
		return selnode;
	}

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary/list
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
	@Produces("text/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/rangesummary/list")
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") BigDecimal since,
			@PathParam("until") BigDecimal until) {

		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..." + schema + " "
				+ db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;
		try {
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

			Map<Long, CoolIovSummary> iovsummary = coolutilsdao
					.computeIovSummaryRangeMap(schema, db, node, seltag,
							selnode.getNodeIovBase(), since, until);

			summarylist = iovsummary.values();
			
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summarylist;
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
						if ((iiov == 0) && (ivr.getSince().compareTo(minsince) != 0)) {
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
						svg.append(svgutil.getSvgLine(ivr.getSince(), ivr.getUntil(),
								ichan, coolsumm.getIovbase(), ivr.getIshole()));
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
