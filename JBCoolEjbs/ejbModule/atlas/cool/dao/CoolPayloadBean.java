package atlas.cool.dao;

import java.io.BufferedReader;
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
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.sql.DataSource;

import oracle.sql.BLOB;
import oracle.sql.CLOB;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.payload.model.CoolPayload;

/**
 * Session Bean implementation class CoolPayloadBean.
 */
@Stateless
@Local(CoolPayloadDAO.class)
public class CoolPayloadBean implements CoolPayloadDAO {

	/**
	 * 
	 */
	@Resource(mappedName = "java:jboss/datasources/JBCoolRestDS")
	private DataSource datasource;

	/**
	 * 
	 */
	private static final int FETCHSIZE = 5000;
	/**
	 * 
	 */
	@Inject
	private Logger log;

	/**
	 * 
	 */
	private CallableStatement cstmt = null;
	/**
	 * 
	 */
	private Connection con = null;

	/**
	 * Default constructor.
	 */
	public CoolPayloadBean() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param time
	 * @param channelId
	 * @return
	 * @throws CoolIOException
	 */
	protected final ResultSet getPayload(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal time,
			final Long channelId) throws CoolIOException {
		try {
			BigDecimal ltime = time;
			if (ltime.longValue() < 0) {
				ltime = new BigDecimal(new Date().getTime());
			}
			return getCursorIov(schemaname, dbname, folder, tagname, ltime, channelId);
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param stime
	 * @param etime
	 * @param channelId
	 * @return
	 * @throws CoolIOException
	 */
	protected final ResultSet getPayloads(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal stime,
			final BigDecimal etime, final Long channelId) throws CoolIOException {
		try {
			if (stime == null || etime == null) {
				return null;
			}
			BigDecimal lstime = stime;
			if (lstime.longValue() < 0) {
				lstime = new BigDecimal(new Date().getTime());
			}
			BigDecimal letime = etime;
			if (letime.longValue() < 0) {
				letime = new BigDecimal(CoolIov.COOL_MAX_DATE);
			}
			return getCursorIovs(schemaname, dbname, folder, tagname, lstime, letime,
					channelId);
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param time
	 * @param channelName
	 * @return
	 * @throws CoolIOException
	 */
	protected final ResultSet getPayload(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal time,
			final String channelName) throws CoolIOException {
		try {
			if (time == null) {
				return null;
			}
			BigDecimal ltime = time;
			if (ltime.longValue() < 0) {
				ltime = new BigDecimal(new Date().getTime());
			}
			return getCursorIov(schemaname, dbname, folder, tagname, ltime, channelName);
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param stime
	 * @param etime
	 * @param channelName
	 * @return
	 * @throws CoolIOException
	 */
	protected final ResultSet getPayloads(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal stime,
			final BigDecimal etime, final String channelName) throws CoolIOException {
		try {
			log.info("Calling getPayloads " + schemaname + " " + dbname + " " + folder
					+ " " + tagname + " " + stime + " " + etime + " " + channelName);

			if (stime == null || etime == null) {
				return null;
			}
			BigDecimal lstime = stime;
			if (lstime.longValue() < 0) {
				lstime = new BigDecimal(new Date().getTime());
			}
			BigDecimal letime = etime;
			if (letime.longValue() < 0) {
				letime = new BigDecimal(CoolIov.COOL_MAX_DATE);
			}
			return getCursorIovs(schemaname, dbname, folder, tagname, lstime, letime,
					channelName);
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolPayloadDAO#getPayloadsObj(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal,
	 * java.math.BigDecimal, java.lang.Long)
	 */
	@Override
	public final CoolPayload getPayloadsObj(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal stime,
			final BigDecimal etime, final Long channelId) throws CoolIOException {

		final ResultSet rs = getPayloads(schemaname, dbname, folder, tagname, stime,
				etime, channelId);
		final CoolPayload payload = new CoolPayload();

		try {
			if (rs != null) {

				final ResultSetMetaData rsmdRs = rs.getMetaData();
				for (int i = 1; i <= rsmdRs.getColumnCount(); i++) {
					log.fine("col " + i + " name = " + rsmdRs.getColumnName(i));
				}
				log.fine(" rs is on first row " + rs.isFirst());
				while (rs.next()) {
					final int ncol = rsmdRs.getColumnCount();
					log.fine(" rs loop on column " + ncol);
					for (int i = 1; i <= ncol; i++) {
						final String colname = rsmdRs.getColumnName(i);
						final Object colval = rs.getObject(i);
						payload.addColumn(i, colname);
						payload.addData(i, colval);
						log.fine("Retrieved " + colname + " = " + dumpObject(colval));
					}
				}
			}
			return payload;

		} catch (final SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolPayloadDAO#getPayloadsObj(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal,
	 * java.math.BigDecimal, java.lang.String)
	 */
	@Override
	public final CoolPayload getPayloadsObj(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal stime,
			final BigDecimal etime, final String channelName) throws CoolIOException {

		log.fine("Calling getPayloadObjs " + schemaname + " " + dbname + " " + folder
				+ " " + tagname + " " + stime + " " + etime + " " + channelName);
		final ResultSet rs = getPayloads(schemaname, dbname, folder, tagname, stime,
				etime, channelName);
		final CoolPayload payload = new CoolPayload();

		try {
			if (rs != null) {
				final ResultSetMetaData rsmdRs = rs.getMetaData();
				for (int i = 1; i <= rsmdRs.getColumnCount(); i++) {
					log.fine("col " + i + " name = " + rsmdRs.getColumnName(i));
				}
				log.fine(" rs is on first row " + rs.isFirst());
				while (rs.next()) {
					final int ncol = rsmdRs.getColumnCount();
					log.fine(" rs loop on column " + ncol);
					for (int i = 1; i <= ncol; i++) {
						final String colname = rsmdRs.getColumnName(i);
						final Object colval = rs.getObject(i);
						payload.addColumn(i, colname);
						payload.addData(i, colval);
						log.fine("Retrieved " + colname + " = " + dumpObject(colval));
					}
				}
			}

			if (payload != null) {
				log.info("Retrieved payload " + payload.getNcol() + " "
						+ payload.getRows());
			}
			return payload;

		} catch (final SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return null;
	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param time
	 * @param channelId
	 * @return
	 */
	protected final ResultSet getCursorIov(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal time,
			final Long channelId) {

		final String stmt = "select cool_select_pkg.f_get_payloadiov(?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");

		try {
			if (con == null) {
				con = datasource.getConnection();
			}

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
			 * pstmt = con.prepareStatement(stmt); pstmt.setString(1, schemaname);
			 * pstmt.setString(2, dbname); pstmt.setString(3, "/" + folder);
			 * pstmt.setString(4, tagname); pstmt.setBigDecimal(5, time);
			 * log.info(" call function..."); if (pstmt.execute()) {
			 */
			rset = cstmt.executeQuery();
			final ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {

				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param stime
	 * @param etime
	 * @param channelId
	 * @return
	 */
	protected final ResultSet getCursorIovs(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal stime,
			final BigDecimal etime, final Long channelId) {

		final String stmt = "select cool_select_pkg.f_get_payloadiovs(?,?,?,?,?,?,?) from dual";
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
		log.fine("- channelId " + channelId);

		try {
			String fld = folder;
			if (!fld.startsWith("/")) {
				fld = "/" + folder;
			}
			if (con == null) {
				con = datasource.getConnection();
			} else {
				log.info("Connection is still active...? " + con.toString());
			}

			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			log.fine(" Setting parameters for callable statement ...");
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, fld);
			if (tagname != null) {
				cstmt.setString(4, tagname);
			} else {
				cstmt.setNull(4, Types.VARCHAR);
			}
			cstmt.setBigDecimal(5, stime);
			cstmt.setBigDecimal(6, etime);
			if (channelId != null) {
				cstmt.setLong(7, channelId);
			} else {
				cstmt.setNull(7, Types.INTEGER);
			}
			// Set the fetch size to some large value
			cstmt.setFetchSize(FETCHSIZE);

			log.fine(" executing query ...");

			rset = cstmt.executeQuery();
			final ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param time
	 * @param channelName
	 * @return
	 */
	protected final ResultSet getCursorIov(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal time,
			final String channelName) {

		final String stmt = "select cool_select_pkg.f_get_payloadiovbychanname(?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");

		try {
			if (con == null) {
				con = datasource.getConnection();
			}

			String node = folder;
			if (!node.startsWith("/")) {
				node = "/" + node;
			}
			cstmt = con.prepareCall(stmt);
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, node);
			if (tagname != null) {
				cstmt.setString(4, tagname);
			} else {
				cstmt.setNull(4, Types.VARCHAR);
			}
			cstmt.setBigDecimal(5, time);
			cstmt.setString(6, channelName);
			log.fine(" executing query ...");

			/*
			 * pstmt = con.prepareStatement(stmt); pstmt.setString(1, schemaname);
			 * pstmt.setString(2, dbname); pstmt.setString(3, "/" + folder);
			 * pstmt.setString(4, tagname); pstmt.setBigDecimal(5, time);
			 * log.info(" call function..."); if (pstmt.execute()) {
			 */
			rset = cstmt.executeQuery();
			final ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {

				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param stime
	 * @param etime
	 * @param channelName
	 * @return
	 */
	protected final ResultSet getCursorIovs(final String schemaname, final String dbname,
			final String folder, final String tagname, final BigDecimal stime,
			final BigDecimal etime, final String channelName) {

		final String stmt = "select cool_select_pkg.f_get_payloadiovsbychanname(?,?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.info("Using CallableStatement / ResultSet");
		log.info("-----------------------------------");
		log.info("- schema  " + schemaname);
		log.info("- db      " + dbname);
		log.info("- folder  " + folder);
		log.info("- tag     " + tagname);
		log.info("- since   " + stime);
		log.info("- until   " + etime);
		log.info("- channel " + channelName);

		try {
			String fld = folder;
			if (!fld.startsWith("/")) {
				fld = "/" + folder;
			}
			if (con == null) {
				con = datasource.getConnection();
			} else {
				log.info("Connection is still active...? " + con.toString());
			}

			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			log.fine(" Setting parameters for callable statement ...");
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, fld);
			if (tagname != null) {
				cstmt.setString(4, tagname);
			} else {
				cstmt.setNull(4, Types.VARCHAR);
			}
			cstmt.setBigDecimal(5, stime);
			cstmt.setBigDecimal(6, etime);
			if (channelName != null) {
				cstmt.setString(7, channelName);
			} else {
				cstmt.setNull(7, Types.VARCHAR);
			}
			// Set the fetch size to some large value
			cstmt.setFetchSize(FETCHSIZE);

			log.fine(" executing query ...");

			rset = cstmt.executeQuery();
			final ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * This method is used to return a REF CURSOR that will be used to retrieve data from
	 * a result set. This REF CUSROR is retrieved by the JDBC program into a ResultSet.
	 * 
	 * This method Uses the the regular CallableStatement and ResultSet classes.
	 */
	protected final CoolPayload performRefCursorIov(final String schemaname,
			final String dbname, final String folder, final String tagname,
			final BigDecimal time) {

		// String stmt =
		// "begin ? := cool_select_pkg.f_get_payloadiov(?,?,?,?,?); end;";
		final String stmt = "select cool_select_pkg.f_get_payloadiov(?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");

		final CoolPayload payload = new CoolPayload();
		try {
			con = datasource.getConnection();

			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, "/" + folder);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, time);
			log.fine(" executing query ...");

			/*
			 * pstmt = con.prepareStatement(stmt); pstmt.setString(1, schemaname);
			 * pstmt.setString(2, dbname); pstmt.setString(3, "/" + folder);
			 * pstmt.setString(4, tagname); pstmt.setBigDecimal(5, time);
			 * log.info(" call function..."); if (pstmt.execute()) {
			 */
			rset = cstmt.executeQuery();
			/*
			 * if (cstmt.execute()) { // rset = (ResultSet) pstmt.getResultSet();
			 * log.info(" check resultset ..."); rset = (ResultSet) cstmt.getObject(1);
			 * 
			 * log.info(" retrieved resultset ..."); }
			 */
			final ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {

				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);

				final ResultSetMetaData rsmd_rs = rs.getMetaData();
				for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
					log.fine("col " + i + " name = " + rsmd_rs.getColumnName(i));
				}
				log.fine(" rs is on first row " + rs.isFirst());
				while (rs.next()) {
					final int ncol = rsmd_rs.getColumnCount();
					log.fine(" rs loop on n columns " + ncol);
					for (int i = 1; i <= ncol; i++) {
						final String colname = rsmd_rs.getColumnName(i);
						final Object colval = rs.getObject(i);
						// payload.addColumn(i, colname);
						// payload.addData(i, colval);
						log.fine("Retrieved " + colname + " = " + dumpObject(colval));
					}
				}
			}

			cstmt.close();
			return payload;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method is used to return a REF CURSOR that will be used to retrieve data from
	 * a result set. This REF CUSROR is retrieved by the JDBC program into a ResultSet.
	 * 
	 * This method Uses the the regular CallableStatement and ResultSet classes.
	 */
	protected CoolPayload performRefCursorIovs(final String schemaname,
			final String dbname, final String folder, final String tagname,
			final BigDecimal stime, final BigDecimal etime) {

		// String stmt =
		// "begin ? := cool_select_pkg.f_get_payloadiov(?,?,?,?,?); end;";
		final String stmt = "select cool_select_pkg.f_get_payloadiovs(?,?,?,?,?,?) from dual";
		CallableStatement cstmt = null;
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.fine("Using CallableStatement / ResultSet");
		log.fine("-----------------------------------");

		final CoolPayload payload = new CoolPayload();

		try {
			con = datasource.getConnection();

			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, "/" + folder);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, stime);
			cstmt.setBigDecimal(6, etime);
			log.fine(" executing query ...");

			rset = cstmt.executeQuery();
			final ResultSetMetaData rsmd = rset.getMetaData();

			log.fine("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.fine("col1name = " + rsmd.getColumnName(1));
			log.fine("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {

				log.fine(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				final ResultSetMetaData rsmd_rs = rs.getMetaData();
				for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
					log.fine("col " + i + " name = " + rsmd_rs.getColumnName(i));
				}
				log.fine(" rs is on first row " + rs.isFirst());
				while (rs.next()) {
					final int ncol = rsmd_rs.getColumnCount();
					log.fine(" rs loop on column " + ncol);
					for (int i = 1; i <= ncol; i++) {
						final String colname = rsmd_rs.getColumnName(i);
						final Object colval = rs.getObject(i);
						// if (colval != null) {
						// // check if it is CLOB, BLOB
						// if (colval instanceof oracle.sql.CLOB) {
						// colval = rs.getClob(i);
						// } else if (colval instanceof oracle.sql.BLOB) {
						// colval = rs.getBlob(i);
						// }
						// }
						payload.addColumn(i, colname);
						payload.addData(i, colval);
						log.fine("Retrieved " + colname + " = " + dumpObject(colval));
					}
				}
			}
			cstmt.close();
			return payload;

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param val
	 * @return
	 * @throws SQLException
	 */
	protected String dumpObject(final Object val) throws SQLException {
		String buf = "null";
		if (val == null) {
			return buf;
		}
		if (val instanceof oracle.sql.CLOB) {
			final CLOB clob = (CLOB) val;
			buf = clob.stringValue();
		} else if (val instanceof oracle.sql.BLOB) {
			final BLOB blob = (BLOB) val;
			try {
				buf = lobtoString(blob);
			} catch (CoolIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			buf = val.toString();
		}
		return buf;
	}

	/**
	 * @param dat
	 * @return
	 * @throws CoolIOException
	 */
	protected String lobtoString(final BLOB dat) throws CoolIOException {
		BufferedReader br = null;
		try {
			final StringBuffer strOut = new StringBuffer();
			String aux = null;
			br = new BufferedReader(new InputStreamReader(dat.asciiStreamValue()));
			while ((aux = br.readLine()) != null) {
				strOut.append(aux);
			}
			return strOut.toString();
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	@Remove
	public final void closeConnection() {
		// should remove the sfsb
		try {
			if (cstmt != null) {
				cstmt.close();
				cstmt = null;
			}
			if (con != null) {
				con.close();
				con = null;
			}
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
