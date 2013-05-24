package atlas.cool.dao;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Remove;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.sql.DataSource;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import java.util.logging.Level;
import java.util.logging.Logger;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.payload.model.CoolPayload;

/**
 * Session Bean implementation class CoolResultSet
 */
@Stateless(mappedName = "coolrset")
@LocalBean
public class CoolResultSet implements CoolResultSetDAO {

	@Resource(mappedName = "java:jboss/datasources/JBCoolRestDS")
	DataSource datasource;

	@Inject
	private Logger log;

	private CallableStatement cstmt = null;
	private Connection con = null;

    /**
     * Default constructor. 
     */
    public CoolResultSet() {
        // TODO Auto-generated constructor stub
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolPayloadDAO#getPayload(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * java.math.BigDecimal, java.lang.Long)
	 */
	@Override
	public ResultSet getPayload(String schemaname, String dbname,
			String folder, String tagname, BigDecimal time, Long channelId)
			throws CoolIOException {
		try {
			BigDecimal _time = time;
			if (_time.longValue() < 0) {
				_time = new BigDecimal(new Date().getTime());
			}
			return getCursorIov(schemaname, dbname, folder, tagname, _time,
					channelId);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolPayloadDAO#getPayloads(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * java.math.BigDecimal, java.math.BigDecimal, java.lang.Long)
	 */
	@Override
	public ResultSet getPayloads(String schemaname, String dbname,
			String folder, String tagname, BigDecimal stime, BigDecimal etime,
			Long channelId) throws CoolIOException {
		try {
			BigDecimal _stime = stime;
			if (_stime.longValue() < 0) {
				_stime = new BigDecimal(new Date().getTime());
			}
			BigDecimal _etime = etime;
			if (_etime.longValue() < 0) {
				_etime = new BigDecimal(CoolIov.COOL_MAX_DATE);
			}
			return getCursorIovs(schemaname, dbname, folder, tagname, _stime,
					_etime, channelId);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	
	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolPayloadDAO#getPayload(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.lang.String)
	 */
	@Override
	public ResultSet getPayload(String schemaname, String dbname,
			String folder, String tagname, BigDecimal time, String channelName)
			throws CoolIOException {
		try {
			BigDecimal _time = time;
			if (_time.longValue() < 0) {
				_time = new BigDecimal(new Date().getTime());
			}
			return getCursorIov(schemaname, dbname, folder, tagname, _time,
					channelName);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolPayloadDAO#getPayloads(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.math.BigDecimal, java.lang.String)
	 */
	@Override
	public ResultSet getPayloads(String schemaname, String dbname,
			String folder, String tagname, BigDecimal stime, BigDecimal etime,
			String channelName) throws CoolIOException {
		try {
			log.fine("Calling getPayloads "+schemaname+" "+dbname+" "+folder+" "+tagname+" "+stime+" "+etime+" "+channelName);

			BigDecimal _stime = stime;
			if (_stime.longValue() < 0) {
				_stime = new BigDecimal(new Date().getTime());
			}
			BigDecimal _etime = etime;
			if (_etime.longValue() < 0) {
				_etime = new BigDecimal(CoolIov.COOL_MAX_DATE);
			}
			return getCursorIovs(schemaname, dbname, folder, tagname, _stime,
					_etime, channelName);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	
	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolResultSetDAO#closeConnection()
	 */
	@Override
	@Remove
	public void closeConnection() {
		// TODO Auto-generated method stub
		try {
			if (cstmt != null) {
				cstmt.close();
				cstmt = null;
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	protected ResultSet getCursorIov(String schemaname, String dbname,
			String folder, String tagname, BigDecimal time, Long channelId) {

		String stmt = "select cool_select_pkg.f_get_payloadiov(?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");

		try {
			con = datasource.getConnection();

			String node = folder;
			if (!node.startsWith("/")) {
				node = "/" + node;
			}
			cstmt = con.prepareCall(stmt);
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, node);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, time);
			cstmt.setLong(6, channelId);
			log.fine(" executing query ...");

			/*
			 * pstmt = con.prepareStatement(stmt); pstmt.setString(1,
			 * schemaname); pstmt.setString(2, dbname); pstmt.setString(3, "/" +
			 * folder); pstmt.setString(4, tagname); pstmt.setBigDecimal(5,
			 * time); log.info(" call function..."); if (pstmt.execute()) {
			 */
			rset = cstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {

				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	protected ResultSet getCursorIovs(String schemaname, String dbname,
			String folder, String tagname, BigDecimal stime, BigDecimal etime,
			Long channelId) {

		String stmt = "select cool_select_pkg.f_get_payloadiovs(?,?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");
		log.fine("- schema  " + schemaname);
		log.fine("- db      " + dbname);
		log.fine("- folder  " + folder);
		log.fine("- tag     " + tagname);
		log.fine("- since   " + stime);
		log.fine("- until   " + etime);
		log.fine("- channel " + channelId);

		try {
			String fld = folder;
			if (!fld.startsWith("/")) {
				fld = "/" + folder;
			}
			con = datasource.getConnection();
			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			log.fine(" Setting parameters for callable statement ...");
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, fld);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, stime);
			cstmt.setBigDecimal(6, etime);
			if (channelId != null)
				cstmt.setLong(7, channelId);
			else
				cstmt.setNull(7, Types.INTEGER);
			// Set the fetch size to some large value
			cstmt.setFetchSize(5000);

			log.fine(" executing query ...");

			rset = cstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	protected ResultSet getCursorIov(String schemaname, String dbname,
			String folder, String tagname, BigDecimal time, String channelName) {

		String stmt = "select cool_select_pkg.f_get_payloadiovbychanname(?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");

		try {
			con = datasource.getConnection();

			String node = folder;
			if (!node.startsWith("/")) {
				node = "/" + node;
			}
			cstmt = con.prepareCall(stmt);
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, node);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, time);
			cstmt.setString(6, channelName);
			log.fine(" executing query ...");

			/*
			 * pstmt = con.prepareStatement(stmt); pstmt.setString(1,
			 * schemaname); pstmt.setString(2, dbname); pstmt.setString(3, "/" +
			 * folder); pstmt.setString(4, tagname); pstmt.setBigDecimal(5,
			 * time); log.info(" call function..."); if (pstmt.execute()) {
			 */
			rset = cstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {

				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	protected ResultSet getCursorIovs(String schemaname, String dbname,
			String folder, String tagname, BigDecimal stime, BigDecimal etime,
			String channelName) {

		String stmt = "select cool_select_pkg.f_get_payloadiovsbychanname(?,?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");
		log.fine("- schema  " + schemaname);
		log.fine("- db      " + dbname);
		log.fine("- folder  " + folder);
		log.fine("- tag     " + tagname);
		log.fine("- since   " + stime);
		log.fine("- until   " + etime);
		log.fine("- channel " + channelName);

		try {
			String fld = folder;
			if (!fld.startsWith("/")) {
				fld = "/" + folder;
			}
			con = datasource.getConnection();
			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			log.fine(" Setting parameters for callable statement ...");
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, fld);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, stime);
			cstmt.setBigDecimal(6, etime);
			if (channelName != null)
				cstmt.setString(7, channelName);
			else
				cstmt.setNull(7, Types.VARCHAR);
			// Set the fetch size to some large value
			cstmt.setFetchSize(5000);

			log.fine(" executing query ...");

			rset = cstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	protected String dumpResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd_rs = rs.getMetaData();
		for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
			log.info("col " + i + " name = " + rsmd_rs.getColumnName(i));
		}
		log.info(" rs is on first row " + rs.isFirst());
		StringBuffer buf = new StringBuffer();
		int ncol = rsmd_rs.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= ncol; i++) {
				String colname = rsmd_rs.getColumnName(i);
				Object colval = rs.getObject(i);
				// payload.addColumn(i, colname);
				// payload.addData(i, colval);
				// log.info("Retrieved " + colname + " = " + dumpObject(colval)
				// );
				buf.append("[" + colname + "]=" + dumpObject(colval) + " ; \n");
			}
		}
		return buf.toString();
	}

	protected String dump2FileResultSet(ResultSet rs, String fname)
			throws SQLException {
		FileWriter fw = null;
		CoolPayload pyld = new CoolPayload();
		List<String> masked = pyld.getMasked();
		try {
			fw = new FileWriter(fname);
			ResultSetMetaData rsmd_rs = rs.getMetaData();
			StringBuffer bufheader = new StringBuffer();
			for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
				String colname = rsmd_rs.getColumnName(i);
				log.info("col " + i + " name = " + colname);
				if (masked.contains(colname)) {
					log.info("Ignore column " + colname
							+ " in the output file ");
				} else
					bufheader.append(colname + "  ");
			}
			fw.write(bufheader.toString() + "\n");
			log.info(" rs is on first row " + rs.isFirst());
			int ncol = rsmd_rs.getColumnCount();
			while (rs.next()) {
				StringBuffer bufline = new StringBuffer();
				for (int i = 1; i <= ncol; i++) {
					String colname = rsmd_rs.getColumnName(i);
					Object colval = rs.getObject(i);
					if (masked.contains(colname)) {
						log.info("Ignore column " + colname
								+ " in the output file ");
					} else
						bufline.append(dumpObject(colval) + " ; ");

				}
				fw.write(bufline.toString() + "\n");
				fw.flush();
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fname;
	}

	protected String dumpObject(Object val) throws SQLException {
		String buf = "null";
		if (val == null)
			return buf;
		if (val instanceof oracle.sql.CLOB) {
			CLOB clob = (CLOB) val;
			buf = clob.stringValue();
		} else if (val instanceof oracle.sql.BLOB) {
			BLOB blob = (BLOB) val;
			try {
				buf = lobtoString(blob);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			buf = val.toString();
		}
		return buf;
	}

	protected String lobtoString(BLOB dat) throws IOException, SQLException {
		StringBuffer strOut = new StringBuffer();
		String aux;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				dat.asciiStreamValue()));
		while ((aux = br.readLine()) != null)
			strOut.append(aux);
		return strOut.toString();
	}

}
