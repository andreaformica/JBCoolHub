/**
 * 
 */
package atlas.cool.jsf.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author formica
 *
 * @param <T>
 */
public class ColumnsGenerator<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6567075362783715010L;

	private final Class<T> obj;
	private List<ColumnModel> columns = null;
	private final Map<String, String> columnsMask = null;
	private final Map<String, String> methodNames = new LinkedHashMap<String, String>();

	/**
	 * 
	 */
	public ColumnsGenerator(final Class<T> clazz) {
		// TODO Auto-generated constructor stub
		this.obj = clazz;
		initMethods();
	}

	/**
	 * 
	 */
	protected void initMethods() {

		final Field[] fields = obj.getDeclaredFields();
		for (final Field field : fields) {

			final String fieldname = field.getName();
			final String mthname = "get" + fieldname.substring(0, 1).toUpperCase()
					+ fieldname.substring(1);
			methodNames.put(fieldname, mthname);
		}
	}

	/**
	 * @param fieldname
	 * @param methname
	 */
	public void addMethod(final String fieldname, final String methname) {
		if (methname != null && fieldname != null) {
			methodNames.put(fieldname, methname);
		}
	}

	/**
	 * @param fieldname
	 * @param methname
	 */
	public void removeMethod(final String fieldname, final String methname) {
		if (methname != null && fieldname != null) {
			if (methodNames.containsKey(fieldname)) {
				methodNames.remove(fieldname);
			}
		}

	}

	/**
	 * @return
	 */
	public List<ColumnModel> getColumns() {

		columns = new ArrayList<ColumnModel>();
		for (final String fieldname : methodNames.keySet()) {
			try {
				final String mthname = methodNames.get(fieldname);
				final Method getmeth = obj.getDeclaredMethod(mthname, (Class<?>[]) null);
				final ColumnModel mod = getColumnModel(getmeth, fieldname);
				if (mod != null && columnsMask != null) {
					// System.out.println("Check if is selected: "+columnsMask.containsKey(mod));
					if (columnsMask.containsKey(mod.header)) {
						columns.add(mod);
						// System.out.println("Add columnmodel "+mod.header+" "+mod.property);
					}
				} else if (mod != null) {
					columns.add(mod);
					// System.out.println("Add columnmodel no checks "+mod.header+" "+mod.property);
				}
			} catch (final SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final NoSuchMethodException e) {

			}

		}
		return columns;
	}

	/**
	 * @param getmeth
	 * @param fieldname
	 * @return
	 */
	protected ColumnModel getColumnModel(final Method getmeth, final String fieldname) {
		if (getmeth != null) {
			if (getmeth.getReturnType().equals(List.class)
					|| getmeth.getReturnType().equals(Set.class)) {
				// does not represent these fields in the data table
			} else if (getmeth.getReturnType().equals(Calendar.class)
					|| getmeth.getReturnType().equals(Timestamp.class)) {

				return new ColumnModel(fieldname.toUpperCase(), fieldname + "Str");
			} else if (getmeth.getReturnType().equals(Integer.class)) {
				if (fieldname.matches(".*Lock.*")) {
					return new ColumnModel("Lock", fieldname);
				}
				return new ColumnModel(fieldname.toUpperCase(), fieldname);
			} else {
				return new ColumnModel(fieldname.toUpperCase(), fieldname);
			}
		}
		return null;
	}

	/*
	 * public void selMethod(String fieldname) {
	 * 
	 * try { if (columnsMask == null) columnsMask = new
	 * HashMap<String,String>(); String mthname =
	 * "get"+fieldname.substring(0,1).toUpperCase() + fieldname.substring(1);
	 * System
	 * .out.println("Search for method "+mthname+" in class "+obj.getName());
	 * Method getmeth = obj.getDeclaredMethod(mthname, (Class<?>[]) null);
	 * ColumnModel maskcol = getColumnModel(getmeth, fieldname); if (maskcol !=
	 * null) { if (!columnsMask.containsKey(maskcol.header))
	 * columnsMask.put(maskcol.header, "selected");
	 * //System.out.println("Selecting columnmodel "
	 * +maskcol.header+" "+maskcol.property); }
	 * 
	 * } catch (SecurityException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (NoSuchMethodException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 * 
	 * public void unselMethod(String fieldname) {
	 * 
	 * try { if (columnsMask == null) return; String mthname =
	 * "get"+fieldname.substring(0,1).toUpperCase() + fieldname.substring(1);
	 * Method getmeth = obj.getDeclaredMethod(mthname, (Class<?>[]) null);
	 * ColumnModel maskcol = getColumnModel(getmeth, fieldname); if
	 * (columnsMask.containsKey(maskcol)) columnsMask.remove(maskcol); } catch
	 * (SecurityException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (NoSuchMethodException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 * 
	 * public void unselAll() {
	 * 
	 * try { if (columnsMask == null) return; columnsMask.clear(); columnsMask =
	 * null;
	 * 
	 * } catch (SecurityException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	/**
	 * @return the obj
	 */
	public Class<T> getObj() {
		return obj;
	}

	/**
	 * @return the columnsMask
	 */
	public Map<String, String> getColumnsMask() {
		return columnsMask;
	}

	/**
	 * @author formica
	 * 
	 */
	public static class ColumnModel implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1378731618746822514L;
		private final String header;
		private final String property;
		private String cssclass;

		/**
		 * @param header
		 * @param property
		 */
		public ColumnModel(final String header, final String property) {
			this.header = header;
			this.property = property;
		}

		public String getHeader() {
			return header;
		}

		public String getProperty() {
			return property;
		}

		/**
		 * @return the cssclass
		 */
		public String getCssclass() {
			return cssclass;
		}

		/**
		 * @param cssclass
		 *            the cssclass to set
		 */
		public void setCssclass(final String cssclass) {
			this.cssclass = cssclass;
		}

	}

}
