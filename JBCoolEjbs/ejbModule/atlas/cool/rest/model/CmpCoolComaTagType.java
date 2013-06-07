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
@NamedNativeQueries({ @NamedNativeQuery(name = CmpCoolComaTagType.QUERY_CMP_TAGS, query = "select   "
		+ " schema_name, "
		+ " db_name, "
		+ " node_name , "
		+ " node_fullpath ,"
		+ " tag_id, "
		+ " tag_name, "
		+ " tag_lock_status, "
		+ " sys_instime, "
		+ " coma_cool_schema, "
		+ " coma_node_fullpath, "
		+ " coma_tag_name, "
		+ " coma_tag_lock_status, "
		+ " rownum "
		+ "from table(comacool_cmp_pkg.f_cmpall_tags(:schema,:dbname,:node,:tag)) "
		+ " order by schema_name, node_fullpath, tag_name ", resultClass = CmpCoolComaTagType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CmpCoolComaTagType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7061216932390622255L;

	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	@Column(name = "SCHEMA_NAME", length = 30)
	private String schemaName;
	@Column(name = "DB_NAME", length = 30)
	private String dbName;

	@Column(name = "NODE_NAME", length = 255)
	private String nodeName;
	@Column(name = "NODE_FULLPATH", length = 255)
	private String nodeFullpath;

	@Column(name = "TAG_ID", precision = 10, scale = 0)
	private Long tagId;
	@Column(name = "TAG_NAME", length = 255)
	private String tagName;
	@Column(name = "TAG_LOCK_STATUS")
	private Integer tagLockStatus;
	@Column(name = "SYS_INSTIME", length = 255)
	private String sysInstime;

	@Column(name = "COMA_COOL_SCHEMA", length = 255)
	private String comaCoolSchema;
	@Column(name = "COMA_NODE_FULLPATH", length = 255)
	private String comaNodeFullpath;
	@Column(name = "COMA_TAG_NAME", length = 255)
	private String comaTagName;
	@Column(name = "COMA_TAG_LOCK_STATUS")
	private Integer comaTagLockStatus;

	@CoolQuery(name = "cool.cmptags", params = "schema;dbname;node;tag")
	public static final String QUERY_CMP_TAGS = "cool.cmptags";

	/**
	 * @return the schemaName
	 */
	public final  String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName
	 *            the schemaName to set
	 */
	public final  void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the dbName
	 */
	public final  String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public final  void setDbName(final String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the nodeName
	 */
	public final  String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName
	 *            the nodeName to set
	 */
	public final  void setNodeName(final String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @return the nodeFullpath
	 */
	public final  String getNodeFullpath() {
		return nodeFullpath;
	}

	/**
	 * @param nodeFullpath
	 *            the nodeFullpath to set
	 */
	public final  void setNodeFullpath(final String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}

	/**
	 * @return the tagId
	 */
	public final  Long getTagId() {
		return tagId;
	}

	/**
	 * @param tagId
	 *            the tagId to set
	 */
	public final  void setTagId(final Long tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the tagName
	 */
	public final  String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 *            the tagName to set
	 */
	public final  void setTagName(final String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the sysInstime
	 */
	public final  String getSysInstime() {
		return sysInstime;
	}

	/**
	 * @param sysInstime
	 *            the sysInstime to set
	 */
	public final  void setSysInstime(final String sysInstime) {
		this.sysInstime = sysInstime;
	}

	/**
	 * @return the comaTagName
	 */
	public final  String getComaTagName() {
		return comaTagName;
	}

	/**
	 * @param comaTagName
	 *            the comaTagName to set
	 */
	public final  void setComaTagName(final String comaTagName) {
		this.comaTagName = comaTagName;
	}

	/**
	 * @return the comaCoolSchema
	 */
	public final  String getComaCoolSchema() {
		return comaCoolSchema;
	}

	/**
	 * @param comaCoolSchema
	 *            the comaCoolSchema to set
	 */
	public final  void setComaCoolSchema(final String comaCoolSchema) {
		this.comaCoolSchema = comaCoolSchema;
	}

	/**
	 * @return the comaNodeFullpath
	 */
	public final  String getComaNodeFullpath() {
		return comaNodeFullpath;
	}

	/**
	 * @param comaNodeFullpath
	 *            the comaNodeFullpath to set
	 */
	public final  void setComaNodeFullpath(final String comaNodeFullpath) {
		this.comaNodeFullpath = comaNodeFullpath;
	}

	/**
	 * @return the tagLockStatus
	 */
	public final  Integer getTagLockStatus() {
		return tagLockStatus;
	}

	/**
	 * @param tagLockStatus
	 *            the tagLockStatus to set
	 */
	public final  void setTagLockStatus(final Integer tagLockStatus) {
		this.tagLockStatus = tagLockStatus;
	}

	/**
	 * @return the comaTagLockStatus
	 */
	public final  Integer getComaTagLockStatus() {
		return comaTagLockStatus;
	}

	/**
	 * @param comaTagLockStatus
	 *            the comaTagLockStatus to set
	 */
	public final  void setComaTagLockStatus(final Integer comaTagLockStatus) {
		this.comaTagLockStatus = comaTagLockStatus;
	}

}
