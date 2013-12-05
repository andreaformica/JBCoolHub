/**
 * 
 */
package atlas.cool.dao;

import java.util.Date;
import java.util.List;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.summary.model.CondNodeStats;
import atlas.cool.summary.model.CoolCoverage;

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

	/**
	 * Insert logging information on the COOL_COVERAGE table.
	 * @param gtag
	 * @param db
	 * @param instime
	 * @param nupdschemas
	 * @param nupdfolders
	 * @param comment
	 * @throws CoolIOException
	 */
	void insertCoverageInfo(String gtag, String db, Date instime, Integer nupdschemas, Integer nupdfolders, String comment) throws CoolIOException;

    /**
     * @param gtag
     * @param dbname
     * @param nupdschemas
     * @param nupdfolders
     * @param comment
     * @throws CoolIOException
     */
    void updateCoverageInfo(String gtag, String db, Integer nupdschemas, Integer nupdfolders, String comment) throws CoolIOException;


    /**
     * Find coverage for the given global tag.
     * @param gtag
     * @return
     * @throws CoolIOException
     */
    CoolCoverage findGlobalTagCoverage(String gtag) throws CoolIOException;
}
