/**
 * 
 */
package atlas.coma.dao;

import java.util.List;

import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbamiGtags;

/**
 * @author formica
 *
 */
public interface ComaCbDAO {

	List<ComaCbamiGtags> findGtagUsageInAmi(String gtagname) throws ComaQueryException;
}
