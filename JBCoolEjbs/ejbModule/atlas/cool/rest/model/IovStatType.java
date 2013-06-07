/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.utils.TimestampStringFormatter;

/**
 * <p>
 * This POJO represents some statistics that can be gathered over IOVs. Cool iovs are
 * defined in the _IOVS table of a COOL schema for every folder (node). The purpose of
 * this POJO is to collect information on the amount of iovs are stored for a given node
 * and tag, computing also minimum and maximum time intervals.
 * </p>
 * <p>
 * The Queries defined for this POJO are: <br>
 * <b>QUERY_FINDIOVS [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, NODE, TAG and retrieves a list of
 * matching nodes/tags; it uses internally the function
 * cool_select_pkg.f_GetAll_IovShort(.....).<br>
 * For every node/tag there is one line with statistics of iovs.
 * </p>
 * 
 * @author formica
 * @since 2013/04/01
 * 
 */
@Entity
@NamedNativeQueries({ @NamedNativeQuery(name = IovStatType.QUERY_FINDIOVS, query = "select   "
		+ " schema_name, "
		+ " dbname as db_name, "
		+ " node_id ,"
		+ " node_fullpath ,"
		+ " tag_id, "
		+ " tag_name, "
		+ " niovs, "
		+ " nchannels, "
		+ " miniov_since, "
		+ " miniov_until, "
		+ " maxiov_since, "
		+ " maxiov_until, "
		+ " iov_base, "
		+ " rownum "
		+ " from table(cool_select_pkg.f_GetAll_IovsShort(:schema,:db,:node,:tag)) ", resultClass = IovStatType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IovStatType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7455572429347968304L;

	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	@Column(name = "SCHEMA_NAME", length = 30)
	private String schemaName;
	@Column(name = "DB_NAME", length = 30)
	private String dbName;
	@Column(name = "NODE_ID", precision = 10, scale = 0)
	private Long nodeId;
	@Column(name = "NODE_FULLPATH", length = 255)
	private String nodeFullpath;

	@Column(name = "TAG_ID", precision = 10, scale = 0)
	private Long tagId;
	@Column(name = "TAG_NAME", length = 255)
	private String tagName;

	@Column(name = "NIOVS", precision = 10, scale = 0)
	private Long niovs;
	@Column(name = "NCHANNELS", precision = 10, scale = 0)
	private Long nchannels;

	@Column(name = "MINIOV_SINCE", precision = 20, scale = 0)
	private BigDecimal miniovSince;
	@Column(name = "MINIOV_UNTIL", precision = 20, scale = 0)
	private BigDecimal miniovUntil;
	@Column(name = "MAXIOV_SINCE", precision = 20, scale = 0)
	private BigDecimal maxiovSince;
	@Column(name = "MAXIOV_UNTIL", precision = 20, scale = 0)
	private BigDecimal maxiovUntil;

	@Column(name = "IOV_BASE", length = 100)
	private String iovBase;

	@CoolQuery(name = "cool.findiovstat", params = "schema;db;node;tag")
	public static final String QUERY_FINDIOVS = "cool.findiovstat";

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
	 * @return the niovs
	 */
	public final Long getNiovs() {
		return niovs;
	}

	/**
	 * @param niovs
	 *            the niovs to set
	 */
	public final void setNiovs(final Long niovs) {
		this.niovs = niovs;
	}

	/**
	 * @return the nchannels
	 */
	public final Long getNchannels() {
		return nchannels;
	}

	/**
	 * @param nchannels
	 *            the nchannels to set
	 */
	public final void setNchannels(final Long nchannels) {
		this.nchannels = nchannels;
	}

	/**
	 * @return the miniovSince
	 */
	public final BigDecimal getMiniovSince() {
		return miniovSince;
	}

	/**
	 * @param miniovSince
	 *            the miniovSince to set
	 */
	public final void setMiniovSince(final BigDecimal miniovSince) {
		this.miniovSince = miniovSince;
	}

	/**
	 * @return the miniovUntil
	 */
	public final BigDecimal getMiniovUntil() {
		return miniovUntil;
	}

	/**
	 * @param miniovUntil
	 *            the miniovUntil to set
	 */
	public final void setMiniovUntil(final BigDecimal miniovUntil) {
		this.miniovUntil = miniovUntil;
	}

	/**
	 * @return the maxiovSince
	 */
	public final BigDecimal getMaxiovSince() {
		return maxiovSince;
	}

	/**
	 * @param maxiovSince
	 *            the maxiovSince to set
	 */
	public final void setMaxiovSince(final BigDecimal maxiovSince) {
		this.maxiovSince = maxiovSince;
	}

	/**
	 * @return the maxiovUntil
	 */
	public final BigDecimal getMaxiovUntil() {
		return maxiovUntil;
	}

	/**
	 * @param maxiovUntil
	 *            the maxiovUntil to set
	 */
	public final void setMaxiovUntil(final BigDecimal maxiovUntil) {
		this.maxiovUntil = maxiovUntil;
	}

	/**
	 * @return the iovBase
	 */
	public final String getIovBase() {
		return iovBase;
	}

	/**
	 * @param iovBase
	 *            the iovBase to set
	 */
	public final void setIovBase(final String iovBase) {
		this.iovBase = iovBase;
	}

	/**
	 * @return
	 */
	public final String getCoolIovMinSince() {
		String iovstr = "";
		if (miniovSince == null) {
			return "null";
		}
		if (iovBase.equals("run-lumi")) {
			final Long run = CoolIov.getRun(miniovSince.toBigInteger());
			final Long lumi = CoolIov.getLumi(miniovSince.toBigInteger());
			iovstr = run + " : " + lumi;
		} else {
			final Long time = CoolIov.getTime(miniovSince.toBigInteger());
			final Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}

	/**
	 * @return
	 */
	public final String getCoolIovMaxSince() {
		String iovstr = "";
		if (maxiovSince == null) {
			return "null";
		}
		if (iovBase.equals("run-lumi")) {
			final Long run = CoolIov.getRun(maxiovSince.toBigInteger());
			final Long lumi = CoolIov.getLumi(maxiovSince.toBigInteger());
			iovstr = run + " : " + lumi;
		} else {
			final Long time = CoolIov.getTime(maxiovSince.toBigInteger());
			final Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}

}
