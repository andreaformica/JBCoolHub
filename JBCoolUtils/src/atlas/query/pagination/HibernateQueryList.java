/**
 * 
 */
package atlas.query.pagination;

import java.util.AbstractList;
import java.util.Iterator;

/**
 * This class can be used as a List for retrieval of very long datasets It uses
 * an iterator which is loading data using findCoolList range queries.
 * 
 * @author formica
 * @since Jun 04, 2013
 * 
 * @param <T>
 *            The pojo.
 * 
 */
public class HibernateQueryList<T> extends AbstractList<T> implements
		Iterable<T> {

	/**
	 * 
	 */
	private Iterator<T> pageIterator = null;

	/**
	 * @param pageiterator
	 *            The iterator.
	 */
	public HibernateQueryList(final Iterator<T> pageiterator) {
		this.pageIterator = pageiterator;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractList#iterator()
	 */
	public final Iterator<T> iterator() {
		return pageIterator;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	public final int size() {
		if (pageIterator instanceof QueryListIterator<?>) {
			return ((QueryListIterator<T>) pageIterator).getSize();
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public final T get(final int index) {
		return ((QueryListIterator<T>) pageIterator).getNextUntilIndex(index);
	}
}
