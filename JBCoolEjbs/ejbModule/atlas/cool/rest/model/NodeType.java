package atlas.cool.rest.model;
/**
 * 
 */


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.BatchSize;

import atlas.cool.annotations.CoolQuery;
import atlas.cool.rest.utils.TimestampStringFormatter;


/**
 * @author formica
 *
 */
@Entity
@NamedNativeQueries( {
	@NamedNativeQuery(name = NodeType.QUERY_FINDNODES, query = "select   node_id,"
 + " node_name , "
 + " node_fullpath ," 
 + " node_description ," 
 + " node_isleaf ," 
 + " node_instime ," 
 + " node_tinstime ," 
 + " lastmod_date ," 
 + " folder_versioning ," 
 + " folder_payloadspec ," 
 + " folder_iovtablename ," 
 + " folder_tagtablename ," 
 + " folder_channeltablename, " 
 + " schema_name, "
 + " dbname, "
 + " iov_base, "
 + " iov_type, "
 + " rownum "
 + "from table(cool_select_pkg.f_getall_nodes(:schema,:dbname,:node))", resultClass = NodeType.class),
	@NamedNativeQuery(name = NodeType.QUERY_FINDALLNODES, query = "select   node_id,"
 + " node_name , "
 + " node_fullpath ," 
 + " node_description ," 
 + " node_isleaf ," 
 + " node_instime ," 
 + " node_tinstime ," 
 + " lastmod_date ," 
 + " folder_versioning ," 
 + " folder_payloadspec ," 
 + " folder_iovtablename ," 
 + " folder_tagtablename ," 
 + " folder_channeltablename, " 
 + " schema_name, "
 + " dbname, "
 + " iov_base, "
 + " iov_type, "
 + " rownum "
 + "from table(cool_select_pkg.f_getall_nodes(:schema,:dbname,:node))", resultClass = NodeType.class)
})
@BatchSize(size=5)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8950148398190695530L;
	
	@Id
	@Column(name = "ROWNUM")
	Long rowid;

	@Column(name = "SCHEMA_NAME", length = 30)
	String schemaName;
	@Column(name = "DBNAME", length = 30)
	String dbName;

	@Column(name = "NODE_ID", precision = 10, scale = 0)
	Long nodeId;
	@Column(name = "NODE_NAME", length = 255)
	String nodeName;
	@Column(name = "NODE_FULLPATH", length = 255)
	String nodeFullpath;
	@Column(name = "NODE_DESCRIPTION", length = 255)
	String nodeDescription;
	@Column(name = "NODE_ISLEAF", precision = 5, scale = 0)
	Integer nodeIsleaf;
	@Column(name = "NODE_INSTIME", length = 255)
	String nodeInstime;

	@Column(name = "IOV_BASE", length = 255)
	String nodeIovBase;
	@Column(name = "IOV_TYPE", length = 255)
	String nodeIovType;

	@Column(name = "NODE_TINSTIME")
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	Timestamp nodeTinstime;

	@Column(name = "LASTMOD_DATE", length = 255)
	String  lastmodDate; 
	@Column(name = "FOLDER_VERSIONING", precision = 10, scale = 0)
	Integer  folderVersioning;
	@Column(name = "FOLDER_PAYLOADSPEC", length = 4000)
	String  folderPayloadSpec;
	@Column(name = "FOLDER_IOVTABLENAME", length = 255)
	String  folderIovtablename;
	@Column(name = "FOLDER_TAGTABLENAME", length = 255)
	String  folderTagtablename; 
	@Column(name = "FOLDER_CHANNELTABLENAME", length = 255)
	String  folderChanneltablename;
	
	@CoolQuery(name="cool.findallnodes",params="schema;dbname;node")
	public static final String QUERY_FINDALLNODES = "cool.findallnodes";
	@CoolQuery(name="cool.findnodes",params="schema;dbname;node")
	public static final String QUERY_FINDNODES = "cool.findnodes";
	@CoolQuery(name="coma.findnodes",params="schema;dbname;node")
	public static final String QUERY_COMA_FINDNODES = "coma.findnodes";

	@Transient
	@XmlElement(name="iov", type=CoolIovType.class)
	List<CoolIovType> iovList = null;

	@Transient
	@XmlElement(name="iovsummary", type=CoolIovSummary.class)
	Collection<CoolIovSummary> iovSummaryList = null;

	/**
	 * @return the nodeId
	 */
	public Long getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
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
		this.nodeName = nodeName;
	}

	/**
	 * @return the nodeFullpath
	 */
	public String getNodeFullpath() {
		return nodeFullpath;
	}

	/**
	 * @param nodeFullpath the nodeFullpath to set
	 */
	public void setNodeFullpath(String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}

	/**
	 * @return the nodeDescription
	 */
	public String getNodeDescription() {
		return nodeDescription;
	}

	/**
	 * @param nodeDescription the nodeDescription to set
	 */
	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}

	/**
	 * @return the nodeIsleaf
	 */
	public Integer getNodeIsleaf() {
		return nodeIsleaf;
	}

	/**
	 * @param nodeIsleaf the nodeIsleaf to set
	 */
	public void setNodeIsleaf(Integer nodeIsleaf) {
		this.nodeIsleaf = nodeIsleaf;
	}

	/**
	 * @return the nodeInstime
	 */
	public String getNodeInstime() {
		return nodeInstime;
	}

	/**
	 * @param nodeInstime the nodeInstime to set
	 */
	public void setNodeInstime(String nodeInstime) {
		this.nodeInstime = nodeInstime;
	}

	/**
	 * @return the nodeTinstime
	 */
	public Timestamp getNodeTinstime() {
		return nodeTinstime;
	}

	/**
	 * @param nodeTinstime the nodeTinstime to set
	 */
	public void setNodeTinstime(Timestamp nodeTinstime) {
		this.nodeTinstime = nodeTinstime;
	}

	/**
	 * @return the lastmodDate
	 */
	public String getLastmodDate() {
		return lastmodDate;
	}

	/**
	 * @param lastmodDate the lastmodDate to set
	 */
	public void setLastmodDate(String lastmodDate) {
		this.lastmodDate = lastmodDate;
	}

	/**
	 * @return the folderIovtablename
	 */
	public String getFolderIovtablename() {
		return folderIovtablename;
	}

	/**
	 * @param folderIovtablename the folderIovtablename to set
	 */
	public void setFolderIovtablename(String folderIovtablename) {
		this.folderIovtablename = folderIovtablename;
	}

	/**
	 * @return the folderTagtablename
	 */
	public String getFolderTagtablename() {
		return folderTagtablename;
	}

	/**
	 * @param folderTagtablename the folderTagtablename to set
	 */
	public void setFolderTagtablename(String folderTagtablename) {
		this.folderTagtablename = folderTagtablename;
	}

	/**
	 * @return the folderChanneltablename
	 */
	public String getFolderChanneltablename() {
		return folderChanneltablename;
	}

	/**
	 * @param folderChanneltablename the folderChanneltablename to set
	 */
	public void setFolderChanneltablename(String folderChanneltablename) {
		this.folderChanneltablename = folderChanneltablename;
	}

	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the nodeIovBase
	 */
	public String getNodeIovBase() {
		return nodeIovBase;
	}

	/**
	 * @param nodeIovBase the nodeIovBase to set
	 */
	public void setNodeIovBase(String nodeIovBase) {
		this.nodeIovBase = nodeIovBase;
	}

	/**
	 * @return the nodeIovType
	 */
	public String getNodeIovType() {
		return nodeIovType;
	}

	/**
	 * @param nodeIovType the nodeIovType to set
	 */
	public void setNodeIovType(String nodeIovType) {
		this.nodeIovType = nodeIovType;
	}

	public String getNodeTinstimeStr() {
		if (nodeTinstime == null)
			return "";
		String ret = TimestampStringFormatter.format("yyyy:MM:dd hh:mm:ss", nodeTinstime);
		return ret;

	}

	/**
	 * @return the folderVersioning
	 */
	public Integer getFolderVersioning() {
		return folderVersioning;
	}

	/**
	 * @param folderVersioning the folderVersioning to set
	 */
	public void setFolderVersioning(Integer folderVersioning) {
		this.folderVersioning = folderVersioning;
	}

	/**
	 * @return the folderPayloadSpec
	 */
	public String getFolderPayloadSpec() {
		return folderPayloadSpec;
	}

	/**
	 * @param folderPayloadSpec the folderPayloadSpec to set
	 */
	public void setFolderPayloadSpec(String folderPayloadSpec) {
		this.folderPayloadSpec = folderPayloadSpec;
	}

	/**
	 * @return the iovList
	 */
	public List<CoolIovType> getIovList() {
		return iovList;
	}

	/**
	 * @param iovList the iovList to set
	 */
	public void setIovList(List<CoolIovType> iovList) {
		this.iovList = iovList;
	}

	/**
	 * @return the iovSummaryList
	 */
	public Collection<CoolIovSummary> getIovSummaryList() {
		return iovSummaryList;
	}

	/**
	 * @param iovSummaryList the iovSummaryList to set
	 */
	public void setIovSummaryList(Collection<CoolIovSummary> iovSummaryList) {
		this.iovSummaryList = iovSummaryList;
	}

}
