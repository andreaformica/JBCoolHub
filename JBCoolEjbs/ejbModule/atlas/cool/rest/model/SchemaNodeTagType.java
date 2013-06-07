package atlas.cool.rest.model;

/**
 * 
 */

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import atlas.cool.annotations.CoolQuery;

/**
 * @author formica
 * 
 */
@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = SchemaNodeTagType.QUERY_FINDTAGS_IN_NODES, 
				query = "select   "
				+ " schema_name, "
				+ " dbname, "
				+ " node_id,"
				+ " node_name , "
				+ " node_fullpath ,"
				+ " node_description ,"
				+ " node_isleaf ,"
				+ " node_instime ,"
				+ " node_tinstime ,"
				+ " lastmod_date ,"
				+ " folder_iovtablename ,"
				+ " folder_tagtablename ,"
				+ " folder_channeltablename, "
				+ " tag_id, "
				+ " tag_name, "
				+ " tag_description, "
				+ " tag_lock_status, "
				+ " sys_instime, "
				+ " rownum "
				+ "from table(cool_select_pkg.f_getall_tags(:schema,:dbname,:node,:tag)) "
				+ " order by schema_name, node_fullpath ", 
				resultClass = SchemaNodeTagType.class),
		@NamedNativeQuery(name = SchemaNodeTagType.QUERY_COMA_FINDTAGS_IN_NODES, 
		query = "select   "
				+ " schema_name, "
				+ " dbname, "
				+ " node_id,"
				+ " node_name , "
				+ " node_fullpath ,"
				+ " node_description ,"
				+ " node_isleaf ,"
				+ " node_instime ,"
				+ " node_tinstime ,"
				+ " lastmod_date ,"
				+ " folder_iovtablename ,"
				+ " folder_tagtablename ,"
				+ " folder_channeltablename, "
				+ " tag_id, "
				+ " tag_name, "
				+ " tag_description, "
				+ " tag_lock_status, "
				+ " sys_instime, "
				+ " rownum "
				+ "from table(coma_select_pkg.f_getall_tags(:schema,:dbname,:node,:tag)) "
				+ " order by schema_name, node_fullpath ", 
				resultClass = SchemaNodeTagType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SchemaNodeTagType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8950148398190695530L;

	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	@Column(name = "TAG_ID", precision = 10, scale = 0)
	private Long tagId;
	@Column(name = "TAG_NAME", length = 255)
	private String tagName;
	@Column(name = "TAG_DESCRIPTION", length = 255)
	private String tagDescription;
	@Column(name = "TAG_LOCK_STATUS")
	private Integer tagLockStatus;
	@Column(name = "SYS_INSTIME", length = 255)
	private String sysInstime;

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

	@Column(name = "NODE_TINSTIME")
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	private Timestamp nodeTinstime;

	@Column(name = "LASTMOD_DATE", length = 255)
	private String lastmodDate;
	@Column(name = "FOLDER_IOVTABLENAME", length = 255)
	private String folderIovtablename;
	@Column(name = "FOLDER_TAGTABLENAME", length = 255)
	private String folderTagtablename;
	@Column(name = "FOLDER_CHANNELTABLENAME", length = 255)
	private String folderChanneltablename;

	@CoolQuery(name = "cool.findtagsinnodes", params = "schema;dbname;node;tag")
	public static final String QUERY_FINDTAGS_IN_NODES = "cool.findtagsinnodes";
	@CoolQuery(name = "coma.findtagsinnodes", params = "schema;dbname;node;tag")
	public static final String QUERY_COMA_FINDTAGS_IN_NODES = "coma.findtagsinnodes";

	/**
	 * @return the tagId
	 */
	public final Long getTagId() {
		return tagId;
	}

	/**
	 * @param tagId
	 *            the tagId to set
	 */
	public final void setTagId(final Long tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the tagName
	 */
	public final String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 *            the tagName to set
	 */
	public final void setTagName(final String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the tagDescription
	 */
	public final String getTagDescription() {
		return tagDescription;
	}

	/**
	 * @param tagDescription
	 *            the tagDescription to set
	 */
	public final void setTagDescription(final String tagDescription) {
		this.tagDescription = tagDescription;
	}

	/**
	 * @return the tagLockStatus
	 */
	public final Integer getTagLockStatus() {
		return tagLockStatus;
	}

	/**
	 * @param tagLockStatus
	 *            the tagLockStatus to set
	 */
	public final void setTagLockStatus(final Integer tagLockStatus) {
		this.tagLockStatus = tagLockStatus;
	}

	/**
	 * @return the sysInstime
	 */
	public final String getSysInstime() {
		return sysInstime;
	}

	/**
	 * @param sysInstime
	 *            the sysInstime to set
	 */
	public final void setSysInstime(final String sysInstime) {
		this.sysInstime = sysInstime;
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

}
