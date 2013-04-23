/**
 * 
 */
package atlas.coma.dao;

import java.util.Date;
import java.util.List;

import atlas.coma.model.PeriodSummary;
import atlas.coma.model.RunSummary;
import atlas.cool.dao.CoolIOException;

/**
 * @author formica
 * 
 */
public interface ComaRunDAO {

	List<RunSummary> getRunSummaryRangeByRunNumber(Integer runstart, Integer runend) throws CoolIOException;
	List<RunSummary> getRunSummaryRangeByTime(final Date since, final Date until) throws CoolIOException;

	RunSummary getRunInfo(Integer runnumber) throws CoolIOException;
	
	List<PeriodSummary> getPeriods() throws CoolIOException;
	
	List<PeriodSummary> getSubPeriodsByProject(String project) throws CoolIOException;
}
