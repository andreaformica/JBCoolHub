/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
	Long since;
	Long until;
	Long niovs;
	Boolean ishole;

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
	 */
	public IovRange(Long since, Long until, Long niovs, Boolean ishole) {
		super();
		this.since = since;
		this.until = until;
		this.niovs = niovs;
		this.ishole = ishole;
	}

	/**
	 * @return the since
	 */
	public Long getSince() {
		return since;
	}

	/**
	 * @param since
	 *            the since to set
	 */
	public void setSince(Long since) {
		this.since = since;
	}

	/**
	 * @return the until
	 */
	public Long getUntil() {
		return until;
	}

	/**
	 * @param until
	 *            the until to set
	 */
	public void setUntil(Long until) {
		this.until = until;
	}

	/**
	 * @return the niovs
	 */
	public Long getNiovs() {
		return niovs;
	}

	/**
	 * @param niovs
	 *            the niovs to set
	 */
	public void setNiovs(Long niovs) {
		this.niovs = niovs;
	}

	/**
	 * @return the ishole
	 */
	public Boolean getIshole() {
		return ishole;
	}

	/**
	 * @param ishole
	 *            the ishole to set
	 */
	public void setIshole(Boolean ishole) {
		this.ishole = ishole;
	}

}
