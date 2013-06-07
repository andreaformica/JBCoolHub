/**
 * 
 */
package atlas.coma.dao;

import java.util.Date;
import java.util.List;

import atlas.coma.model.PeriodSummary;
import atlas.coma.model.RunSummary;
import atlas.cool.exceptions.CoolIOException;

/**
 * @author formica
 * 
 */
public interface ComaRunDAO {

	/**
	 * @param runstart
	 * @param runend
	 * @return
	 * @throws CoolIOException
	 */
	List<RunSummary> getRunSummaryRangeByRunNumber(Integer runstart, Integer runend)
			throws CoolIOException;
	/**
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	List<RunSummary> getRunSummaryRangeByTime(final Date since, final Date until) throws CoolIOException;

	/**
	 * @param runnumber
	 * @return
	 * @throws CoolIOException
	 */
	RunSummary getRunInfo(Integer runnumber) throws CoolIOException;
	
	/**
	 * @return
	 * @throws CoolIOException
	 */
	List<PeriodSummary> getPeriods() throws CoolIOException;
	
	/**
	 * @param project
	 * @return
	 * @throws CoolIOException
	 */
	List<PeriodSummary> getSubPeriodsByProject(String project) throws CoolIOException;
}
