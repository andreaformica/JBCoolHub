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
 * This POJO represents the association between a global tag and a leaf tag in a
 * given node. Cool tags are defined in the _TAGS table of a COOL schema for
 * every folder (node), as well as inside the main _TAGS table, where global
 * tags are stored.
 * </p>
 * <p>
 * The Queries defined for this POJO are:<br>
 * 
 * <b>QUERY_FINDGTAGS_TAGS_TRACE [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of
 * matching nodes/tags; it uses internally the function
 * cool_select_pkg.f_getall_tagsforgtag(.....).<br>
 * For every node/tag associated with the given gtag, there is one line with
 * information on schema and db, gtag informations, tag informations, and node
 * informations.<br>
 * 
 * <b>QUERY_FINDGTAGS_TAGS_FULLTRACE [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of
 * matching nodes/tags; it uses internally the function
 * cool_select_pkg.f_getall_branchtagsforgtag(.....).<br>
 * For every node/tag associated with the given gtag, there is one line with
 * information on schema and db, gtag informations, tag informations, and node
 * informations. The result contains all intermediate branch level tags.<br>
 * 
 * <b>QUERY_FINDGTAGS_FORTAG [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, GTAG, TAG, NODE and retrieves a
 * list of matching nodes/tags/gtags; it uses internally the function
 * cool_select_pkg.f_getall_tagsforgtag(.....).<br>
 * For every node/tag associated with the given gtag, there is one line with
 * information on schema and db, gtag informations, tag informations, and node
 * informations.<br>
 * 
 * <b>QUERY_FINDGTAG_DOUBLEFLD [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of
 * matching nodes/tags/gtags; it uses internally the function
 * cool_select_pkg.f_getall_doubletagsforgtag(.....).<br>
 * It is used to search for folders which are associated twice to a given global
 * tag.
 * 
 * <b>QUERY_COMA_FINDGTAGS_TAGS_TRACE [coma_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of
 * matching nodes/tags/gtags; it uses internally the function
 * cool_select_pkg.f_getall_tagsforgtag(.....).<br>
 * It is the same as the first query described, but the info is retrieved from
 * COMA.
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
		@NamedNativeQuery(name = NodeGtagTagType.QUERY_FINDGTAGS_TAGS_FULLTRACE, query = "select   "
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
				+ "from table(cool_select_pkg.f_getall_branchtagsforgtag(:schema,:dbname,:gtag))", resultClass = NodeGtagTagType.class),
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
				+ "from table(coma_select_pkg.f_getall_tagsforgtag(:schema,:dbname,:gtag))", resultClass = NodeGtagTagType.class),
		@NamedNativeQuery(name = NodeGtagTagType.QUERY_COMA_FINDTAGS_GTAGS_BACKTRACE, query = "select   "
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
				+ "from table(coma_select_pkg.f_getall_gtagsfortag(:schema,:dbname,:tag))", resultClass = NodeGtagTagType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeGtagTagType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2990371156354454615L;

	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	@Column(name = "GTAG_ID", precision = 10, scale = 0)
	private Long gtagId;
	@Column(name = "GTAG_NAME", length = 255)
	private String gtagName;
	@Column(name = "GTAG_DESCRIPTION", length = 255)
	private String gtagDescription;
	@Column(name = "GTAG_LOCK_STATUS")
	private Integer gtagLockStatus;

	@Column(name = "SCHEMA_NAME", length = 30)
	private String schemaName;
	@Column(name = "DB_NAME", length = 30)
	private String dbName;

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

	@Column(name = "NODE_ID", precision = 10, scale = 0)
	private Long nodeId;
	@Column(name = "NODE_NAME", length = 255)
	private String nodeName;
	@Column(name = "NODE_FULLPATH", length = 255)
	private String nodeFullpath;

	@CoolQuery(name = "cool.findgtagstagstrace", params = "schema;dbname;gtag")
	public static final String QUERY_FINDGTAGS_TAGS_TRACE = "cool.findgtagstagstrace";
	@CoolQuery(name = "cool.findgtagsdouble", params = "schema;dbname;gtag")
	public static final String QUERY_FINDGTAG_DOUBLEFLD = "cool.findgtagsdouble";
	@CoolQuery(name = "cool.findgtagsfortag", params = "schema;dbname;gtag;tag;node")
	public static final String QUERY_FINDGTAGS_FORTAG = "cool.findgtagsfortag";
	@CoolQuery(name = "coma.findgtagstagstrace", params = "schema;dbname;gtag")
	public static final String QUERY_COMA_FINDGTAGS_TAGS_TRACE = "coma.findgtagstagstrace";
	@CoolQuery(name = "coma.findtaggtagsbacktrace", params = "schema;dbname;tag")
	public static final String QUERY_COMA_FINDTAGS_GTAGS_BACKTRACE = "coma.findtaggtagsbacktrace";
	@CoolQuery(name = "cool.findgtagstagsfulltrace", params = "schema;dbname;gtag")
	public static final String QUERY_FINDGTAGS_TAGS_FULLTRACE = "cool.findgtagstagsfulltrace";

	/**
	 * @return the gtagId
	 */
	public final Long getGtagId() {
		return gtagId;
	}

	/**
	 * @param gtagId
	 *            the gtagId to set
	 */
	public final void setGtagId(final Long gtagId) {
		this.gtagId = gtagId;
	}

	/**
	 * @return the gtagName
	 */
	public final String getGtagName() {
		return gtagName;
	}

	/**
	 * @param gtagName
	 *            the gtagName to set
	 */
	public final void setGtagName(final String gtagName) {
		this.gtagName = gtagName;
	}

	/**
	 * @return the gtagDescription
	 */
	public final String getGtagDescription() {
		return gtagDescription;
	}

	/**
	 * @param gtagDescription
	 *            the gtagDescription to set
	 */
	public final void setGtagDescription(final String gtagDescription) {
		this.gtagDescription = gtagDescription;
	}

	/**
	 * @return the gtagLockStatus
	 */
	public final Integer getGtagLockStatus() {
		return gtagLockStatus;
	}

	/**
	 * @param gtagLockStatus
	 *            the gtagLockStatus to set
	 */
	public final void setGtagLockStatus(final Integer gtagLockStatus) {
		this.gtagLockStatus = gtagLockStatus;
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
	 * @param tagDescription
	 *            the tagDescription to set
	 */
	public final void setTagDescription(final String tagDescription) {
		this.tagDescription = tagDescription;
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

}
