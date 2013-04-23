/**
 * 
 */
package atlas.cool.jsf.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.dao.ComaRunDAO;
import atlas.coma.model.RunSummary;
import atlas.cool.dao.CoolIOException;
import atlas.cool.meta.CoolIov;

@Named("calendarparams")
@SessionScoped
/**
 * @author formica
 *
 */
public class CalendarBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8491789905226077064L;

	@Inject
	private Logger log;

	@Inject
	private FacesContext facesContext;

	@Inject
	private ComaRunDAO comarundao;

	private Date dateSince;
	private Date dateUntil;

	private RunSummary runSince;
	private RunSummary runUntil;

	private Long lumiSince = 0L;
	private Long lumiUntil = 0L;

	private Long runLow = 0L;
	private Long runHigh = 999999L;

	private List<RunSummary> runList;
	private List<RunSummary> runListFiltered;

	/**
	 * 
	 */
	public CalendarBean() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the dateSince
	 */
	public Date getDateSince() {
		return dateSince;
	}

	/**
	 * @param dateSince
	 *            the dateSince to set
	 */
	public void setDateSince(Date dateSince) {
		this.dateSince = dateSince;
	}

	/**
	 * @return the dateUntil
	 */
	public Date getDateUntil() {
		return dateUntil;
	}

	/**
	 * @param dateUntil
	 *            the dateUntil to set
	 */
	public void setDateUntil(Date dateUntil) {
		this.dateUntil = dateUntil;
	}

	/**
	 * @return the runSince
	 */
	public RunSummary getRunSince() {
		return runSince;
	}

	/**
	 * @param runSince
	 *            the runSince to set
	 */
	public void setRunSince(final RunSummary runSince) {
		log.info("Setting run since " + runSince);
		if (runSince != null) {
			this.runSince = runSince;
			this.lumiSince = runSince.getSlb();
		}
	}

	/**
	 * @return the runUntil
	 */
	public RunSummary getRunUntil() {
		return runUntil;
	}

	/**
	 * @param runUntil
	 *            the runUntil to set
	 */
	public void setRunUntil(final RunSummary runUntil) {
		log.info("Setting run until " + runUntil);
		if (runUntil != null) {
			this.runUntil = runUntil;
			this.lumiUntil = runUntil.getElb();
		}
	}

	/**
	 * @return the lumiSince
	 */
	public Long getLumiSince() {
		return lumiSince;
	}

	/**
	 * @param lumiSince
	 *            the lumiSince to set
	 */
	public void setLumiSince(Long lumiSince) {
		this.lumiSince = lumiSince;
	}

	/**
	 * @return the lumiUntil
	 */
	public Long getLumiUntil() {
		return lumiUntil;
	}

	/**
	 * @param lumiUntil
	 *            the lumiUntil to set
	 */
	public void setLumiUntil(Long lumiUntil) {
		this.lumiUntil = lumiUntil;
	}

	/**
	 * @return the runLow
	 */
	public Long getRunLow() {
		return runLow;
	}

	/**
	 * @param runLow the runLow to set
	 */
	public void setRunLow(Long runLow) {
		this.runLow = runLow;
	}

	/**
	 * @return the runHigh
	 */
	public Long getRunHigh() {
		return runHigh;
	}

	/**
	 * @param runHigh the runHigh to set
	 */
	public void setRunHigh(Long runHigh) {
		this.runHigh = runHigh;
	}

	public BigDecimal getCoolTimeSince() {
		BigDecimal time = new BigDecimal(dateSince.getTime()
				* CoolIov.TO_NANOSECONDS);
		return time;
	}

	public BigDecimal getCoolTimeUntil() {
		BigDecimal time = new BigDecimal(dateUntil.getTime()
				* CoolIov.TO_NANOSECONDS);
		return time;
	}

	/**
	 * @return the runSince + lumiSince
	 */
	public BigDecimal getCoolRunSince() {
		Long start = ((runSince.getRunNumber().longValue()) << 32) + lumiSince;
		log.info("Getting cool run since for " + runSince +" and lumi "+ lumiSince + " : " + start);
		return new BigDecimal(start);
	}

	/**
	 * @return the runUntil + lumiUntil
	 */
	public BigDecimal getCoolRunUntil() {
		Long start = ((runUntil.getRunNumber().longValue()) << 32) + lumiUntil;
		log.info("Getting cool run since for " + runUntil +" and lumi "+ lumiUntil + " : " + start);
		return new BigDecimal(start);
	}

	/**
	 * @return the runList
	 */
	public List<RunSummary> getRunList() {
		return runList;
	}

	/**
	 * @param runList
	 *            the runList to set
	 */
	public void setRunList(List<RunSummary> runList) {
		this.runList = runList;
	}

	/**
	 * @return the runListFiltered
	 */
	public List<RunSummary> getRunListFiltered() {
		return runListFiltered;
	}

	/**
	 * @param runListFiltered
	 *            the runListFiltered to set
	 */
	public void setRunListFiltered(List<RunSummary> runListFiltered) {
		this.runListFiltered = runListFiltered;
	}

	public void loadRuns() {
		log.info("Searching runs using since " + dateSince + " and until "
				+ dateUntil);
		try {
			runList = comarundao.getRunSummaryRangeByTime(dateSince, dateUntil);
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadRunsByRunNumber() {
		log.info("Searching runs using since " + runLow + " and until "
				+ runHigh);
		try {
			runList = comarundao.getRunSummaryRangeByRunNumber(runLow.intValue(), runHigh.intValue());
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
