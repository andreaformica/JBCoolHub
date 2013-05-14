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
@NamedNativeQueries( {
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
 + "from table(cool_select_pkg.f_Get_IovsRangeForChannel(:schema,:db,:node,:tag,:chanid,:since,:until))", resultClass = CoolIovType.class),
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
+ "from table(cool_select_pkg.f_Get_IovsRangeForChannelName(:schema,:db,:node,:tag,:channame,:since,:until)) "
+ "order by channel_id, iov_since asc", resultClass = CoolIovType.class)
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CoolIovType implements Serializable {

	@Id
	@Column(name = "OBJECT_ID", precision = 20, scale = 0)
	BigDecimal objectId; 
	@Column(name = "CHANNEL_ID", precision = 10, scale = 0)
	Long channelId;
	@Column(name = "CHANNEL_NAME", length = 255)
	String channelName;
	@Column(name = "IOV_SINCE", precision = 20, scale = 0)
	BigDecimal iovSince;
	@Column(name = "IOV_UNTIL", precision = 20, scale = 0)
	BigDecimal iovUntil;
	@Column(name = "USER_TAG_ID", precision = 10, scale = 0)
	Long tagId;
	@Column(name = "SYS_INSTIME")
	Timestamp sysInstime;
	@Column(name = "LASTMOD_DATE")
	Timestamp  lastmodDate; 
	@Column(name = "NEW_HEAD_ID", length = 255)
	BigDecimal newHeadId;
	@Column(name = "TAG_NAME", length = 255)
	String tagName;
	@Column(name = "IOV_BASE", length = 30)
	String iovBase;

	@Transient
	String sinceCoolStr="";
	@Transient
	String untilCoolStr="";

	@Transient
	Map<String,String> payload = new LinkedHashMap<String, String>();
	

	@CoolQuery(name = "cool.findiovsinrange", params = "schema;db;node;tag;chanid;since;until")
	public static final String QUERY_FINDIOVS_INRANGE = "cool.findiovsinrange";
	@CoolQuery(name = "cool.findiovsinrangebychan", params = "schema;db;node;tag;channame;since;until")
	public static final String QUERY_FINDIOVS_INRANGE_BYCHAN = "cool.findiovsinrangebychan";

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
	public CoolIovType(BigDecimal objectId, Long channelId, String channelName,
			BigDecimal iovSince, BigDecimal iovUntil, Long tagId,
			Timestamp sysInstime, Timestamp lastmodDate, BigDecimal newHeadId,
			String tagName, String iovBase) {
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
		untilCoolStr = CoolIov.getCoolTimeRunLumiString(iovUntil.longValueExact(), iovBase);
		sinceCoolStr = CoolIov.getCoolTimeRunLumiString(iovSince.longValueExact(), iovBase);
	}

	/**
	 * @return the objectId
	 */
	@XmlElement
	public BigDecimal getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(BigDecimal objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the channelId
	 */
	@XmlElement
	public Long getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the channelName
	 */
	@XmlElement
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the iovSince
	 */
	@XmlElement
	public BigDecimal getIovSince() {
		return iovSince;
	}

	/**
	 * @param iovSince the iovSince to set
	 */
	public void setIovSince(BigDecimal iovSince) {
		this.iovSince = iovSince;
	}

	/**
	 * @return the iovUntil
	 */
	@XmlElement
	public BigDecimal getIovUntil() {
		return iovUntil;
	}

	/**
	 * @param iovUntil the iovUntil to set
	 */
	public void setIovUntil(BigDecimal iovUntil) {
		this.iovUntil = iovUntil;
	}

	/**
	 * @return the tagId
	 */
	@XmlElement
	public Long getTagId() {
		return tagId;
	}

	/**
	 * @param tagId the tagId to set
	 */
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the sysInstime
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	public Timestamp getSysInstime() {
		return sysInstime;
	}

	/**
	 * @param sysInstime the sysInstime to set
	 */
	public void setSysInstime(Timestamp sysInstime) {
		this.sysInstime = sysInstime;
	}

	/**
	 * @return the lastmodDate
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	public Timestamp getLastmodDate() {
		return lastmodDate;
	}

	/**
	 * @param lastmodDate the lastmodDate to set
	 */
	public void setLastmodDate(Timestamp lastmodDate) {
		this.lastmodDate = lastmodDate;
	}

	/**
	 * @return the newHeadId
	 */
	@XmlElement
	public BigDecimal getNewHeadId() {
		return newHeadId;
	}

	/**
	 * @param newHeadId the newHeadId to set
	 */
	public void setNewHeadId(BigDecimal newHeadId) {
		this.newHeadId = newHeadId;
	}

	/**
	 * @return the tagName
	 */
	@XmlElement
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the iovBase
	 */
	@XmlElement
	public String getIovBase() {
		return iovBase;
	}

	/**
	 * @param iovBase the iovBase to set
	 */
	public void setIovBase(String iovBase) {
		this.iovBase = iovBase;
	}

	/**
	 * @return the sinceCoolStr
	 */
	@XmlElement
	public String getSinceCoolStr() {
		if (sinceCoolStr==null || sinceCoolStr.isEmpty())
			sinceCoolStr = CoolIov.getCoolTimeRunLumiString(iovSince.longValueExact(), iovBase);
		return sinceCoolStr;
	}

	/**
	 * @param sinceCoolStr the sinceCoolStr to set
	 */
	public void setSinceCoolStr(String sinceCoolStr) {
		this.sinceCoolStr = sinceCoolStr;
	}

	/**
	 * @return the untilCoolStr
	 */
	@XmlElement
	public String getUntilCoolStr() {
		if (untilCoolStr==null || untilCoolStr.isEmpty())
			untilCoolStr = CoolIov.getCoolTimeRunLumiString(iovUntil.longValueExact(), iovBase);
		return untilCoolStr;
	}

	/**
	 * @param untilCoolStr the untilCoolStr to set
	 */
	public void setUntilCoolStr(String untilCoolStr) {
		this.untilCoolStr = untilCoolStr;
	}

	/**
	 * @return the payload
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.MapAdapter.class)
	public Map<String, String> getPayload() {
		return payload;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayload(Map<String, String> payload) {
		this.payload = payload;
	}

}
