/**
 * 
 */
package atlas.coma.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbamiGtags;
import atlas.coma.model.CrViewRuninfo;

/**
 * @author formica
 *
 */
public interface ComaCbDAO {

	List<ComaCbamiGtags> findGtagUsageInAmi(String gtagname) throws ComaQueryException;
	List<CrViewRuninfo> findRunsInRange(BigDecimal runstart, BigDecimal runend) throws ComaQueryException;
	List<CrViewRuninfo> findRunsInRange(Timestamp since, Timestamp until) throws ComaQueryException;
	CrViewRuninfo findRun(BigDecimal run) throws ComaQueryException;
}
