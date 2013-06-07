/**
 * 
 */
package atlas.cool.annotations;

/**
 * @author formica
 * 
 */
public class QueryParams {

	/**
	 * 
	 */
	private String qryname;
	/**
	 * 
	 */
	private String[] params;

	/**
	 * @param pqryname
	 * 	The query name
	 * @param pparams
	 * 	The parameters names.
	 */
	public QueryParams(final String pqryname, final String[] pparams) {
		super();
		this.qryname = pqryname;
		this.params = pparams;
	}

	/**
	 * @return 
	 * 	the query name.
	 */
	public final String getQryname() {
		return qryname;
	}

	/**
	 * @return the params
	 */
	public final String[] getParams() {
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		// TODO Auto-generated method stub
		return "Query " + qryname + " has n= "
				+ ((params != null) ? params.length : 0) + " parameters";
	}

}
