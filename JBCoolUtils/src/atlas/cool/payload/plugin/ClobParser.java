/**
 * 
 */
package atlas.cool.payload.plugin;


/**
 * @author formica
 *
 */
public interface ClobParser {

	/**
	 * Utility method to provide a formatted output from a payload column which is structured 
	 * as a CLOB in COOL.
	 * 
	 * @param payloadColumn
	 * @return
	 */
	Object  parseClob(String payloadColumn, String content);
	
	/**
	 * @param payloadColumn
	 * @return
	 */
	String  header(String payloadColumn);
}
