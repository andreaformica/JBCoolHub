package atlas.cool.dao;

import java.util.List;

public interface CoolRepositoryDAO {

	public abstract List<?> findCoolList(String qryname, Object[] params)
			throws CoolIOException;

	public abstract List<?> findCoolListByRange(String qryname,
			Object[] params, int firstResult, int maxResults)
			throws CoolIOException;

}