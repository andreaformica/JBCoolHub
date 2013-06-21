/**
 * 
 */
package atlas.cool.rest.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author formica
 *
 */
public class UrlLinkUtils {

	
	
	/**
	 * 
	 */
	public UrlLinkUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param schema
	 * @param db
	 * @param tag
	 * @param command
	 * @return
	 */
	public static String createLink(final String schema, final String db, final String node, final String tag,
			final String command) {
		String urlbase = null;
		String commandurl = null;
		try {
			urlbase = "http://" + InetAddress.getLocalHost().getHostName()
					+ ":8080/JBRestCool/rest";
			commandurl = "plsqlcool/" + schema + "/" + db + node + "/fld/" + tag + "/tag/" + command;
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlbase + "/" + commandurl;
	}

}
