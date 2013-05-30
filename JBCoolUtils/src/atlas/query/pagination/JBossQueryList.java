/**
 * 
 */
package atlas.query.pagination;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author formica
 *
 */
public class JBossQueryList<T> extends AbstractCollection<T> implements
		Iterable<T> {

	private Iterator<T> pageIterator = null;

	
	/**
	 * @param pageIterator
	 */
	public JBossQueryList(Iterator<T> pageIterator) {
		super();
		this.pageIterator = pageIterator;
	}

	@Override
	public Iterator<T> iterator() {
		return pageIterator;
	}

	@Override
	public int size() {
//		if (pageIterator instanceof PageBeanIterator<?>) {
//			return ((PageBeanIterator<T>)pageIterator).totalElements;
//		} else if (pageIterator instanceof JBossValueListIterator<?>) {
//			return ((JBossValueListIterator<T>)pageIterator).totalElements;
//		} else if (pageIterator instanceof JBossIovPagination<?>) {
//			return ((JBossIovPagination<T>)pageIterator).totalElements;
		if (pageIterator instanceof JBossValueListIterator<?>) {
			return ((JBossValueListIterator<T>)pageIterator).totalElements;		
		} else
			return 0;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#add(java.lang.Object)
	 */
	@Override
	public boolean add(T o) {
		return super.add(o);
	}

}
