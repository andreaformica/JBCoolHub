/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import atlas.cool.meta.CoolIov;

/**
 * @author formica
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IovRange implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5570577920594243136L;
	/**
	 * 
	 */
	private Long since;
	/**
	 * 
	 */
	private Long until;
	/**
	 * 
	 */
	private Long niovs;
	/**
	 * 
	 */
	private Boolean ishole;
	/**
	 * 
	 */
	private String sinceCoolStr;
	/**
	 * 
	 */
	private String untilCoolStr;

	/**
	 * 
	 */
	@XmlTransient
	private String iovbase;

	/**
	 * 
	 */
	public IovRange() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param since
	 * @param until
	 * @param niovs
	 * @param ishole
	 * @param iovbase
	 */
	public IovRange(final Long since, final Long until, final Long niovs,
			final Boolean ishole, final String iovbase) {
		super();
		this.since = since;
		this.until = until;
		this.niovs = niovs;
		this.ishole = ishole;
		this.iovbase = iovbase;

		sinceCoolStr = CoolIov.getCoolTimeRunLumiString(since, iovbase);
		untilCoolStr = CoolIov.getCoolTimeRunLumiString(until, iovbase);
	}

	/**
	 * @return the since
	 */
	public final  Long getSince() {
		return since;
	}

	/**
	 * @param since
	 *            the since to set
	 */
	public final  void setSince(final Long since) {
		this.since = since;
	}

	/**
	 * @return the until
	 */
	public final  Long getUntil() {
		return until;
	}

	/**
	 * @param until
	 *            the until to set
	 */
	public final  void setUntil(final Long until) {
		this.until = until;
	}

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
	 * @return the ishole
	 */
	public final  Boolean getIshole() {
		return ishole;
	}

	/**
	 * @param ishole
	 *            the ishole to set
	 */
	public final  void setIshole(final Boolean ishole) {
		this.ishole = ishole;
	}

	/**
	 * @return the sinceCoolStr
	 */
	public final  String getSinceCoolStr() {
		return sinceCoolStr;
	}

	/**
	 * @return the untilCoolStr
	 */
	public final  String getUntilCoolStr() {
		return untilCoolStr;
	}

	/**
	 * @param iovbase
	 *            the iovbase to set
	 */
	public final  void setIovbase(final String iovbase) {
		this.iovbase = iovbase;
	}

	/**
	 * @param sinceCoolStr
	 *            the sinceCoolStr to set
	 */
	public final  void setSinceCoolStr(final String sinceCoolStr) {
		this.sinceCoolStr = sinceCoolStr;
	}

	/**
	 * @param untilCoolStr
	 *            the untilCoolStr to set
	 */
	public final void setUntilCoolStr(final String untilCoolStr) {
		this.untilCoolStr = untilCoolStr;
	}

	public final  void addNIovs(final Long niovs) {
		this.niovs += niovs;
	}
}
