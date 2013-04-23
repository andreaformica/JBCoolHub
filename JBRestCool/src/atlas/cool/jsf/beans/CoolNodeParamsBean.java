/**
 * 
 */
package atlas.cool.jsf.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

@Named("coolnodeparams")
@SessionScoped
/**
 * @author formica
 *
 */
public class CoolNodeParamsBean implements Serializable {

	@Inject
	private Logger log;

	@Inject
	private FacesContext facesContext;

	@Inject
	private CoolDAO cooldao;


	String schemaName = "ATLAS_COOL";
	String dbName = "COMP200";
	String nodeName = "";
	String tagName = "";

	List<String> dbList = null;
	List<NodeType> nodeList = null;
	List<SchemaNodeTagType> tagList = null;
	List<NodeType> nodeListFiltered = null;
	List<SchemaNodeTagType> tagListFiltered = null;


	/**
	 * 
	 */
	public CoolNodeParamsBean() {
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

	public void retrieveNodesData() {
		try {
			log.info("Retrieving nodes for :"+schemaName+ " "+ dbName
					+" "+nodeName+"!");
			nodeList = cooldao.retrieveNodesFromSchemaAndDb(schemaName+"%", dbName,
					"%"+nodeName+"%");
			log.info("Retrieved nodes of size :"+nodeList.size());
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void retrieveTagsData() {
		try {
			log.info("Retrieving tags for :"+schemaName+ " "+ dbName
					+" "+nodeName+" "+tagName+"!");
			tagList = cooldao.retrieveTagsFromNodesSchemaAndDb(schemaName+"%", dbName,
					"%"+nodeName+"%", "%"+tagName+"%");
			log.info("Retrieved tags of size :"+tagList.size());
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
		}
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		String dbn = nodeName;
		if (nodeName.contains("%")) {
			dbn = nodeName.replaceAll("%", "");
		}
		if (!dbn.equals(this.nodeName)) {
			this.nodeName = dbn;
		}
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		String dbn = tagName;
		if (tagName.contains("%")) {
			dbn = tagName.replaceAll("%", "");
		}
		if (!dbn.equals(this.tagName)) {
			this.tagName = dbn;
		}
	}

	/**
	 * @return the dbList
	 */
	public List<String> getDbList() {
		return dbList;
	}

	/**
	 * @return the nodeList
	 */
	public List<NodeType> getNodeList() {
		return nodeList;
	}

	/**
	 * @param nodeList the nodeList to set
	 */
	public void setNodeList(List<NodeType> nodeList) {
		this.nodeList = nodeList;
	}

	/**
	 * @return the tagList
	 */
	public List<SchemaNodeTagType> getTagList() {
		return tagList;
	}

	/**
	 * @param tagList the tagList to set
	 */
	public void setTagList(List<SchemaNodeTagType> tagList) {
		this.tagList = tagList;
	}

	/**
	 * @return the nodeListFiltered
	 */
	public List<NodeType> getNodeListFiltered() {
		return nodeListFiltered;
	}

	/**
	 * @param nodeListFiltered the nodeListFiltered to set
	 */
	public void setNodeListFiltered(List<NodeType> nodeListFiltered) {
		this.nodeListFiltered = nodeListFiltered;
	}

	/**
	 * @return the tagListFiltered
	 */
	public List<SchemaNodeTagType> getTagListFiltered() {
		return tagListFiltered;
	}

	/**
	 * @param tagListFiltered the tagListFiltered to set
	 */
	public void setTagListFiltered(List<SchemaNodeTagType> tagListFiltered) {
		this.tagListFiltered = tagListFiltered;
	}

	
}
