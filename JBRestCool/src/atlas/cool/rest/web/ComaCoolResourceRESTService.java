package atlas.cool.rest.web;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.dao.ComaCoolDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.CmpCoolComaGlobalTagType;
import atlas.cool.rest.model.CmpCoolComaNodeType;
import atlas.cool.rest.model.CmpCoolComaTagType;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/comacool")
@RequestScoped
public class ComaCoolResourceRESTService {

	@Inject
	private ComaCoolDAO cocodao;

	@Inject
	private Logger log;

	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/nodes")
	public String cmpNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {
		StringBuffer buf = new StringBuffer();
		log.info("Calling cmpNodesInSchema..."+schema+" "+db);
		List<CmpCoolComaNodeType> results = null;
		try {
			buf.append("<html><body><h1>Analyse "+schema+" "+db+"</h1><br>");
			String safeschema = schema;
			String[] _scharr = schema.split("_");
			if (schema.startsWith("ATLAS")) {
				int i = 0;
				safeschema = _scharr[1];
				while (i<_scharr.length) {
					safeschema += ("_" + _scharr[i++]);
				}
			}
			safeschema +="%";
			log.info("Safe schema name "+safeschema);
			results = cocodao.retrieveNodesStatus(safeschema, db);
			log.info("Retrieved list of size "+results.size());
			for (CmpCoolComaNodeType ccnt : results) {
				if (ccnt == null) {
					buf.append("<p>Object is null in list...</p>");
				} else if (ccnt.getNodeId() == null) {
					buf.append("<font color=\"#FF4000\"> schema "+ccnt.getSchemaName()+", node "+ccnt.getComaNodeFullpath()+" does not exists in COOL ....</font><br>");
				} else if (ccnt.getComaNodeId() == null) {
					buf.append("<font color=\"#FF0080\"> schema "+ccnt.getSchemaName()+", node "+ccnt.getNodeFullpath()+" does not exists in COMA ....</font><br>");					
				} else if (ccnt.getNodeFullpath().equals(ccnt.getComaNodeFullpath())) {
					buf.append("<font color=\"#04B431\"> schema "+ccnt.getSchemaName()+", matching node "+ccnt.getNodeFullpath()+"</font><br>");										
				} else {
					buf.append("<p> schema "+ccnt.getSchemaName()+": unknown situation...</p><br>");
				}
			}
			buf.append("</body></html>");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/tags")
	public String cmpTagsInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {
		StringBuffer buf = new StringBuffer();
		log.info("Calling cmpTagsInSchema..."+schema+" "+db);
		List<CmpCoolComaTagType> results = null;
		try {
			buf.append("<html><body><h1>Analyse "+schema+" "+db+"</h1><br>");
			String safeschema = schema;
			String[] _scharr = schema.split("_");
			if (schema.startsWith("ATLAS")) {
				int i = 0;
				safeschema = _scharr[1];
				while (i<_scharr.length) {
					safeschema += ("_" + _scharr[i++]);
				}
			}
			safeschema +="%";
			log.info("Safe schema name "+safeschema);
			results = cocodao.retrieveTagsStatus(safeschema, db);
			log.info("Retrieved list of size "+results.size());
			buf.append("<h3>Retrieved list of "+results.size()+"</h3><br>");
			for (CmpCoolComaTagType ccnt : results) {
				log.info("Found obj "+ccnt.getComaCoolSchema()+" "+ccnt.getComaNodeFullpath()+" "+ccnt.getComaTagName());
				if (ccnt == null) {
					buf.append("<p>Object is null in list...</p>");
				} else if (ccnt.getTagName() == null) {
					buf.append("<font color=\"#FF4000\"> schema "+ccnt.getComaCoolSchema()+", node "+ccnt.getComaNodeFullpath()+", tag "+ccnt.getComaTagName()+" does not exists in COOL ....</font><br>");
				} else if (ccnt.getComaTagName() == null) {
					buf.append("<font color=\"#FF0080\"> schema "+ccnt.getSchemaName()+", node "+ccnt.getNodeFullpath()+", tag "+ccnt.getTagName()+" does not exists in COMA ....</font><br>");					
				} else if (ccnt.getComaTagLockStatus() != ccnt.getTagLockStatus()) {
					buf.append("<font color=\"#FF0090\"> schema "+ccnt.getSchemaName()+", node "+ccnt.getNodeFullpath()+", tag "+ccnt.getTagName()+" has different lock status in COMA ....</font><br>");					
				}  else if (ccnt.getTagName().equals(ccnt.getComaTagName())) {
					buf.append("<font color=\"#04B431\"> schema "+ccnt.getSchemaName()+", node "+ccnt.getNodeFullpath()+", matching tag "+ccnt.getTagName()+"</font><br>");										
				} else {
					buf.append("<p> schema "+ccnt.getSchemaName()+", node "+ccnt.getNodeFullpath()+": unknown situation...</font>");
				}
			}
			buf.append("</body></html>");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}

	
	@GET
	@Produces("text/html")
	@Path("/{schema}/{db}/{gtag}/trace")
	public String cmpGlobalTagsInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("gtag")String gtag) {
		StringBuffer buf = new StringBuffer();
		log.info("Calling cmpTagsInSchema..."+schema+" "+db);
		List<CmpCoolComaGlobalTagType> results = null;
		try {
			buf.append("<html><body><h1>Analyse "+schema+" "+db+" - "+gtag+"</h1><br>");
			String safeschema = schema;
			String[] _scharr = schema.split("_");
			if (schema.startsWith("ATLAS")) {
				int i = 0;
				safeschema = _scharr[1];
				while (i<_scharr.length) {
					safeschema += ("_" + _scharr[i++]);
				}
			}
			safeschema +="%";
			log.info("Safe schema name "+safeschema);
			results = cocodao.retrieveGlobalTagsStatus(safeschema, db, gtag+"%");
			log.info("Retrieved list of size "+results.size());
			buf.append("<h3>Retrieved list of "+results.size()+"</h3><br>");
			for (CmpCoolComaGlobalTagType ccnt : results) {
				//log.info("Found obj "+ccnt.getComaCoolSchema()+" "+ccnt.getComaNodeFullpath()+" "+ccnt.getComaTagName());
				if (ccnt == null) {
					buf.append("<p>Object is null in list...</p>");
				} else if (ccnt.getTagName() == null) {
					buf.append("<font color=\"#FF4000\"> schema "+ccnt.getComaCoolSchema()+", gtag "+ccnt.getGtagName()
							+", node "+ccnt.getComaNodeFullpath()+", tag "+ccnt.getComaTagName()+" does not exists in COOL ....</font><br>");
				} else if (ccnt.getComaTagName() == null) {
					buf.append("<font color=\"#FF0080\"> schema "+ccnt.getSchemaName()+", gtag "+ccnt.getGtagName()
							+", node "+ccnt.getNodeFullpath()+", tag "+ccnt.getTagName()+" does not exists in COMA ....</font><br>");					
				} else if (ccnt.getComaTagLockStatus() != ccnt.getTagLockStatus()) {
					buf.append("<font color=\"#FF0090\"> schema "+ccnt.getSchemaName()+", gtag "+ccnt.getGtagName()
							+", node "+ccnt.getNodeFullpath()+", tag "+ccnt.getTagName()+" has different lock status in COMA ....</font><br>");					
				} else if (ccnt.getTagName().equals(ccnt.getComaTagName())) {
					buf.append("<font color=\"#04B431\"> schema "+ccnt.getSchemaName()+", gtag "+ccnt.getGtagName()
							+", node "+ccnt.getNodeFullpath()+", matching tag "+ccnt.getTagName()+"</font><br>");										
				} else {
					buf.append("<p> schema "+ccnt.getSchemaName()+", node "+ccnt.getNodeFullpath()+": unknown situation...</font>");
				}
			}
			buf.append("</body></html>");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}

}
