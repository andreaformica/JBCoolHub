/**
 * 
 */
package atlas.cool.dao.remote;

import atlas.cool.exceptions.CoolIOException;

/**
 * @author formica
 *
 */
public interface CondToolsDAORemote {

	void checkGlobalTagForSchemaDB(String gtag, String schema, String db, Boolean ignoreExistingSchemas) throws CoolIOException;
	
	void insertCoolIovRanges(String schema, String db, String node, String tag);
}
