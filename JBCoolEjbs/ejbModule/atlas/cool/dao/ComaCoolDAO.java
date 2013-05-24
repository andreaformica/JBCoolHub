/**
 * 
 */
package atlas.cool.dao;

import java.util.List;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.CmpCoolComaGlobalTagType;
import atlas.cool.rest.model.CmpCoolComaNodeType;
import atlas.cool.rest.model.CmpCoolComaTagType;

/**
 * @author formica
 *
 */
public interface ComaCoolDAO {

	public List<CmpCoolComaNodeType> retrieveNodesStatus(String schema, String db) throws CoolIOException;
	public List<CmpCoolComaTagType> retrieveTagsStatus(String schema, String db) throws CoolIOException;
	public List<CmpCoolComaGlobalTagType> retrieveGlobalTagsStatus(String schema, String db, String gtag) throws CoolIOException;
}
