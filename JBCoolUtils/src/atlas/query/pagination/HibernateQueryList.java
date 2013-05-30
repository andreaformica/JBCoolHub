/*
 * Created on Aug 21, 2006 by formica
 *
 */
package atlas.query.pagination;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Iterator;

/**
 * @author formica
 * Aug 21, 2006
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
		if (pageIterator instanceof PageIterator<?>) {
			return ((PageIterator<T>)pageIterator).totalElements;
		} else
			return 0;
	}

	@Override
	public T get(int index) {
		return ((PageIterator<T>)pageIterator).getNextUntilIndex(index);
	}
}
