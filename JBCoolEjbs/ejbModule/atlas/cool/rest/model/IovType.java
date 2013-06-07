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
import javax.persistence.Transient;
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
 * and tag and for every channel, computing also minimum and maximum time intervals. The
 * main usage for this POJO is to explore time coverage for a given node/tag, which is
 * very important when we want to verify the consistency of a GlobalTag.
 * </p>
 * <p>
 * The Queries defined for this POJO are: <br>
 * <b>QUERY_FINDIOVS [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, NODE, TAG and retrieves a list of
 * matching nodes/tags; it uses internally the function cool_select_pkg.f_Get_Iovs(.....).
 * <br>
 * For every node/tag/channel there is one line with statistics of iovs.<br>
 * 
 * <b>QUERY_FINDHOLES [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, NODE, TAG and retrieves a list of
 * matching nodes/tags; it uses internally the function cool_select_pkg.f_Get_Iovs(.....).
 * <br>
 * For every node/tag/channel there is one line with statistics of holes spotted.<br>
 * 
 * <b>QUERY_FINDIOVSUMMARY [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, NODE, TAG and retrieves a list of
 * matching nodes/tags; it uses internally the function cool_select_pkg.f_Get_Iovs(.....).
 * <br>
 * For every node/tag/channel there is at least one(+) line(s) with a summary of time
 * ranges covered.<br>
 * For example: ch 1 : [0 - Inf] Niovs <br>
 * or ch 1 : [0 - 100] Niovs ; [100 - 110] hole; [110 - Inf] Miovs; <br>
 * </p>
 * 
 * @author formica
 * @since 2013/04/01
 */
@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = IovType.QUERY_FINDIOVS, query = "select   "
				+ "user_tag_id*10000000+channel_id as row_id, "
				+ "user_tag_id, max(tag_name) as tag_name, "
				+ "channel_id, "
				+ "max(channel_name) as channel_name, "
				+ "min(iov_since) as miniov_since, "
				+ "min(iov_until) as miniov_until, "
				+ "max(iov_since) as maxiov_since, "
				+ "max(iov_until) as maxiov_until, "
				+ "sum(hole) as iov_hole, "
				+ " 0 as hole_until,"
				+ "count(channel_id) as niovs "
				+ " from ( "
				+ "select channel_id, channel_name,iov_since, iov_until, next_since, (case when (next_since-iov_until)>0 then (next_since-iov_until) else 0 end) as hole "
				+ " , user_tag_id, tag_name "
				+ " from ("
				+ "select "
				+ " user_tag_id, tag_name, "
				+ "channel_id, channel_name, iov_since, iov_until,  LEAD(iov_since) OVER(ORDER BY channel_id, iov_since) as next_since "
				+ "from table(cool_select_pkg.f_get_iovs(:schema,:db,:node,:tag)) order by channel_id asc ) "
				+ ")  group by user_tag_id, channel_id ", resultClass = IovType.class),
		@NamedNativeQuery(name = IovType.QUERY_FINDHOLES, query = "select   "
				+ "user_tag_id*10000000+channel_id as row_id, "
				+ "user_tag_id, max(tag_name) as tag_name, "
				+ "channel_id, "
				+ "max(channel_name) as channel_name, "
				+ "min(iov_since) as miniov_since, "
				+ "min(iov_until) as miniov_until, "
				+ "max(iov_since) as maxiov_since, "
				+ "max(iov_until) as maxiov_until, "
				+ "sum(hole) as iov_hole, "
				+ " 0 as hole_until,"
				+ "count(channel_id) as niovs "
				+ " from ( "
				+ "select "
				+ " user_tag_id, tag_name, "
				+ "channel_id, channel_name,iov_since, iov_until, next_since, (case when (next_since-iov_until)>0 then (next_since-iov_until) else 0 end) as hole "
				+ " from ("
				+ "select "
				+ " user_tag_id, tag_name, "
				+ "channel_id, channel_name, iov_since, iov_until,  LEAD(iov_since) OVER(ORDER BY channel_id, iov_since) as next_since "
				+ "from table(cool_select_pkg.f_get_iovs(:schema,:db,:node,:tag)) order by channel_id asc ) "
				+ ") where hole>0 group by user_tag_id, channel_id ", resultClass = IovType.class),
		@NamedNativeQuery(name = IovType.QUERY_FINDIOVSUMMARY, query = "select "
				+ " rownum as row_id, "
				+ " user_tag_id, tag_name, "
				+ " channel_id, "
				+ " channel_name, "
				+ " lowest_since as miniov_since, "
				+ " iov_until as miniov_until, "
				+ " iov_since as maxiov_since, "
				+ " highest_until as maxiov_until, "
				+ " hole as iov_hole, "
				+ " next_since as hole_until,"
				+ " niovs "
				+ " from (select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name, iov_since, iov_until, next_since, hole, iov_sum, "
				+ " FIRST_VALUE(iov_since) IGNORE NULLS OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since) AS lowest_since, "
				+ " LAST_VALUE(iov_until) IGNORE NULLS OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since RANGE BETWEEN "
				+ "  UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS highest_until, "
				+ " COUNT(iov_sum) OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since) as niovs "
				+ " from ( select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name,iov_since, iov_until,  next_since, iov_number, hole, "
				+ " iov_number+LAG(iov_number,1,0) OVER (PARTITION BY channel_id ORDER BY iov_since) as iov_sum "
				// +" abs(iov_number+LAG(iov_number,1,0) OVER (order by channel_id, iov_since)) as iov_sum "
				+ " from ( select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name,iov_since, iov_until, next_since, "
				+ " (case when (next_since-iov_until)>0 then (next_since-iov_until) else 0 end) as hole, "
				+ " SUM(case when (next_since-iov_until)>0 then 1 else 0 end) OVER (PARTITION BY channel_id ORDER BY iov_since "
				+ " RANGE UNBOUNDED PRECEDING) as iov_number "
				+ " from ( select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name, iov_since, iov_until,  LEAD(iov_since) OVER (ORDER BY channel_id, iov_since) next_since "
				+ " from table(cool_select_pkg.f_get_iovs(:schema,:db,:node,:tag)) order by user_tag_id, channel_id asc )))) "
				+ " where iov_until=highest_until ", resultClass = IovType.class),
		@NamedNativeQuery(name = IovType.QUERY_FINDIOVSUMMARY_INRANGE, query = "select "
				+ " rownum as row_id, "
				+ " user_tag_id, tag_name, "
				+ " channel_id, "
				+ " channel_name, "
				+ " lowest_since as miniov_since, "
				+ " iov_until as miniov_until, "
				+ " iov_since as maxiov_since, "
				+ " highest_until as maxiov_until, "
				+ " hole as iov_hole, "
				+ " next_since as hole_until,"
				+ " niovs "
				+ " from (select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name, iov_since, iov_until, next_since, hole, iov_sum, "
				+ " FIRST_VALUE(iov_since) IGNORE NULLS OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since) AS lowest_since, "
				+ " LAST_VALUE(iov_until) IGNORE NULLS OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since RANGE BETWEEN "
				+ "  UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS highest_until, "
				+ " COUNT(iov_sum) OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since) as niovs "
				+ " from ( select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name,iov_since, iov_until,  next_since, iov_number, hole, "
				+ " iov_number+LAG(iov_number,1,0) OVER (PARTITION BY channel_id ORDER BY iov_since) as iov_sum "
				// +" abs(iov_number+LAG(iov_number,1,0) OVER (order by channel_id, iov_since)) as iov_sum "
				+ " from ( select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name,iov_since, iov_until, next_since, "
				+ " (case when (next_since-iov_until)>0 then (next_since-iov_until) else 0 end) as hole, "
				+ " SUM(case when (next_since-iov_until)>0 then 1 else 0 end) OVER (PARTITION BY channel_id ORDER BY iov_since "
				+ " RANGE UNBOUNDED PRECEDING) as iov_number "
				+ " from ( select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name, iov_since, iov_until,  LEAD(iov_since) OVER (ORDER BY channel_id, iov_since) next_since "
				+ " from table(cool_select_pkg.f_get_iovsrange(:schema,:db,:node,:tag,:since,:until)) order by channel_id asc )))) "
				+ " where iov_until=highest_until ", resultClass = IovType.class),
		@NamedNativeQuery(name = IovType.QUERY_FINDHOLES_INRANGE, query = "select   "
				+ " user_tag_id*10000000+channel_id as row_id, "
				+ " user_tag_id, tag_name, "
				+ "channel_id, "
				+ "max(channel_name) as channel_name, "
				+ "min(iov_since) as miniov_since, "
				+ "min(iov_until) as miniov_until, "
				+ "max(iov_since) as maxiov_since, "
				+ "max(iov_until) as maxiov_until, "
				+ "sum(hole) as iov_hole, "
				+ " 0 as hole_until,"
				+ "count(channel_id) as niovs "
				+ " from ( "
				+ "select "
				+ " user_tag_id, tag_name, "
				+ " channel_id, channel_name,iov_since, iov_until, next_since, (case when (next_since-iov_until)>0 then (next_since-iov_until) else 0 end) as hole "
				+ " from ("
				+ "select "
				+ " user_tag_id, tag_name, "
				+ "channel_id, channel_name, iov_since, iov_until,  LEAD(iov_since) OVER(ORDER BY channel_id, iov_since) as next_since "
				+ "from table(cool_select_pkg.f_get_iovsrange(:schema,:db,:node,:tag,:since,:until)) order by channel_id asc ) "
				+ ") where hole>0 group by user_tag_id, channel_id ", resultClass = IovType.class)

})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IovType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 368387938580604823L;

	@Id
	@Column(name = "ROW_ID", precision = 20, scale = 0)
	BigDecimal rowId;

	@Column(name = "USER_TAG_ID", precision = 10, scale = 0)
	Long userTagId;
	@Column(name = "TAG_NAME", length = 255)
	String tagName;
	@Column(name = "CHANNEL_ID", precision = 10, scale = 0)
	Long channelId;
	@Column(name = "CHANNEL_NAME", length = 255)
	String channelName;

	@Column(name = "NIOVS", precision = 10, scale = 0)
	Long niovs;

	@Column(name = "MINIOV_SINCE", precision = 20, scale = 0)
	BigDecimal miniovSince;
	@Column(name = "MINIOV_UNTIL", precision = 20, scale = 0)
	BigDecimal miniovUntil;
	@Column(name = "MAXIOV_SINCE", precision = 20, scale = 0)
	BigDecimal maxiovSince;
	@Column(name = "MAXIOV_UNTIL", precision = 20, scale = 0)
	BigDecimal maxiovUntil;

	@Column(name = "HOLE_UNTIL", precision = 20, scale = 0)
	BigDecimal holeUntil;

	@Column(name = "IOV_HOLE", precision = 20, scale = 0)
	BigDecimal iovHole;

	@Transient
	String iovBase;
	@Transient
	String coolIovMinSince;
	@Transient
	String coolIovMinUntil;
	@Transient
	String coolIovMaxSince;
	@Transient
	String coolIovMaxUntil;

	@CoolQuery(name = "cool.findiovsperchan", params = "schema;db;node;tag")
	public static final String QUERY_FINDIOVS = "cool.findiovsperchan";
	@CoolQuery(name = "cool.findholesperchan", params = "schema;db;node;tag")
	public static final String QUERY_FINDHOLES = "cool.findholesperchan";
	@CoolQuery(name = "cool.findiovsummaryperchan", params = "schema;db;node;tag")
	public static final String QUERY_FINDIOVSUMMARY = "cool.findiovsummaryperchan";
	@CoolQuery(name = "cool.findiovsummaryperchaninrange", params = "schema;db;node;tag;since;until")
	public static final String QUERY_FINDIOVSUMMARY_INRANGE = "cool.findiovsummaryperchaninrange";
	@CoolQuery(name = "cool.findholesperchaninrange", params = "schema;db;node;tag;since;until")
	public static final String QUERY_FINDHOLES_INRANGE = "cool.findholesperchaninrange";

	/**
	 * @return the niovs
	 */
	public final  Long getNiovs() {
		return niovs;
	}

	/**
	 * @param niovs
	 *            the niovs to set
	 */
	public final  void setNiovs(final Long niovs) {
		this.niovs = niovs;
	}

	/**
	 * @return the channelId
	 */
	public final  Long getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
	public final  void setChannelId(final Long channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the channelName
	 */
	public final  String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName
	 *            the channelName to set
	 */
	public final  void setChannelName(final String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the miniovSince
	 */
	public final  BigDecimal getMiniovSince() {
		return miniovSince;
	}

	/**
	 * @param miniovSince
	 *            the miniovSince to set
	 */
	public final  void setMiniovSince(final BigDecimal miniovSince) {
		this.miniovSince = miniovSince;
	}

	/**
	 * @return the miniovUntil
	 */
	public final  BigDecimal getMiniovUntil() {
		return miniovUntil;
	}

	/**
	 * @param miniovUntil
	 *            the miniovUntil to set
	 */
	public final  void setMiniovUntil(final BigDecimal miniovUntil) {
		this.miniovUntil = miniovUntil;
	}

	/**
	 * @return the maxiovSince
	 */
	public final  BigDecimal getMaxiovSince() {
		return maxiovSince;
	}

	/**
	 * @param maxiovSince
	 *            the maxiovSince to set
	 */
	public final  void setMaxiovSince(final BigDecimal maxiovSince) {
		this.maxiovSince = maxiovSince;
	}

	/**
	 * @return the maxiovUntil
	 */
	public final  BigDecimal getMaxiovUntil() {
		return maxiovUntil;
	}

	/**
	 * @param maxiovUntil
	 *            the maxiovUntil to set
	 */
	public final  void setMaxiovUntil(final BigDecimal maxiovUntil) {
		this.maxiovUntil = maxiovUntil;
	}

	/**
	 * @return the iovHole
	 */
	public final  BigDecimal getIovHole() {
		return iovHole;
	}

	/**
	 * @param iovHole
	 *            the iovHole to set
	 */
	public final  void setIovHole(final BigDecimal iovHole) {
		this.iovHole = iovHole;
	}

	/**
	 * @return the iovBase
	 */
	public final  String getIovBase() {
		return iovBase;
	}

	/**
	 * @param iovBase
	 *            the iovBase to set
	 */
	public final  void setIovBase(final String iovBase) {
		this.iovBase = iovBase;
	}

	/**
	 * @return the holeUntil
	 */
	public final  BigDecimal getHoleUntil() {
		return holeUntil;
	}

	/**
	 * @param holeUntil
	 *            the holeUntil to set
	 */
	public final  void setHoleUntil(final BigDecimal holeUntil) {
		this.holeUntil = holeUntil;
	}

	/**
	 * @return the userTagId
	 */
	public final  Long getUserTagId() {
		return userTagId;
	}

	/**
	 * @param userTagId
	 *            the userTagId to set
	 */
	public final  void setUserTagId(final Long userTagId) {
		this.userTagId = userTagId;
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
	 * @return
	 */
	public final  String getCoolHoleUntil() {
		String iovstr = "";
		if (iovBase.equals("run-lumi")) {
			final Long run = CoolIov.getRun(holeUntil.toBigInteger());
			// Long lumi = CoolIov.getLumi(miniovSince.toBigInteger());
			iovstr = run.toString();
		} else {
			if (holeUntil.intValue() == 0) {
				return "0";
			}
			final Long time = CoolIov.getTime(holeUntil.toBigInteger());
			if (time == CoolIov.COOL_MAX_DATE) {
				return new Long(CoolIov.COOL_MAX_DATE).toString();
			}
			final Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}

	/**
	 * @return
	 */
	public final  String getCoolIovMinSince() {
		String iovstr = "";
		if (iovBase.equals("run-lumi")) {
			final Long run = CoolIov.getRun(miniovSince.toBigInteger());
			// Long lumi = CoolIov.getLumi(miniovSince.toBigInteger());
			iovstr = run.toString();
		} else {
			if (miniovSince.intValue() == 0) {
				return "0";
			}
			final Long time = CoolIov.getTime(miniovSince.toBigInteger());
			if (time == CoolIov.COOL_MAX_DATE) {
				return new Long(CoolIov.COOL_MAX_DATE).toString();
			}
			final Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}

	/**
	 * @return
	 */
	public final  String getCoolIovMinUntil() {
		String iovstr = "";
		if (iovBase.equals("run-lumi")) {
			final Long run = CoolIov.getRun(miniovUntil.toBigInteger());
			// Long lumi = CoolIov.getLumi(maxiovUntil.toBigInteger());
			// iovstr = run + " : " + lumi;
			iovstr = run.toString();
		} else {
			if (miniovUntil.intValue() == 0) {
				return "0";
			}
			final Long time = CoolIov.getTime(miniovUntil.toBigInteger());
			if (time == CoolIov.COOL_MAX_DATE) {
				return new Long(CoolIov.COOL_MAX_DATE).toString();
			}
			final Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}

	/**
	 * @return
	 */
	public final  String getCoolIovMaxSince() {
		String iovstr = "";
		if (iovBase.equals("run-lumi")) {
			final Long run = CoolIov.getRun(maxiovSince.toBigInteger());
			// Long lumi = CoolIov.getLumi(maxiovSince.toBigInteger());
			if (run == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			iovstr = run.toString();
		} else {
			if (maxiovSince.intValue() == 0) {
				return "0";
			}
			final Long time = CoolIov.getTime(maxiovSince.toBigInteger());
			if (time == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			// return new Long(CoolIov.COOL_MAX_DATE).toString();
			final Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}

	/**
	 * @return
	 */
	public final String getCoolIovMaxUntil() {
		String iovstr = "";
		if (iovBase.equals("run-lumi")) {
			final Long run = CoolIov.getRun(maxiovUntil.toBigInteger());
			// Long lumi = CoolIov.getLumi(maxiovUntil.toBigInteger());
			// iovstr = run + " : " + lumi;
			if (run == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}

			iovstr = run.toString();
		} else {
			if (maxiovUntil.intValue() == 0) {
				return "0";
			}
			final Long time = CoolIov.getTime(maxiovUntil.toBigInteger());
			if (time == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			// return new Long(CoolIov.COOL_MAX_DATE).toString();
			final Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}

}
