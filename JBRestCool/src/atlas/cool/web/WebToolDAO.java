/**
 * 
 */
package atlas.cool.web;

import java.util.Collection;

import atlas.coma.exceptions.ComaQueryException;
import atlas.cool.rest.model.CoolIovSummary;

/**
 * @author formica
 *
 */
public interface WebToolDAO {

	public String checkHoles(Collection<CoolIovSummary> iovsummaryColl)
			throws ComaQueryException;
}
