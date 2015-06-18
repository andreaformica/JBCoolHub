package atlas.cool.rest.web.xml;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
import javax.ws.rs.core.MediaType;

import atlas.cool.dao.CoolDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/cooldb")
@RequestScoped
@Api(value="/cooldb")
public class CoolCherryPyResourceRESTService {

	@Inject
	private CoolDAO cooldao;
	@Inject
	private Logger log;

	/**
	 * @param schema
	 * @param db
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}")
	public List<NodeType> listNodesInSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db) {

		log.info("Calling listNodesInSchema..." + schema + " " + db);
		List<NodeType> results = null;
		try {
			results = cooldao.retrieveNodesFromSchemaAndDb(schema + "%", db, "%");
			if (results == null) {
				// create a fake entry
				final NodeType nt = new NodeType();
				nt.setNodeId(1L);
				nt.setNodeFullpath("this is a fake node");
				nt.setNodeTinstime(new Timestamp(new Date().getTime()));
				final List<NodeType> _fakes = new ArrayList<NodeType>();
				_fakes.add(nt);
				results = _fakes;
			}
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * @param schema
	 * @param db
	 * @return
	 */
	@GET
	@Produces("text/plain")
	@ApiOperation(produces="text/plain", value = "Node list in Cherrypy Cool style.")
	@Path("/{schema}/{db}/nodelist")
	public String listNodesInSchemaString(@PathParam("schema") final String schema,
			@PathParam("db") final String db) {

		log.info("Calling listNodesInSchemaString..." + schema + " " + db);
		List<NodeType> results = null;
		final StringBuffer output = new StringBuffer();
		try {
			output.append("<nodelist>\n");
			results = cooldao.retrieveNodesFromSchemaAndDb(schema + "%", db, "%");
			if (results != null) {
				for (final NodeType anode : results) {
					output.append("<node>" + anode.getNodeFullpath() + "</node>\n");
				}
			}
			output.append("</nodelist>\n");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 */
	@GET
	@ApiOperation(produces="text/plain", value = "Node list length in Cherrypy Cool style.")
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/length")
	public String countChannelsInSchemaNodeString(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node) {

		log.info("Calling countChannelsInSchemaNodeString..." + schema + " " + db);
		List<ChannelType> results = null;
		final StringBuffer output = new StringBuffer();
		try {
			String lnode = node;
			if (!lnode.startsWith("/")) {
				lnode = "/" + node;
			}
			results = cooldao
					.retrieveChannelsFromNodeSchemaAndDb(schema, db, lnode, null);
			if (results != null) {
				output.append(results.size());
			}
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 */
	@GET
	@ApiOperation(produces="text/plain", value = "Node list in Cherrypy Cool style.")
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/list")
	public String listChannelsInSchemaNodeString(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node) {

		log.info("Calling listChannelsInSchemaNodeString..." + schema + " " + db);
		List<ChannelType> results = null;
		final StringBuffer output = new StringBuffer();
		try {
			String lnode = node;
			if (!lnode.startsWith("/")) {
				lnode = "/" + node;
			}
			results = cooldao
					.retrieveChannelsFromNodeSchemaAndDb(schema, db, lnode, null);
			if (results != null) {
				for (final ChannelType achan : results) {
					output.append(achan.getChannelId() + " ");
				}
				output.append("\n");
			}
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 */
	@GET
	@ApiOperation(produces="text/plain", value = "Node names in Cherrypy Cool style.")
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/names")
	public String namesChannelsInSchemaNodeString(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node) {

		log.info("Calling namesChannelsInSchemaNodeString..." + schema + " " + db);
		List<ChannelType> results = null;
		final StringBuffer output = new StringBuffer();
		try {
			String lnode = node;
			if (!lnode.startsWith("/")) {
				lnode = "/" + node;
			}
			output.append("<channelNames>\n");
			results = cooldao
					.retrieveChannelsFromNodeSchemaAndDb(schema, db, lnode, null);
			if (results != null) {
				for (final ChannelType achan : results) {
					output.append("<name id=\"" + achan.getChannelId() + "\">"
							+ achan.getChannelName() + "</name>\n");
				}
			}
			output.append("</channelNames>\n");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 */
	@GET
	@ApiOperation(produces="text/plain", value = "Node tags list in Cherrypy Cool style.")
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/tags")
	public String listTagsInSchemaNodeString(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("node") final String node) {

		log.info("Calling listTagsInSchemaNodeString..." + schema + " " + db);
		List<SchemaNodeTagType> results = null;
		final StringBuffer output = new StringBuffer();
		try {
			String lnode = node;
			if (!lnode.startsWith("/")) {
				lnode = "/" + node;
			}
			output.append("<tagList>\n");
			results = cooldao.retrieveTagsFromNodesSchemaAndDb(schema, db, lnode, "%");
			if (results != null) {
				for (final SchemaNodeTagType atag : results) {
					final String lock = atag.getTagLockStatus() > 0 ? "locked"
							: "unlocked";
					final String description = atag.getTagDescription();
					final String inserttime = atag.getSysInstime();
					output.append("<tag lock=\"" + lock + "\" " + "description=\""
							+ description + "\" " + "insertionTime=\"" + inserttime
							+ "\">" + atag.getTagName() + "</tag>\n");
				}
			}
			output.append("</tagList>\n");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 */
	@GET
	@ApiOperation(produces="text/plain", value = "Global Tag trace in Cherrypy Cool style.")	
	@Produces("text/plain")
	@Path("/{schema}/{db}/tags/{gtag}")
	public String listAssociatedTagsInSchemaNodeString(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {

		log.info("Calling listTagsInSchemaNodeString..." + schema + " " + db);
		List<NodeGtagTagType> results = null;
		final StringBuffer output = new StringBuffer();
		try {
			output.append("<tagList>\n");
			results = cooldao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db, "%"
					+ gtag + "%");
			if (results != null) {
				for (final NodeGtagTagType atag : results) {
					final String lock = atag.getTagLockStatus() > 0 ? "locked"
							: "unlocked";
					final String description = atag.getTagDescription();
					final String inserttime = atag.getSysInstime();
					final String coolschema = atag.getSchemaName();
					final String fld = atag.getNodeFullpath();
					output.append("<tag lock=\"" + lock + "\" " + "schema=\""
							+ coolschema + "\" " + "globaltag=\"" + atag.getGtagName()
							+ "\" " + "folder=\"" + fld + "\" " + "description=\""
							+ description + "\" " + "insertionTime=\"" + inserttime
							+ "\">" + atag.getTagName() + "</tag>\n");
				}
			}
			output.append("</tagList>\n");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

}
