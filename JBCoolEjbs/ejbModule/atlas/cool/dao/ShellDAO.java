/**
 * 
 */
package atlas.cool.dao;

import atlas.cool.exceptions.CoolIOException;

/**
 * @author formica
 *
 */
public interface ShellDAO {

	String executeCommand(String command) throws CoolIOException;
	
}
