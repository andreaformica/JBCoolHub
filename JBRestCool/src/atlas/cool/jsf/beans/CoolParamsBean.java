/**
 * 
 */
package atlas.cool.jsf.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.SchemaType;

@Named("coolparams")
@SessionScoped
/**
 * @author formica
 *
 */
public class CoolParamsBean implements Serializable {

	@Inject
	private Logger log;

	@Inject
	private FacesContext facesContext;

	@Inject
	private CoolDAO cooldao;

	@Inject
	private Event<GtagType> changeGtagselection;

	String schemaName = "ATLAS_COOL";
	String dbName = "COMP200";
	String gtagName = "";

	List<String> dbList = null;
	List<SchemaType> schemaList = null;
	List<GtagType> gtagList = null;
	List<NodeGtagTagType> nodegtagdblfldList = null;
	List<NodeGtagTagType> nodegtagtagList = null;
	List<NodeGtagTagType> nodegtagtagListFiltered = null;

	GtagType selGtag = null;

	/**
	 * 
	 */
	public CoolParamsBean() {
		// TODO Auto-generated constructor stub
		initDbs();
	}

	protected void initDbs() {
		if (dbList == null) {
			dbList = new ArrayList<String>();
			dbList.add("COMP200");
			dbList.add("OFLP200");
			dbList.add("MONP200");
		}
	}

	public void retrieveGtagsData() {
		try {
			log.info("Retrieving gtags for :"+schemaName+ " "+ dbName
					+" "+gtagName+"!");
			gtagList = cooldao.retrieveGtagsFromSchemaAndDb(schemaName+"%", dbName,
					"%"+gtagName+"%");
			log.info("Retrieved gtags of size :"+gtagList.size());
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void retrieveNodeGtagTagsData() {
		try {
			log.info("Retrieving node tags for :"+schemaName+ " "+ dbName
					+" "+selGtag.getGtagName()+"!");
			nodegtagtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schemaName+"%", dbName,
					selGtag.getGtagName());
			log.info("Retrieved nodegtagtag of size :"+nodegtagtagList.size());
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void retrieveNodeGtagDoubleFldData() {
		try {
			log.info("Retrieving node tags for :"+schemaName+ " "+ dbName
					+" "+selGtag.getGtagName()+"!");
			nodegtagdblfldList = cooldao.retrieveGtagDoublFldFromSchemaDb(schemaName+"%", dbName,
					selGtag.getGtagName());
			log.info("Retrieved nodegtagtag of size :"+nodegtagdblfldList.size());
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadSchemas() {
		try {
			log.info("Retrieving schemas for :"+schemaName+ " "+ dbName);
			schemaList = cooldao.retrieveSchemasFromNodeSchemaAndDb("ATLAS_COOL%", dbName, "%");
			log.info("Retrieved schemas of size :"+schemaList.size());
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName
	 *            the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		String dbn = schemaName;
		if (schemaName.contains("%")) {
			dbn = schemaName.replaceAll("%", "");
		}
		this.schemaName = dbn;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public void setDbName(String dbName) {
		String dbn = dbName;
		if (dbName.contains("%")) {
			dbn = dbName.replaceAll("%", "");
		}
		if (!dbn.equals(this.dbName)) {
			this.dbName = dbn;
			loadSchemas();
		}
	}

	/**
	 * @return the gtagName
	 */
	public String getGtagName() {
		return gtagName;
	}

	/**
	 * @param gtagName
	 *            the gtagName to set
	 */
	public void setGtagName(String gtagName) {
		String dbn = gtagName;
		if (gtagName.contains("%")) {
			dbn = gtagName.replaceAll("%", "");
		}
		this.gtagName = dbn;
	//	this.gtagName = gtagName;
	}

	/**
	 * @return the dbList
	 */
	public List<String> getDbList() {
		return dbList;
	}

	/**
	 * @return the gtagList
	 */
	public List<GtagType> getGtagList() {
		return gtagList;
	}

	/**
	 * @return the selGtag
	 */
	public GtagType getSelGtag() {
		return selGtag;
	}

	/**
	 * @param selGtag the selGtag to set
	 */
	public void setSelGtag(GtagType selGtag) {
		boolean changeselection = false;
		if (this.selGtag != null && this.selGtag.equals(selGtag)) {
			log.info("Ignoring changes of selection if node tags data are loaded");
			if (this.nodegtagtagList != null
					&& this.nodegtagtagList.size()>0 ) {
				log.info("Node tags data are loaded");
			} else {
				changeselection = true;
			}
		} else {
			changeselection = true;
		}
		
		if (changeselection) {
			this.selGtag = selGtag;
			log.info("Selected Gtag " + selGtag);
			retrieveNodeGtagTagsData();
			retrieveNodeGtagDoubleFldData();
			changeGtagselection.fire(this.selGtag);
		}
	}

	/**
	 * @return the nodegtagtagList
	 */
	public List<NodeGtagTagType> getNodegtagtagList() {
		return nodegtagtagList;
	}

	/**
	 * @return the nodegtagtagListFiltered
	 */
	public List<NodeGtagTagType> getNodegtagtagListFiltered() {
		return nodegtagtagListFiltered;
	}

	/**
	 * @param nodegtagtagListFiltered the nodegtagtagListFiltered to set
	 */
	public void setNodegtagtagListFiltered(
			List<NodeGtagTagType> nodegtagtagListFiltered) {
		this.nodegtagtagListFiltered = nodegtagtagListFiltered;
	}

	/**
	 * @return the nodegtagdblfldList
	 */
	public List<NodeGtagTagType> getNodegtagdblfldList() {
		return nodegtagdblfldList;
	}

	
}
