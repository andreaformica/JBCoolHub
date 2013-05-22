/**
 * 
 */
package atlas.cool.interceptors;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.EmptyInterceptor;

/**
 * @author formica
 * 
 */
public class ApplyOrderByInterceptor extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2517085568705192529L;

	private static final String orderby = "";

	public final static Logger log = Logger
			.getLogger(ApplyOrderByInterceptor.class.getName());

	/**
	 * 
	 */
	public ApplyOrderByInterceptor() {
		super();
	}

	protected SessionContext getContext() {
		try {
			InitialContext ic = new InitialContext();
			SessionContext sctxLookup = (SessionContext) ic
					.lookup("java:comp/EJBContext");
			return sctxLookup;
		} catch (NamingException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onPrepareStatement(java.lang.String)
	 */
	@Override
	public String onPrepareStatement(String sql) {
		log.log(Level.FINE, "Interceptor received prepared statement : " + sql);

		String orderbyclause = null;
		if (WebRestContextHolder.containsKey("OrderBy")) {
			orderbyclause = (String) WebRestContextHolder.get("OrderBy");
		}
		if (orderbyclause != null) {
			// check if order by clause contains correct col fields
			if (!isOrderByCorrect(sql, orderbyclause)) {
				orderbyclause = "";
				log.log(Level.FINE,
						"Interceptor is removing orderby clause...wrong columns...");
			} else {
				// remove previous order by
//				if (sql.contains("order by")) {
//					int endIndex = sql.indexOf("order by");
//					String newquerySql = sql.substring(0, endIndex);
//					sql = newquerySql;
//				}
				sql = sql + (" order by " + orderbyclause);
			}
			log.log(Level.INFO, "Interceptor returning prepared statement : "
					+ sql);
		}
		return super.onPrepareStatement(sql);
	}

	protected Boolean isOrderByCorrect(String sql, String orderby) {
		log.log(Level.FINE,
				"Interceptor is checking orderby clause "+orderby);
		
		String[] orderbylist = orderby.split(",");
		if (orderbylist.length==0) {
			orderbylist[0] = new String(orderby);
		}
		log.log(Level.FINE,
				"Interceptor found order by list of  "+orderbylist.length);
		Boolean retval = true;
		for (int i = 0; i < orderbylist.length; i++) {
			String colname = (orderbylist[i] != null) ? orderbylist[i]
					.split(" ")[0] : "none";
			if (!colname.equals("none")) {
				if (sql.contains(colname) || sql.contains(colname.toLowerCase())) {
					log.log(Level.FINE,
							"Interceptor found column  "+colname+" in "+sql);
					retval = (retval & true);
				} else {
					log.log(Level.FINE,
							"Interceptor did NOT find column  "+colname+" in "+sql);
					retval = (retval & false);
				}
			}
		}
		return retval;
	}
}
