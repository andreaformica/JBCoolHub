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
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.sql.DataSource;

import oracle.sql.BLOB;
import oracle.sql.CLOB;
import atlas.cool.meta.CoolIov;
import atlas.cool.meta.CoolPayload;

/**
 * Session Bean implementation class CoolPayloadBean
 */
@Stateful
@Local(CoolPayloadDAO.class)
public class CoolPayloadBean implements CoolPayloadDAO {

	@Resource(mappedName = "java:jboss/datasources/JBCoolRestDS")
	DataSource datasource;

	@Inject
	private Logger log;

	private CallableStatement cstmt = null;
	private Connection con = null;
	
//	private int firstResult=0;
//	private int maxResults=0;
//	private int step=10000;
//	private boolean doPagination = false;
	
	/**
	 * Default constructor.
	 */
	public CoolPayloadBean() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResultSet getPayload(String schemaname, String dbname,
			String folder, String tagname, BigDecimal time, Integer channelId)
			throws CoolIOException {
		// TODO Auto-generated method stub
		try {
			BigDecimal _time = time;
			if (_time.longValue()<0) {
				_time = new BigDecimal(new Date().getTime());
			}
			return getCursorIov(schemaname, dbname, folder, tagname, _time, channelId);
			//return pyld;
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	@Override
	public ResultSet getPayloads(String schemaname, String dbname,
			String folder, String tagname, BigDecimal stime, BigDecimal etime, Integer channelId)
			throws CoolIOException {
		try {
			BigDecimal _stime = stime;
			if (_stime.longValue()<0) {
				_stime = new BigDecimal(new Date().getTime());
			}
			BigDecimal _etime = etime;
			if (_etime.longValue()<0) {
				_etime = new BigDecimal(CoolIov.COOL_MAX_DATE);
			}
			
			//CoolPayload pyld = performRefCursorIovs(schemaname, dbname, folder, tagname, _stime, _etime);
			return getCursorIovs(schemaname, dbname, folder, tagname,  _stime, _etime, channelId);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	protected ResultSet getCursorIov(String schemaname, String dbname,
			String folder, String tagname, BigDecimal time, Integer channelId) {

		String stmt = "select cool_select_pkg.f_get_payloadiov(?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.info("Using CallableStatement / ResultSet");
		log.info("-----------------------------------");

		try {
			con = datasource.getConnection();

			String node = folder;
			if (!node.startsWith("/")) {
				node = "/"+node;
			}
			cstmt = con.prepareCall(stmt);
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, node);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, time);
			cstmt.setInt(6, channelId);
			log.info(" executing query ...");

			/*
			 * pstmt = con.prepareStatement(stmt); pstmt.setString(1,
			 * schemaname); pstmt.setString(2, dbname); pstmt.setString(3, "/" +
			 * folder); pstmt.setString(4, tagname); pstmt.setBigDecimal(5,
			 * time); log.info(" call function..."); if (pstmt.execute()) {
			 */
			rset = cstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			log.info("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.info("col1name = " + rsmd.getColumnName(1));
			log.info("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				
				log.info(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	protected ResultSet getCursorIovs(String schemaname, String dbname,
			String folder, String tagname, BigDecimal stime, BigDecimal etime, Integer channelId) {

		String stmt = "select cool_select_pkg.f_get_payloadiovs(?,?,?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.info("Using CallableStatement / ResultSet");
		log.info("-----------------------------------");
		log.info("- schema  "+schemaname);
		log.info("- db      "+dbname);
		log.info("- folder  "+folder);
		log.info("- tag     "+tagname);
		log.info("- since   "+stime);
		log.info("- until   "+etime);
		log.info("- channel "+channelId);

		try {
			String fld = folder;
			if (!fld.startsWith("/")) {
				fld = "/"+folder;
			}
			con = datasource.getConnection();
			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			log.info(" Setting parameters for callable statement ...");
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, fld);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, stime);
			cstmt.setBigDecimal(6, etime);
			if (channelId != null)
				cstmt.setInt(7, channelId);
			else
				cstmt.setNull(7, Types.INTEGER);
			// Set the fetch size to some large value
			cstmt.setFetchSize(5000);
			
			log.info(" executing query ...");

			rset = cstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			log.info("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.info("col1name = " + rsmd.getColumnName(1));
			log.info("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				
				log.info(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				return rs;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	
	/**
	 * This method is used to return a REF CURSOR that will be used to retrieve
	 * data from a result set. This REF CUSROR is retrieved by the JDBC program
	 * into a ResultSet.
	 * 
	 * This method Uses the the regular CallableStatement and ResultSet classes.
	 */
	protected CoolPayload performRefCursorIov(String schemaname, String dbname,
			String folder, String tagname, BigDecimal time) {

		// String stmt =
		// "begin ? := cool_select_pkg.f_get_payloadiov(?,?,?,?,?); end;";
		String stmt = "select cool_select_pkg.f_get_payloadiov(?,?,?,?,?) from dual";
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.info("Using CallableStatement / ResultSet");
		log.info("-----------------------------------");

		CoolPayload payload = new CoolPayload();
		try {
			con = datasource.getConnection();

			cstmt = con.prepareCall(stmt);
			// cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.setString(1, schemaname);
			cstmt.setString(2, dbname);
			cstmt.setString(3, "/" + folder);
			cstmt.setString(4, tagname);
			cstmt.setBigDecimal(5, time);
			log.info(" executing query ...");

			/*
			 * pstmt = con.prepareStatement(stmt); pstmt.setString(1,
			 * schemaname); pstmt.setString(2, dbname); pstmt.setString(3, "/" +
			 * folder); pstmt.setString(4, tagname); pstmt.setBigDecimal(5,
			 * time); log.info(" call function..."); if (pstmt.execute()) {
			 */
			rset = cstmt.executeQuery();
			/*
			 * if (cstmt.execute()) { // rset = (ResultSet)
			 * pstmt.getResultSet(); log.info(" check resultset ..."); rset =
			 * (ResultSet) cstmt.getObject(1);
			 * 
			 * log.info(" retrieved resultset ..."); }
			 */
			ResultSetMetaData rsmd = rset.getMetaData();

			log.info("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.info("col1name = " + rsmd.getColumnName(1));
			log.info("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				
				log.info(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				
				ResultSetMetaData rsmd_rs = rs.getMetaData();
				for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
					log.info("col " + i + " name = " + rsmd_rs.getColumnName(i));
				}
				log.info(" rs is on first row "+rs.isFirst());
				while (rs.next()) {
					int ncol = rsmd_rs.getColumnCount();
					log.info(" rs loop on n columns "+ncol);
					for (int i = 1; i <= ncol; i++) {
						String colname = rsmd_rs.getColumnName(i);
						Object colval = rs.getObject(i);
//						payload.addColumn(i, colname);
//						payload.addData(i, colval);
						log.info("Retrieved " + colname + " = " + dumpObject(colval) );
					}
				}
			}

			cstmt.close();
			return payload;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * This method is used to return a REF CURSOR that will be used to retrieve
	 * data from a result set. This REF CUSROR is retrieved by the JDBC program
	 * into a ResultSet.
	 * 
	 * This method Uses the the regular CallableStatement and ResultSet classes.
	 */
	protected CoolPayload performRefCursorIovs(String schemaname, String dbname,
			String folder, String tagname, BigDecimal stime, BigDecimal etime) {

		// String stmt =
		// "begin ? := cool_select_pkg.f_get_payloadiov(?,?,?,?,?); end;";
		String stmt = "select cool_select_pkg.f_get_payloadiovs(?,?,?,?,?,?) from dual";
		CallableStatement cstmt = null;
		ResultSet rset = null;
		ResultSet rs = null;
		// PreparedStatement pstmt = null;
		log.info("Using CallableStatement / ResultSet");
		log.info("-----------------------------------");

		CoolPayload payload = new CoolPayload();

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
			log.info(" executing query ...");

			rset = cstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			log.info("Retrieved n = " + rsmd.getColumnCount() + " columns ");
			log.info("col1name = " + rsmd.getColumnName(1));
			log.info("col1type = " + rsmd.getColumnClassName(1));
			while (rset.next()) {
				
				log.info(" - " + rset.getObject(1).toString());
				rs = (ResultSet) rset.getObject(1);
				ResultSetMetaData rsmd_rs = rs.getMetaData();
				for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
					log.info("col " + i + " name = " + rsmd_rs.getColumnName(i));
				}
				log.info(" rs is on first row "+rs.isFirst());
				while (rs.next()) {
					int ncol = rsmd_rs.getColumnCount();
					log.info(" rs loop on column "+ncol);
					for (int i = 1; i <= ncol; i++) {
						String colname = rsmd_rs.getColumnName(i);
						Object colval = rs.getObject(i);
//						if (colval != null) {
//							// check if it is CLOB, BLOB
//							if (colval instanceof oracle.sql.CLOB) {
//								colval = rs.getClob(i);
//							} else if (colval instanceof oracle.sql.BLOB) {
//								colval = rs.getBlob(i);
//							}						
//						}
						payload.addColumn(i, colname);
						payload.addData(i, colval);
						log.info("Retrieved " + colname + " = " + dumpObject(colval));
					}
				}
			}
			cstmt.close();
			return payload;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String dumpObject(Object val) throws SQLException {
		String buf = "null";
		if (val ==null)
			return buf;
		if (val instanceof oracle.sql.CLOB) {
			CLOB clob = (CLOB)val;
			buf = clob.stringValue();
		} else if (val instanceof oracle.sql.BLOB) {
			BLOB blob = (BLOB)val;
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
		   BufferedReader br = new BufferedReader(new InputStreamReader(dat.asciiStreamValue()));
		    while ((aux=br.readLine())!=null)
		             	strOut.append(aux);
		    return strOut.toString();
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolPayloadDAO#remove()
	 */
	@Override
	@Remove
	public void remove() throws CoolIOException {
		try {
			if (cstmt != null) {
				log.info("Close statement...");
				cstmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			cstmt = null;
		}
	}
	
	
}
