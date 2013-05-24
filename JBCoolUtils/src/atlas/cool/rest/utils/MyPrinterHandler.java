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
 */
public class MyPrinterHandler<T> {


	String separator = " | ";
	T _obj;
	Map<String, Integer> exclusionMap = new HashMap<String, Integer>();
	Vector<Method> methods = new Vector<Method>();

	/**
	 * @param _obj
	 */
	public MyPrinterHandler(final T _obj) {
		this._obj = _obj;
	}

	/**
	 * @param _obj
	 * @param separator
	 */
	public MyPrinterHandler(final T _obj, final String separator) {
		this._obj = _obj;
		this.separator = separator;
	}

	public void exclude(final String methname, final Integer exclusion) {
		if (exclusionMap.containsKey(methname)) {
			Integer value = exclusionMap.get(methname);
			if (value != exclusion) {
				exclusionMap.put(methname, exclusion);
			}
		} else {
			exclusionMap.put(methname, exclusion);
		}
	}

	public void add(final Method meth) {
		methods.add(meth);
	}

	private void clear() {
		exclusionMap.clear();
	}

	private String printOrdered() {
		if (methods.size() > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < methods.size(); i++) {
				try {
					//log.debug("printOrdered: using method "+methods.elementAt(i).getName());
					Object retfield = methods.elementAt(i).invoke(_obj, (Object[]) null);
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

	public String print() {
		StringBuffer buf = new StringBuffer();
		if (methods.size() > 0) {
			return printOrdered();
		}
		// buf.append(olname);
		Method[] _gets = _obj.getClass().getDeclaredMethods();
		for (int i = 0; i < _gets.length; i++) {
			String methName = _gets[i].getName();
			if (methName.startsWith("get")) {
				if (exclusionMap.containsKey(methName)) {
					if (exclusionMap.get(methName) == 1) {
						continue;
					}
				}
				try {
					//log.debug("print: using method "+methName);
					String field = methName.substring(3);
					Object retfield = _gets[i].invoke(_obj, (Object[]) null);
					if (retfield != null) {
						buf.append(field + "=" + retfield.toString() + separator);
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
