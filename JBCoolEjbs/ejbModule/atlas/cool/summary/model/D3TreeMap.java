/**
 * 
 */
package atlas.cool.summary.model;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author formica
 *
 */
@XmlRootElement
public class D3TreeMap {

	private String name;
	private Set<?> children;

	
	
	/**
	 * 
	 */
	public D3TreeMap() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 * @param children
	 */
	public D3TreeMap(String name, Set<?> children) {
		super();
		this.name = name;
		this.children = children;
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
	public Set<?> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(Set<?> children) {
		this.children = children;
	}

}
