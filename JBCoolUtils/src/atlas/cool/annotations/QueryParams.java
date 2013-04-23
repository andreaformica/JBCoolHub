/**
 * 
 */
package atlas.cool.annotations;

/**
 * @author formica
 *
 */
public class QueryParams {

	private String qryname;
	private String[] params;
	/**
	 * @param qryname
	 * @param params
	 */
	public QueryParams(String qryname, String[] params) {
		super();
		this.qryname = qryname;
		this.params = params;
	}
	/**
	 * @return the qryname
	 */
	public String getQryname() {
		return qryname;
	}
	/**
	 * @return the params
	 */
	public String[] getParams() {
		return params;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Query "+qryname+" has n= "+((params != null) ? params.length : 0) + " parameters";
	}

	
}
