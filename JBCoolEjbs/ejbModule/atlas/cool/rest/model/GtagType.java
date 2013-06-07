/**
 * 
 */
package atlas.cool.rest.model;

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

import atlas.cool.annotations.CoolQuery;
import atlas.cool.rest.utils.TimestampStringFormatter;

/**
 * <p>
 * This POJO represents the COOL Global Tag. Cool global tags are defined in the main
 * _TAGS table of a COOL schema. The purpose of this POJO is to gather information on the
 * global tag in order to see how many schemas are using it.
 * </p>
 * <p>
 * The Queries defined for this POJO are:<br>
 * <b>QUERY_FINDTAGS [cool_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of matching
 * global tags; it uses internally the function cool_select_pkg.f_GetAll_GlobalTags(.....)
 * </p>
 * <p>
 * <b>QUERY_COMA_FINDTAGS [coma_select_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, GTAG and retrieves a list of matching
 * global tags; it uses internally the function
 * coma_select_pkg.f_GetAll_GlobalTags(.....). The difference respect to previous query is
 * that the source is not COOL, but COMA.
 * </p>
 * 
 * @author formica
 * @since 2013/04/01
 * 
 */
@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = GtagType.QUERY_FINDGTAGS, query = "select   "
				+ " tag_name as gtag_name, "
				+ " max(tag_description) as gtag_description, "
				+ " max(tag_lock_status) as gtag_lock_status, "
				+ " count(tag_name) as n_schemas, "
				// + " max(sys_instime) as sys_instime "
				+ " TO_TIMESTAMP(substr(min(SYS_INSTIME),0,18),'DD-MON-RR HH24.MI.SS') as sys_instime "
				+ " from table(cool_select_pkg.f_GetAll_GlobalTags(:schema,:dbname,:gtag)) where node_id=0 "
				+ " group by tag_name " + " order by sys_instime desc ", resultClass = GtagType.class),
		@NamedNativeQuery(name = GtagType.QUERY_COMA_FINDGTAGS, query = "select   "
				+ " tag_name as gtag_name, "
				+ " max(tag_description) as gtag_description, "
				+ " max(tag_lock_status) as gtag_lock_status, "
				+ " count(tag_name) as n_schemas, "
				+ " min(sys_instime) as sys_instime "
				+ " from table(coma_select_pkg.f_GetAll_GlobalTags(:schema, :dbname,:gtag)) "
				+ " group by tag_name " + " order by sys_instime desc ", resultClass = GtagType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GtagType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2990371156354454615L;

	/**
	 * 
	 */
	@Id
	@Column(name = "GTAG_NAME", length = 255)
	private String gtagName;
	/**
	 * 
	 */
	@Column(name = "GTAG_DESCRIPTION", length = 255)
	private String gtagDescription;
	/**
	 * 
	 */
	@Column(name = "GTAG_LOCK_STATUS")
	private Integer gtagLockStatus;

	/**
	 * 
	 */
	@Column(name = "N_SCHEMAS")
	private Integer nschemas;

	/**
	 * 
	 */
	@Column(name = "SYS_INSTIME")
	private Timestamp sysInstime;

	@CoolQuery(name = "cool.findgtags", params = "schema;dbname;gtag")
	public static final String QUERY_FINDGTAGS = "cool.findgtags";
	@CoolQuery(name = "coma.findgtags", params = "schema;dbname;gtag")
	public static final String QUERY_COMA_FINDGTAGS = "coma.findgtags";

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
	 * @return the nschemas
	 */
	public final Integer getNschemas() {
		return nschemas;
	}

	/**
	 * @param nschemas
	 *            the nschemas to set
	 */
	public final void setNschemas(final Integer nschemas) {
		this.nschemas = nschemas;
	}

	/**
	 * @return the sysInstime
	 */
	public final Timestamp getSysInstime() {
		return sysInstime;
	}

	/**
	 * @param sysInstime
	 *            the sysInstime to set
	 */
	public final void setSysInstime(final Timestamp sysInstime) {
		this.sysInstime = sysInstime;
	}

	/**
	 * @return
	 */
	public final String getSysInstimeStr() {
		if (sysInstime == null) {
			return "";
		}
		final String ret = TimestampStringFormatter.format("yyyy:MM:dd hh:mm:ss",
				sysInstime);
		// return sysInstime.toString();
		return ret;
	}

}
