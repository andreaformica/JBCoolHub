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
 * This iterator loads data from DB using the generic DAO CoolRepository.
 * 
 * @author formica
 * 
 */
public class QueryListIterator<E> implements Iterator<E> {

	private Logger log = Logger.getLogger(QueryListIterator.class.getName());

	protected static final int MAX_PAGES = 1000;

	protected int pageSize = 2000;

	protected int pageNumber = 0;

	protected int totalElements = 0;

	protected int size = 0;

	protected int ielement = 0;

	protected int first = 0;

	protected List<?> scrollableResults;

	protected Iterator<?> iresult = null;

	protected E _lastObj = null;

	protected CoolRepositoryDAO _bean;

	protected String query = null;

	protected Object[] methodparams;

	/**
	 * @param _bean
	 * @param query
	 * @param methodparams
	 */
	public QueryListIterator(CoolRepositoryDAO _bean, String query,
			Object[] methodparams) {
		super();
		this._bean = _bean; // This will be a reference to the CoolRepository
							// session bean
		this.query = query;
		this.methodparams = methodparams;
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
			log.info("Loading entities from " + _bean.toString());
			log.info("       using first " + first);
			log.info("       and method  findCoolList ");

			scrollableResults = (List<?>) _bean.findCoolListByRange(query,
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
			if (first < size) {
				// system is querying an old bunch of data
				pageNumber = first/pageSize;
			} else {
				// system is querying a new bunch of data
				size += totalElements;
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
	public boolean hasNext() {
		try {
			if (ielement > (pageSize * MAX_PAGES)) {
				return false;
			} else {
				ielement++;
				if (!iresult.hasNext()) {
					// Try to retrieve more data if ielement has reached the
					// page size
					if ((ielement % pageSize) > 1) {
						log.info("Cannot query next: "+ielement+" is less than the total of "+totalElements);
						//return false;
					} else {
						log.info("Perform query next: "+ielement+" is greater than the total of "+totalElements);
						// Clean scrollable
						scrollableResults.clear();
						scrollableResults = null;
						_lastObj = null;
						// get the first result index via ielement counter
						first += pageSize;
						// Now determine elements using the new parameter
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		try {
			_lastObj = (E) iresult.next();
			if ((ielement-1)%1000 == 0) 
				log.info("select next object..."+_lastObj);
			return _lastObj;
		} catch (Exception e) {
			log.fine("next() method catched exception,returning null obj!");
			return null;
		}
	}

	public E getNextUntilIndex(int index) {
		E obj = null;
		try {
			Integer ipage = index / pageSize; // should give the page number
			Integer iline = index % pageSize;
			Integer iselpage = ielement / pageSize;
			if (ipage == iselpage) {
				log.info("get object at index "+index+" ielement="+ielement);
				obj = (E) scrollableResults.get(index-first);
			} else {
				// query the correct page
				first = pageSize * ipage;
				ielement = first;
				pageNumber = ipage;
				determineElements();
				log.info("get object after new query at index "+index+" ielement="+ielement);
				obj = (E) scrollableResults.get(index-first);
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
		//iresult.remove();
	}

}
