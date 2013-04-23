/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;
import java.sql.Timestamp;
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
import atlas.cool.rest.utils.TimestampStringFormatter;

/**
 * @author formica
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

	@Id
	@Column(name = "GTAG_NAME", length = 255)
	String gtagName;
	@Column(name = "GTAG_DESCRIPTION", length = 255)
	String gtagDescription;
	@Column(name = "GTAG_LOCK_STATUS")
	Integer gtagLockStatus;

	@Column(name = "N_SCHEMAS")
	Integer nschemas;

	@Column(name = "SYS_INSTIME")
	Timestamp sysInstime;

	@CoolQuery(name = "cool.findgtags", params = "schema;dbname;gtag")
	public static final String QUERY_FINDGTAGS = "cool.findgtags";
	@CoolQuery(name = "coma.findgtags", params = "schema;dbname;gtag")
	public static final String QUERY_COMA_FINDGTAGS = "coma.findgtags";

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
	 * @return the nschemas
	 */
	public Integer getNschemas() {
		return nschemas;
	}

	/**
	 * @param nSchemas
	 *            the nSchemas to set
	 */
	public void setNschemas(Integer nschemas) {
		this.nschemas = nschemas;
	}

	/**
	 * @return the sysInstime
	 */
	public Timestamp getSysInstime() {
		return sysInstime;
	}

	/**
	 * @param sysInstime
	 *            the sysInstime to set
	 */
	public void setSysInstime(Timestamp sysInstime) {
		this.sysInstime = sysInstime;
	}

	public String getSysInstimeStr() {
		if (sysInstime == null)
			return "";
		String ret = TimestampStringFormatter.format("yyyy:MM:dd hh:mm:ss", sysInstime);
//		return sysInstime.toString();
		return ret;
	}

}
