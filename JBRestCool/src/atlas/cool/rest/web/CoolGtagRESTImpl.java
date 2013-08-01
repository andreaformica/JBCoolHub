/**
 * 
 */
package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.coma.exceptions.ComaQueryException;
import atlas.cool.dao.CondToolsDAO;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.summary.model.CondNodeStats;
import atlas.cool.summary.model.CondSchema;
import atlas.cool.summary.model.D3TreeMap;

/**
 * @author formica
 * 
 */
@RequestScoped
public class CoolGtagRESTImpl implements ICoolGtagREST {

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolUtilsDAO coolutilsdao;
	@Inject
	private CondToolsDAO condtoolsdao;

	@Inject
	private Logger log;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listIovsSummaryInNodesSchemaAsList(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeType> listIovsSummaryInNodesSchemaAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {

		log.info("Calling listIovsSummaryInNodesSchema..." + schema + " " + db);
		List<NodeGtagTagType> nodeingtagList = null;
		final List<NodeType> nodeList = new ArrayList<NodeType>();
		try {

			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db,
					gtag);
			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				final String node = nodeGtagTagType.getNodeFullpath();
				final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
						db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (final NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath() + " of type "
								+ anode.getNodeIovType());
						selnode = anode;
					}
				}
				if (selnode == null) {
					log.log(Level.SEVERE, "Cannot use null node");
					continue;
				}
				final String seltag = nodeGtagTagType.getTagName();

				final Collection<CoolIovSummary> summarylist = coolutilsdao
						.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
								selnode.getNodeFullpath(), seltag, new BigDecimal(0L),
								new BigDecimal(CoolIov.COOL_MAX_DATE));
				selnode.setIovSummaryList(summarylist);
				nodeList.add(selnode);
			}

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listGlobalTagsInSchema(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	@Override
	public List<GtagType> listGlobalTagsInSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {

		try {
			String coolschema = schema + "%";
			if (schema.equals("all")) {
				coolschema = "%";
			}
			String globaltag = "%" + gtag + "%";
			if (gtag.equals("all")) {
				globaltag = "%";
			}

			return cooldao.retrieveGtagsFromSchemaAndDb(coolschema, db, globaltag);
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listIovsSummaryInNodesSchema(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String listIovsSummaryInNodesSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("gtag") final String gtag,
			@PathParam("type") final String type) {
		log.info("Calling listIovsSummaryInNodesSchema..." + schema + " " + db);
		final StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		final String header = "<head><style>" + "h1 {font-size:20px;} "
				+ "h2 {font-size:18px;}" + "h3 {font-size:16px;}" + "hr {color:sienna;}"
				+ "p {font-size:14px;}" + "p.small {line-height:80%;}"
				+ "</style></head>";
		try {
			results.append(header);
			results.append("<body>");
			results.append("<p>Choose output mode: ");
			results.append("<a href=" + createLink(schema, db, gtag, "text/summary")
					+ ">text</a> or ");
			results.append("<a href=" + createLink(schema, db, gtag, "svg/summary")
					+ ">svg</a></p>");
			results.append("<h2>List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "</h2><br><hr>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db,
					gtag);
			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				final String node = nodeGtagTagType.getNodeFullpath();
				// List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
				// schema, db, node);
				// NodeType selnode = null;
				// if (nodes != null && nodes.size() > 0) {
				// for (NodeType anode : nodes) {
				// log.info("Found " + anode.getNodeFullpath()
				// + " of type " + anode.getNodeIovType());
				// selnode = anode;
				// }
				// }

				final String seltag = nodeGtagTagType.getTagName();

				// Select full range of COOL IOVs
				final BigDecimal lsince = new BigDecimal(0L);
				final BigDecimal luntil = new BigDecimal(CoolIov.COOL_MAX_DATE);

				final Collection<CoolIovSummary> iovsummaryColl = coolutilsdao
						.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, node,
								seltag, lsince, luntil);
				if (iovsummaryColl == null) {
					results.append("Empty list of cool iov summary");
					results.append("</body>");
					return results.toString();
				}
				final int channels = iovsummaryColl.size();
				final String resultsDefault = "Empty result string...retrieved list of "
						+ channels + " channels ";
				if (type.equals("text")) {
					log.fine("Dumping list as text html");
					results.append(coolutilsdao.dumpIovSummaryAsText(iovsummaryColl));
					try {
						final String coverage = coolutilsdao.checkHoles(iovsummaryColl);
						results.append(coverage);
					} catch (final ComaQueryException e) {
						e.printStackTrace();
						results.append("Error while checking run coverage");
					}
				} else if (type.equals("svg")) {
					log.fine("Dumping list as svg and html");
					results.append(coolutilsdao.dumpIovSummaryAsSvg(iovsummaryColl));
				} else {
					results.append(resultsDefault);
				}
			}
			results.append("</body>");

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
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
	public String listIovsSummaryInNodesSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("gtag") final String gtag,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan,
			@PathParam("type") final String type) {

		log.info("Calling listIovsSummaryInNodesSchema..." + schema + " " + db);
		final StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "</h1><hr>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db,
					gtag);

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

			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				results.append("<br><hr>");
				final String node = nodeGtagTagType.getNodeFullpath();

				final String seltag = nodeGtagTagType.getTagName();

				// Convert time range using iov_base
				final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
						db, node);
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
						.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, node,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listIovsSummaryInNodesSchemaGpl(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String listIovsSummaryInNodesSchemaGpl(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		log.info("Calling listIovsSummaryInNodesSchemaGpl..." + schema + " " + db);
		final StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;

		try {
			results.append("# List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "\n");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db,
					gtag);
			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				results.append("\n");
				final String node = nodeGtagTagType.getNodeFullpath();
				final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
						db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (final NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath() + " of type "
								+ anode.getNodeIovType());
						selnode = anode;
					}
				}
				if (selnode == null) {
					log.log(Level.SEVERE, "Cannot use null node");
					continue;
				}
				results.append("# " + schema + " > " + " " + selnode.getNodeFullpath()
						+ " ; " + nodeGtagTagType.getTagName() + " ! " + "\n");

				results.append("# chanId chanName iovbase isvalid/ishole niovs [since] [until] [time span] .... \n");
				final List<IovType> iovperchanList = cooldao
						.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(schema, db,
								node, nodeGtagTagType.getTagName());

				for (final IovType aniov : iovperchanList) {
					aniov.setIovBase(selnode.getNodeIovBase());
					results.append(+aniov.getChannelId() + " " + aniov.getChannelName()
							+ " " + aniov.getIovBase() + " ");
					final Double isvalid = aniov.getIovHole().doubleValue();
					Long since = 0L;
					Long until = 0L;
					Long timespan = 0L;
					if (aniov.getIovBase().equals("time")) {
						since = CoolIov.getTime(aniov.getMiniovSince().toBigInteger());
						until = CoolIov.getTime(aniov.getMaxiovUntil().toBigInteger());
					} else {
						since = CoolIov.getRun(aniov.getMiniovSince().toBigInteger());
						until = CoolIov.getRun(aniov.getMaxiovUntil().toBigInteger());
					}
					timespan = until - since;
					final long niovs = aniov.getNiovs();

					if (isvalid > 0) {
						// in this case the isvalid flag has to be 0 for this
						// iov
						results.append(0 + " " + niovs + " " + since + " " + until + " "
								+ timespan + "\n");
					} else {
						results.append(isvalid + " " + niovs + " " + since + " " + until
								+ " " + timespan + "\n");
					}
					// If there is a hole then take its times from other fields
					// and add a line for the previous good iov and the hole
					if (isvalid > 0) {
						results.append(+aniov.getChannelId() + " "
								+ aniov.getChannelName() + " " + aniov.getIovBase() + " ");
						since = until; // the since of the hole is the until of
										// the last good
						if (aniov.getIovBase().equals("time")) {
							until = CoolIov.getTime(aniov.getHoleUntil().toBigInteger());
						} else {
							until = CoolIov.getRun(aniov.getHoleUntil().toBigInteger());
						}
						timespan = until - since;
						results.append(isvalid + " " + 1 + " " + since + " " + until
								+ " " + timespan + "\n");
					}
				}
			}

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	/**
	 * @param schema
	 * @param db
	 * @param tag
	 * @param command
	 * @return
	 */
	protected String createLink(final String schema, final String db, final String tag,
			final String command) {
		String urlbase = null;
		String commandurl = null;
		try {
			urlbase = "http://" + InetAddress.getLocalHost().getHostName()
					+ ":8080/JBRestCool/rest";
			commandurl = "coolgtag/" + schema + "/" + db + "/" + tag + "/" + command;
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlbase + "/" + commandurl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ICoolGtagREST#listSchemaSummaryInSchemaDb(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/schemasummary")
	public D3TreeMap listSchemaSummaryInSchemaDb(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		D3TreeMap dt3m = null;
		Set<CondSchema> schemaList = new HashSet<CondSchema>();
		try {
			final List<CondNodeStats> nodestatlist = condtoolsdao
					.getNodeStatsForSchemaDb(schema + "%", db, gtag);
			for (final CondNodeStats condNodeStats : nodestatlist) {
				final CondSchema temp = new CondSchema(condNodeStats.getSchemaName(),
						condNodeStats.getDbName(), new HashSet<CondNodeStats>());
				if (schemaList.contains(temp)) {
					// update existing object
					Iterator<CondSchema> it = schemaList.iterator();
					while (it.hasNext()) {
						CondSchema cs = (CondSchema) it.next();
						if (cs.equals(temp)) {
							Set<CondNodeStats> nodestats = cs.getChildren();
							if (nodestats == null) {
								nodestats = new HashSet<CondNodeStats>();
							}
							nodestats.add(condNodeStats);
							break;
						}
					}
					/*
					 * Set<CondNodeStats> nodestats = objschema.getChildren();
					 * if (nodestats == null) { nodestats = new
					 * HashSet<CondNodeStats>(); } nodestats.add(condNodeStats);
					 */
				} else {
					temp.getChildren().add(condNodeStats);
					schemaList.add(temp);
				}
				dt3m = new D3TreeMap("schemasummary", schemaList);
			}
		} catch (final CoolIOException e) {
			e.printStackTrace();
		}
		return dt3m;
	}

}
