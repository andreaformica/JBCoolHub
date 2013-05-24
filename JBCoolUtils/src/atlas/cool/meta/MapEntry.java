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

	@XmlAttribute(name="name")	
	String column;
	@XmlElement 
	String value;
	/**
	 * 
	 */
	public MapEntry() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param column
	 * @param value
	 */
	public MapEntry(String column, String value) {
		super();
		this.column = column;
		this.value = value;
	}
	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}
	/**
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
