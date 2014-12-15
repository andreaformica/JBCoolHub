/**
 * 
 */
package atlas.cool.jsf.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author formica
 *
 */
public class DbList {

	private static List<String> dbList = null;

	public static List<String> createDbList() {
		//TODO: load instances from COMA
		if (dbList == null) {
			System.out.println("Create list of instances by hand...should be replaced by COMA loading !");
			dbList = new ArrayList<String>();
			dbList.add("COMP200");
			dbList.add("OFLP200");
			dbList.add("MONP200");
			dbList.add("CONDBR2");
		}
		return dbList;
	}
}
