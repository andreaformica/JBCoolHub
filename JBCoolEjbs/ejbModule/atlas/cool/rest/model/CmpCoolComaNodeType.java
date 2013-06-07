package atlas.cool.rest.model;

/**
 * 
 */

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;

/**
 * @author formica
 * 
 */
@Entity
@NamedNativeQueries({ @NamedNativeQuery(name = CmpCoolComaNodeType.QUERY_CMP_NODES, query = "select "
		+ " schema_name, "
		+ " db_name, "
		+ " node_id,"
		+ " node_name , "
		+ " node_fullpath ,"
		+ " lastmod_date ,"
		+ " coma_node_id, "
		+ " coma_node_name, "
		+ " coma_node_fullpath, "
		+ " coma_node_lastmod_date, "
		+ " rownum "
		+ "from table(comacool_cmp_pkg.f_CmpAll_Nodes(:schema,:dbname,:node)) "
		+ " order by schema_name, node_fullpath ", resultClass = CmpCoolComaNodeType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CmpCoolComaNodeType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1840642508469078591L;
	/**
	 * 
	 */
	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	/**
	 * 
	 */
	@Column(name = "SCHEMA_NAME", length = 30)
	private String schemaName;
	/**
	 * 
	 */
	@Column(name = "DB_NAME", length = 30)
	private String dbName;

	/**
	 * 
	 */
	@Column(name = "NODE_ID", precision = 10, scale = 0)
	private Long nodeId;
	/**
	 * 
	 */
	@Column(name = "NODE_NAME", length = 255)
	private String nodeName;
	/**
	 * 
	 */
	@Column(name = "NODE_FULLPATH", length = 255)
	private String nodeFullpath;

	/**
	 * 
	 */
	@Column(name = "LASTMOD_DATE", length = 255)
	private String lastmodDate;

	/**
	 * 
	 */
	@Column(name = "COMA_NODE_ID", precision = 10, scale = 0)
	private Long comaNodeId;
	/**
	 * 
	 */
	@Column(name = "COMA_NODE_NAME", length = 255)
	private String comaNodeName;
	/**
	 * 
	 */
	@Column(name = "COMA_NODE_FULLPATH", length = 255)
	private String comaNodeFullpath;

	/**
	 * 
	 */
	@Column(name = "COMA_NODE_LASTMOD_DATE", length = 255)
	private String comaLastmodDate;

	/**
	 * 
	 */
	@CoolQuery(name = "cool.cmpnodes", params = "schema;dbname;node")
	public static final String QUERY_CMP_NODES = "cool.cmpnodes";

	/**
	 * @return the schemaName
	 */
	public final String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName
	 *            the schemaName to set
	 */
	public final void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the dbName
	 */
	public final String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public final void setDbName(final String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the nodeId
	 */
	public final Long getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
	 */
	public final void setNodeId(final Long nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the nodeName
	 */
	public final String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName
	 *            the nodeName to set
	 */
	public final void setNodeName(final String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @return the nodeFullpath
	 */
	public final String getNodeFullpath() {
		return nodeFullpath;
	}

	/**
	 * @param nodeFullpath
	 *            the nodeFullpath to set
	 */
	public final void setNodeFullpath(final String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}

	/**
	 * @return the lastmodDate
	 */
	public final String getLastmodDate() {
		return lastmodDate;
	}

	/**
	 * @param lastmodDate
	 *            the lastmodDate to set
	 */
	public final void setLastmodDate(final String lastmodDate) {
		this.lastmodDate = lastmodDate;
	}

	/**
	 * @return the comaNodeId
	 */
	public final Long getComaNodeId() {
		return comaNodeId;
	}

	/**
	 * @param comaNodeId
	 *            the comaNodeId to set
	 */
	public final void setComaNodeId(final Long comaNodeId) {
		this.comaNodeId = comaNodeId;
	}

	/**
	 * @return the comaNodeName
	 */
	public final String getComaNodeName() {
		return comaNodeName;
	}

	/**
	 * @param comaNodeName
	 *            the comaNodeName to set
	 */
	public final void setComaNodeName(final String comaNodeName) {
		this.comaNodeName = comaNodeName;
	}

	/**
	 * @return the comaNodeFullpath
	 */
	public final String getComaNodeFullpath() {
		return comaNodeFullpath;
	}

	/**
	 * @param comaNodeFullpath
	 *            the comaNodeFullpath to set
	 */
	public final void setComaNodeFullpath(final String comaNodeFullpath) {
		this.comaNodeFullpath = comaNodeFullpath;
	}

	/**
	 * @return the comaLastmodDate
	 */
	public final String getComaLastmodDate() {
		return comaLastmodDate;
	}

	/**
	 * @param comaLastmodDate
	 *            the comaLastmodDate to set
	 */
	public final void setComaLastmodDate(final String comaLastmodDate) {
		this.comaLastmodDate = comaLastmodDate;
	}

}
