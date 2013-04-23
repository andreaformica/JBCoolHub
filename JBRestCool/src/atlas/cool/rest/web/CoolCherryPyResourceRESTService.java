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
import atlas.cool.rest.model.ChannelType;
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
@Path("/cooldb")
@RequestScoped
public class CoolCherryPyResourceRESTService {

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolPayloadDAO payloaddao;

	@Inject
	private Logger log;

	@GET
	@Produces("text/xml")
	@Path("/{schema}/{db}")
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {
		
		log.info("Calling listNodesInSchema..."+schema+" "+db);
		List<NodeType> results = null;
		try {
			results = cooldao.retrieveNodesFromSchemaAndDb(schema+"%", db, "%");
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
	@Produces("text/plain")
	@Path("/{schema}/{db}/nodelist")
	public String listNodesInSchemaString(@PathParam("schema") String schema,
			@PathParam("db") String db) {
		
		log.info("Calling listNodesInSchemaString..."+schema+" "+db);
		List<NodeType> results = null;
		StringBuffer output = new StringBuffer();
		try {
			output.append("<nodelist>\n");
			results = cooldao.retrieveNodesFromSchemaAndDb(schema+"%", db, "%");
			if (results != null) {
				for (NodeType anode : results) {
					output.append("<node>"+anode.getNodeFullpath()+"</node>\n");
				}
			}
			output.append("</nodelist>\n");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/length")
	public String countChannelsInSchemaNodeString(@PathParam("schema") String schema,
			@PathParam("db") String db,
			@PathParam("node") String node) {
		
		log.info("Calling countChannelsInSchemaNodeString..."+schema+" "+db);
		List<ChannelType> results = null;
		StringBuffer output = new StringBuffer();
		try {
			String _node = node;
			if (!_node.startsWith("/")) {
				_node = "/"+node;
			}
			results = cooldao.retrieveChannelsFromNodeSchemaAndDb(schema, db, _node, null);
			if (results != null) {
				output.append(results.size());
			}
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/list")
	public String listChannelsInSchemaNodeString(@PathParam("schema") String schema,
			@PathParam("db") String db,
			@PathParam("node") String node) {
		
		log.info("Calling listChannelsInSchemaNodeString..."+schema+" "+db);
		List<ChannelType> results = null;
		StringBuffer output = new StringBuffer();
		try {
			String _node = node;
			if (!_node.startsWith("/")) {
				_node = "/"+node;
			}
			results = cooldao.retrieveChannelsFromNodeSchemaAndDb(schema, db, _node, null);
			if (results != null) {
				for (ChannelType achan : results) {
					output.append(achan.getChannelId()+" ");	
				}
				output.append("\n");
			}
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/names")
	public String namesChannelsInSchemaNodeString(@PathParam("schema") String schema,
			@PathParam("db") String db,
			@PathParam("node") String node) {
		
		log.info("Calling namesChannelsInSchemaNodeString..."+schema+" "+db);
		List<ChannelType> results = null;
		StringBuffer output = new StringBuffer();
		try {
			String _node = node;
			if (!_node.startsWith("/")) {
				_node = "/"+node;
			}
			output.append("<channelNames>\n");
			results = cooldao.retrieveChannelsFromNodeSchemaAndDb(schema, db, _node, null);
			if (results != null) {
				for (ChannelType achan : results) {
					output.append("<name id=\""+achan.getChannelId()+"\">"
							+achan.getChannelName()
							+"</name>\n");	
				}
			}
			output.append("</channelNames>\n");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/{node:.*}/tags")
	public String listTagsInSchemaNodeString(@PathParam("schema") String schema,
			@PathParam("db") String db,
			@PathParam("node") String node) {
		
		log.info("Calling listTagsInSchemaNodeString..."+schema+" "+db);
		List<SchemaNodeTagType> results = null;
		StringBuffer output = new StringBuffer();
		try {
			String _node = node;
			if (!_node.startsWith("/")) {
				_node = "/"+node;
			}
			output.append("<tagList>\n");
			results = cooldao.retrieveTagsFromNodesSchemaAndDb(schema, db, _node, "%");
			if (results != null) {
				for (SchemaNodeTagType atag : results) {
					String lock = (atag.getTagLockStatus()>0) ? "locked" : "unlocked";
					String description = atag.getTagDescription();
					String inserttime = atag.getSysInstime();
					output.append("<tag lock=\""+lock+"\" "
							+"description=\""+description+"\" "
							+"insertionTime=\""+inserttime+"\">"
							+atag.getTagName()
							+"</tag>\n");	
				}
			}
			output.append("</tagList>\n");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	@GET
	@Produces("text/plain")
	@Path("/{schema}/{db}/tags/{gtag}")
	public String listAssociatedTagsInSchemaNodeString(@PathParam("schema") String schema,
			@PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		
		log.info("Calling listTagsInSchemaNodeString..."+schema+" "+db);
		List<NodeGtagTagType> results = null;
		StringBuffer output = new StringBuffer();
		try {
			output.append("<tagList>\n");
			results = cooldao.retrieveGtagTagsFromSchemaAndDb(schema+"%", db, "%"+gtag+"%");
			if (results != null) {
				for (NodeGtagTagType atag : results) {
					String lock = (atag.getTagLockStatus()>0) ? "locked" : "unlocked";
					String description = atag.getTagDescription();
					String inserttime = atag.getSysInstime();
					String coolschema = atag.getSchemaName();
					String fld = atag.getNodeFullpath();
					output.append("<tag lock=\""+lock+"\" "
							+"schema=\""+coolschema+"\" "
							+"globaltag=\""+atag.getGtagName()+"\" "
							+"folder=\""+fld+"\" "
							+"description=\""+description+"\" "
							+"insertionTime=\""+inserttime+"\">"
							+atag.getTagName()
							+"</tag>\n");	
				}
			}
			output.append("</tagList>\n");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

}
