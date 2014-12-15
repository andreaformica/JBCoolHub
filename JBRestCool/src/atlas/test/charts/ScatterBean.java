/**
 * 
 */
package atlas.test.charts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;

/**
 * @author formica
 *
 */
@Model
public class ScatterBean {

	private final Map<Double, Double> serie = new HashMap<Double, Double>();

	/**
	 * @return the serie
	 */
	public Map<Double, Double> getSerie() {
		return serie;
	}

	/**
	 * 
	 */
	@PostConstruct
	public void initialize() {

		serie.put(1.2, 3.4);
		serie.put(4.1, 6.5);
		serie.put(3.3, 8.4);
		serie.put(12.2, 2.1);
		serie.put(0.2, 1.8);
		serie.put(9.5, 5.9);
		serie.put(8.8, 6.8);
		serie.put(3.5, 7.2);
	}
}