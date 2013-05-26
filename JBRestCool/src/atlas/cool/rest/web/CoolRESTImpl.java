package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
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
 * This class implements the RESTful services to read the contents of the Cool
 * tables via PL/SQL.
 * 
 * @author formica
 * 
 */
@RequestScoped
public class CoolRESTImpl {

	@Inject
	protected CoolDAO cooldao;
	@Inject
	protected CoolUtilsDAO coolutilsdao;
	@Inject
	protected ComaCbDAO comadao;
	@Inject
	protected Logger log;

	protected SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

	public CoolRESTImpl() {
		super();
	}

	protected void setSort(String orderByName) {
		atlas.cool.interceptors.WebRestContextHolder
				.put("OrderBy", orderByName);
		if (orderByName == null || orderByName.isEmpty()) {
			atlas.cool.interceptors.WebRestContextHolder.cleanupThread();
		}
	}

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

	public NodeType listIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		NodeType selnode = null;
		try {
			// Time selection
			Map<String, BigDecimal> trmap = getTimeRange(since, until, timespan);
			BigDecimal _since = trmap.get("since");
			BigDecimal _until = trmap.get("until");

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
				throw new CoolIOException(
						"Wrong REST syntax...refer to documentation");
			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	public NodeType listIovsInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("sort") String sort,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		NodeType selnode = null;
		try {
			String orderByName = "";
			String[] sortcolumns = sort.split("/");
			if (sortcolumns.length >= 0) {
				Map<String, Boolean> colmap = new LinkedHashMap<String, Boolean>();
				for (int i = 0; i < sortcolumns.length; i++) {
					String[] colsort = sortcolumns[i].split("-");
					String colname = colsort[0];
					String sortkey = (colsort[1] != null) ? colsort[1] : "ASC";
					Boolean sortflag = true;
					if (sortkey.equals("DESC"))
						sortflag = false;
					colmap.put(colname, sortflag);
				}
				orderByName = QueryTools
						.getOrderedBy(new CoolIovType(), colmap);
			}
			setSort(orderByName);
			selnode = listIovsInNodesSchemaTagRangeAsList(schema, db, fld, tag,
					channel, chansel, since, until, timespan);
			setSort(null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	public NodeType listPayloadInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {
		NodeType selnode = null;
		try {
			// Time selection
			Map<String, BigDecimal> trmap = getTimeRange(since, until, timespan);
			BigDecimal _since = trmap.get("since");
			BigDecimal _until = trmap.get("until");

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
				throw new CoolIOException(
						"Wrong REST syntax...refer to documentation");
			}
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	public NodeType listPayloadInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("sort") String sort,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		NodeType selnode = null;
		try {
			String orderByName = "";
			String[] sortcolumns = sort.split("/");
			if (sortcolumns.length >= 0) {
				Map<String, Boolean> colmap = new LinkedHashMap<String, Boolean>();
				for (int i = 0; i < sortcolumns.length; i++) {
					String[] colsort = sortcolumns[i].split("-");
					String colname = colsort[0];
					String sortkey = (colsort[1] != null) ? colsort[1] : "ASC";
					Boolean sortflag = true;
					if (sortkey.equals("DESC"))
						sortflag = false;
					colmap.put(colname, sortflag);
				}
				orderByName = QueryTools
						.getOrderedBy(new CoolIovType(), colmap);
			}
			setSort(orderByName);
			selnode = listPayloadInNodesSchemaTagRangeAsList(schema, db, fld,
					tag, channel, chansel, since, until, timespan);
			setSort(null);
			// InNodesSchemaTagDateTimeRangeAsList(schema, db, fld, tag,
			// channel, chansel, since, until, timespan);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selnode;
	}

	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..."
				+ schema + " " + db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;
		try {
			// Time selection
			Map<String, BigDecimal> trmap = getTimeRange(since, until, timespan);
			BigDecimal _since = trmap.get("since");
			BigDecimal _until = trmap.get("until");
			if (_since == null || _until == null) {
				CoolIovSummary iovsumm = new CoolIovSummary();
				iovsumm.setSummary("Wrong time interval has been used: since or until times are null");
				summarylist = new ArrayList<CoolIovSummary>();
				summarylist.add(iovsumm);
				return summarylist;
			}
			summarylist = coolutilsdao
					.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
							fld, tag, _since, _until);

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summarylist;
	}

	/**
	 * <p>
	 * This method is used to parse the timespan string in the URL. Several
	 * format options are then available when asking for input time range. Users
	 * should know in advance, nevertheless, the format of the folder their are
	 * asking for data: time or run-lumi based.
	 * </p>
	 * <p>
	 * List of format depending on timespan field, in bold the type of folder
	 * for which they should be used:
	 * </p>
	 * <p>
	 * <ul>
	 * <li>time : give since and until times in Cool time format (nanoseconds
	 * from Epoch) <b>time</b></li>
	 * <li>date : give since and until times in date format yyyyMMddhhmmss
	 * <b>time</b></li>
	 * <li>runlb : give since and until times in run and lumi bloc as [run]-[lb]
	 * <b>run-lumi</b></li>
	 * <li>runtime: give since and until times in run number, will be converted
	 * in time using start and end of selected runs <b>time</b></li>
	 * <li>daterun: give since and until times in date format yyyyMMddhhmmss,
	 * will be converted in run range <b>run-lumi</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param since
	 * @param until
	 * @param timespan
	 * @return
	 * @throws CoolIOException
	 */
	protected Map<String, BigDecimal> getTimeRange(String since, String until,
			String timespan) throws CoolIOException {
		// Time selection
		Map<String, BigDecimal> timerangeMap = new HashMap<String, BigDecimal>();
		BigDecimal _since = null;
		BigDecimal _until = null;
		try {
			if (since.equals("0") && until.equals("Inf")) {
				// Select full range of COOL IOVs
				_since = new BigDecimal(0L);
				_until = new BigDecimal(CoolIov.COOL_MAX_DATE);
			} else {
				if (timespan.equals("time")) {
					// Interpret field as BigDecimal
					_since = new BigDecimal(since);
					_until = new BigDecimal(until);
				} else if (timespan.equals("date")) {
					// Interpret fields as dates in the yyyyMMddhhmmss format
					Date st = df.parse(since);
					Date ut = df.parse(until);
					_since = new BigDecimal(st.getTime()
							* CoolIov.TO_NANOSECONDS);
					_until = new BigDecimal(ut.getTime()
							* CoolIov.TO_NANOSECONDS);
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
				} else if (timespan.equals("runtime")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						BigDecimal runstart = new BigDecimal(since);
						BigDecimal runend = new BigDecimal(until);
						results = comadao.findRunsInRange(runstart, runend);
						if (results.size() > 0) {
							Timestamp runsince = results.get(0).getStartTime();
							Timestamp rununtil = results.get(0).getEndTime();
							if (results.size() > 1)
								rununtil = results.get(results.size() - 1)
										.getEndTime();
							_since = new BigDecimal(runsince.getTime()
									* CoolIov.TO_NANOSECONDS);
							_until = new BigDecimal(rununtil.getTime()
									* CoolIov.TO_NANOSECONDS);
						}
					} catch (ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (timespan.equals("daterun")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						Date st = df.parse(since);
						Date ut = df.parse(until);
						results = comadao.findRunsInRange(
								new Timestamp(st.getTime()),
								new Timestamp(ut.getTime()));
						if (results.size() > 0) {
							Long run = results.get(0).getRunNumber()
									.longValue();
							_since = CoolIov
									.getCoolRunLumi(run.toString(), "0");
							Long endrun = run + 1L;
							_until = CoolIov.getCoolRunLumi(endrun.toString(),
									"0");
							if (results.size() > 1) {
								endrun = results.get(results.size() - 1)
										.getRunNumber().longValue();
								endrun += 1L;
								_until = CoolIov.getCoolRunLumi(
										endrun.toString(), "0");
							}
						}
					} catch (ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					throw new CoolIOException("Cannot search using timespan "
							+ timespan);
				}
			}
			if (_since.longValue()>_until.longValue()) {
				log.log(Level.SEVERE, "Until time preceeds Since time...!!!!");
				throw new CoolIOException("Cannot query DB with this range...");
			}
			timerangeMap.put("since", _since);
			timerangeMap.put("until", _until);
			log.info("Converted "+since+" to "+_since+" and "+until+" to "+_until);
		} catch (ParseException e) {
			throw new CoolIOException(e.getMessage());
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
		return timerangeMap;
	}
}