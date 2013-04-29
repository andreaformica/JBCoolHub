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
import java.util.Date;
import java.util.List;
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
import atlas.cool.meta.CoolPayload;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/plsqlcool")
@RequestScoped
public class CoolResourceRESTService {

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolPayloadDAO payloaddao;

	@Inject
	private Logger log;

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
					schema, db, node, tag);
			for (IovType aniov : results) {
				aniov.setIovBase(selnode.getNodeIovBase());
			}
		} catch (CoolIOException e) {
			e.printStackTrace();
		}
		return results;
	}

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
			// nodeingtagList = cooldao.re
			// for (NodeGtagTagType nodeGtagTagType : nodeingtagList) {
			// results.append("<br><hr>");
			// String node = nodeGtagTagType.getNodeFullpath();
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
			results.append("<h2>" + colorseptagstart + schema + " > " + " "
					+ selnode.getNodeFullpath() + " ; " + tag + colortagend
					+ "</h2>" + "<br>");

			results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");
			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(schema,
							db, node, tag);
			IovType previov = null;
			String previovDump = null;
			String comment = "";
			String colortagstart = colorgoodtagstart;
			for (IovType aniov : iovperchanList) {
				aniov.setIovBase(selnode.getNodeIovBase());
				if (previov == null) {
					previov = aniov;
					results.append("<p class=\"small\">" + aniov.getChannelId()
							+ " " + aniov.getChannelName() + " - "
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

					previovDump = colortagstart + comment + " " + niovs + " ["
							+ since + "] [" + until + "] " + colortagend;

				} else {
					// Modify the previous dump string
					// In case we see a hole, takes also the previous since
					// time, add niovs ....
					if (previovDump != null) {
						previovDump = colortagstart + (previov.getNiovs() + 1L)
								+ " [" + previov.getCoolIovMinSince() + "] ["
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
			// }

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
