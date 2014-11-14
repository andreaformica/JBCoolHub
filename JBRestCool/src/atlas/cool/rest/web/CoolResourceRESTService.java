package atlas.cool.rest.web;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import oracle.sql.BLOB;
import oracle.sql.CLOB;
import atlas.coma.exceptions.ComaQueryException;
import atlas.cool.dao.CoolResultSetDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.payload.model.CoolPayload;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.model.SchemaType;

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
	protected void setSort(final String orderByName) {
		// TODO Auto-generated method stub
		super.setSort(orderByName);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listSchemasInDb(java.lang.String, java.lang.String)
	 */
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/schemas")
	public  List<SchemaType> listSchemasInDb(
			@PathParam("schema") final String schema, @PathParam("db") final String db) {
		return super.listSchemasInDb(schema, db);
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
	public List<NodeType> listNodesInSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db) {
		// TODO Auto-generated method stub
		return super.listNodesInSchema(schema, db);
	}
	
	

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listNodesInSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{node:.*}/nodes")
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("node") String node) {
		return super.listNodesInSchema(schema, db, node);
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
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node) {
		// TODO Auto-generated method stub
		return super.listTagsInNodesSchema(schema, db, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listChannelsInNodesSchema(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{node:.*}/fld/{channel}/channels")
	public List<ChannelType> listChannelsInNodesSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node,
			@PathParam("channel") final String channame) {
		// TODO Auto-generated method stub
		return super.listChannelsInNodesSchema(schema, db, node, channame);
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
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		// TODO Auto-generated method stub
		return super.listGlobalTagsTagsInNodesSchema(schema, db, gtag);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listGlobalTagsTagsInBranchNodesSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{gtag}/fulltrace")
	public List<NodeGtagTagType> listGlobalTagsTagsInBranchNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		// TODO Auto-generated method stub
		return super.listGlobalTagsTagsInBranchNodesSchema(schema, db, gtag);
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
	public List<IovType> getIovStatPerChannel(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("fld") final String fld,
			@PathParam("tag") final String tag) {
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
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listIovsInNodesSchemaTagRangeAsList(schema, db, fld, tag, channel,
				chansel, since, until, timespan);
	}

	
	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listNumIovsInNodesSchemaTagAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{num}/lastiovs")
	public NodeType listNumIovsInNodesSchemaTagAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("num") String num) {
		// TODO Auto-generated method stub
		return super.listNumIovsInNodesSchemaTagAsList(schema, db, fld, tag, num);
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
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("sort") final String sort,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listIovsInNodesSchemaTagRangeSortedAsList(schema, db, fld, tag,
				sort, channel, chansel, since, until, timespan);
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
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {
		return super.listPayloadInNodesSchemaTagRangeAsList(schema, db, fld, tag,
				channel, chansel, since, until, timespan);
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
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("sort") final String sort,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listPayloadInNodesSchemaTagRangeSortedAsList(schema, db, fld, tag,
				sort, channel, chansel, since, until, timespan);
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
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, fld, tag,
				since, until, timespan);
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
	public String getPayload(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("fld") final String fld,
			@PathParam("tag") final String tag, @PathParam("time") final BigDecimal time,
			@PathParam("chan") final Long channel) {
		log.info("Calling getPayload..." + schema + " " + db + " " + fld + " " + channel);
		try {
			Long chid = channel;
			if (channel < 0) {
				chid = null;
			}
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			String output = "";
			final ResultSet pyld = payloaddao.getPayload(schema, db, node, tag, time,
					chid);
			if (pyld != null) {
				output = dumpResultSet(pyld);
			}
			payloaddao.closeConnection();
			return output;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SQLException e) {
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
	public String getPayloads(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("fld") final String fld,
			@PathParam("tag") final String tag,
			@PathParam("stime") final BigDecimal stime,
			@PathParam("etime") final BigDecimal etime,
			@PathParam("chan") final Long channel) {
		/*
		 * MultivaluedMap<String,String> params = id.getMatrixParameters();
		 * String schema = params.get("schema").get(0); String db =
		 * params.get("db").get(0); String fld = params.get("fld").get(0);
		 * String tag = params.get("tag").get(0); BigDecimal time = new
		 * BigDecimal(params.get("time").get(0));
		 */log.info("Calling getPayload..." + schema + " " + db + " " + fld);
		try {
			Long chid = channel;
			if (channel < 0) {
				chid = null;
			}
			final String fldname = fld.replaceAll("/", "_");
			String output = "";
			final ResultSet pyld = payloaddao.getPayloads(schema, db, fld, tag, stime,
					etime, chid);
			if (pyld != null) {
				// output = dumpResultSet(pyld);
				output = dump2FileResultSet(pyld, "/tmp/" + schema + "_" + fldname + "_"
						+ tag);
			}
			return output;
			// payloaddao.remove();
		} catch (final CoolIOException e) {
			e.printStackTrace();
		} catch (final SQLException e) {
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
	public List<IovType> getIovHolesPerChannel(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("fld") final String fld,
			@PathParam("tag") final String tag,
			@PathParam("since") final BigDecimal since,
			@PathParam("until") final BigDecimal until) {

		log.info("Calling getIovHolesPerChannel..." + schema + " " + db + " " + fld + " "
				+ tag + " " + since + " " + until);
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
			final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
					node);
			NodeType selnode = null;
			if (nodes != null && nodes.size() > 0) {
				for (final NodeType anode : nodes) {
					log.info("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			results = cooldao.retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(
					schema, db, node, seltag, since, until);
			for (final IovType aniov : results) {
				aniov.setIovBase(selnode.getNodeIovBase());
			}
		} catch (final CoolIOException e) {
			e.printStackTrace();
		}
		return results;
	}

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsummary/{type}/dump")
	public String dumpIovsSummaryInNodesSchemaTag(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("type") final String type) {

		log.info("Calling dumpIovsSummaryInNodesSchemaTag..." + schema + " " + db
				+ " folder " + fld + " tag " + tag);
		final Collection<CoolIovSummary> iovsummaryColl = super
				.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, fld, tag, "0",
						"Inf", "time");
		Integer channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();
		}
		String header =
		"<head><style>" + "h1 {font-size:20px;} " + "h2 {font-size:18px;}"
				+ "h3 {font-size:16px;}" + "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>";
		String results = "Empty result string...retrieved list of " + channels;
		if (type.equals("text")) {
			results = "";
			log.info("Dumping list as text html");
			results = super.coolutilsdao.dumpIovSummaryAsText(iovsummaryColl);
		} else if (type.equals("svg")) {
			results = "";
			log.info("Dumping list as text html");
			results = super.coolutilsdao.dumpIovSummaryAsSvg(iovsummaryColl);
		}
		return header+results;
	}

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/{timespan}/rangesummary/{type}/dump")
	public String dumpIovsSummaryInNodesSchemaTagRange(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan,
			@PathParam("type") final String type) {

		log.info("Calling dumpIovsSummaryInNodesSchemaTagRange..." + schema + " " + db
				+ " folder " + fld + " tag " + tag + " " + since + " " + until);
		final Collection<CoolIovSummary> iovsummaryColl = super
				.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, fld, tag, since,
						until, timespan);
		Integer channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();
		}
		final StringBuffer results = new StringBuffer();
		results.append("<head><style>" + "h1 {font-size:20px;} " + "h2 {font-size:18px;}"
				+ "h3 {font-size:16px;}" + "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");
		results.append("<body>");
		final String resultsDefault = "Empty result string...retrieved list of "
				+ channels + " channels ";
		if (type.equals("text")) {
			log.info("Dumping list as text html");
			results.append(super.coolutilsdao.dumpIovSummaryAsText(iovsummaryColl));
			String coverage = "<p>All important runs are covered</p>";
			try {
				coverage = super.coolutilsdao.checkHoles(iovsummaryColl);
			} catch (final ComaQueryException e) {
				e.printStackTrace();
				coverage = "<p>Error in coverage checking...</p>";
			}
			results.append(coverage);

		} else if (type.equals("svg")) {
			log.info("Dumping list as svg and html");
			results.append(super.coolutilsdao.dumpIovSummaryAsSvg(iovsummaryColl));
		} else {
			results.append(resultsDefault);
		}
		results.append("</body>");
		return results.toString();
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected String dumpResultSet(final ResultSet rs) throws SQLException {
		final ResultSetMetaData rsmdRs = rs.getMetaData();
		for (int i = 1; i <= rsmdRs.getColumnCount(); i++) {
			log.info("col " + i + " name = " + rsmdRs.getColumnName(i));
		}
		log.info(" rs is on first row " + rs.isFirst());
		final StringBuffer buf = new StringBuffer();
		final int ncol = rsmdRs.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= ncol; i++) {
				final String colname = rsmdRs.getColumnName(i);
				final Object colval = rs.getObject(i);
				// payload.addColumn(i, colname);
				// payload.addData(i, colval);
				// log.info("Retrieved " + colname + " = " + dumpObject(colval)
				// );
				buf.append("[" + colname + "]=" + dumpObject(colval) + " ; \n");
			}
		}
		return buf.toString();
	}

	/**
	 * @param rs
	 * @param fname
	 * @return
	 * @throws SQLException
	 */
	protected String dump2FileResultSet(final ResultSet rs, final String fname)
			throws SQLException {
		FileWriter fw = null;
		final CoolPayload pyld = new CoolPayload();
		final List<String> masked = pyld.getMasked();
		try {
			fw = new FileWriter(fname);
			final ResultSetMetaData rsmdRs = rs.getMetaData();
			final StringBuffer bufheader = new StringBuffer();
			for (int i = 1; i <= rsmdRs.getColumnCount(); i++) {
				final String colname = rsmdRs.getColumnName(i);
				log.info("col " + i + " name = " + colname);
				if (masked.contains(colname)) {
					log.info("Ignore column " + colname + " in the output file ");
				} else {
					bufheader.append(colname + "  ");
				}
			}
			fw.write(bufheader.toString() + "\n");
			log.info(" rs is on first row " + rs.isFirst());
			final int ncol = rsmdRs.getColumnCount();
			while (rs.next()) {
				final StringBuffer bufline = new StringBuffer();
				for (int i = 1; i <= ncol; i++) {
					final String colname = rsmdRs.getColumnName(i);
					final Object colval = rs.getObject(i);
					if (masked.contains(colname)) {
						log.info("Ignore column " + colname + " in the output file ");
					} else {
						bufline.append(dumpObject(colval) + " ; ");
					}

				}
				fw.write(bufline.toString() + "\n");
				fw.flush();
			}
			fw.flush();
			fw.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fname;
	}

	/**
	 * @param val
	 * @return
	 * @throws SQLException
	 */
	protected String dumpObject(final Object val) throws SQLException {
		String buf = "null";
		if (val == null) {
			return buf;
		}
		if (val instanceof oracle.sql.CLOB) {
			final CLOB clob = (CLOB) val;
			buf = clob.stringValue();
		} else if (val instanceof oracle.sql.BLOB) {
			final BLOB blob = (BLOB) val;
			try {
				buf = lobtoString(blob);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			buf = val.toString();
		}
		return buf;
	}

	/**
	 * @param dat
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	protected String lobtoString(final BLOB dat) throws IOException, SQLException {
		final StringBuffer strOut = new StringBuffer();
		String aux;
		final BufferedReader br = new BufferedReader(new InputStreamReader(
				dat.asciiStreamValue()));
		while ((aux = br.readLine()) != null) {
			strOut.append(aux);
		}
		return strOut.toString();
	}

}
