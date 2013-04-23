/**
 * 
 */
package atlas.cool.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import atlas.cool.annotations.CoolQueryRepository;
import atlas.cool.annotations.QueryParams;

@RequestScoped
/**
 * @author formica
 *
 */
public class CoolRepository {

	@Inject
	private EntityManager em;

	@Inject
	private Logger log;

	@Inject
	private CoolQueryRepository coolqry;

	private final int maxFetchResults = 20000;
	/**
	 * 
	 */
	public CoolRepository() {
		// TODO Auto-generated constructor stub
	}

	public synchronized List<?> findCoolList(String qryname, Object[] params)
			throws CoolIOException {

		try {
			Query q = getQuery(qryname, params);
			q.setMaxResults(maxFetchResults);
			return q.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized List<?> findCoolListByRange(String qryname, Object[] params, int firstResult, int maxResults)
			throws CoolIOException {

		try {
			Query q = getQuery(qryname, params);
			if (firstResult>0)	
				q.setFirstResult(firstResult);
			if (maxResults>0)
				q.setMaxResults(maxResults);
			return q.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return null;
	}


	protected synchronized Query getQuery(String qry, Object[] params)
			throws CoolIOException {
		// log.info("Build query "+qry+" with N parameters "+params.length);
		try {
			log.info("Search query in "+coolqry);
			QueryParams annparams = coolqry.getQueryParams(qry);
			String[] paramnames = annparams.getParams();
			log.info("Creating named query "+qry);
			Query q = em.createNamedQuery(qry);
			for (int i = 0; i < paramnames.length; i++) {
				String key = paramnames[i];
				Object val = params[i];
				log.info("setting query parameter " + key+" value "+val);
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
