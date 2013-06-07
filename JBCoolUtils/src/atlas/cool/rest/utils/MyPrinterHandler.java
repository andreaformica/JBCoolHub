/**
 * 
 */
package atlas.cool.rest.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author formica
 * 
 * @param <T>
 */
public class MyPrinterHandler<T> {

	/**
	 * 
	 */
	private String separator = " | ";
	/**
	 * 
	 */
	private T objPrint;
	/**
	 * 
	 */
	private Map<String, Integer> exclusionMap = new HashMap<String, Integer>();
	/**
	 * 
	 */
	private Vector<Method> methods = new Vector<Method>();

	/**
	 * @param obj
	 *            The object to print.
	 */
	public MyPrinterHandler(final T obj) {
		this.objPrint = obj;
	}

	/**
	 * @param obj
	 *            The object to print.
	 * @param pseparator
	 *            The separator.
	 */
	public MyPrinterHandler(final T obj, final String pseparator) {
		this.objPrint = obj;
		this.separator = pseparator;
	}

	/**
	 * @param methname
	 * 	The method name.
	 * @param exclusion
	 * 	Exclusion flag.
	 */
	public final void exclude(final String methname, final Integer exclusion) {
		if (exclusionMap.containsKey(methname)) {
			Integer value = exclusionMap.get(methname);
			if (value != exclusion) {
				exclusionMap.put(methname, exclusion);
			}
		} else {
			exclusionMap.put(methname, exclusion);
		}
	}

	/**
	 * @param meth
	 * 	The method to add.
	 */
	public final void add(final Method meth) {
		methods.add(meth);
	}

	/**
	 * 
	 */
	private void clear() {
		exclusionMap.clear();
	}

	/**
	 * @return String.
	 */
	private String printOrdered() {
		if (methods.size() > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < methods.size(); i++) {
				try {
// log.debug("printOrdered: using method "+methods.elementAt(i).getName());
					Object retfield = methods.elementAt(i).invoke(objPrint,
							(Object[]) null);
					if (retfield != null) {
						buf.append(retfield.toString() + separator);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return buf.toString();
		} else {
			return "no ordered methods";
		}
	}

	/**
	 * @return
	 * 	String.
	 */
	public final String print() {
		StringBuffer buf = new StringBuffer();
		if (methods.size() > 0) {
			return printOrdered();
		}
		// buf.append(olname);
		Method[] getsMths = objPrint.getClass().getDeclaredMethods();
		for (int i = 0; i < getsMths.length; i++) {
			String methName = getsMths[i].getName();
			if (methName.startsWith("get")) {
				if (exclusionMap.containsKey(methName)) {
					if (exclusionMap.get(methName) == 1) {
						continue;
					}
				}
				try {
					// log.debug("print: using method "+methName);
					String field = methName.substring(3);
					Object retfield = getsMths[i]
							.invoke(objPrint, (Object[]) null);
					if (retfield != null) {
						buf.append(field + "=" + retfield.toString()
								+ separator);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.clear();
		return buf.toString();
	}
}
