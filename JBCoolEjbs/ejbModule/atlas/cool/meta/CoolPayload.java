/**
 * 
 */
package atlas.cool.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

/**
 * @author formica
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CoolPayload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6318160906099910879L;

	private static Logger log = Logger.getLogger(CoolPayload.class.getName());

	Map<Integer, String> columns = new LinkedHashMap<Integer, String>();
	// Map<String, Class<?>> types = new HashMap<String, Class<?>>();
	Map<Integer, Vector<Object>> data = new HashMap<Integer, Vector<Object>>();
	List<String> masked = null;

	/**
	 * 
	 */
	public CoolPayload() {
		// TODO Auto-generated constructor stub
		initListOfMasked();
	}

	public void addColumn(int icol, String name) {
		if (!columns.containsKey(icol) && !columns.containsValue(name)) {
			columns.put(icol, name);
			log.fine("Adding column "+icol+" "+name+" to payload...");
			// types.put(name, type);
		}
	}

	public void addData(int icol, Object datacol) {
		if (!data.containsKey(icol)) {
			if (datacol == null)
				data.put(icol, null);
			else
				data.put(icol, new Vector<Object>());
		}
		Vector<Object> datavector = data.get(icol);
		if (datavector != null)
			datavector.add(datacol);

	}

	public String getColumn(int i) {
		if (!columns.containsKey(i))
			return null;
		return columns.get(i);
	}
	
	public Integer getColumnIndex(String col) {
		if (!columns.containsValue(col))
			return null;
		for (Integer key : columns.keySet()) {
			if (columns.get(key).equals(col))
				return key;
		}
		return null;
	}

	public List<String> getColumns() {
		
		List<String> columnlist = new ArrayList<String>();
		for (Integer icol : columns.keySet()) {
			String colname = getColumn(icol);
			if (colname != null) {
				columnlist.add(colname);
				log.fine("Adding column "+colname +" to list of columns ");
			}
		}
		return columnlist;
	}

	public Class<?> getColumnType(int i) {
		Vector<Object> dataobj = getDataColumn(i);
		if (dataobj != null && dataobj.size()>0) {
			return dataobj.get(0).getClass();
		}
		return null;
	}

	public List<String> getNumberColumns() {
		List<String> columnlist = new ArrayList<String>();
		for (Integer icol : columns.keySet()) {
			String colname = getColumn(icol);
			if (colname != null) {
				if (isNumber(colname)) {
					columnlist.add(colname);
					log.fine("Adding column "+colname +" to list of chart fields ");
				}
			}
		}
		return columnlist;
	}

	public Vector<Object> getDataColumn(int i) {
		if (!data.containsKey(i))
			return null;
		return data.get(i);
	}

	public Vector<Object> getDataColumn(String colname) {
		Integer i = 0;
		if (columns.containsValue(colname)) {
			for(Integer icol : columns.keySet()) {
				if (columns.get(icol).equals(colname))
					i = icol;
			}
		}
		log.fine("Selected column "+colname+" with index "+i);
		if (!data.containsKey(i))
			return null;
		return data.get(i);
	}

	public Map<String, Object> getDataRow(int i) throws SQLException,
			IOException {
		Map<String, Object> rowdata = new HashMap<String, Object>();
		Set<Integer> colkeyset = columns.keySet();
		for (Integer icol : colkeyset) {
			String colname = columns.get(icol);
			Vector<Object> coldata = data.get(icol);
			Object valatrow = (coldata == null) ? null : coldata.get(i);

			log.fine("Adding value "+valatrow +" to columns "+colname);

			String valstr = null;
			if (valatrow instanceof oracle.sql.CLOB) {
				//CLOB clob = (CLOB) valatrow;
				valstr = "CLOB";
				rowdata.put(colname, valstr);
			} else if (valatrow instanceof oracle.sql.BLOB) {
				//BLOB blob = (BLOB) valatrow;
				valstr = "BLOB";
				rowdata.put(colname, valstr);
			} else
				rowdata.put(colname, valatrow);
		}
		return rowdata;
	}

	public List<Map<String, Object>> getDataList() {

		List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < getRows(); i++) {
			Map<String, Object> rowdata;
			try {
				rowdata = getDataRow(i);
				log.fine("got map of size "+rowdata.size());
				datalist.add(rowdata);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.fine("return list of data of size "+datalist.size());
		return datalist;
	}

	public Integer getNcol() {
		if (columns != null)
			return columns.keySet().size();

		return 0;
	}

	public Integer getRows() {
		Set<Integer> icolset = data.keySet();
		for (Integer icol : icolset) {
			Vector<Object> datacol = data.get(icol);
			if (datacol != null)
				return datacol.size();
		}

		return 0;
	}

	public String toString() {

		int nrows = getRows();
		// return "collected nrows = "+nrows;

		StringBuffer databuf = new StringBuffer();
		try {
			for (int irow = 0; irow < nrows; irow++) {
				Map<String, Object> rowdata = getDataRow(irow);
				if (rowdata != null) {
					databuf.append("Row = " + irow + " : \n");
					for (int icol = 0; icol <= getNcol(); icol++) {
						// Set<String> keyset = rowdata.keySet();
						// for (String cn : keyset) {
						String colname = columns.get(icol);
						Object val = rowdata.get(colname);
						String valstr = null;
						if (val instanceof oracle.sql.CLOB) {
							CLOB clob = (CLOB) val;
							valstr = clob.stringValue();
						} else if (val instanceof oracle.sql.BLOB) {
							BLOB blob = (BLOB) val;
							valstr = lobtoString(blob);
						} else {
							if (val == null)
								valstr = "NULL";
							else
								valstr = val.toString();
						}
						databuf.append("[" + colname + "]=" + valstr + " ; \n");
					}
				}
				databuf.append("---------------------------------------\n");
			}
			return databuf.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "done";
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

	protected void initListOfMasked() {
		List<String> maskedColumns = new ArrayList<String>();
		maskedColumns.add("OBJECT_ID");
		maskedColumns.add("USER_TAG_ID");
		maskedColumns.add("SYS_INSTIME");
		maskedColumns.add("LASTMOD_DATE");
		maskedColumns.add("ORIGINAL_ID");
		maskedColumns.add("NEW_HEAD_ID");
		maskedColumns.add("LAST_OBJECT_ID");
		maskedColumns.add("HAS_NEW_DATA");
		maskedColumns.add("TAG_ID");
		maskedColumns.add("TAG_DESCRIPTION");
		maskedColumns.add("TAG_NAME");
		maskedColumns.add("IOV_BASE");
		masked = maskedColumns;
	}

	public boolean isMasked(String col) {
		if (masked.contains(col)) {
			return true;
		}
		if (col.equals("CHANNEL_ID") || 
				col.equals("CHANNEL_NAME") || 
				col.equals("CHANNEL_DESCRIPTION") || 
				col.equals("IOV_SINCE") || 
				col.equals("IOV_UNTIL") 
				) {
			return true;
		}
		return false;
	}
	
	public boolean isNumber(String col) {
		if (!isMasked(col)) {
			//log.info("Check if column "+col +" is a number ");
			Class<?> clazz = getColumnType(getColumnIndex(col));
			if (clazz != null) {
				//log.info("Class is "+clazz.getName());
				if (clazz.equals(Number.class)  ||
						clazz.equals(Boolean.class)||
						clazz.equals(String.class)||
						clazz.equals(BigDecimal.class)||
						clazz.equals(Short.class)||
						clazz.equals(Long.class)||
						clazz.equals(Integer.class)||
						clazz.equals(Float.class)||
						clazz.equals(Double.class)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return the masked
	 */
	public List<String> getMasked() {
		return masked;
	}

}
