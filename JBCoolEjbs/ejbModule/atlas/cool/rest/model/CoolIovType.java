/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

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

import atlas.cool.annotations.CoolQuery;
import atlas.cool.meta.CoolIov;

/**
 * @author formica
 * 
 */
@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = CoolIovType.QUERY_FINDIOVS_INRANGE, query = "select   object_id,"
				+ " channel_name , "
				+ " channel_id ,"
				+ " iov_since ,"
				+ " iov_until ,"
				+ " user_tag_id ,"
				+ " tag_name ,"
				+ " sys_instime ,"
				+ " lastmod_date ,"
				+ " new_head_id, "
				+ " iov_base "
				+ " from table(cool_select_pkg.f_Get_IovsRangeForChannel("
				+ " :schema,:db,:node,:tag,:chanid,:since,:until))", resultClass = CoolIovType.class),
		@NamedNativeQuery(name = CoolIovType.QUERY_FINDIOVS_INRANGE_BYCHAN, query = "select   object_id,"
				+ " channel_name , "
				+ " channel_id ,"
				+ " iov_since ,"
				+ " iov_until ,"
				+ " user_tag_id ,"
				+ " tag_name ,"
				+ " sys_instime ,"
				+ " lastmod_date ,"
				+ " new_head_id, "
				+ " iov_base "
				+ " from table(cool_select_pkg.f_Get_IovsRangeForChannelName("
				+ " :schema,:db,:node,:tag,:channame,:since,:until)) "
				+ " order by channel_id, iov_since asc", resultClass = CoolIovType.class),
		@NamedNativeQuery(name = CoolIovType.QUERY_LASTMODIOV, query = "select   object_id,"
				+ " channel_name , "
				+ " channel_id ,"
				+ " iov_since ,"
				+ " iov_until ,"
				+ " user_tag_id ,"
				+ " tag_name ,"
				+ " sys_instime ,"
				+ " lastmod_date ,"
				+ " new_head_id, "
				+ " iov_base "
				+ " from table(cool_select_pkg.f_Get_LastModIov("
				+ " :schema,:db,:node,:seqid,:lmd)) " + " ", resultClass = CoolIovType.class),
		@NamedNativeQuery(name = CoolIovType.QUERY_LASTNIOVS, query = "select   object_id,"
				+ " channel_name , "
				+ " channel_id ,"
				+ " iov_since ,"
				+ " iov_until ,"
				+ " user_tag_id ,"
				+ " tag_name ,"
				+ " sys_instime ,"
				+ " lastmod_date ,"
				+ " new_head_id, "
				+ " iov_base "
				+ " from table(cool_select_pkg.f_Get_LastNumIovs("
				+ " :schema,:db,:node,:mtag,:num)) " + " ", resultClass = CoolIovType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CoolIovType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6890975317475240932L;
	/**
	 * 
	 */
	@Id
	@Column(name = "OBJECT_ID", precision = 20, scale = 0)
	private BigDecimal objectId;
	/**
	 * 
	 */
	@Column(name = "CHANNEL_ID", precision = 10, scale = 0)
	private Long channelId;
	/**
	 * 
	 */
	@Column(name = "CHANNEL_NAME", length = 255)
	private String channelName;
	/**
	 * 
	 */
	@Column(name = "IOV_SINCE", precision = 20, scale = 0)
	private BigDecimal iovSince;
	/**
	 * 
	 */
	@Column(name = "IOV_UNTIL", precision = 20, scale = 0)
	private BigDecimal iovUntil;
	/**
	 * 
	 */
	@Column(name = "USER_TAG_ID", precision = 10, scale = 0)
	private Long tagId;
	/**
	 * 
	 */
	@Column(name = "SYS_INSTIME")
	private Timestamp sysInstime;
	/**
	 * 
	 */
	@Column(name = "LASTMOD_DATE")
	private Timestamp lastmodDate;
	/**
	 * 
	 */
	@Column(name = "NEW_HEAD_ID", length = 255)
	private BigDecimal newHeadId;
	/**
	 * 
	 */
	@Column(name = "TAG_NAME", length = 255)
	private String tagName;
	/**
	 * 
	 */
	@Column(name = "IOV_BASE", length = 30)
	private String iovBase;

	/**
	 * 
	 */
	@Transient
	private String sinceCoolStr = "";
	/**
	 * 
	 */
	@Transient
	private String untilCoolStr = "";

	/**
	 * 
	 */
	@Transient
	private Map<String, String> payload = new LinkedHashMap<String, String>();

	/**
	 * 
	 */
	@Transient
	private Map<String, Object> payloadObj = new LinkedHashMap<String, Object>();

	/**
	 * 
	 */
	@CoolQuery(name = "cool.findiovsinrange", params = "schema;db;node;tag;chanid;since;until")
	public static final String QUERY_FINDIOVS_INRANGE = "cool.findiovsinrange";
	/**
	 * 
	 */
	@CoolQuery(name = "cool.findiovsinrangebychan", params = "schema;db;node;tag;channame;since;until")
	public static final String QUERY_FINDIOVS_INRANGE_BYCHAN = "cool.findiovsinrangebychan";
	/**
	 * 
	 */
	@CoolQuery(name = "cool.findlastmodiov", params = "schema;db;node;seqid;lmd")
	public static final String QUERY_LASTMODIOV = "cool.findlastmodiov";
	/**
	 * 
	 */
	@CoolQuery(name = "cool.findlastniovs", params = "schema;db;node;mtag;num")
	public static final String QUERY_LASTNIOVS = "cool.findlastniovs";

	/**
	 * 
	 */
	public CoolIovType() {
		// TODO Auto-generated constructor stub
		sinceCoolStr = "";
		untilCoolStr = "";
	}

	/**
	 * @param objectId
	 * @param channelId
	 * @param channelName
	 * @param iovSince
	 * @param iovUntil
	 * @param tagId
	 * @param sysInstime
	 * @param lastmodDate
	 * @param newHeadId
	 * @param tagName
	 * @param iovBase
	 */
	public CoolIovType(final BigDecimal objectId, final Long channelId,
			final String channelName, final BigDecimal iovSince,
			final BigDecimal iovUntil, final Long tagId,
			final Timestamp sysInstime, final Timestamp lastmodDate,
			final BigDecimal newHeadId, final String tagName,
			final String iovBase) {
		super();
		this.objectId = objectId;
		this.channelId = channelId;
		this.channelName = channelName;
		this.iovSince = iovSince;
		this.iovUntil = iovUntil;
		this.tagId = tagId;
		this.sysInstime = sysInstime;
		this.lastmodDate = lastmodDate;
		this.newHeadId = newHeadId;
		this.tagName = tagName;
		this.iovBase = iovBase;
		untilCoolStr = CoolIov.getCoolTimeRunLumiString(
				iovUntil.longValueExact(), iovBase);
		sinceCoolStr = CoolIov.getCoolTimeRunLumiString(
				iovSince.longValueExact(), iovBase);
	}

	/**
	 * @return the channelId
	 */
	@XmlElement
	public final Long getChannelId() {
		return channelId;
	}

	/**
	 * @return the channelName
	 */
	@XmlElement
	public final String getChannelName() {
		return channelName;
	}

	/**
	 * @return the iovBase
	 */
	@XmlElement
	public final String getIovBase() {
		return iovBase;
	}

	/**
	 * @return the iovSince
	 */
	@XmlElement
	public final BigDecimal getIovSince() {
		return iovSince;
	}

	/**
	 * @return the iovUntil
	 */
	@XmlElement
	public final BigDecimal getIovUntil() {
		return iovUntil;
	}

	/**
	 * @return the lastmodDate
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	public final Timestamp getLastmodDate() {
		return lastmodDate;
	}

	/**
	 * @return the newHeadId
	 */
	@XmlElement
	public final BigDecimal getNewHeadId() {
		return newHeadId;
	}

	/**
	 * @return the objectId
	 */
	@XmlElement
	public final BigDecimal getObjectId() {
		return objectId;
	}

	/**
	 * @return the payload
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.MapAdapter.class)
	public final Map<String, String> getPayload() {
		return payload;
	}

	/**
	 * @return the payload map with original objects
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.MapObjAdapter.class)
	public final Map<String, Object> getPayloadObj() {
		return payloadObj;
	}

	/**
	 * @return the sinceCoolStr
	 */
	@XmlElement
	public final String getSinceCoolStr() {
		if (sinceCoolStr == null || sinceCoolStr.isEmpty()) {
			sinceCoolStr = CoolIov.getCoolTimeRunLumiString(
					iovSince.longValueExact(), iovBase);
		}
		return sinceCoolStr;
	}

	/**
	 * @return the sysInstime
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	public final Timestamp getSysInstime() {
		return sysInstime;
	}

	/**
	 * @return the tagId
	 */
	@XmlElement
	public final Long getTagId() {
		return tagId;
	}

	/**
	 * @return the tagName
	 */
	@XmlElement
	public final String getTagName() {
		return tagName;
	}

	/**
	 * @return the untilCoolStr
	 */
	@XmlElement
	public final String getUntilCoolStr() {
		if (untilCoolStr == null || untilCoolStr.isEmpty()) {
			untilCoolStr = CoolIov.getCoolTimeRunLumiString(
					iovUntil.longValueExact(), iovBase);
		}
		return untilCoolStr;
	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
	public final void setChannelId(final Long channelId) {
		this.channelId = channelId;
	}

	/**
	 * @param channelName
	 *            the channelName to set
	 */
	public final void setChannelName(final String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @param iovBase
	 *            the iovBase to set
	 */
	public final void setIovBase(final String iovBase) {
		this.iovBase = iovBase;
	}

	/**
	 * @param iovSince
	 *            the iovSince to set
	 */
	public final void setIovSince(final BigDecimal iovSince) {
		this.iovSince = iovSince;
	}

	/**
	 * @param iovUntil
	 *            the iovUntil to set
	 */
	public final void setIovUntil(final BigDecimal iovUntil) {
		this.iovUntil = iovUntil;
	}

	/**
	 * @param lastmodDate
	 *            the lastmodDate to set
	 */
	public final void setLastmodDate(final Timestamp lastmodDate) {
		this.lastmodDate = lastmodDate;
	}

	/**
	 * @param newHeadId
	 *            the newHeadId to set
	 */
	public final void setNewHeadId(final BigDecimal newHeadId) {
		this.newHeadId = newHeadId;
	}

	/**
	 * @param objectId
	 *            the objectId to set
	 */
	public final void setObjectId(final BigDecimal objectId) {
		this.objectId = objectId;
	}

	/**
	 * @param payload
	 *            the payload to set
	 */
	public final void setPayload(final Map<String, String> payload) {
		this.payload = payload;
	}

	/**
	 * @param payloadobj
	 *            the payload to set
	 */
	public final void setPayloadObj(final Map<String, Object> payloadobj) {
		this.payloadObj = payloadobj;
	}

	/**
	 * @param sinceCoolStr
	 *            the sinceCoolStr to set
	 */
	public final void setSinceCoolStr(final String sinceCoolStr) {
		this.sinceCoolStr = sinceCoolStr;
	}

	/**
	 * @param sysInstime
	 *            the sysInstime to set
	 */
	public final void setSysInstime(final Timestamp sysInstime) {
		this.sysInstime = sysInstime;
	}

	/**
	 * @param tagId
	 *            the tagId to set
	 */
	public final void setTagId(final Long tagId) {
		this.tagId = tagId;
	}

	/**
	 * @param tagName
	 *            the tagName to set
	 */
	public final void setTagName(final String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @param untilCoolStr
	 *            the untilCoolStr to set
	 */
	public final void setUntilCoolStr(final String untilCoolStr) {
		this.untilCoolStr = untilCoolStr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		CoolIovType cobj = null;
		if (obj instanceof CoolIovType) {
			cobj = (CoolIovType) obj;
			if (cobj.getChannelId().equals(this.getChannelId())
					&& cobj.getIovSince().equals(this.getIovSince())
					&& cobj.getIovUntil().equals(this.getIovUntil())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getChannelId().hashCode()
				+ this.getIovSince().toString().hashCode()
				+ this.getIovUntil().toString().hashCode();
	}

}
