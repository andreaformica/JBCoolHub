/**
 * 
 */
package atlas.cool.rest.utils;

/**
 * @author formica
 *
 * @param <T>
 */
public class PrintPojo<T> {

	/**
	 * 
	 */
	private MyPrinterHandler<T> handler = null;
	
	/**
	 * 
	 */
	private T objPrint;

	/**
	 * @param obj
	 * 	The pojo to print.
	 */
	public PrintPojo(final T obj) {
		super();
		this.objPrint = obj;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		handler = new MyPrinterHandler<T>(objPrint);
		return handler.print();
	}
}
