/**
 * 
 */
package atlas.sqlite.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.inject.Inject;

import atlas.cool.rest.utils.SQLiteConnectionMgr;

/**
 * @author formica
 * 
 */
@Stateful
public class SQLiteBean {

	@Inject
	private Logger log;

	Connection conn = null;

	String dbname = null;

	public void initConnection(String db) {
		if (db != null)
			dbname = db;
		conn = SQLiteConnectionMgr.getConnection(dbname);
	}

	public ResultSet listAllTables() {
		try {
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'");
			while (rs.next()) {
				// read the result set
				System.out.println("name = " + rs.getString("name"));
			}
			return rs;
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Remove
	public void close() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			// connection close failed.
			System.err.println(e);
		}

	}
}
