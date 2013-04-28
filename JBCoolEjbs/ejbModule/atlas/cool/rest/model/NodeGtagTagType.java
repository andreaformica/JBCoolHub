/**
 * 
 */
package atlas.cool.rest.model;

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
 * <p>
 * This POJO represents the association between a global tag and a leaf tag in a given node. 
 * Cool tags are defined in the _TAGS table of a COOL schema for every folder (node), as well as inside the
 * main _TAGS table, where global tags are stored. 
 * </p>
 * <p>
 * The Queries defined for this POJO are:<br> 
 *   
 * 		<b>QUERY_FINDGTAGS_TAGS_TRACE [cool_select_pkg]</b><br>
 * 		This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of matching nodes/tags;
 * 		it uses internally the function cool_select_pkg.f_getall_tagsforgtag(.....).<br>
 * 		For every node/tag associated with the given gtag, there is one line with information on schema
 * 		and db, gtag informations, tag informations, and node informations.<br>
 * 
 * 		<b>QUERY_FINDGTAGS_FORTAG [cool_select_pkg]</b><br>
 * 		This query takes as arguments the SCHEMA, DB, GTAG, TAG, NODE and retrieves a list of matching nodes/tags/gtags;
 * 		it uses internally the function cool_select_pkg.f_getall_tagsforgtag(.....).<br>
 * 		For every node/tag associated with the given gtag, there is one line with information on schema
 * 		and db, gtag informations, tag informations, and node informations.<br>
 * 
 * 		<b>QUERY_FINDGTAG_DOUBLEFLD [cool_select_pkg]</b><br>
 * 		This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of matching nodes/tags/gtags;
 * 		it uses internally the function cool_select_pkg.f_getall_doubletagsforgtag(.....).<br>
 * 		It is used to search for folders which are associated twice to a given global tag.
 * 
 * 		<b>QUERY_COMA_FINDGTAGS_TAGS_TRACE [coma_select_pkg]</b><br>
 * 		This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of matching nodes/tags/gtags;
 * 		it uses internally the function cool_select_pkg.f_getall_tagsforgtag(.....).<br>
 * 		It is the same as the first query described, but the info is retrieved from COMA.
 * </p>
 * 
 * @author formica
 * 
 * @since 2013/04/01.
 * 
 * @version 1.0
 * 
 */
@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = NodeGtagTagType.QUERY_FINDGTAGS_TAGS_TRACE, query = "select   "
				+ " schema_name, "
				+ " db_name, "
				+ " gtag_id, "
				+ " gtag_name, "
				+ " gtag_description, "
				+ " gtag_lock_status, "
				+ " node_id,"
				+ " node_name , "
				+ " node_fullpath ,"
				+ " tag_id, "
				+ " tag_name, "
				+ " tag_description, "
				+ " tag_lock_status, "
				+ " sys_instime, "
				+ " rownum "
				+ "from table(cool_select_pkg.f_getall_tagsforgtag(:schema,:dbname,:gtag))", resultClass = NodeGtagTagType.class),
		@NamedNativeQuery(name = NodeGtagTagType.QUERY_FINDGTAGS_FORTAG, query = "select   "
				+ " schema_name, "
				+ " db_name, "
				+ " gtag_id, "
				+ " gtag_name, "
				+ " gtag_description, "
				+ " gtag_lock_status, "
				+ " node_id,"
				+ " node_name , "
				+ " node_fullpath ,"
				+ " tag_id, "
				+ " tag_name, "
				+ " tag_description, "
				+ " tag_lock_status, "
				+ " sys_instime, "
				+ " rownum "
				+ "from table(cool_select_pkg.f_getall_tagsforgtag(:schema,:dbname,:gtag)) "
				+ " where tag_name=:tag and node_fullpath=:node order by gtag_id desc", resultClass = NodeGtagTagType.class),
		@NamedNativeQuery(name = NodeGtagTagType.QUERY_FINDGTAG_DOUBLEFLD, query = "select   "
				+ " schema_name, "
				+ " db_name, "
				+ " gtag_id, "
				+ " gtag_name, "
				+ " gtag_description, "
				+ " gtag_lock_status, "
				+ " node_id,"
				+ " node_name , "
				+ " node_fullpath ,"
				+ " tag_id, "
				+ " tag_name, "
				+ " tag_description, "
				+ " tag_lock_status, "
				+ " sys_instime, "
				+ " rownum "
				+ "from table(cool_select_pkg.f_getall_doubletagsforgtag(:schema,:dbname,:gtag)) "
				+ " order by node_fullpath desc", resultClass = NodeGtagTagType.class),
		@NamedNativeQuery(name = NodeGtagTagType.QUERY_COMA_FINDGTAGS_TAGS_TRACE, query = "select   "
				+ " schema_name, "
				+ " db_name, "
				+ " gtag_id, "
				+ " gtag_name, "
				+ " gtag_description, "
				+ " gtag_lock_status, "
				+ " node_id,"
				+ " node_name , "
				+ " node_fullpath ,"
				+ " tag_id, "
				+ " tag_name, "
				+ " tag_description, "
				+ " tag_lock_status, "
				+ " sys_instime, "
				+ " rownum "
				+ "from table(coma_select_pkg.f_getall_tagsforgtag(:schema,:dbname,:gtag))", resultClass = NodeGtagTagType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeGtagTagType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2990371156354454615L;

	@Id
	@Column(name = "ROWNUM")
	Long rowid;

	@Column(name = "GTAG_ID", precision = 10, scale = 0)
	Long gtagId;
	@Column(name = "GTAG_NAME", length = 255)
	String gtagName;
	@Column(name = "GTAG_DESCRIPTION", length = 255)
	String gtagDescription;
	@Column(name = "GTAG_LOCK_STATUS")
	Integer gtagLockStatus;

	@Column(name = "SCHEMA_NAME", length = 30)
	String schemaName;
	@Column(name = "DB_NAME", length = 30)
	String dbName;

	@Column(name = "TAG_ID", precision = 10, scale = 0)
	Long tagId;
	@Column(name = "TAG_NAME", length = 255)
	String tagName;
	@Column(name = "TAG_DESCRIPTION", length = 255)
	String tagDescription;
	@Column(name = "TAG_LOCK_STATUS")
	Integer tagLockStatus;
	@Column(name = "SYS_INSTIME", length = 255)
	String sysInstime;

	@Column(name = "NODE_ID", precision = 10, scale = 0)
	Long nodeId;
	@Column(name = "NODE_NAME", length = 255)
	String nodeName;
	@Column(name = "NODE_FULLPATH", length = 255)
	String nodeFullpath;

	@CoolQuery(name = "cool.findgtagstagstrace", params = "schema;dbname;gtag")
	public static final String QUERY_FINDGTAGS_TAGS_TRACE = "cool.findgtagstagstrace";
	@CoolQuery(name = "cool.findgtagsdouble", params = "schema;dbname;gtag")
	public static final String QUERY_FINDGTAG_DOUBLEFLD = "cool.findgtagsdouble";
	@CoolQuery(name = "cool.findgtagsfortag", params = "schema;dbname;gtag;tag;node")
	public static final String QUERY_FINDGTAGS_FORTAG = "cool.findgtagsfortag";
	@CoolQuery(name = "coma.findgtagstagstrace", params = "schema;dbname;gtag")
	public static final String QUERY_COMA_FINDGTAGS_TAGS_TRACE = "coma.findgtagstagstrace";

	/**
	 * @return the gtagId
	 */
	public Long getGtagId() {
		return gtagId;
	}

	/**
	 * @param gtagId
	 *            the gtagId to set
	 */
	public void setGtagId(Long gtagId) {
		this.gtagId = gtagId;
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
		this.gtagName = gtagName;
	}

	/**
	 * @return the gtagDescription
	 */
	public String getGtagDescription() {
		return gtagDescription;
	}

	/**
	 * @param gtagDescription
	 *            the gtagDescription to set
	 */
	public void setGtagDescription(String gtagDescription) {
		this.gtagDescription = gtagDescription;
	}

	/**
	 * @return the gtagLockStatus
	 */
	public Integer getGtagLockStatus() {
		return gtagLockStatus;
	}

	/**
	 * @param gtagLockStatus
	 *            the gtagLockStatus to set
	 */
	public void setGtagLockStatus(Integer gtagLockStatus) {
		this.gtagLockStatus = gtagLockStatus;
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
		this.schemaName = schemaName;
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
		this.dbName = dbName;
	}

	/**
	 * @return the tagId
	 */
	public Long getTagId() {
		return tagId;
	}

	/**
	 * @param tagId
	 *            the tagId to set
	 */
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 *            the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the tagDescription
	 */
	public String getTagDescription() {
		return tagDescription;
	}

	/**
	 * @return the tagLockStatus
	 */
	public Integer getTagLockStatus() {
		return tagLockStatus;
	}

	/**
	 * @param tagLockStatus
	 *            the tagLockStatus to set
	 */
	public void setTagLockStatus(Integer tagLockStatus) {
		this.tagLockStatus = tagLockStatus;
	}

	/**
	 * @param tagDescription
	 *            the tagDescription to set
	 */
	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	/**
	 * @return the sysInstime
	 */
	public String getSysInstime() {
		return sysInstime;
	}

	/**
	 * @param sysInstime
	 *            the sysInstime to set
	 */
	public void setSysInstime(String sysInstime) {
		this.sysInstime = sysInstime;
	}

	/**
	 * @return the nodeId
	 */
	public Long getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
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
	 * @param nodeName
	 *            the nodeName to set
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
	 * @param nodeFullpath
	 *            the nodeFullpath to set
	 */
	public void setNodeFullpath(String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}

}
