package atlas.cool.rest.web;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
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
						log.info("Channel ID changed..."+aniov.getChannelId()+" previous was "+previov.getChannelId()+" iovdump is "+previovDump);
						if (previovDump != null) {
							results.append(" | "+previovDump + "</p>");
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
							results.append(" | "+previovDump);
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
							previovDump = colortagstart + niovs
									+ " [" + since + "] [" + until + "] "
									+ colortagend;
						}
						results.append(" | "+previovDump);
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
						results.append(" | "+previovDump);
						previovDump = null;
					}
					previov = aniov;
				}
				results.append(" | "+previovDump + "</p>");
				results.append("</body>");
			}

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

}
