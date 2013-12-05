/**
 * 
 */
package atlas.coma.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.ComaCbSchemas;
import atlas.coma.model.ComaCbamiGtags;
import atlas.coma.model.CrViewRuninfo;

/**
 * @author formica
 * 
 */
public interface ComaCbDAO {

	/**
	 * @param name
	 * @return
	 * @throws ComaQueryException
	 */
	List<ComaCbSchemas> findSchemas(String name)
			throws ComaQueryException;

	/**
	 * @param gtagname
	 * @return
	 * @throws ComaQueryException
	 */
	List<ComaCbamiGtags> findGtagUsageInAmi(String gtagname) throws ComaQueryException;

	/**
	 * @param state
	 * @return
	 * @throws ComaQueryException
	 */
	List<ComaCbGtagStates> findGtagState(String state) throws ComaQueryException;

	/**
	 * @param state
	 * @return
	 * @throws ComaQueryException
	 */
	List<ComaCbGtagStates> findGtagStateAtTime(String state, Date time) throws ComaQueryException;

	/**
	 * @param runstart
	 * @param runend
	 * @return
	 * @throws ComaQueryException
	 */
	List<CrViewRuninfo> findRunsInRange(BigDecimal runstart, BigDecimal runend)
			throws ComaQueryException;

	/**
	 * @param runstart
	 * @param runend
	 * @param rtype
	 * @param pproj
	 * @return
	 * @throws ComaQueryException
	 */
	List<CrViewRuninfo> findRunsInRange(BigDecimal runstart, BigDecimal runend, String rtype, String pproj)
			throws ComaQueryException;

	/**
	 * @param since
	 * @param until
	 * @return
	 * @throws ComaQueryException
	 */
	List<CrViewRuninfo> findRunsInRange(Timestamp since, Timestamp until)
			throws ComaQueryException;

	/**
	 * @param runstart
	 * @param runend
	 * @param rtype
	 * @param pproj
	 * @return
	 * @throws ComaQueryException
	 */
	List<CrViewRuninfo> findRunsInRange(Timestamp since, Timestamp until, String rtype, String pproj)
			throws ComaQueryException;

	/**
	 * @param run
	 * @return
	 * @throws ComaQueryException
	 */
	CrViewRuninfo findRun(BigDecimal run) throws ComaQueryException;
}
