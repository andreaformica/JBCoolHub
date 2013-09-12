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

	@FormParam("checktype")	
	private String checkType;

	@FormParam("db")	
	private String db;

	
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

	/**
	 * @return the checkType
	 */
	public String getCheckType() {
		return checkType;
	}

	/**
	 * @param checkType the checkType to set
	 */
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	/**
	 * @return the db
	 */
	public String getDb() {
		return db;
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(String db) {
		this.db = db;
	}
	
	
}
