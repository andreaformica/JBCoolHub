/**
 * 
 */
package atlas.query.pagination;

import java.util.AbstractList;
import java.util.Iterator;

/**
 * This class can be used as a List for retrieval of very long datasets
 * It uses an iterator which is loading data using findCoolList range queries.
 * 
 * @author formica
 * @since  Jun 04, 2013
 */
public class HibernateQueryList<T> extends AbstractList<T> implements Iterable<T> {

	private Iterator<T> pageIterator = null;
	
	public HibernateQueryList(Iterator<T> pageiterator) {
		this.pageIterator = pageiterator;
	}
	
	public Iterator<T> iterator() {
		return pageIterator;
	}
	
	public int size() {
		if (pageIterator instanceof QueryListIterator<?>) {
			return ((QueryListIterator<T>)pageIterator).size;
		} else
			return 0;
	}

	@Override
	public T get(int index) {
		return ((QueryListIterator<T>)pageIterator).getNextUntilIndex(index);
	}
}
