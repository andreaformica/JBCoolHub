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
@NamedNativeQueries({
		@NamedNativeQuery(name = NodeType.QUERY_FINDNODES, query = "select   node_id,"
				+ " node_name , " + " node_fullpath ," + " node_description ,"
				+ " node_isleaf ," + " node_instime ," + " node_tinstime ,"
				+ " lastmod_date ," + " folder_versioning ," + " folder_payloadspec ,"
				+ " folder_iovtablename ," + " folder_tagtablename ,"
				+ " folder_channeltablename, " + " schema_name, " + " dbname, "
				+ " iov_base, " + " iov_type, " + " rownum "
				+ "from table(cool_select_pkg.f_getall_nodes(:schema,:dbname,:node))", resultClass = NodeType.class),
		@NamedNativeQuery(name = NodeType.QUERY_FINDALLNODES, query = "select   node_id,"
				+ " node_name , " + " node_fullpath ," + " node_description ,"
				+ " node_isleaf ," + " node_instime ," + " node_tinstime ,"
				+ " lastmod_date ," + " folder_versioning ," + " folder_payloadspec ,"
				+ " folder_iovtablename ," + " folder_tagtablename ,"
				+ " folder_channeltablename, " + " schema_name, " + " dbname, "
				+ " iov_base, " + " iov_type, " + " rownum "
				+ "from table(cool_select_pkg.f_getall_nodes(:schema,:dbname,:node))", resultClass = NodeType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8950148398190695530L;

	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	@Column(name = "SCHEMA_NAME", length = 30)
	private String schemaName;
	@Column(name = "DBNAME", length = 30)
	private String dbName;

	@Column(name = "NODE_ID", precision = 10, scale = 0)
	private Long nodeId;
	@Column(name = "NODE_NAME", length = 255)
	private String nodeName;
	@Column(name = "NODE_FULLPATH", length = 255)
	private String nodeFullpath;
	@Column(name = "NODE_DESCRIPTION", length = 255)
	private String nodeDescription;
	@Column(name = "NODE_ISLEAF", precision = 5, scale = 0)
	private Integer nodeIsleaf;
	@Column(name = "NODE_INSTIME", length = 255)
	private String nodeInstime;

	@Column(name = "IOV_BASE", length = 255)
	private String nodeIovBase;
	@Column(name = "IOV_TYPE", length = 255)
	private String nodeIovType;

	@Column(name = "NODE_TINSTIME")
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	private Timestamp nodeTinstime;

	@Column(name = "LASTMOD_DATE", length = 255)
	private String lastmodDate;
	@Column(name = "FOLDER_VERSIONING", precision = 10, scale = 0)
	private Integer folderVersioning;
	@Column(name = "FOLDER_PAYLOADSPEC", length = 4000)
	private String folderPayloadSpec;
	@Column(name = "FOLDER_IOVTABLENAME", length = 255)
	private String folderIovtablename;
	@Column(name = "FOLDER_TAGTABLENAME", length = 255)
	private String folderTagtablename;
	@Column(name = "FOLDER_CHANNELTABLENAME", length = 255)
	private String folderChanneltablename;

	@CoolQuery(name = "cool.findallnodes", params = "schema;dbname;node")
	public static final String QUERY_FINDALLNODES = "cool.findallnodes";
	@CoolQuery(name = "cool.findnodes", params = "schema;dbname;node")
	public static final String QUERY_FINDNODES = "cool.findnodes";
	@CoolQuery(name = "coma.findnodes", params = "schema;dbname;node")
	public static final String QUERY_COMA_FINDNODES = "coma.findnodes";

	/**
	 * 
	 */
	@Transient
	@XmlElement(name = "iov", type = CoolIovType.class)
	private List<CoolIovType> iovList = null;

	/**
	 * 
	 */
	@Transient
	@XmlElement(name = "iovsummary", type = CoolIovSummary.class)
	private Collection<CoolIovSummary> iovSummaryList = null;

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
	 * @return the nodeDescription
	 */
	public final String getNodeDescription() {
		return nodeDescription;
	}

	/**
	 * @param nodeDescription
	 *            the nodeDescription to set
	 */
	public final void setNodeDescription(final String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}

	/**
	 * @return the nodeIsleaf
	 */
	public final Integer getNodeIsleaf() {
		return nodeIsleaf;
	}

	/**
	 * @param nodeIsleaf
	 *            the nodeIsleaf to set
	 */
	public final void setNodeIsleaf(final Integer nodeIsleaf) {
		this.nodeIsleaf = nodeIsleaf;
	}

	/**
	 * @return the nodeInstime
	 */
	public final String getNodeInstime() {
		return nodeInstime;
	}

	/**
	 * @param nodeInstime
	 *            the nodeInstime to set
	 */
	public final void setNodeInstime(final String nodeInstime) {
		this.nodeInstime = nodeInstime;
	}

	/**
	 * @return the nodeTinstime
	 */
	public final Timestamp getNodeTinstime() {
		return nodeTinstime;
	}

	/**
	 * @param nodeTinstime
	 *            the nodeTinstime to set
	 */
	public final void setNodeTinstime(final Timestamp nodeTinstime) {
		this.nodeTinstime = nodeTinstime;
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
	 * @return the folderIovtablename
	 */
	public final String getFolderIovtablename() {
		return folderIovtablename;
	}

	/**
	 * @param folderIovtablename
	 *            the folderIovtablename to set
	 */
	public final void setFolderIovtablename(final String folderIovtablename) {
		this.folderIovtablename = folderIovtablename;
	}

	/**
	 * @return the folderTagtablename
	 */
	public final String getFolderTagtablename() {
		return folderTagtablename;
	}

	/**
	 * @param folderTagtablename
	 *            the folderTagtablename to set
	 */
	public final void setFolderTagtablename(final String folderTagtablename) {
		this.folderTagtablename = folderTagtablename;
	}

	/**
	 * @return the folderChanneltablename
	 */
	public final String getFolderChanneltablename() {
		return folderChanneltablename;
	}

	/**
	 * @param folderChanneltablename
	 *            the folderChanneltablename to set
	 */
	public final void setFolderChanneltablename(final String folderChanneltablename) {
		this.folderChanneltablename = folderChanneltablename;
	}

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
	 * @return the nodeIovBase
	 */
	public final String getNodeIovBase() {
		return nodeIovBase;
	}

	/**
	 * @param nodeIovBase
	 *            the nodeIovBase to set
	 */
	public final void setNodeIovBase(final String nodeIovBase) {
		this.nodeIovBase = nodeIovBase;
	}

	/**
	 * @return the nodeIovType
	 */
	public final String getNodeIovType() {
		return nodeIovType;
	}

	/**
	 * @param nodeIovType
	 *            the nodeIovType to set
	 */
	public final void setNodeIovType(final String nodeIovType) {
		this.nodeIovType = nodeIovType;
	}

	/**
	 * @return
	 */
	public final String getNodeTinstimeStr() {
		if (nodeTinstime == null) {
			return "";
		}
		final String ret = TimestampStringFormatter.format("yyyy:MM:dd hh:mm:ss",
				nodeTinstime);
		return ret;

	}

	/**
	 * @return the folderVersioning
	 */
	public final Integer getFolderVersioning() {
		return folderVersioning;
	}

	/**
	 * @param folderVersioning
	 *            the folderVersioning to set
	 */
	public final void setFolderVersioning(final Integer folderVersioning) {
		this.folderVersioning = folderVersioning;
	}

	/**
	 * @return the folderPayloadSpec
	 */
	public final String getFolderPayloadSpec() {
		return folderPayloadSpec;
	}

	/**
	 * @param folderPayloadSpec
	 *            the folderPayloadSpec to set
	 */
	public final void setFolderPayloadSpec(final String folderPayloadSpec) {
		this.folderPayloadSpec = folderPayloadSpec;
	}

	/**
	 * @return the iovList
	 */
	public final List<CoolIovType> getIovList() {
		return iovList;
	}

	/**
	 * @param iovList
	 *            the iovList to set
	 */
	public final void setIovList(final List<CoolIovType> iovList) {
		this.iovList = iovList;
	}

	/**
	 * @return the iovSummaryList
	 */
	public final Collection<CoolIovSummary> getIovSummaryList() {
		return iovSummaryList;
	}

	/**
	 * @param iovSummaryList
	 *            the iovSummaryList to set
	 */
	public final void setIovSummaryList(final Collection<CoolIovSummary> iovSummaryList) {
		this.iovSummaryList = iovSummaryList;
	}

}
