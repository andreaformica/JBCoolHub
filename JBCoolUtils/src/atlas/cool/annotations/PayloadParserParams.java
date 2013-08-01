/**
 * 
 */
package atlas.cool.annotations;

/**
 * @author formica
 * 
 */
public class PayloadParserParams {

	/**
	 * 
	 */
	private String schemaname;
	/**
	 * 
	 */
	private String foldername;

	
	/**
	 * @param schemaname
	 * @param foldername
	 */
	public PayloadParserParams(final String schemaname, final String foldername) {
		super();
		this.schemaname = schemaname;
		this.foldername = foldername;
	}


	/**
	 * @return the schemaname
	 */
	public String getSchemaname() {
		return schemaname;
	}


	/**
	 * @return the foldername
	 */
	public String getFoldername() {
		return foldername;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		// TODO Auto-generated method stub
		return "Plugin for " + schemaname + " folder "
				+ foldername;
	}

}
