package atlas.connection.dao;

import java.util.List;
import java.util.Map;

import atlas.cool.exceptions.CoolIOException;
import atlas.query.pagination.HibernateQueryList;

/**
 * @author formica
 *
 */
public interface CoolRepositoryDAO {

	/**
	 * @param qryname
	 * @param params
	 * @return
	 * @throws CoolIOException
	 */
	List<?> findCoolList(String qryname, Object[] params)
			throws CoolIOException;

	/**
	 * @param qryname
	 * @param params
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws CoolIOException
	 */
	List<?> findCoolListByRange(String qryname, Object[] params,
			int firstResult, int maxResults) throws CoolIOException;

	/**
	 * @param domain
	 * @param qryname
	 * @param params
	 * @param orderbyParams
	 * @return
	 * @throws CoolIOException
	 */
	List<?> findCoolList(Object domain, String qryname, Object[] params,
			Map<String, Boolean> orderbyParams) throws CoolIOException;

	/**
	 * @param domain
	 * @param qryname
	 * @param params
	 * @param firstResult
	 * @param maxResults
	 * @param orderbyParams
	 * @return
	 * @throws CoolIOException
	 */
	List<?> findCoolListByRange(Object domain, String qryname, Object[] params,
			int firstResult, int maxResults, Map<String, Boolean> orderbyParams)
			throws CoolIOException;

	/**
	 * @param clazz
	 * @param pk
	 * @return
	 * @throws CoolIOException
	 */
	<T> T findObj(Class<T> clazz, Object pk) throws CoolIOException;

	/**
	 * @param clazz
	 * @param qryname
	 * @param params
	 * @return
	 * @throws CoolIOException
	 */
	<T> HibernateQueryList<T> findCoolList(Class<T> clazz, String qryname,
			Object[] params) throws CoolIOException;

}