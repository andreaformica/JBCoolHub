/**
 * 
 */
package atlas.cool.rest.post;

import javax.ws.rs.FormParam;

/**
 * @author formica
 *
 */
public class GlobalTagForm {

	@FormParam("gtagname")	
	private String globalTagName;

	
	/**
	 * 
	 */
	public GlobalTagForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param globalTagName
	 */
	public GlobalTagForm(final String globalTagName) {
		super();
		this.globalTagName = globalTagName;
	}

	/**
	 * @return the globalTagName
	 */
	public String getGlobalTagName() {
		return globalTagName;
	}

	/**
	 * @param globalTagName the globalTagName to set
	 */
	public void setGlobalTagName(final String globalTagName) {
		this.globalTagName = globalTagName;
	}
	
	
}
