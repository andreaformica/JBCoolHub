package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.exceptions.CoolQueryException;
import atlas.cool.meta.CoolIov;
import atlas.cool.query.tools.QueryTools;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.model.SchemaType;

/**
 * This class implements the RESTful services to read the contents of the Cool
 * tables via PL/SQL.
 * 
 * @author formica
 * 
 */
@RequestScoped
public class CoolRESTImpl implements ICoolREST {

	@Inject
	protected CoolDAO cooldao;
	@Inject
	protected CoolUtilsDAO coolutilsdao;
	@Inject
	protected ComaCbDAO comadao;
	@Inject
	protected Logger log;

	// protected SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

	public CoolRESTImpl() {
		super();
	}

	/**
	 * @param orderByName
	 */
	protected void setSort(final String orderByName) {
		atlas.cool.interceptors.WebRestContextHolder.put("OrderBy", orderByName);
		if (orderByName == null || orderByName.isEmpty()) {
			atlas.cool.interceptors.WebRestContextHolder.cleanupThread();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.ICoolREST#listSchemasInDb(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/schemas")
	public List<SchemaType> listSchemasInDb(@PathParam("schema") final String schema,
			@PathParam("db") final String db) {
		log.info("Calling listSchemasInDb..." + schema + " " + db);
		List<SchemaType> results = null;
		try {

			results = cooldao.retrieveSchemasFromNodeSchemaAndDb(schema + "%", db, "%");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.ICoolREST#listNodesInSchema(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<NodeType> listNodesInSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db) {

		log.info("Calling listNodesInSchema..." + schema + " " + db);
		List<NodeType> results = null;
		try {
			results = coolutilsdao.listNodesInSchema(schema, db);
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listTagsInNodesSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node) {

		List<SchemaNodeTagType> results = null;
		try {
			results = coolutilsdao.listTagsInNodesSchema(schema, db, node);
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listChannelsInNodesSchema(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ChannelType> listChannelsInNodesSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String fld,
			@PathParam("channel") final String channame) {

		log.info("Calling listChannelsInNodesSchema..." + schema + " " + db + " " + fld
				+ " " + channame);
		List<ChannelType> results = null;
		try {
			String chan = "%" + channame + "%";
			if (channame.equals("all")) {
				chan = "%";
			}
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}

			results = cooldao.retrieveChannelsFromNodeSchemaAndDb(schema, db, node, chan);
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listGlobalTagsTagsInNodesSchema(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {

		log.info("Calling listGlobalTagsTagsInNodesSchema..." + schema + " " + db);
		List<NodeGtagTagType> results = null;
		try {
			results = cooldao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db, "%"
					+ gtag + "%");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.ICoolREST#getIovStatPerChannel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<IovType> getIovStatPerChannel(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("fld") final String fld,
			@PathParam("tag") final String tag) {

		log.info("Calling getIovStatPerChannel..." + schema + " " + db + " " + fld + " "
				+ tag);
		List<IovType> results = null;
		try {
			results = coolutilsdao.getIovStatPerChannel(schema, db, fld, tag);
		} catch (final CoolIOException e) {
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listIovsInNodesSchemaTagRangeAsList(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public NodeType listIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		NodeType selnode = null;
		try {
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until,
					timespan);
			final BigDecimal lsince = (BigDecimal) trmap.get("since");
			final BigDecimal luntil = (BigDecimal) trmap.get("until");

			final String chan = channel;
			// Channel Selection
			if (chansel.equals("chanid")) {
				// Treat the channel in input as a Long
				final Long chanid = new Long(channel);
				selnode = coolutilsdao.listIovsInNodesSchemaTagRangeAsList(schema, db,
						fld, tag, chanid, lsince, luntil);

			} else if (chansel.equals("channel")) {
				selnode = coolutilsdao.listIovsInNodesSchemaTagRangeAsList(schema, db,
						fld, tag, chan, lsince, luntil);
			} else {
				throw new CoolIOException("Wrong REST syntax...refer to documentation");
			}

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listIovsInNodesSchemaTagRangeSortedAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public NodeType listIovsInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("sort") final String sort,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		NodeType selnode = null;
		try {
			String orderByName = "";
			final String[] sortcolumns = sort.split("/");
			if (sortcolumns.length >= 0) {
				final Map<String, Boolean> colmap = new LinkedHashMap<String, Boolean>();
				for (int i = 0; i < sortcolumns.length; i++) {
					final String[] colsort = sortcolumns[i].split("-");
					final String colname = colsort[0];
					final String sortkey = colsort[1] != null ? colsort[1] : "ASC";
					Boolean sortflag = true;
					if (sortkey.equals("DESC")) {
						sortflag = false;
					}
					colmap.put(colname, sortflag);
				}
				orderByName = QueryTools.getOrderedBy(new CoolIovType(), colmap);
			}
			setSort(orderByName);
			selnode = listIovsInNodesSchemaTagRangeAsList(schema, db, fld, tag, channel,
					chansel, since, until, timespan);
			setSort(null);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listDiffIovsInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag1:.*}/tag1/{tag2:.*}/tag2/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listDiffIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag1") final String tag1,
			@PathParam("tag2") final String tag2,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {
		NodeType selnode = null;
		try {
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until,
					timespan);
			final BigDecimal lsince = (BigDecimal) trmap.get("since");
			final BigDecimal luntil = (BigDecimal) trmap.get("until");

			final String chan = channel;
			// Channel Selection
			if (chansel.equals("chanid")) {
				// Treat the channel in input as a Long
				final Long chanid = new Long(channel);
				selnode = coolutilsdao.listIovsDiffInNodesSchemaTagRangeAsList(schema,
						db, fld, tag1, tag2, chanid, lsince, luntil);

			} else if (chansel.equals("channel")) {
				selnode = coolutilsdao.listIovsDiffInNodesSchemaTagRangeAsList(schema,
						db, fld, tag1, tag2, chan, lsince, luntil);
			} else {
				throw new CoolIOException("Wrong REST syntax...refer to documentation");
			}

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listPayloadInNodesSchemaTagRangeAsList(
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public NodeType listPayloadInNodesSchemaTagRangeAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {
		NodeType selnode = null;
		try {
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until,
					timespan);
			final BigDecimal lsince = (BigDecimal) trmap.get("since");
			final BigDecimal luntil = (BigDecimal) trmap.get("until");

			final String chan = channel;
			// Channel Selection
			if (chansel.equals("chanid")) {
				// Treat the channel in input as a Long
				final Long chanid = new Long(channel);
				selnode = coolutilsdao.listPayloadInNodesSchemaTagRangeAsList(schema, db,
						fld, tag, chanid, lsince, luntil);

			} else if (chansel.equals("channel")) {
				selnode = coolutilsdao.listPayloadInNodesSchemaTagRangeAsList(schema, db,
						fld, tag, chan, lsince, luntil);
			} else {
				throw new CoolIOException("Wrong REST syntax...refer to documentation");
			}
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	@Override
	public NodeType listPayloadInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("sort") final String sort,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		NodeType selnode = null;
		try {
			String orderByName = "";
			final String[] sortcolumns = sort.split("/");
			if (sortcolumns.length >= 0) {
				final Map<String, Boolean> colmap = new LinkedHashMap<String, Boolean>();
				for (int i = 0; i < sortcolumns.length; i++) {
					final String[] colsort = sortcolumns[i].split("-");
					final String colname = colsort[0];
					final String sortkey = colsort[1] != null ? colsort[1] : "ASC";
					Boolean sortflag = true;
					if (sortkey.equals("DESC")) {
						sortflag = false;
					}
					colmap.put(colname, sortflag);
				}
				orderByName = QueryTools.getOrderedBy(new CoolIovType(), colmap);
			}
			setSort(orderByName);
			selnode = listPayloadInNodesSchemaTagRangeAsList(schema, db, fld, tag,
					channel, chansel, since, until, timespan);
			setSort(null);
			// InNodesSchemaTagDateTimeRangeAsList(schema, db, fld, tag,
			// channel, chansel, since, until, timespan);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolREST#listIovsSummaryInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..." + schema + " "
				+ db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;
		try {
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until,
					timespan);
			final BigDecimal lsince = (BigDecimal) trmap.get("since");
			final BigDecimal luntil = (BigDecimal) trmap.get("until");
			if (lsince == null || luntil == null) {
				final CoolIovSummary iovsumm = new CoolIovSummary();
				iovsumm.setSummary("Wrong time interval has been used: since or until times are null");
				summarylist = new ArrayList<CoolIovSummary>();
				summarylist.add(iovsumm);
				return summarylist;
			}
			summarylist = coolutilsdao.listIovsSummaryInNodesSchemaTagRangeAsList(schema,
					db, fld, tag, lsince, luntil);

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summarylist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.ICoolREST#dumpIovsSummaryInNodesSchemaTagRangeAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String dumpIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan, @PathParam("type") String type) {

		log.info("Calling dumpIovsSummaryInNodesSchemaTagRangeAsList..." + schema + " " + db);
		final StringBuffer results = new StringBuffer();
		List<SchemaNodeTagType> nodetagList = null;
		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ tag + "</h1><hr>");
			
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}

			nodetagList = cooldao.retrieveTagsFromNodesSchemaAndDb(schema, db, node, tag);

			// Time selection...use timespan from URL then check what kind of
			// time the folder wants
			final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until,
					timespan);
			BigDecimal lsince = (BigDecimal) trmap.get("since");
			BigDecimal luntil = (BigDecimal) trmap.get("until");
			String outputformat = (String) trmap.get("iovbase");
			if (lsince == null || luntil == null) {
				results.append("<p>Timespan cannot be determined using arguments "
						+ since + " " + until + " " + timespan + "</p>");
				results.append("</body>");
				return results.toString();
			}

			for (final SchemaNodeTagType nodeTagType : nodetagList) {
				results.append("<br><hr>");
				final String nodetag = nodeTagType.getNodeFullpath();

				final String seltag = nodeTagType.getTagName();

				// Convert time range using iov_base
				final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
						db, nodetag);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (final NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath() + " of type "
								+ anode.getNodeIovType());
						selnode = anode;
					}
				}
				if (!outputformat.equals("fullspan")
						&& !selnode.getNodeIovBase().equals(outputformat)) {
					// the format is not good for this folder
					if (outputformat.equals("time")) {
						// convert it into run
						final Map<String, Object> trmapnew = coolutilsdao.getTimeRange(
								lsince.toString(), luntil.toString(), "timerunlb");
						lsince = (BigDecimal) trmapnew.get("since");
						luntil = (BigDecimal) trmapnew.get("until");
						outputformat = "run-lumi";
					} else if (outputformat.equals("run-lumi")) {
						final Map<String, Object> trmapnew = coolutilsdao.getTimeRange(
								lsince.toString(), luntil.toString(), "runlbtime");
						lsince = (BigDecimal) trmapnew.get("since");
						luntil = (BigDecimal) trmapnew.get("until");
						outputformat = "time";
					}
				}

				results.append("<p>Setting the time span to " + lsince + " " + luntil
						+ "</p>");

				final Collection<CoolIovSummary> iovsummaryColl = coolutilsdao
						.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, nodetag,
								seltag, new BigDecimal(0L), new BigDecimal(
										CoolIov.COOL_MAX_DATE));
				if (iovsummaryColl == null) {
					results.append("Empty list of cool iov summary");
					results.append("</body>");
					return results.toString();
				}
				final int channels = iovsummaryColl.size();
				final String resultsDefault = "Empty result string...retrieved list of "
						+ channels + " channels ";
				if (type.equals("text")) {
					log.info("Dumping list as text html");
					results.append(coolutilsdao.dumpIovSummaryAsText(iovsummaryColl,
							lsince, luntil));
				} else if (type.equals("svg")) {
					log.info("Dumping list as svg and html");
					results.append(coolutilsdao.dumpIovSummaryAsSvg(iovsummaryColl,
							lsince, luntil));
				} else {
					results.append(resultsDefault);
				}
				String coverage = "<p>All important runs are covered</p>";
				try {
					coverage = coolutilsdao.checkHoles(iovsummaryColl);
				} catch (final ComaQueryException e) {
					e.printStackTrace();
					coverage = "<p>Error in coverage checking...</p>";
				}
				results.append(coverage);
			}
			results.append("</body>");

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}
	
	

	// /**
	// * <p>
	// * This method is used to parse the timespan string in the URL. Several
	// * format options are then available when asking for input time range.
	// Users
	// * should know in advance, nevertheless, the format of the folder their
	// are
	// * asking for data: time or run-lumi based.
	// * </p>
	// * <p>
	// * List of format depending on timespan field, in bold the type of folder
	// * for which they should be used:
	// * </p>
	// * <p>
	// * <ul>
	// * <li>time : give since and until times in Cool time format (nanoseconds
	// * from Epoch) <b>time</b></li>
	// * <li>date : give since and until times in date format yyyyMMddhhmmss
	// * <b>time</b></li>
	// * <li>runlb : give since and until times in run and lumi bloc as
	// [run]-[lb]
	// * <b>run-lumi</b></li>
	// * <li>runtime: give since and until times in run number, will be
	// converted
	// * in time using start and end of selected runs <b>time</b></li>
	// * <li>daterun: give since and until times in date format yyyyMMddhhmmss,
	// * will be converted in run range <b>run-lumi</b></li>
	// * </ul>
	// * </p>
	// *
	// * @param since
	// * @param until
	// * @param timespan
	// * @return
	// * @throws CoolIOException
	// */
	// protected Map<String, BigDecimal> getTimeRange(String since, String
	// until,
	// String timespan) throws CoolIOException {
	// // Time selection
	// Map<String, BigDecimal> timerangeMap = new HashMap<String, BigDecimal>();
	// BigDecimal _since = null;
	// BigDecimal _until = null;
	// try {
	// if (since.equals("0") && until.equals("Inf")) {
	// // Select full range of COOL IOVs
	// _since = new BigDecimal(0L);
	// _until = new BigDecimal(CoolIov.COOL_MAX_DATE);
	// } else {
	// if (timespan.equals("time")) {
	// // Interpret field as BigDecimal
	// _since = new BigDecimal(since);
	// _until = new BigDecimal(until);
	// } else if (timespan.equals("date")) {
	// // Interpret fields as dates in the yyyyMMddhhmmss format
	// Date st = df.parse(since);
	// Date ut = df.parse(until);
	// _since = new BigDecimal(st.getTime()
	// * CoolIov.TO_NANOSECONDS);
	// _until = new BigDecimal(ut.getTime()
	// * CoolIov.TO_NANOSECONDS);
	// } else if (timespan.equals("runlb")) {
	// String[] sinceargs = since.split("-");
	// String[] untilargs = until.split("-");
	//
	// String lbstr = null;
	// if (sinceargs.length > 0 && !sinceargs[1].isEmpty()) {
	// lbstr = sinceargs[1];
	// }
	// _since = CoolIov.getCoolRunLumi(sinceargs[0], lbstr);
	//
	// lbstr = null;
	// if (untilargs.length > 0 && !untilargs[1].isEmpty()) {
	// lbstr = untilargs[1];
	// }
	// _until = CoolIov.getCoolRunLumi(untilargs[0], lbstr);
	// } else if (timespan.equals("runtime")) {
	// // Convert run request into time range given by start of
	// // since run
	// // and end of until run
	// List<CrViewRuninfo> results = null;
	// try {
	// BigDecimal runstart = new BigDecimal(since);
	// BigDecimal runend = new BigDecimal(until);
	// results = comadao.findRunsInRange(runstart, runend);
	// if (results.size() > 0) {
	// Timestamp runsince = results.get(0).getStartTime();
	// Timestamp rununtil = results.get(0).getEndTime();
	// if (results.size() > 1)
	// rununtil = results.get(results.size() - 1)
	// .getEndTime();
	// _since = new BigDecimal(runsince.getTime()
	// * CoolIov.TO_NANOSECONDS);
	// _until = new BigDecimal(rununtil.getTime()
	// * CoolIov.TO_NANOSECONDS);
	// }
	// } catch (ComaQueryException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// } else if (timespan.equals("daterun")) {
	// // Convert run request into time range given by start of
	// // since run
	// // and end of until run
	// List<CrViewRuninfo> results = null;
	// try {
	// Date st = df.parse(since);
	// Date ut = df.parse(until);
	// results = comadao.findRunsInRange(
	// new Timestamp(st.getTime()),
	// new Timestamp(ut.getTime()));
	// if (results.size() > 0) {
	// Long run = results.get(0).getRunNumber()
	// .longValue();
	// _since = CoolIov
	// .getCoolRunLumi(run.toString(), "0");
	// Long endrun = run + 1L;
	// _until = CoolIov.getCoolRunLumi(endrun.toString(),
	// "0");
	// if (results.size() > 1) {
	// endrun = results.get(results.size() - 1)
	// .getRunNumber().longValue();
	// endrun += 1L;
	// _until = CoolIov.getCoolRunLumi(
	// endrun.toString(), "0");
	// }
	// }
	// } catch (ComaQueryException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// } else {
	// throw new CoolIOException("Cannot search using timespan "
	// + timespan);
	// }
	// }
	// if (_since.longValue()>_until.longValue()) {
	// log.log(Level.SEVERE, "Until time preceeds Since time...!!!!");
	// throw new CoolIOException("Cannot query DB with this range...");
	// }
	// timerangeMap.put("since", _since);
	// timerangeMap.put("until", _until);
	// log.info("Converted "+since+" to "+_since+" and "+until+" to "+_until);
	// } catch (ParseException e) {
	// throw new CoolIOException(e.getMessage());
	// } catch (Exception e) {
	// throw new CoolIOException(e.getMessage());
	// }
	// return timerangeMap;
	// }
}