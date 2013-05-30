/**
 * 
 */
package atlas.query.pagination;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;


import atlas.cool.exceptions.CoolQueryException;

/**
 * @author formica
 * 
 */
public class JBossValueListIterator<T> implements Iterator<T> {


	@Inject
	private Logger log;

	protected static final int MAX_PAGES = 1000;

	protected int pageSize = 1;

	protected int pageNumber = 0;

	protected int totalElements = 0;

	protected int ielement = 0;

	protected List<?> scrollableResults;

	protected Iterator<?> iresult = null;

	protected T _lastObj = null;

	protected Object _bean;

	protected Method _method = null;

	protected Object[] methodparams;

	/**
	 * The method that the iterator uses for the access to POJOs needs as last
	 * argument either null (retrieve a list of simple objects with no
	 * collections loaded) or a POJO (to fetch the full object with collections)
	 * 
	 * @param bean
	 * @param method
	 * @param params
	 */
	public JBossValueListIterator(Object bean, Method method, Object[] params) {

		_bean = bean;
		_method = method;
		methodparams = params;
		for (int i = 0; i < params.length; i++) {
			log.info("parameter " + i + " in constructor: " + methodparams[i]);
		}
		initScroll();
	}

	protected void initScroll() {
		try {
			determineElements();
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void determineElements() throws CoolQueryException {
		try {
			if (pageNumber > MAX_PAGES)
				throw new CoolQueryException("Page number exceeded "
						+ pageNumber);
			Integer first = ((Integer) methodparams[methodparams.length - 1]);
			log.info("Loading entities from " + _bean.toString());
			log.info("       using first " + first);
			log.info("       and method  " + _method.getName());

			scrollableResults = (List<?>) _method.invoke(_bean, methodparams);
			if (scrollableResults == null || scrollableResults.size() == 0) {
				log.info("Retrieved empty list ");
				totalElements = 0;
			} else {
				totalElements = scrollableResults.size();
				log.info("Retrieved list of " + totalElements
						+ " POJOs for page " + pageNumber);
				// Increment the page number
				pageNumber++;
				// Get the iterator on the list
				iresult = scrollableResults.iterator();
			}
		} catch (IllegalArgumentException e) {
			throw new CoolQueryException(e);
		} catch (IllegalAccessException e) {
			throw new CoolQueryException(e);
		} catch (InvocationTargetException e) {
			throw new CoolQueryException(e);
		}
	}

	public boolean hasNext() {
		try {
			if (ielement > (totalElements * MAX_PAGES)) {
				return false;
			} else {
				ielement++;
				if (!iresult.hasNext()) {
					// Clean scrollable
					scrollableResults.clear();
					scrollableResults = null;
					_lastObj = null;
					// Try to retrieve more data
					// get the first result index
					Integer value_index = (Integer) methodparams[methodparams.length - 1];
					if (value_index == null)
						value_index = new Integer(totalElements);
					else
						value_index += totalElements;
					// Now determine elements using the new parameter
					methodparams[methodparams.length - 1] = value_index;
					try {
						// This will change iresult
						determineElements();
						if (totalElements == 0)
							return false;
					} catch (CoolQueryException e) {
						log.info("Exception retrieving list");
						// e.printStackTrace();
						return false;
					}
				}
				if (_lastObj != null)
					remove();
				return iresult.hasNext();
			}
		} catch (Exception e) {
			log.fine("hasNext has catched an internal exception "
					+ e.getMessage());
			log.fine("hasNext return false");
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public T next() {
		try {
			_lastObj = (T) iresult.next();
			return _lastObj;
		} catch (Exception e) {
			log.fine("next() method catched exception,returning null obj!");
			return null;
		}
	}

	public void remove() {
		iresult.remove();
	}

}
