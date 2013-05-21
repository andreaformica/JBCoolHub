package atlas.cool.dao;

import java.util.List;
import java.util.Map;

public interface CoolRepositoryDAO {

	public abstract List<?> findCoolList(String qryname, Object[] params)
			throws CoolIOException;

	public abstract List<?> findCoolListByRange(String qryname,
			Object[] params, int firstResult, int maxResults)
			throws CoolIOException;

	public abstract List<?> findCoolList(Object domain, String qryname, Object[] params, Map<String, Boolean> orderbyParams)
			throws CoolIOException;

	public abstract List<?> findCoolListByRange(Object domain, String qryname,
			Object[] params, int firstResult, int maxResults, Map<String, Boolean> orderbyParams)
			throws CoolIOException;

}