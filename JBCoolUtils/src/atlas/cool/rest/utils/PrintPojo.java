/**
 * 
 */
package atlas.cool.rest.utils;

/**
 * @author formica
 *
 */
public class PrintPojo<T> {

	MyPrinterHandler<T> handler = null;
	
	T _obj;

	/**
	 * @param _obj
	 */
	public PrintPojo(T _obj) {
		super();
		this._obj = _obj;
	}
	
	public String toString() {
		handler = new MyPrinterHandler<T>(_obj);
		return handler.print();
	}
}
