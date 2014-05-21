/**
 * 
 */
package atlas.coma.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import atlas.cool.annotations.CoolQuery;

/**
 * @author formica
 * 
 */
/**
 * @author formica
 * 
 */
@Entity
@Table(name = "ATLAS_COOL_GLOBAL.NEMOP_RUN")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
		@NamedQuery(name = NemoRun.QUERY_FINDRUNLIST_INTIMERANGE, query = "FROM atlas.coma.model.NemoRun nemor "
				+ "WHERE nemor.sor >= :since AND nemor.sor <= :until order by nemor.run asc "),
		@NamedQuery(name = NemoRun.QUERY_FINDRUNLIST_INRANGE, query = "FROM atlas.coma.model.NemoRun nemor "
				+ "WHERE nemor.run <= :rmax AND nemor.run >= :rmin order by nemor.run asc ") 
})
@NamedNativeQueries({ 
	    @NamedNativeQuery(name = NemoRun.QUERY_FINDRUNLIST_ATTIME, query = "select run, active, runstart, runend, nlb "
		+ " FROM ATLAS_COOL_GLOBAL.NEMOP_RUN WHERE (runstart <= :since AND runend > :since) "
		+ " or (runend = COALESCE((SELECT MAX(runend) FROM ATLAS_COOL_GLOBAL.NEMOP_RUN WHERE runend < :since), :since)) "
		+ " or (runstart = COALESCE(( SELECT MIN(runstart) FROM ATLAS_COOL_GLOBAL.NEMOP_RUN WHERE runstart > :since), :since)) "
		+ " order by runstart asc ", resultClass = NemoRun.class), 
	    @NamedNativeQuery(name = NemoRun.QUERY_FINDTDAQRUNLIST_INTIMERANGE, query = "select "
	    + " run_number as run, cleanstop as active, runstart, runend, 99999 as nlb "
		+ " FROM TABLE(cond_tools_pkg.f_getrun_info(:dbname)) "
	    + " WHERE (runstart >= :since AND runstart <= :until) order by run_number asc ", resultClass = NemoRun.class),
	    @NamedNativeQuery(name = NemoRun.QUERY_FINDTDAQRUNLIST_INRANGE, query = "select "
	    + " run_number as run, cleanstop as active, runstart, runend, 99999 as nlb "
		+ " FROM TABLE(cond_tools_pkg.f_getrun_info(:dbname)) "
	    + " WHERE (run_number <= :rmax AND run_number >= :rmin) order by run_number asc ", resultClass = NemoRun.class),
	    @NamedNativeQuery(name = NemoRun.QUERY_FINDTDAQRUNLIST_ATTIME, query = "select "
	    + " run_number as run, cleanstop as active, runstart, runend, 99999 as nlb "
		+ " FROM TABLE(cond_tools_pkg.f_getrun_info(:dbname)) "
	    + " WHERE (runstart <= :since AND runend > :since) "
		+ " or (runend = COALESCE((SELECT MAX(runend) FROM TABLE(cond_tools_pkg.f_getrun_info(:dbname)) WHERE runend < :since), :since)) "
		+ " or (runstart = COALESCE(( SELECT MIN(runstart) FROM TABLE(cond_tools_pkg.f_getrun_info(:dbname)) WHERE runstart > :since), :since)) "
		+ " order by run_number asc ", resultClass = NemoRun.class),
	    
})
public class NemoRun implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8924550986038845802L;
	/**
	 * Run number.
	 */
	@Id
	@Column(name = "RUN", precision = 20)
	private Integer run;
	/**
	 * Active run.
	 */
	@Column(name = "ACTIVE", precision = 5)
	private Integer active;
	/**
	 * Start of run in seconds.
	 */
	@Column(name = "RUNSTART", precision = 20)
	private Long sor;
	/**
	 * End of run in seconds.
	 */
	@Column(name = "RUNEND", precision = 20)
	private Long eor;
	/**
	 * Number of lumi blocks.
	 */
	@Column(name = "NLB", precision = 5)
	private Integer nlb;

	/**
	 * 
	 */
	@CoolQuery(name = "nemo.findrunsintime", params = "since;until")
	public static final String QUERY_FINDRUNLIST_INTIMERANGE = "nemo.findrunsintime";
	/**
	 * 
	 */
	@CoolQuery(name = "nemo.findtdaqrunsintime", params = "since;until;dbname")
	public static final String QUERY_FINDTDAQRUNLIST_INTIMERANGE = "nemo.findtdaqrunsintime";
	/**
	 * 
	 */
	@CoolQuery(name = "nemo.findrunsattime", params = "since")
	public static final String QUERY_FINDRUNLIST_ATTIME = "nemo.findrunsattime";
	/**
	 * 
	 */
	@CoolQuery(name = "nemo.findtdaqrunsattime", params = "since;dbname")
	public static final String QUERY_FINDTDAQRUNLIST_ATTIME = "nemo.findtdaqrunsattime";
	/**
	 * 
	 */
	@CoolQuery(name = "nemo.findrunsinrange", params = "rmin;rmax")
	public static final String QUERY_FINDRUNLIST_INRANGE = "nemo.findrunsinrange";
	/**
	 * 
	 */
	@CoolQuery(name = "nemo.findtdaqrunsinrange", params = "rmin;rmax;dbname")
	public static final String QUERY_FINDTDAQRUNLIST_INRANGE = "nemo.findtdaqrunsinrange";

	/**
	 * 
	 */
	public NemoRun() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param run
	 * @param active
	 * @param sor
	 * @param eor
	 * @param nlb
	 */
	public NemoRun(final Integer run, final Integer active, final Long sor,
			final Long eor, final Integer nlb) {
		this.run = run;
		this.active = active;
		this.sor = sor;
		this.eor = eor;
		this.nlb = nlb;
	}

	/**
	 * @param run
	 * @param timestamp
	 */
	public NemoRun(final Integer run, final Long timestamp) {
		this.run = run;
		this.sor = timestamp;
	}

	/**
	 * @return the active
	 */
	@XmlElement
	public final Integer getActive() {
		return this.active;
	}

	/**
	 * @return the eor
	 */
	@XmlElement
	public final Long getEor() {
		return this.eor;
	}

	/**
	 * @return the nlb
	 */
	@XmlElement
	public final Integer getNlb() {
		return this.nlb;
	}

	/**
	 * @return the run
	 */
	@XmlElement
	public Integer getRun() {
		return this.run;
	}

	/**
	 * @return the string representation of eor date.
	 */
	@XmlElement
	public String getRunEor() {
		return new Date(this.eor * 1000L).toString();
	}

	/**
	 * @return the string representation of sor date.
	 */
	@XmlTransient
	public final Date getRunSorDate() {
		return new Date(this.sor * 1000L);
	}

	/**
	 * @return the string representation of eor date.
	 */
	@XmlTransient
	public Date getRunEorDate() {
		return new Date(this.eor * 1000L);
	}

	/**
	 * @return the string representation of sor date.
	 */
	@XmlElement
	public final String getRunSor() {
		return new Date(this.sor * 1000L).toString();
	}

	/**
	 * @return the timestamp
	 */
	@XmlElement
	public final Long getSor() {
		return this.sor;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public final void setActive(final Integer active) {
		this.active = active;
	}

	/**
	 * @param eor
	 *            the eor to set
	 */
	public final void setEor(final Long eor) {
		this.eor = eor;
	}

	/**
	 * @param nlb
	 *            the nlb to set
	 */
	public final void setNlb(final Integer nlb) {
		this.nlb = nlb;
	}

	/**
	 * @param run
	 *            the run to set.
	 */
	public final void setRun(final Integer run) {
		this.run = run;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set.
	 */
	public final void setSor(final Long timestamp) {
		this.sor = timestamp;
	}
}
