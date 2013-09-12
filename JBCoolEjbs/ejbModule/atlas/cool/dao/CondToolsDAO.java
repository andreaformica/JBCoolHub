/**
 * 
 */
package atlas.cool.dao;

import java.util.List;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.summary.model.CondNodeStats;

/**
 * @author formica
 *
 */
public interface CondToolsDAO {

	
	/**
	 * Using the global tag name, checks associations and synchronize the 
	 * cool_iov_summary db table.
	 * @param globaltag
	 * 	The global tag name.
	 * @param db
	 * 	The db instance name (COMP200, OFLP200, ..).
	 * @throws CoolIOException
	 */
	void synchroIovSummary(String globaltag, String db) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 * @throws CoolIOException
	 */
	List<CondNodeStats> getNodeStatsForSchemaDb(String schema, String db, String gtag) throws CoolIOException;


	/**
	 * @param gtag
	 * @param schema
	 * @param db
	 * @param ignoreExistingSchemas
	 * @throws CoolIOException
	 */
	void updateGlobalTagForSchemaDB(String gtag, String schema, String db, Boolean ignoreExistingSchemas) throws CoolIOException;

}
