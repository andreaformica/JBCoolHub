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
import atlas.cool.payload.model.CoolPayload;


@Named("pyldhelper")
@SessionScoped
/**
 * @author formica
 *
 */
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

	public String dump2FileResultSet(ResultSet rs, String path, String fname) throws SQLException {
		FileWriter fw = null;
		CoolPayload pyld = new CoolPayload();
		List<String> masked = pyld.getMasked(); 
		String outurl = "web/"+fname;
		try {
			fw = new FileWriter(path+fname);
			ResultSetMetaData rsmd_rs = rs.getMetaData();
			StringBuffer bufheader = new StringBuffer();
			for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
				String colname = rsmd_rs.getColumnName(i);
				log.info("col " + i + " name = " + colname);
				if (masked.contains(colname)) {
					log.fine("Ignore column "+colname+" in the output file ");
				} else
					bufheader.append(colname+"  ");
			}
			fw.write(bufheader.toString()+"\n");
			if (!rs.isFirst()) {
				return "ResultSet already parsed";
			}
			log.info(" rs is on first row "+rs.isFirst());
			int ncol = rsmd_rs.getColumnCount();
			while (rs.next()) {
				StringBuffer bufline = new StringBuffer();
				for (int i = 1; i <= ncol; i++) {
					String colname = rsmd_rs.getColumnName(i);
					Object colval = rs.getObject(i);
					if (masked.contains(colname)) {
						log.fine("Ignore column "+colname+" in the output file ");
					} else
						bufline.append(dumpObject(colval) + " ; ");
					
				}
				fw.write(bufline.toString()+"\n");
				fw.flush();
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outurl;
	}

	public String dump2FileCoolPayload(CoolPayload pyld, String path, String fname) throws SQLException {
		FileWriter fw = null;
		String outurl = "web/"+fname;
		try {
			fw = new FileWriter(path+fname);
			StringBuffer bufheader = new StringBuffer();
			for (String colname : pyld.getColumns()) {
				bufheader.append(colname+"  ");
			}			
			fw.write(bufheader.toString()+"\n");
			for (int irow=0; irow<pyld.getRows();irow++ ) {
				StringBuffer bufline = new StringBuffer();
				Map<String,Object> rowmap = pyld.getDataRow(irow);
				for (String colname : rowmap.keySet()) {
					Object colval = rowmap.get(colname);
					bufline.append(dumpObject(colval) + " ; ");
				}
				fw.write(bufline.toString()+"\n");
				fw.flush();
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outurl;
	}

	public CoolPayload resultSetToPayload(ResultSet rs) {

		CoolPayload pyld = new CoolPayload();
		List<String> masked = pyld.getMasked(); 
		try {
			ResultSetMetaData rsmd_rs = rs.getMetaData();
			// get payload columns
			for (int i = 1; i <= rsmd_rs.getColumnCount(); i++) {
				String colname = rsmd_rs.getColumnName(i);
				log.info("col " + i + " name = " + colname);
				if (masked.contains(colname)) {
					log.info("Ignore column "+colname+" in the output class ");
				} else
					pyld.addColumn(i, colname);
			}
		
			if (!rs.isFirst()) {
				log.info("ResultSet already parsed...returning null");
			}
			log.fine(" rs is on first row "+rs.isFirst());
			int ncol = rsmd_rs.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= ncol; i++) {
					String colname = rsmd_rs.getColumnName(i);
					Object colval = rs.getObject(i);
					if (masked.contains(colname)) {
						log.fine("Ignore column "+colname+" in the output class ");
					} else {
//						log.info("Adding data "+colval+" to column "+colname+" index "+i);
						if (pyld.getColumn(i) != null) {
							log.fine("Adding data "+colval+" to column "+colname+" index "+i);
							pyld.addData(i, colval);
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pyld;
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

}
