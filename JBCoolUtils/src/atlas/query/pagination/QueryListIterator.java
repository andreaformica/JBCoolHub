/**
 * 
 */
package atlas.query.pagination;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.exceptions.CoolQueryException;

/**
 * @author formica
 * 
 * @param <E>
 */
public class QueryListIterator<E> implements Iterator<E> {

	/**
	 * Logger.
	 */
	private Logger log = Logger.getLogger(QueryListIterator.class.getName());

	/**
	 * Maximum number of pages.
	 */
	protected static final int MAX_PAGES = 1000;

	/**
	 * Number of rows retrieved per page.
	 */
	private static int pageSize = 2000;

	/**
	 * Page counter.
	 */
	private int pageNumber = 0;

	/**
	 * Element counter.
	 */
	private int totalElements = 0;

	/**
	 * ResultSet size.
	 */
	private int size = 0;

	/**
	 * Iterator counter.
	 */
	private int ielement = 0;

	/**
	 * 
	 */
	private int first = 0;

	/**
	 * Container of the ResultSet.
	 */
	private List<?> scrollableResults;

	/**
	 * 
	 */
	private Iterator<?> iresult = null;

	/**
	 * 
	 */
	private E lastObj = null;

	/**
	 * The DAO for DB access.
	 */
	private CoolRepositoryDAO daobean;

	/**
	 * The query name.
	 */
	private String query = null;

	/**
	 * The method parameters.
	 */
	private Object[] methodparams;

	/**
	 * @param bean
	 *            The dao bean.
	 * @param pquery
	 *            The query string.
	 * @param pmethodparams
	 *            The method parameters.
	 * 
	 */
	public QueryListIterator(final CoolRepositoryDAO bean, final String pquery,
			final Object[] pmethodparams) {
		super();
		this.daobean = bean; // This will be a reference to the CoolRepository
								// session bean
		this.query = pquery;
		this.methodparams = pmethodparams;
		try {
			determineElements();
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Execute partial query and retrieve data.
	 * 
	 * @throws CoolQueryException
	 *             The Cool query Exception.
	 */
	protected final void determineElements() throws CoolQueryException {
		try {
			if (pageNumber > MAX_PAGES) {
				throw new CoolQueryException("Page number exceeded "
						+ pageNumber);
			}
			log.info("Loading entities from " + daobean.toString());
			log.info("       using first " + first);
			log.info("       and method  findCoolList ");

			scrollableResults = (List<?>) daobean.findCoolListByRange(query,
					methodparams, first, pageSize);
			if (scrollableResults == null || scrollableResults.size() == 0) {
				log.info("Retrieved empty list ");
				totalElements = 0;
			} else {
				totalElements = scrollableResults.size();
				log.info("Retrieved list of " + totalElements
						+ " POJOs for page " + pageNumber);
				// Get the iterator on the list
				iresult = scrollableResults.iterator();
			}
			if (first < getSize()) {
				// system is querying an old bunch of data
				pageNumber = first / pageSize;
			} else {
				// system is querying a new bunch of data
				size = (size + totalElements);
				// Increment the page number
				pageNumber++;
			}
		} catch (IllegalArgumentException e) {
			throw new CoolQueryException(e);
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public final boolean hasNext() {
		try {
			if (ielement > (pageSize * MAX_PAGES)) {
				return false;
			} else {
				ielement++;
				if (!iresult.hasNext()) {
					// Try to retrieve more data if ielement has reached the
					// page size
					if ((ielement % pageSize) > 1) {
						log.info("Cannot query next: " + ielement
								+ " is less than the total of " + totalElements);
						// return false;
					} else {
						log.info("Perform query next: " + ielement
								+ " is greater than the total of "
								+ totalElements);
						// Clean scrollable
						scrollableResults.clear();
						scrollableResults = null;
						lastObj = null;
						// get the first result index via ielement counter
						first += pageSize;
						// Now determine elements using the new parameter
						try {
							// This will change iresult
							determineElements();
							if (totalElements == 0) {
								return false;
							}
						} catch (CoolQueryException e) {
							log.info("Exception retrieving list");
							// e.printStackTrace();
							return false;
						}
					}
				}
				if (lastObj != null) {
					remove();
				}
				return iresult.hasNext();
			}
		} catch (Exception e) {
			log.fine("hasNext has catched an internal exception "
					+ e.getMessage());
			log.fine("hasNext return false");
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public final E next() {
		try {
			lastObj = (E) iresult.next();
//			if ((ielement - 1) % 1000 == 0) {
//				log.info("select next object..." + lastObj);
//			}
			return lastObj;
		} catch (Exception e) {
			log.fine("next() method catched exception,returning null obj!");
			return null;
		}
	}

	/**
	 * @param index
	 *            The index of the element.
	 * @return The object from the list.
	 */
	public final E getNextUntilIndex(final int index) {
		E obj = null;
		try {
			Integer ipage = index / pageSize; // should give the page number
			//Integer iline = index % pageSize;
			Integer iselpage = ielement / pageSize;
			if (ipage == iselpage) {
				log.info("get object at index " + index + " ielement="
						+ ielement);
				obj = (E) scrollableResults.get(index - first);
			} else {
				// query the correct page
				first = pageSize * ipage;
				ielement = first;
				pageNumber = ipage;
				determineElements();
				log.info("get object after new query at index " + index
						+ " ielement=" + ielement);
				obj = (E) scrollableResults.get(index - first);
			}
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// iresult.remove();
	}

	/**
	 * @return The size.
	 */
	public final int getSize() {
		return size;
	}

}
