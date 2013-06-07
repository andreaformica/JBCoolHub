/**
 * 
 */
package atlas.cool.meta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author formica
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class MapEntry {

	/**
	 * 
	 */
	@XmlAttribute(name = "name")
	private String column;
	/**
	 * 
	 */
	@XmlElement
	private String value;

	/**
	 * 
	 */
	public MapEntry() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pcolumn
	 * 	Column name.
	 * @param pvalue
	 * 	Column val.
	 */
	public MapEntry(final String pcolumn, final String pvalue) {
		super();
		this.column = pcolumn;
		this.value = pvalue;
	}

	/**
	 * @return the column
	 */
	public final String getColumn() {
		return column;
	}

	/**
	 * @param pcolumn
	 *            the column to set
	 */
	public final void setColumn(final String pcolumn) {
		this.column = pcolumn;
	}

	/**
	 * @return the value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * @param pvalue
	 *            the value to set
	 */
	public final void setValue(final String pvalue) {
		this.value = pvalue;
	}

}
