/**
 * 
 */
package atlas.cool.jsf.utils;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import oracle.sql.BLOB;
import oracle.sql.CLOB;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.payload.model.CoolPayload;

/**
 * @author formica
 *
 */
@Named("pyldhelper")
@SessionScoped
public class PayloadHelperBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8876014275731405370L;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public PayloadHelperBean() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rs
	 * @param path
	 * @param fname
	 * @return
	 * @throws SQLException
	 */
	public String dump2FileResultSet(final ResultSet rs, final String path,
			final String fname) throws SQLException {
		FileWriter fw = null;
		final CoolPayload pyld = new CoolPayload();
		final List<String> masked = pyld.getMasked();
		final String outurl = "web/" + fname;
		try {
			fw = new FileWriter(path + fname);
			final ResultSetMetaData rsmdRs = rs.getMetaData();
			final StringBuffer bufheader = new StringBuffer();
			for (int i = 1; i <= rsmdRs.getColumnCount(); i++) {
				final String colname = rsmdRs.getColumnName(i);
				log.info("col " + i + " name = " + colname);
				if (masked.contains(colname)) {
					log.fine("Ignore column " + colname + " in the output file ");
				} else {
					bufheader.append(colname + "  ");
				}
			}
			fw.write(bufheader.toString() + "\n");
			if (!rs.isFirst()) {
				fw.close();
				return "ResultSet already parsed";
			}
			log.info(" rs is on first row " + rs.isFirst());
			final int ncol = rsmdRs.getColumnCount();
			while (rs.next()) {
				final StringBuffer bufline = new StringBuffer();
				for (int i = 1; i <= ncol; i++) {
					final String colname = rsmdRs.getColumnName(i);
					final Object colval = rs.getObject(i);
					if (masked.contains(colname)) {
						log.fine("Ignore column " + colname + " in the output file ");
					} else {
						bufline.append(dumpObject(colval) + " ; ");
					}

				}
				fw.write(bufline.toString() + "\n");
				fw.flush();
			}
			fw.flush();
			fw.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outurl;
	}

	/**
	 * @param pyld
	 * @param path
	 * @param fname
	 * @return
	 * @throws SQLException
	 */
	public String dump2FileCoolPayload(final CoolPayload pyld, final String path,
			final String fname) throws SQLException {
		FileWriter fw = null;
		final String outurl = "web/" + fname;
		try {
			fw = new FileWriter(path + fname);
			final StringBuffer bufheader = new StringBuffer();
			for (final String colname : pyld.getColumns()) {
				bufheader.append(colname + "  ");
			}
			fw.write(bufheader.toString() + "\n");
			for (int irow = 0; irow < pyld.getRows(); irow++) {
				final StringBuffer bufline = new StringBuffer();
				final Map<String, Object> rowmap = pyld.getDataRow(irow);
				for (final String colname : rowmap.keySet()) {
					final Object colval = rowmap.get(colname);
					bufline.append(dumpObject(colval) + " ; ");
				}
				fw.write(bufline.toString() + "\n");
				fw.flush();
			}
			fw.flush();
			fw.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outurl;
	}

	/**
	 * @param rs
	 * @return
	 */
	public CoolPayload resultSetToPayload(final ResultSet rs) {

		final CoolPayload pyld = new CoolPayload();
		final List<String> masked = pyld.getMasked();
		try {
			final ResultSetMetaData rsmdRs = rs.getMetaData();
			// get payload columns
			for (int i = 1; i <= rsmdRs.getColumnCount(); i++) {
				final String colname = rsmdRs.getColumnName(i);
				log.info("col " + i + " name = " + colname);
				if (masked.contains(colname)) {
					log.info("Ignore column " + colname + " in the output class ");
				} else {
					pyld.addColumn(i, colname);
				}
			}

			if (!rs.isFirst()) {
				log.info("ResultSet already parsed...returning null");
			}
			log.fine(" rs is on first row " + rs.isFirst());
			final int ncol = rsmdRs.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= ncol; i++) {
					final String colname = rsmdRs.getColumnName(i);
					final Object colval = rs.getObject(i);
					if (masked.contains(colname)) {
						log.fine("Ignore column " + colname + " in the output class ");
					} else {
						// log.info("Adding data "+colval+" to column "+colname+" index "+i);
						if (pyld.getColumn(i) != null) {
							log.fine("Adding data " + colval + " to column " + colname
									+ " index " + i);
							pyld.addData(i, colval);
						}
					}
				}
			}
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pyld;
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
	 * @throws IOException
	 * @throws SQLException
	 */
	protected final String lobtoString(final BLOB dat) throws CoolIOException {

		BufferedReader br = null;
		try {
		final StringBuffer strOut = new StringBuffer();
		String aux;
		br = new BufferedReader(new InputStreamReader(
				dat.asciiStreamValue()));
		while ((aux = br.readLine()) != null) {
			strOut.append(aux);
		}
		return strOut.toString();
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
