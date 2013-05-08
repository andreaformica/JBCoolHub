/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.Map;

import atlas.cool.rest.model.CoolIovSummary;


/**
 * @author formica
 *
 */
public interface CoolUtilsDAO {

	public Map<Long, CoolIovSummary> computeIovSummaryMap(String schema,
			String db, String node, String tag, String iovbase) throws CoolIOException;
	public Map<Long, CoolIovSummary> computeIovSummaryRangeMap(String schema,
			String db, String node, String tag, String iovbase, BigDecimal since, BigDecimal until) throws CoolIOException;
}
