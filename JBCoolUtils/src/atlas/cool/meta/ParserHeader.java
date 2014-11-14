/**
 * 
 */
package atlas.cool.meta;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author formica
 *
 */
@XmlRootElement
public class ParserHeader {

	@XmlTransient
	private String name;
	
	private Set<HeaderMapEntry> iovList;

	
	/**
	 * 
	 */
	public ParserHeader() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 * @param children
	 */
	public ParserHeader(String name, Set<HeaderMapEntry> children) {
		super();
		this.name = name;
		this.iovList = children;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the children
	 */
	public Set<HeaderMapEntry> getIovList() {
		return iovList;
	}
	/**
	 * @param children the children to set
	 */
	public void setIovList(Set<HeaderMapEntry> children) {
		this.iovList = children;
	}

}
