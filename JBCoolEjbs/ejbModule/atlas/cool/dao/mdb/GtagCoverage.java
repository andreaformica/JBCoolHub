/**
 * 
 */
package atlas.cool.dao.mdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author formica
 *
 */
public class GtagCoverage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8817862807431000865L;
	
	private String globaltagname;
	private List<String> destAddrs;

	/**
	 * @param globaltagname
	 * @param destAddrs
	 */
	public GtagCoverage(final String globaltagname, final List<String> destAddrs) {
		super();
		this.globaltagname = globaltagname;
		this.destAddrs = destAddrs;
		if (destAddrs == null) {
			this.destAddrs = new ArrayList<String>();
			this.destAddrs.add("andrea.formica1971@gmail.com");
		}
	}

	/**
	 * @return the globaltagname
	 */
	public String getGlobaltagname() {
		return globaltagname;
	}

	/**
	 * @param globaltagname the globaltagname to set
	 */
	public void setGlobaltagname(String globaltagname) {
		this.globaltagname = globaltagname;
	}

	/**
	 * @return the destAddrs
	 */
	public List<String> getDestAddrs() {
		return destAddrs;
	}

	/**
	 * @param destAddrs the destAddrs to set
	 */
	public void setDestAddrs(List<String> destAddrs) {
		this.destAddrs = destAddrs;
	}
	
}
