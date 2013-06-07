/**
 * 
 */
package atlas.cool.query.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;

import atlas.cool.exceptions.CoolQueryException;

/**
 * @author formica
 * 
 */
public class QueryTools {

	private static final Pattern badQueryPattern = Pattern
			.compile("[^\\p{ASCII}]*");

	private static final String ORDER_BY_CLAUSE_START = " order by ";

	/**
	 * 
	 */
	public QueryTools() {
		// TODO Auto-generated constructor stub
	}

	protected static String getNamedQueryString(EntityManager em,
			String queryName) throws SQLException {
		Query tmpQuery = em.createNamedQuery(queryName);
		SQLQuery sqlQuery = tmpQuery.unwrap(SQLQuery.class);
		String queryString = sqlQuery.getQueryString();
		if (badQueryPattern.matcher(queryString).matches()) {
			throw new SQLException("Bad query string.");
		}
		return queryString;
	}

	/**
	 * @param em
	 * 	The Entity Manager.
	 * @param domain
	 * 	The domain object.
	 * @param queryName
	 * 	The query name.
	 * @param params
	 * 	The query parameters.
	 * @param columnNames
	 * 	The column names.
	 * @return
	 * 	A Query.
	 * @throws SQLException
	 * 	Sql Exception.
	 * @throws CoolQueryException
	 * 	Cool Exception.
	 */
	public static Query getNamedQueryOrderedBy(
			final EntityManager em, final Object domain,
			final String queryName, final Object[] params, 
			final Map<String, Boolean> columnNames)
			throws SQLException, CoolQueryException {

		StringBuilder sb = new StringBuilder();
		String querySql = getNamedQueryString(em, queryName);
		if (domain != null && columnNames != null) {

			int limit = columnNames.size();
			int i = 0;
			if (limit != 0) {
				// sb.append(ORDER_BY_CLAUSE_START);

				for (String columnName : columnNames.keySet()) {
					String fieldName = isColumnName(domain, columnName);
					if (fieldName.isEmpty()) {
						continue;
					}
					sb.append(fieldName);

					if (columnNames.get(columnName)) {
						sb.append(" ASC");
					} else {
						sb.append(" DESC");
					}

					if (i != (limit - 1)) {
						sb.append(", \n");
					}
				}
				// remove previous order by
				if (querySql.contains("order by")) {
					int endIndex = querySql.indexOf("order by");
					String newquerySql = querySql.substring(0, endIndex);
					querySql = newquerySql;
				}
			}
		}

		Query jpaQuery = em.createNativeQuery(querySql + sb.toString());

		return jpaQuery;
	}

	/**
	 * @param domain
	 * 	The domain object.
	 * @param columnNames
	 * 	The columns names.
	 * @return
	 * 	The order by clause.
	 * @throws SQLException
	 * 	Sql Exception.
	 * @throws CoolQueryException
	 * 	Cool query Exception.
	 */
	public static String getOrderedBy(
			final Object domain,
			final Map<String, Boolean> columnNames) throws SQLException,
			CoolQueryException {

		StringBuilder sb = new StringBuilder();
		if (domain != null && columnNames != null) {

			int limit = columnNames.size();
			int i = 0;
			if (limit != 0) {
				// sb.append(ORDER_BY_CLAUSE_START);

				for (String columnName : columnNames.keySet()) {
					String fieldName = isColumnName(domain, columnName);
					if (fieldName.isEmpty()) {
						continue;
					}
					sb.append(fieldName);

					if (columnNames.get(columnName)) {
						sb.append(" ASC");
					} else {
						sb.append(" DESC");
					}

					if (i != (limit - 1)) {
						sb.append(", \n");
					}
					i++;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * @param domain
	 * 	The domain object.
	 * @param columnName
	 * 	The column name.
	 * @return
	 * 	The String with the columns.
	 */
	protected static String isColumnName(
			final Object domain, final String columnName) {
		Field[] fields = domain.getClass().getDeclaredFields();
		String fieldColumnName = "";
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Column) {
					Column column = (Column) annotation;
					String foundColumnName = "";
					if (column.name() != null && !column.name().isEmpty()) {
						foundColumnName = column.name();
						fieldColumnName = field.getName();
					}
					if (columnName.toUpperCase().equals(
							fieldColumnName.toUpperCase())) {
						return foundColumnName;
					}
				}
			}
		}
		return "";
	}

}
