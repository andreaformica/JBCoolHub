/**
 * 
 */
package atlas.connection.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import atlas.cool.annotations.CoolQueryRepository;
import atlas.cool.annotations.QueryParams;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.exceptions.CoolQueryException;
import atlas.cool.query.tools.QueryTools;
import atlas.query.pagination.HibernateQueryList;
import atlas.query.pagination.QueryListIterator;

///////@////RequestScoped
//TODO Investigate is this could be SessionScoped...
/**
 * @author formica
 * 
 */
@Stateless
@Local(CoolRepositoryDAO.class)
public class CoolRepository implements CoolRepositoryDAO {

	/**
	 * 
	 */
//	@PersistenceContext(unitName="primary") : this one is defined in the
//  Resources class inside atlas.cool.rest.utils
	@Inject
	private EntityManager em;

	/**
	 * 
	 */
	@Inject
	private Logger log;

	/**
	 * 
	 */
	@Inject
	private CoolQueryRepository coolqry;

	/**
	 * 
	 */
	private final int maxFetchResults = 50000;

	/**
	 * 
	 */
	public CoolRepository() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.connection.dao.CoolRepositoryDAO#persist(java.lang.Object)
	 */
	@Override
	public void persist(final Object obj) throws CoolIOException {
		try {
			em.persist(obj);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.connection.dao.CoolRepositoryDAO#remove(java.lang.Object)
	 */
	@Override
	public void remove(final Object obj) throws CoolIOException {
		try {
			em.remove(obj);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.connection.dao.CoolRepositoryDAO#merge(java.lang.Object)
	 */
	@Override
	public void merge(final Object obj) throws CoolIOException {
		try {
			em.merge(obj);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see atlas.connection.dao.CoolRepositoryDAO#flush()
	 */
	@Override
	public void flush() throws CoolIOException {
		try {
			em.flush();
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	
	/* (non-Javadoc)
	 * @see atlas.connection.dao.CoolRepositoryDAO#commit()
	 */
	@Override
	public void commit() throws CoolIOException {
		try {
			em.getTransaction().commit();
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see atlas.connection.dao.CoolRepositoryDAO#clear()
	 */
	@Override
	public void clear() throws CoolIOException {
		try {
			em.clear();
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolRepositoryDAO#findObj(java.lang.Class,
	 * java.lang.Object)
	 */
	@Override
	public <T> T findObj(final Class<T> clazz, final Object pk)
			throws CoolIOException {
		try {
			T obj = em.find(clazz, pk);
			return obj;
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolRepositoryDAO#findCoolList(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.util.Map)
	 */
	@Override
	public List<?> findCoolList(final Object domain, final String qryname,
			final Object[] params, final Map<String, Boolean> orderbyParams)
			throws CoolIOException {
		try {
			return findCoolListByRange(domain, qryname, params, 0, 0,
					orderbyParams);
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolRepositoryDAO#findCoolListByRange(java.lang.Object,
	 * java.lang.String, java.lang.Object[], int, int, java.util.Map)
	 */
	@Override
	public List<?> findCoolListByRange(final Object domain,
			final String qryname, final Object[] params, final int firstResult,
			final int maxResults, final Map<String, Boolean> orderbyParams)
			throws CoolIOException {
		try {
			Query q = null;
			if (domain != null && orderbyParams != null) {
				q = QueryTools.getNamedQueryOrderedBy(em, domain, qryname,
						params, orderbyParams);
			} else {
				q = getQuery(qryname, params);
			}
			if (firstResult > 0) {
				q.setFirstResult(firstResult);
			}
			if (maxResults > 0) {
				q.setMaxResults(maxResults);
			} else {
				q.setMaxResults(maxFetchResults);
			}
			return q.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolRepositoryDAO#findCoolList(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public List<?> findCoolList(final String qryname, final Object[] params)
			throws CoolIOException {
		try {
			return findCoolListByRange(qryname, params, 0, 0);
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolRepositoryDAO#findCoolListByRange(java.lang.String,
	 * java.lang.Object[], int, int)
	 */
	@Override
	public List<?> findCoolListByRange(final String qryname,
			final Object[] params, final int firstResult, final int maxResults)
			throws CoolIOException {
		try {
			Query q = getQuery(qryname, params);
			if (firstResult > 0) {
				q.setFirstResult(firstResult);
			}
			if (maxResults > 0) {
				q.setMaxResults(maxResults);
			} else {
				q.setMaxResults(maxFetchResults);
			}
			List<?> results = q.getResultList();
			em.clear();
			return results;
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.connection.dao.CoolRepositoryDAO#findCoolList(java.lang.Class,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> HibernateQueryList<T> findCoolList(final Class<T> clazz,
			final String qryname, final Object[] params) throws CoolIOException {
		return new HibernateQueryList<T>(new QueryListIterator<T>(this,
				qryname, params));
	}

	/**
	 * @param qry
	 *            The query name.
	 * @param params
	 *            The parameters.
	 * @return A Query object.
	 * @throws CoolIOException
	 *             Cool exception.
	 */
	protected Query getQuery(final String qry, final Object[] params)
			throws CoolIOException {
		// log.info("Build query "+qry+" with N parameters "+params.length);
		try {
			log.fine("Search query in " + coolqry);
			QueryParams annparams = coolqry.getQueryParams(qry);
			String[] paramnames = annparams.getParams();
			log.fine("Creating named query " + qry);
			Query q = em.createNamedQuery(qry);
			for (int i = 0; i < paramnames.length; i++) {
				String key = paramnames[i];
				Object val = params[i];
				log.fine("setting query parameter " + key + " value " + val);
				if (val == null && paramnames[i].equals("node")) {
					val = "%";
				} else if (val == null && paramnames[i].equals("tag")) {
					val = "%";
				}
				q.setParameter(key, val);
			}

			return q;
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}
}
