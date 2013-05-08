package atlas.cool.rest.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.CoolIovSummary.IovRange;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;

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

	final Integer svglinewidth = 1000;
	Long svgabsmin = 0L;
	Long svgabsmax = CoolIov.COOL_MAX_DATE;
	Integer linewidth = 3;
	Long svgheight = 10L;

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
				Map<Long, CoolIovSummary> iovsummary = coolutilsdao.computeIovSummaryMap(
						schema, db, node, seltag, selnode.getNodeIovBase());
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
									&& (ivr.since.compareTo(minsince) != 0)) {
								colortagstart = colorwarntagstart;
							}
							String holedump = "";
							if (ivr.ishole) {
								colortagstart = colorbadtagstart;
								long timespan = ivr.until - ivr.since;
								if (coolsumm.getIovbase().equals("time")) {
									timespan = timespan / 1000L;
								}
								holedump = "[" + timespan + "] ";
							}
							iovDump = colortagstart
									+ ivr.niovs
									+ " ["
									+ CoolIov.getCoolTimeString(ivr.since,
											coolsumm.getIovbase())
									+ "] ["
									+ CoolIov.getCoolTimeString(ivr.until,
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
/*
	protected Map<Long, CoolIovSummary> computeIovRangeMap(String schema,
			String db, String node, String tag, String iovbase) {
		try {

			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(schema,
							db, node, tag);

			svgabsmin = 0L;
			svgabsmax = CoolIov.COOL_MAX_DATE;
			Long niovs = 0L;
			Map<Long, CoolIovSummary> iovsummary = new HashMap<Long, CoolIovSummary>();
			for (IovType aniov : iovperchanList) {
				aniov.setIovBase(iovbase);
				Double isvalid = aniov.getIovHole().doubleValue();
				Long since = 0L;
				Long until = 0L;
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

				// niovs += aniov.getNiovs();

				CoolIovSummary iovsumm = null;
				if (iovsummary.containsKey(aniov.getChannelId())) {
					iovsumm = iovsummary.get(aniov.getChannelId());
				} else {
					iovsumm = new CoolIovSummary(aniov.getChannelId());
					iovsumm.setIovbase(aniov.getIovBase());
					iovsumm.setChannelName(aniov.getChannelName());
				}
				try {

					iovsumm.appendIov(since, until, aniov.getNiovs(), false);
					// If there is a hole then take its times from other
					// fields and add a line for the previous good iov and
					// the hole
					if (isvalid > 0) {

						since = until; // the since of the hole is the until
										// of the last good
						if (aniov.getIovBase().equals("time")) {
							until = CoolIov.getTime(aniov.getHoleUntil()
									.toBigInteger());
						} else {
							until = CoolIov.getRun(aniov.getHoleUntil()
									.toBigInteger());
						}
						iovsumm.appendIov(since, until, 0L, true);
					}
					iovsummary.put(aniov.getChannelId(), iovsumm);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return iovsummary;

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
*/
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/iovsummary/svg")
	public String listIovsSummaryInNodesSchemaSvg(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {

		log.info("Calling listIovsSummaryInNodesSchemaSvg..." + schema + " "
				+ db);
		StringBuffer results = new StringBuffer();
		StringBuffer svg = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		try {

			String colorgoodstart = "<span style=\"color:#20D247\">";
			String colorwarnstart = "<span style=\"color:#D1A22C\">";
			String colorsepstart = "<span style=\"color:#1A91C4\">";
			String colorbadstart = "<span style=\"color:#B43613\">";
			String colortagend = "</span>";

			results.append("<head><style>" + "h1 {font-size:24px;} "
					+ "h2 {font-size:18px;}" + "h3 {font-size:14px;}"
					+ "hr {color:sienna;}" + "p {font-size:12px;}"
					+ "p.small {line-height:80%;}" + "</style></head>");

			results.append("<body>");

			results.append("<h1>" + colorsepstart
					+ "List of NODEs and TAGs iovs statistic associated to "
					+ gtag + colortagend + "</h1><br>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			
			String link = createLink(schema, db, gtag, "");
			results.append("<p>Follow this link for a text output "
					+"<a href=\""+link+">"+"text output for iov coverage summary..."+"</a>");

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
				results.append("<h3># " + schema + " > " + " "
						+ selnode.getNodeFullpath() + " ; "
						+ nodeGtagTagType.getTagName() + " ! " + "</h3><br>");

				svgabsmin = 0L;
				svgabsmax = CoolIov.COOL_MAX_DATE;
				String seltag = nodeGtagTagType.getTagName();
				// Long niovs = 0L;
				Map<Long, CoolIovSummary> iovsummary = coolutilsdao.computeIovSummaryMap(
						schema, db, node, seltag, selnode.getNodeIovBase());
				List<ChannelType> chanList = cooldao
						.retrieveChannelsFromNodeSchemaAndDb(schema, db, node,
								"%");
				Set<Long> channelList = iovsummary.keySet();
				if (channelList.size() < 20) {
					linewidth = 10;
				} else if (channelList.size() < 100) {
					linewidth = 6;
				} else if (channelList.size() < 200) {
					linewidth = 3;
				} else {
					linewidth = 1;
				}
				results.append("<p>Number of channels used "
						+ channelList.size() + " over a total of "
						+ chanList.size());
				String svgcanvas = "<svg width=\""
						+ svglinewidth
						+ "px\" height=\""
						+ (channelList.size() * linewidth + svgheight)
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

						svgabsmin = coolsumm.getMinsince();
						svgabsmax = coolsumm.getMaxuntil();
						if (coolsumm.getMinuntil() < CoolIov.COOL_MAX_DATE) {
							Long iovspan = coolsumm.getMinuntil()
									- coolsumm.getMinsince();
							if (iovspan < 1000L)
								svgabsmin = coolsumm.getMinuntil()
										- (Long) (iovspan / 10L);
							else
								svgabsmin = coolsumm.getMinuntil() - 1000L;
						}
						if (coolsumm.getMaxuntil() >= CoolIov.COOL_MAX_RUN) {
							svgabsmax = coolsumm.getMaxsince() + 1000L;
						}
					}
					log.finer("Node " + node + " tag " + seltag + ": Chan "
							+ chanid + " is using svgmin " + svgabsmin
							+ " and svgmax " + svgabsmax + " from "
							+ coolsumm.getMinsince() + " "
							+ coolsumm.getMinuntil() + " "
							+ coolsumm.getMaxsince());
					Map<Long, IovRange> timeranges = coolsumm.getIovRanges();
					if (timeranges != null) {
						Set<Long> sincetimes = timeranges.keySet();
						for (Long asince : sincetimes) {
							IovRange ivr = timeranges.get(asince);
							svg.append(getSvgLine(ivr.since, ivr.until, ichan,
									coolsumm.getIovbase(), ivr.ishole));
						}
					}
					ichan++;
				}
				results.append(svg.toString() + "</svg><br>");
				svg.delete(0, svg.length());
			}
			results.append("</body>");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	/**
	 * Description: convert point from iov in real times to iov in a range
	 * 0-1000 for svg visualization. In the following , INF is the infinity
	 * (from Cool iovs), MW is the maximum width (set to 1000). f(x) = a.x + b
	 * ==> f(t0) = a.t0 + b = 0 a = sinTh / cosTh = MW/(INF-t0) ==> t0 . MW/
	 * (INF-t0) + b = 0 ==> b = -t0.MW/(INF-t0)
	 * 
	 * @param point
	 * @param endrange
	 * @return
	 */
	private Double convert(Long point, Long endrange) {
		// map the point from the range svgabsmin - inf to 0 svglinewidth
		Double b = -(svgabsmin.doubleValue() * (Double) (svglinewidth
				.doubleValue() / (endrange.doubleValue() - svgabsmin
				.doubleValue())));
		Double newpoint = (point.doubleValue())
				* (Double) (svglinewidth.doubleValue() / (endrange
						.doubleValue() - svgabsmin.doubleValue())) + b;
		// log.info("conversion gives " + point.doubleValue() + " ==> " +
		// newpoint
		// + " using " + svgabsmin);
		return newpoint;
	}

	protected String getSvgLine(Long start, Long end, Long ichan,
			String iovtype, Boolean ishole) {
		StringBuffer svgline = new StringBuffer();

		Long infinity = new Date().getTime();
		if (!iovtype.equals("time")) {
			// infinity = CoolIov.COOL_MAX_RUN;
			infinity = svgabsmax;
		}

		if (start > infinity)
			start = infinity;
		if (end > infinity)
			end = infinity;
		if (start < svgabsmin) {
			start = svgabsmin;
		}
		log.info("Using start = " + start + " -> " + convert(start, infinity)
				+ " end= " + end + " -> " + convert(end, infinity)
				+ " with svgmin " + svgabsmin + " and svgmax " + svgabsmax);
		if (ishole) {
			/*
			 * svgline = "<circle"; Double radius = ((convert(end, infinity) -
			 * convert(start,infinity)) / 2); Double xcenter =
			 * convert(start,infinity) + (radius); svgline +=
			 * (" cx=\""+xcenter+"\" cy=\""
			 * +ichan*linewidth+"\" r=\""+radius*5+"\" ");
			 */
			// svgline = "<rect";
			// Double width = ((convert(end, infinity) -
			// convert(start,infinity)));
			// svgline +=
			// (" x=\""+convert(start,infinity)+"\" y=\""+0+"\" width=\""+width+"\" height=\""+svgheight*linewidth+"\"");
			svgline.append("<line");
			svgline.append(" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ (ichan * linewidth + svgheight) + "\" x2=\""
					+ convert(end, infinity) + "\" y2=\""
					+ (ichan * linewidth + svgheight) + "\"");
			svgline.append(" stroke=\"red\" stroke-width=\"" + linewidth
					+ "\"/>");

			// Now add a vertical line plus a text giving the limits in the hole
			// time range
			svgline.append("<line");
			svgline.append(" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ (2) + "\" x2=\"" + convert(start, infinity) + "\" y2=\""
					+ (ichan * linewidth + svgheight) + "\"");
			svgline.append(" stroke=\"black\" stroke-width=\"" + 1 + "\"/>");
			svgline.append("<text x=\"" + convert(start, infinity)
					+ "\" y=\"2\">");
			svgline.append(CoolIov.getCoolTimeString(start, iovtype));
			svgline.append("</text>");

		} else {
			svgline.append("<line");
			svgline.append(" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ ichan * linewidth + "\" x2=\"" + convert(end, infinity)
					+ "\" y2=\"" + ichan * linewidth + "\"");
			svgline.append(" stroke=\"green\" stroke-width=\"" + linewidth
					+ "\"/>");
		}

		return svgline.toString();
	}

	protected String createLink(String schema, String db, 
			String tag, String command) {
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
