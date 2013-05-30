/*
 * Created on Aug 21, 2006 by formica
 *
 */
package atlas.query.pagination;

import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.ejb.QueryImpl;
import org.jboss.logging.Logger;

/**
 * @author formica Aug 21, 2006
 */
public class PageIterator<T> implements Iterator<T> {

	public static Logger log = Logger.getLogger(PageIterator.class.getName());

	protected boolean DRIVERISSCROLLABLE = false;

	private static final int MAX_PAGES = 1000;

	protected int pageSize = 5000;

	protected int pageNumber = 0;

	protected int totalElements = 0;
	
	private int ielement = 0;

	private ScrollableResults scrollableResults;

	private org.hibernate.Query hbQuery;

	private EntityManager _em = null;
	
	public PageIterator(javax.persistence.Query q, EntityManager em, boolean isscrollable) {

		_em = em;
		this.DRIVERISSCROLLABLE = isscrollable;
		this.hbQuery = getHbQuery(q);
		if (this.DRIVERISSCROLLABLE) {
			initScroll();
		} else {
			initList();
		}
	}

	private org.hibernate.Query getHbQuery(Query q) {

		QueryImpl<?> hs = (QueryImpl<?>) q;
		org.hibernate.Query _hbQuery = hs.getHibernateQuery();
		return _hbQuery;
	}

	private void initScroll() {
		scrollableResults = hbQuery.setCacheMode(CacheMode.GET)
			.setMaxResults(MAX_PAGES*pageSize).scroll(ScrollMode.FORWARD_ONLY);
		totalElements = MAX_PAGES*pageSize;
		log.debug("total elements in initScroll fixed to "+totalElements);
		log.debug("scrollable result in initScroll "+scrollableResults.toString());
	}

	private void initList() {

		scrollableResults = hbQuery.setCacheMode(CacheMode.GET)
			.setMaxResults(4*pageSize).scroll(ScrollMode.FORWARD_ONLY);
		totalElements = calculateTotalElementsByList();
		log.debug("total elements in initList fixed to "+totalElements);
		log.debug("scrollable result in initList "+scrollableResults.toString());

		determineElements();
	}

	private void determineElementsByScroll() throws HibernateException {
		scrollableResults.scroll(this.pageSize);
	}


	private void determineElements() throws HibernateException {

		if (this.DRIVERISSCROLLABLE) {
			determineElementsByScroll();
		} else
			System.err.println("Cannot determine elements");
	}

	private int calculateTotalElementsByList() throws HibernateException {
		return hbQuery.list().size();
	}

	// It does not work in Oracle either...I get a OutOfMemoryError
	// This is very bad for MySQL : should not be used !
	@SuppressWarnings("unused")
	private int calculateTotalElementsByCursor() throws HibernateException {
		ScrollableResults tmp = hbQuery.setCacheMode(CacheMode.GET).scroll(ScrollMode.SCROLL_INSENSITIVE);
		tmp.last();
		return tmp.getRowNumber(); 	
	}


	@SuppressWarnings("unused")
	private int getNextPageNumber() {
		return getPageNumber() + 1;
	}

	private int getPageSize() {
		return pageSize;
	}

	private int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @return the ielement
	 */
	public int getIelement() {
		return ielement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		log.debug("hasNext i "+ielement+" pagesize "+getPageSize());
		log.debug("hasNext scrollable row number "+scrollableResults.getRowNumber());

		if (ielement % getPageSize() == 0) {
			_em.clear();
		}
		if (!scrollableResults.next()) {
//			_em.close();
			log.debug("scrollable has not next");
			return false;
		} else
			return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@SuppressWarnings("unchecked")
	public T next() {
		ielement++;
		return (T)scrollableResults.get(0);
	}

	public T getNextUntilIndex(int index) {
		T obj = null;
		if (index>ielement) {
			while (index > ielement) {
				if (hasNext()) {
					 obj = next();
				}
			}
		}
		return obj;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		// TODO Auto-generated method stub

	}

}
