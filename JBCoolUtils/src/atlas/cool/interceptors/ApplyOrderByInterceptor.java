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

	private final Logger log = Logger.getLogger(ApplyOrderByInterceptor.class
			.getName());

	/**
	 * 
	 */
	public ApplyOrderByInterceptor() {
		super();
	}

	/**
	 * @return Session Context.
	 */
	protected final SessionContext getContext() {
		try {
			final InitialContext ic = new InitialContext();
			final SessionContext sctxLookup = (SessionContext) ic
					.lookup("java:comp/EJBContext");
			return sctxLookup;
		} catch (final NamingException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onPrepareStatement(java.lang.String)
	 */
	@Override
	public final String onPrepareStatement(String sql) {
		log.log(Level.FINE, "Interceptor received prepared statement : " + sql);

		String orderbyclause = null;
		if (WebRestContextHolder.containsKey("OrderBy")) {
			orderbyclause = (String) WebRestContextHolder.get("OrderBy");
		}
		if (orderbyclause != null) {
			// check if order by clause contains correct col fields
			if (!isOrderByCorrect(sql, orderbyclause)) {
				orderbyclause = "";
				log.log(Level.FINE, "Interceptor is removing orderby clause"
						+ "...wrong columns!");
			} else {
				// remove previous order by
				// if (sql.contains("order by")) {
				// int endIndex = sql.indexOf("order by");
				// String newquerySql = sql.substring(0, endIndex);
				// sql = newquerySql;
				// }
				sql = sql + " order by " + orderbyclause;
			}
			log.log(Level.INFO, "Interceptor returning prepared statement : "
					+ sql);
		}
		return super.onPrepareStatement(sql);
	}

	/**
	 * @param sql
	 *            The sql statement.
	 * @param porderby
	 *            The order by clause.
	 * @return boolean.
	 */
	protected final Boolean isOrderByCorrect(final String sql,
			final String porderby) {
		log.log(Level.FINE, "Interceptor is checking orderby clause "
				+ porderby);

		final String[] orderbylist = porderby.split(",");
		if (orderbylist.length == 0) {
			orderbylist[0] = porderby;
		}
		log.log(Level.FINE, "Interceptor found order by list of  "
				+ orderbylist.length);
		Boolean retval = true;
		for (int i = 0; i < orderbylist.length; i++) {
			final String colname = orderbylist[i] != null ? orderbylist[i]
					.split(" ")[0] : "none";
			if (!colname.equals("none")) {
				if (sql.contains(colname)
						|| sql.contains(colname.toLowerCase())) {
					log.log(Level.FINE, "Interceptor found column  " + colname
							+ " in " + sql);
					retval = retval & true;
				} else {
					log.log(Level.FINE, "Interceptor did NOT find column  "
							+ colname + " in " + sql);
					retval = retval & false;
				}
			}
		}
		return retval;
	}
}
