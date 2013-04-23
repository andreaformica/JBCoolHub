/**
 * 
 */
package atlas.cool.rest.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author formica
 *
 */
public class SQLiteConnectionMgr {

	public static Connection getConnection(String file) {
	
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:"+file);
			return connection;
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
}
