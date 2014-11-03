/**
 * 
 */
package atlas.cool.meta;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author formica
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class HeaderMapEntry {

	private Map<String, Object> col;
	
	
	public void setCol(Map<String, Object> col) {
		this.col = col;
	}


	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.MapObjAdapter.class)
	public final Map<String, Object> getCol() {
		return col;
	}

}
