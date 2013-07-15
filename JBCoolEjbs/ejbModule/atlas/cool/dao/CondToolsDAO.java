/**
 * 
 */
package atlas.cool.dao;

import java.util.Collection;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.IovRange;
import atlas.cool.summary.model.CoolIovSummary;

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
	 * Synchro a collection of iovranges with the table cool_iov_range.
	 * @param iovsumm
	 * @param iovrangelist
	 * @throws CoolIOException
	 */
	void synchroIovRanges(CoolIovSummary iovsumm, Collection<IovRange> iovrangelist) throws CoolIOException;


}
