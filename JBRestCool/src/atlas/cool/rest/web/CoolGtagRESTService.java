package atlas.cool.rest.web;

import java.util.Date;
import java.util.HashMap;
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
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.utils.CoolIovSummary;
import atlas.cool.rest.utils.CoolIovSummary.IovRange;

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
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
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
				List<IovType> iovperchanList = cooldao
						.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(
								schema, db, node, nodeGtagTagType.getTagName());
				IovType previov = null;
				String previovDump = null;
				String comment = "";
				String colortagstart = colorgoodtagstart;
				for (IovType aniov : iovperchanList) {
					aniov.setIovBase(selnode.getNodeIovBase());
					if (previov == null) {
						previov = aniov;
						results.append("<p class=\"small\">"
								+ aniov.getChannelId() + " "
								+ aniov.getChannelName() + " - "
								+ aniov.getIovBase() + " : ");
					}
					String since = aniov.getCoolIovMinSince();
					String until = aniov.getCoolIovMaxUntil();
					long niovs = aniov.getNiovs();

					// Now print the previous if no holes are found...
					// if (previovDump != null) {
					if (aniov.getChannelId() != previov.getChannelId()) {
						log.info("Channel ID changed..." + aniov.getChannelId()
								+ " previous was " + previov.getChannelId()
								+ " iovdump is " + previovDump);
						if (previovDump != null) {
							results.append(" | " + previovDump + "</p>");
							previovDump = null;
							comment = "";
							colortagstart = colorgoodtagstart;
							results.append("<p class=\"small\">"
									+ aniov.getChannelId() + " "
									+ aniov.getChannelName() + " - "
									+ aniov.getIovBase() + " : ");
						}
					}

					if (aniov.getIovHole().doubleValue() == 0) {
						// If the present iov is not a hole...then print the
						// previous
						if (previovDump != null)
							results.append(" | " + previovDump);
						comment = "";
						previovDump = null;
						colortagstart = colorgoodtagstart;
						// In this part, we create a string for the NEXT iov,
						// without writing it, since
						// we may need to change it when holes are present....
						if (previov.getChannelId() != aniov.getChannelId()
								&& (!(previov.getCoolIovMinSince().equals(aniov
										.getCoolIovMinSince())) || !(previov
										.getCoolIovMaxUntil().equals(aniov
										.getCoolIovMaxUntil())))) {
							colortagstart = colorwarntagstart;
							comment = "WARN";
						}

						previovDump = colortagstart + comment + " " + niovs
								+ " [" + since + "] [" + until + "] "
								+ colortagend;

					} else {
						// Modify the previous dump string
						// In case we see a hole, takes also the previous since
						// time, add niovs ....
						if (previovDump != null) {
							previovDump = colortagstart
									+ (previov.getNiovs() + 1L) + " ["
									+ previov.getCoolIovMinSince() + "] ["
									+ aniov.getCoolIovMaxUntil() + "] "
									+ colortagend;
						} else {
							colortagstart = colorgoodtagstart;
							previovDump = colortagstart + niovs + " [" + since
									+ "] [" + until + "] " + colortagend;
						}
						results.append(" | " + previovDump);
						// Now add the hole...
						previovDump = null;
						colortagstart = colorbadtagstart;
						comment = "HOLE";
						Double hole = aniov.getIovHole().doubleValue();
						if (selnode.getNodeIovBase().startsWith("time")) {
							hole = hole / (1000000000.); // Express the hole in
															// seconds !
						}
						since = aniov.getCoolIovMaxUntil();
						until = aniov.getCoolHoleUntil();
						previovDump = colortagstart + comment + " [" + since
								+ "] [" + until + "] " + "[" + hole.intValue()
								+ "] " + colortagend;
						results.append(" | " + previovDump);
						previovDump = null;
					}
					previov = aniov;
				}
				results.append(" | " + previovDump + "</p>");
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
			results.append("<head><style>" + "h1 {font-size:25px;} "
					+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
					+ "hr {color:sienna;}" + "p {font-size:14px;}"
					+ "p.small {line-height:80%;}" + "</style></head>");

			results.append("<body>");

			results.append("<h1># List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "</h1><br>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			int itag = 0;
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
				results.append("<h3># " + schema + " > " + " "
						+ selnode.getNodeFullpath() + " ; "
						+ nodeGtagTagType.getTagName() + " ! " + "</h3><br>");

				// results.append("<p># chanId chanName iovbase isvalid/ishole niovs [since] [until] [time span] .... </p><br>");
				List<IovType> iovperchanList = cooldao
						.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(
								schema, db, node, nodeGtagTagType.getTagName());
				List<ChannelType> chanList = cooldao
						.retrieveChannelsFromNodeSchemaAndDb(schema, db, node,
								"%");
				Integer viewboxy1 = 0;
				if (itag > 0) {
					viewboxy1 += (chanList.size() + 10);
				}
				// String svgcanvas =
				// "<svg width=\""+svglinewidth+"px\" height=\""+chanList.size()+"px\" viewBox=\"0 "+viewboxy1+" "+svglinewidth+" "+chanList.size()+" >";
				String svgcanvas = "<svg width=\""
						+ svglinewidth
						+ "px\" height=\""
						+ (chanList.size() * linewidth + 10)
						+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
				svg.append(svgcanvas);
				IovType previov = null;
				itag++;
				Long ichan = 0L;
				svgheight = (long) chanList.size();
				results.append("<p>Number of channels " + chanList.size() + " ");
				long niovs = 0;
				Long svgabsmax = 0L;
				for (IovType aniov : iovperchanList) {
					aniov.setIovBase(selnode.getNodeIovBase());
					// results.append("<p>"
					// + aniov.getChannelId() + " "
					// + aniov.getChannelName() + " "
					// + aniov.getIovBase() + "</p><br> ");
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

					if (until > svgabsmax) {
						svgabsmax = until;
					}
					niovs += aniov.getNiovs();
					if (previov == null) {
						svgabsmin = since;
						if (svgabsmin == 0) {
							svgabsmin = until;
						}
						results.append(" Since = " + since + " ");
					}

					if (previov != null) {
						if (previov.getChannelId() != aniov.getChannelId()) {
							ichan++;
							svgabsmin = since;
							if (ichan == 1) {
								results.append(" Until = " + svgabsmax + " ");
								results.append(" Niovs = " + niovs);
							}
							niovs = 0;
						}
					}
					if (isvalid > 0) {
						// in this case the isvalid flag has to be 0 for this
						// iov
						// results.append("<p>"+0 + " "
						// +niovs+" "+since+" "+until+" "+timespan+"</p><br>");
						svg.append(getSvgLine(since, until, ichan,
								aniov.getIovBase(), false));
					} else {
						// results.append("<p>"+isvalid + " "
						// +niovs+" "+since+" "+until+" "+timespan+"</p><br>");
						svg.append(getSvgLine(since, until, ichan,
								aniov.getIovBase(), false));
					}
					// If there is a hole then take its times from other fields
					// and add a line for the previous good iov and the hole
					if (isvalid > 0) {
						// results.append("<p>"
						// + aniov.getChannelId() + " "
						// + aniov.getChannelName() + " "
						// + aniov.getIovBase() + "</p><br> ");
						since = until; // the since of the hole is the until of
										// the last good
						if (aniov.getIovBase().equals("time")) {
							until = CoolIov.getTime(aniov.getHoleUntil()
									.toBigInteger());
						} else {
							until = CoolIov.getRun(aniov.getHoleUntil()
									.toBigInteger());
						}
						// results.append("<p>"+isvalid + " "
						// +1+" "+since+" "+until+" "+timespan+"</p><br>");
						svg.append(getSvgLine(since, until, ichan,
								aniov.getIovBase(), true));
					}
					previov = aniov;
				}
				if (ichan == 0) {
					results.append(" Until = " + svgabsmax + " ");
					results.append(" Niovs = " + niovs);
				}
				results.append("</p><br>");
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

	protected Map<Long, CoolIovSummary> computeIovRangeMap(String schema,
			String db, String node, String tag, String iovbase) {
		List<NodeGtagTagType> nodeingtagList = null;
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
				}
				iovsumm.setIovbase(aniov.getIovBase());
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

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/iovsummary/svgv1")
	public String listIovsSummaryInNodesSchemaSvgv1(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {

		log.info("Calling listIovsSummaryInNodesSchemaSvg..." + schema + " "
				+ db);
		StringBuffer results = new StringBuffer();
		StringBuffer svg = new StringBuffer();
		List<NodeGtagTagType> nodeingtagList = null;
		try {
			results.append("<head><style>" + "h1 {font-size:25px;} "
					+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
					+ "hr {color:sienna;}" + "p {font-size:14px;}"
					+ "p.small {line-height:80%;}" + "</style></head>");

			results.append("<body>");

			results.append("<h1># List of NODEs and TAGs iovs statistic associated to "
					+ gtag + "</h1><br>");
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema
					+ "%", db, gtag);
			int itag = 0;
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
				results.append("<h3># " + schema + " > " + " "
						+ selnode.getNodeFullpath() + " ; "
						+ nodeGtagTagType.getTagName() + " ! " + "</h3><br>");

				svgabsmin = 0L;
				svgabsmax = CoolIov.COOL_MAX_DATE;
				String seltag = nodeGtagTagType.getTagName();
				// Long niovs = 0L;
				Map<Long, CoolIovSummary> iovsummary = computeIovRangeMap(
						schema, db, node, seltag, selnode.getNodeIovBase());
				List<ChannelType> chanList = cooldao
						.retrieveChannelsFromNodeSchemaAndDb(schema, db, node,
								"%");
				Set<Long> channelList = iovsummary.keySet();
				if (channelList.size()<20) {
					linewidth = 10;
				} else if (channelList.size()<100) {
					linewidth = 6;
				} else if (channelList.size()<200) {
					linewidth = 3;
				} else {
					linewidth = 1;
				}
				results.append("<p>Number of channels used "
						+ channelList.size() + " over a total of "
						+ chanList.size());
				// Integer viewboxy1 = 0;
				// if (itag > 0) {
				// viewboxy1 += (channelList.size() + 10);
				// }
				// String svgcanvas =
				// "<svg width=\""+svglinewidth+"px\" height=\""+chanList.size()+"px\" viewBox=\"0 "+viewboxy1+" "+svglinewidth+" "+chanList.size()+" >";
				String svgcanvas = "<svg width=\""
						+ svglinewidth
						+ "px\" height=\""
						+ (channelList.size() * linewidth + 10)
						+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
				svg.append(svgcanvas);
				itag++;
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
					log.info("Node " + node + " tag " + seltag + ": Chan "
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
		String svgline = "";

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
			svgline = "<line";
			svgline += (" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ ichan * linewidth + "\" x2=\"" + convert(end, infinity)
					+ "\" y2=\"" + ichan * linewidth + "\"");
			svgline += " stroke=\"red\" stroke-width=\"" + linewidth + "\"/>";
		} else {
			svgline = "<line";
			svgline += (" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ ichan * linewidth + "\" x2=\"" + convert(end, infinity)
					+ "\" y2=\"" + ichan * linewidth + "\"");
			svgline += " stroke=\"green\" stroke-width=\"" + linewidth + "\"/>";
		}

		return svgline;
	}
}
