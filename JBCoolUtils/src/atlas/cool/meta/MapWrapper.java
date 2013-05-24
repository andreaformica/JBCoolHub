/**
 * 
 */
package atlas.cool.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author formica
 *
 */
public class MapWrapper {
	@XmlElement
	public List<MapEntry>column;
}
