package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
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

import atlas.coma.exceptions.ComaQueryException;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.utils.SvgRestUtils;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/coolgtag")
@RequestScoped
public class CoolGtagRESTService {

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolUtilsDAO coolutilsdao;

	@Inject
	private Logger log;

	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/{gtag}/checkcoverage")
	public String listIovsStatInNodesSchema(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("gtag") String gtag) {

		log.info("Calling listGlobalTagsTagsInNodesSchema..." + schema + " "
				+ db);
		StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		try {
			results.append("List of NODEs and TAGs iovs statistic associated to"
					+ gtag + "\n");
			results.append("============================================================\n");
			results.append("schema > node ; tag - chanId chanName - niovs iovbase - holes ; [minsince] [minuntil] [maxsince] [maxuntil]\n");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {

				String node = nodeGtagTagType.getNodeFullpath();
				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
						schema, db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath()
								+ " of type " + anode.getNodeIovType());
						selnode = anode;
					}
				}
				List<IovType> iovperchanList = cooldao
						.retrieveIovStatPerChannelFromNodeSchemaAndDb(schema,
								db, node, nodeGtagTagType.getTagName());
				for (IovType aniov : iovperchanList) {
					aniov.setIovBase(selnode.getNodeIovBase());
					results.append(schema + " > " + " "
							+ selnode.getNodeFullpath() + " ; "
							+ nodeGtagTagType.getTagName() + " - "
							+ aniov.getChannelId() + " "
							+ aniov.getChannelName() + " - " + aniov.getNiovs()
							+ " " + aniov.getIovBase() + " - "
							+ aniov.getIovHole() + " ; ["
							+ aniov.getCoolIovMinSince() + "] ["
							+ aniov.getCoolIovMinUntil() + "] ["
							+ aniov.getCoolIovMaxSince() + "] ["
							+ aniov.getCoolIovMaxUntil() + "]\n");
				}

			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/{gtag}/checkholes")
	public String listHolesStatInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {

		log.info("Calling listGlobalTagsTagsInNodesSchema..." + schema + " "
				+ db);
		StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		try {
			results.append("List of NODEs and TAGs iovs statistic associated to"
					+ gtag + "\n");
			results.append("============================================================\n");
			results.append("schema > node ; tag - chanId chanName - niovs iovbase - holes ; [minsince] [minuntil] [maxsince] [maxuntil]\n");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {

				String node = nodeGtagTagType.getNodeFullpath();
				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
						schema, db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath()
								+ " of type " + anode.getNodeIovType());
						selnode = anode;
					}
				}
				List<IovType> iovperchanList = cooldao
						.retrieveHolesStatPerChannelFromNodeSchemaAndDb(schema,
								db, node, nodeGtagTagType.getTagName());
				for (IovType aniov : iovperchanList) {
					aniov.setIovBase(selnode.getNodeIovBase());
					results.append(schema + " > " + " "
							+ selnode.getNodeFullpath() + " ; "
							+ nodeGtagTagType.getTagName() + " - "
							+ aniov.getChannelId() + " "
							+ aniov.getChannelName() + " - " + aniov.getNiovs()
							+ " " + aniov.getIovBase() + " - "
							+ aniov.getIovHole() + " ; ["
							+ aniov.getCoolIovMinSince() + "] ["
							+ aniov.getCoolIovMinUntil() + "] ["
							+ aniov.getCoolIovMaxSince() + "] ["
							+ aniov.getCoolIovMaxUntil() + "]\n");
				}

			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	@GET
	@Produces("application/xml")
	@Path("/{schema}/{db}/{gtag}/iovsummary/list")
	public List<NodeType> listIovsSummaryInNodesSchemaAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {

		log.info("Calling listIovsSummaryInNodesSchema..." + schema + " " + db);
		List<NodeGtagTagType> nodeingtagList = null;
		List<NodeType> nodeList = new ArrayList<NodeType>();
		try {

			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				String node = nodeGtagTagType.getNodeFullpath();
				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
						schema, db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath()
								+ " of type " + anode.getNodeIovType());
						selnode = anode;
					}
				}

				String seltag = nodeGtagTagType.getTagName();

				Collection<CoolIovSummary> summarylist = coolutilsdao
						.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
								selnode.getNodeFullpath(), seltag,
								new BigDecimal(0L), new BigDecimal(
										CoolIov.COOL_MAX_DATE));
				selnode.setIovSummaryList(summarylist);
				nodeList.add(selnode);
			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeList;
	}

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/iovsummary")
	public String listIovsSummaryInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {

		log.info("Calling listIovsSummaryInNodesSchema..." + schema + " " + db);
		StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:24px;} "
				+ "h2 {font-size:18px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:12px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");
		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "</h1><hr>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				results.append("<br><hr>");
				String node = nodeGtagTagType.getNodeFullpath();
				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
						schema, db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath()
								+ " of type " + anode.getNodeIovType());
						selnode = anode;
					}
				}
				results.append("<h2>" + colorseptagstart + schema + " > " + " "
						+ selnode.getNodeFullpath() + " ; "
						+ nodeGtagTagType.getTagName() + colortagend + "</h2>"
						+ "<br>");

				results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

				String seltag = nodeGtagTagType.getTagName();
				// Long niovs = 0L;
				Map<Long, CoolIovSummary> iovsummary = coolutilsdao
						.computeIovSummaryMap(schema, db, node, seltag,
								selnode.getNodeIovBase());

				List<ChannelType> chanList = cooldao
						.retrieveChannelsFromNodeSchemaAndDb(schema, db, node,
								"%");
				Set<Long> channelList = iovsummary.keySet();

				results.append("<p>Number of channels used "
						+ channelList.size() + " over a total of "
						+ chanList.size());

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
			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/{type}/summary")
	public String listIovsSummaryInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag, 
			@PathParam("type") String type) {

		log.info("Calling listIovsSummaryInNodesSchema..." + schema + " " + db);
		StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "</h1><hr>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				results.append("<br><hr>");
				String node = nodeGtagTagType.getNodeFullpath();
//				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
//						schema, db, node);
//				NodeType selnode = null;
//				if (nodes != null && nodes.size() > 0) {
//					for (NodeType anode : nodes) {
//						log.info("Found " + anode.getNodeFullpath()
//								+ " of type " + anode.getNodeIovType());
//						selnode = anode;
//					}
//				}

				String seltag = nodeGtagTagType.getTagName();

				// Select full range of COOL IOVs
				BigDecimal _since = new BigDecimal(0L);
				BigDecimal _until = new BigDecimal(CoolIov.COOL_MAX_DATE);

				Collection<CoolIovSummary> iovsummaryColl = coolutilsdao
						.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
								node, seltag, _since, _until);
				if (iovsummaryColl == null) {
					results.append("Empty list of cool iov summary");
					results.append("</body>");
					return results.toString();
				}
				int channels = iovsummaryColl.size();
				String resultsDefault = "Empty result string...retrieved list of "+channels+" channels ";
				if (type.equals("text")) {
					log.info("Dumping list as text html");
					results.append(coolutilsdao.dumpIovSummaryAsText(iovsummaryColl));
				} else if (type.equals("svg")){
					log.info("Dumping list as svg and html");
					results.append(coolutilsdao.dumpIovSummaryAsSvg(iovsummaryColl));			
				} else {
					results.append(resultsDefault);
				}
				String coverage = "<p>All important runs are covered</p>";
				try {
					coverage = coolutilsdao.checkHoles(iovsummaryColl);
				} catch (ComaQueryException e) {
					e.printStackTrace();
					coverage = "<p>Error in coverage checking...</p>";
				}
				results.append(coverage);
			}
			results.append("</body>");

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/{since}/{until}/{timespan}/{type}/summary")
	public String listIovsSummaryInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag, 
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan,
			@PathParam("type") String type) {

		log.info("Calling listIovsSummaryInNodesSchema..." + schema + " " + db);
		StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		try {
			results.append("<body>");
			results.append("<h1>List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "</h1><hr>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);

			// Time selection...use timespan from URL then check what kind of time the folder wants
			Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until, timespan);
			BigDecimal _since = (BigDecimal)trmap.get("since");
			BigDecimal _until = (BigDecimal)trmap.get("until");
			String outputformat = (String)trmap.get("iovbase");
			if (_since == null || _until == null) {
				results.append("<p>Timespan cannot be determined using arguments "
						+ since+" "+until+" "+timespan + "</p>");
				results.append("</body>");
				return results.toString();
			}

			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				results.append("<br><hr>");
				String node = nodeGtagTagType.getNodeFullpath();

				String seltag = nodeGtagTagType.getTagName();

				// Convert time range using iov_base
				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
						schema, db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath()
								+ " of type " + anode.getNodeIovType());
						selnode = anode;
					}
				}
				if (!outputformat.equals("fullspan") && !selnode.getNodeIovBase().equals(outputformat)) {
					// the format is not good for this folder
					if (outputformat.equals("time")) {
						// convert it into run
						Map<String, Object> trmapnew = coolutilsdao.getTimeRange(_since.toString(), _until.toString(), "timerunlb");
						_since = (BigDecimal)trmapnew.get("since");
						_until = (BigDecimal)trmapnew.get("until");
						outputformat = "run-lumi";
					} else if (outputformat.equals("run-lumi")) {
						Map<String, Object> trmapnew = coolutilsdao.getTimeRange(_since.toString(), _until.toString(), "runlbtime");
						_since = (BigDecimal)trmapnew.get("since");
						_until = (BigDecimal)trmapnew.get("until");
						outputformat = "time";
					}
				}

				results.append("<p>Setting the time span to "
						+ _since + " "+_until+"</p>");

				Collection<CoolIovSummary> iovsummaryColl = coolutilsdao
						.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
								node, seltag, new BigDecimal(0L), new BigDecimal(CoolIov.COOL_MAX_DATE));
				if (iovsummaryColl == null) {
					results.append("Empty list of cool iov summary");
					results.append("</body>");
					return results.toString();
				}
				int channels = iovsummaryColl.size();
				String resultsDefault = "Empty result string...retrieved list of "+channels+" channels ";
				if (type.equals("text")) {
					log.info("Dumping list as text html");
					results.append(coolutilsdao.dumpIovSummaryAsText(iovsummaryColl,_since,_until));
				} else if (type.equals("svg")){
					log.info("Dumping list as svg and html");
					results.append(coolutilsdao.dumpIovSummaryAsSvg(iovsummaryColl,_since,_until));			
				} else {
					results.append(resultsDefault);
				}
				String coverage = "<p>All important runs are covered</p>";
				try {
					coverage = coolutilsdao.checkHoles(iovsummaryColl);
				} catch (ComaQueryException e) {
					e.printStackTrace();
					coverage = "<p>Error in coverage checking...</p>";
				}
				results.append(coverage);
			}
			results.append("</body>");

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	@GET
	@Produces("text/ascii")
	@Path("/{schema}/{db}/{gtag}/iovsummary/gpl")
	public String listIovsSummaryInNodesSchemaGpl(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {

		log.info("Calling listIovsSummaryInNodesSchemaGpl..." + schema + " "
				+ db);
		StringBuffer results = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;

		try {
			results.append("# List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "\n");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				results.append("\n");
				String node = nodeGtagTagType.getNodeFullpath();
				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
						schema, db, node);
				NodeType selnode = null;
				if (nodes != null && nodes.size() > 0) {
					for (NodeType anode : nodes) {
						log.info("Found " + anode.getNodeFullpath()
								+ " of type " + anode.getNodeIovType());
						selnode = anode;
					}
				}
				results.append("# " + schema + " > " + " "
						+ selnode.getNodeFullpath() + " ; "
						+ nodeGtagTagType.getTagName() + " ! " + "\n");

				results.append("# chanId chanName iovbase isvalid/ishole niovs [since] [until] [time span] .... \n");
				List<IovType> iovperchanList = cooldao
						.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(
								schema, db, node, nodeGtagTagType.getTagName());

				for (IovType aniov : iovperchanList) {
					aniov.setIovBase(selnode.getNodeIovBase());
					results.append(+aniov.getChannelId() + " "
							+ aniov.getChannelName() + " " + aniov.getIovBase()
							+ " ");
					Double isvalid = aniov.getIovHole().doubleValue();
					Long since = 0L;
					Long until = 0L;
					Long timespan = 0L;
					if (aniov.getIovBase().equals("time")) {
						since = CoolIov.getTime(aniov.getMiniovSince()
								.toBigInteger());
						until = CoolIov.getTime(aniov.getMaxiovUntil()
								.toBigInteger());
					} else {
						since = CoolIov.getRun(aniov.getMiniovSince()
								.toBigInteger());
						until = CoolIov.getRun(aniov.getMaxiovUntil()
								.toBigInteger());
					}
					timespan = until - since;
					long niovs = aniov.getNiovs();

					if (isvalid > 0) {
						// in this case the isvalid flag has to be 0 for this
						// iov
						results.append(0 + " " + niovs + " " + since + " "
								+ until + " " + timespan + "\n");
					} else {
						results.append(isvalid + " " + niovs + " " + since
								+ " " + until + " " + timespan + "\n");
					}
					// If there is a hole then take its times from other fields
					// and add a line for the previous good iov and the hole
					if (isvalid > 0) {
						results.append(+aniov.getChannelId() + " "
								+ aniov.getChannelName() + " "
								+ aniov.getIovBase() + " ");
						since = until; // the since of the hole is the until of
										// the last good
						if (aniov.getIovBase().equals("time")) {
							until = CoolIov.getTime(aniov.getHoleUntil()
									.toBigInteger());
						} else {
							until = CoolIov.getRun(aniov.getHoleUntil()
									.toBigInteger());
						}
						timespan = until - since;
						results.append(isvalid + " " + 1 + " " + since + " "
								+ until + " " + timespan + "\n");
					}
				}
			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

//	@GET
//	@Produces("text/html")
//	@Path("/{schema}/{db}/{gtag}/iovsummary/svg")
//	public String listIovsSummaryInNodesSchemaSvg(
//			@PathParam("schema") String schema, @PathParam("db") String db,
//			@PathParam("gtag") String gtag) {
//
//		log.info("Calling listIovsSummaryInNodesSchemaSvg..." + schema + " "
//				+ db);
//		StringBuffer results = new StringBuffer();
//		StringBuffer svg = new StringBuffer();
//		List<NodeGtagTagType> nodeingtagList = null;
//		try {
//
//			String colorgoodstart = "<span style=\"color:#20D247\">";
//			String colorwarnstart = "<span style=\"color:#D1A22C\">";
//			String colorsepstart = "<span style=\"color:#1A91C4\">";
//			String colorbadstart = "<span style=\"color:#B43613\">";
//			String colortagend = "</span>";
//
//			results.append("<head><style>" + "h1 {font-size:24px;} "
//					+ "h2 {font-size:18px;}" + "h3 {font-size:14px;}"
//					+ "hr {color:sienna;}" + "p {font-size:12px;}"
//					+ "p.small {line-height:80%;}" + "</style></head>");
//
//			results.append("<body>");
//
//			results.append("<h1>" + colorsepstart
//					+ "List of NODEs and TAGs iovs statistic associated to "
//					+ gtag + colortagend + "</h1><br>");
//			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
//					+ "%", db, gtag);
//
//			String link = createLink(schema, db, gtag, "");
//			results.append("<p>Follow this link for a text output "
//					+ "<a href=\"" + link + ">"
//					+ "text output for iov coverage summary..." + "</a>");
//
//			for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {
//				results.append("<br><hr>");
//				String node = nodeGtagTagType.getNodeFullpath();
//				List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(
//						schema, db, node);
//				NodeType selnode = null;
//				if (nodes != null && nodes.size() > 0) {
//					for (NodeType anode : nodes) {
//						log.info("Found " + anode.getNodeFullpath()
//								+ " of type " + anode.getNodeIovType());
//						selnode = anode;
//					}
//				}
//				results.append("<h3># " + schema + " > " + " "
//						+ selnode.getNodeFullpath() + " ; "
//						+ nodeGtagTagType.getTagName() + " ! " + "</h3><br>");
//
//				SvgRestUtils svgutil = new SvgRestUtils();
//				svgutil.setSvgabsmin(0L);
//				svgutil.setSvgabsmax(CoolIov.COOL_MAX_DATE);
//				String seltag = nodeGtagTagType.getTagName();
//				// Long niovs = 0L;
//				Map<Long, CoolIovSummary> iovsummary = coolutilsdao
//						.computeIovSummaryMap(schema, db, node, seltag,
//								selnode.getNodeIovBase());
//				List<ChannelType> chanList = cooldao
//						.retrieveChannelsFromNodeSchemaAndDb(schema, db, node,
//								"%");
//				Set<Long> channelList = iovsummary.keySet();
//				if (channelList.size() < 20) {
//					svgutil.setLinewidth(10);
//				} else if (channelList.size() < 100) {
//					svgutil.setLinewidth(6);
//				} else if (channelList.size() < 200) {
//					svgutil.setLinewidth(3);
//				} else {
//					svgutil.setLinewidth(1);
//				}
//				results.append("<p>Number of channels used "
//						+ channelList.size() + " over a total of "
//						+ chanList.size());
//				String svgcanvas = "<svg width=\""
//						+ svgutil.getSvglinewidth()
//						+ "px\" height=\""
//						+ (channelList.size() * svgutil.getLinewidth() + svgutil
//								.getSvgheight())
//						+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
//				svg.append(svgcanvas);
//				Long ichan = 0L;
//				for (Long chanid : channelList) {
//					CoolIovSummary coolsumm = iovsummary.get(chanid);
//					if (ichan == 0) {
//						results.append(" | Info in ichan 0: niovs="
//								+ coolsumm.getTotalIovs() + " from "
//								+ coolsumm.getMinsince() + " / "
//								+ coolsumm.getMinuntil() + " to "
//								+ coolsumm.getMaxsince() + " / "
//								+ coolsumm.getMaxuntil() + "</p><br>");
//
//						svgutil.setSvgabsmin(coolsumm.getMinsince());
//						svgutil.setSvgabsmax(coolsumm.getMaxuntil());
//						if (coolsumm.getMinuntil() < CoolIov.COOL_MAX_DATE) {
//							Long iovspan = coolsumm.getMinuntil()
//									- coolsumm.getMinsince();
//							if (iovspan < 1000L)
//								svgutil.setSvgabsmin(coolsumm.getMinuntil()
//										- (Long) (iovspan / 10L));
//							else
//								svgutil.setSvgabsmin(coolsumm.getMinuntil() - 1000L);
//						}
//						if (coolsumm.getMaxuntil() >= CoolIov.COOL_MAX_RUN) {
//							svgutil.setSvgabsmax(coolsumm.getMaxsince() + 1000L);
//						}
//					}
//					log.finer("Node " + node + " tag " + seltag + ": Chan "
//							+ chanid + " is using svgmin "
//							+ svgutil.getSvgabsmin() + " and svgmax "
//							+ svgutil.getSvgabsmax() + " from "
//							+ coolsumm.getMinsince() + " "
//							+ coolsumm.getMinuntil() + " "
//							+ coolsumm.getMaxsince());
//					Map<Long, IovRange> timeranges = coolsumm.getIovRanges();
//					if (timeranges != null) {
//						Set<Long> sincetimes = timeranges.keySet();
//						for (Long asince : sincetimes) {
//							IovRange ivr = timeranges.get(asince);
//							svg.append(svgutil.getSvgLine(ivr.getSince(),
//									ivr.getUntil(), ichan,
//									coolsumm.getIovbase(), ivr.getIshole()));
//						}
//					}
//					ichan++;
//				}
//				results.append(svg.toString() + "</svg><br>");
//				svg.delete(0, svg.length());
//			}
//			results.append("</body>");
//		} catch (CoolIOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return results.toString();
//	}

	protected String createLink(String schema, String db, String tag,
			String command) {
		String urlbase = null;
		String commandurl = null;
		try {
			urlbase = "https://" + InetAddress.getLocalHost().getHostName()
					+ ":8443/JBRestCool/rest";
			commandurl = ("coolgtag/" + schema + "/" + db + "/" + tag + "/" + command);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlbase + "/" + commandurl;
	}
}
