/**
 * 
 */
package atlas.cool.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.CmpCoolComaGlobalTagType;
import atlas.cool.rest.model.CmpCoolComaNodeType;
import atlas.cool.rest.model.CmpCoolComaTagType;

/**
 * @author formica
 * 
 */
@Named
@Stateless
public class ComaCoolBean implements ComaCoolDAO {

	/**
	 * 
	 */
	@Inject
	private CoolRepositoryDAO coolrep;

	/**
	 * 
	 */
	@Inject
	private Logger log;

	/**
	 * 
	 */
	public ComaCoolBean() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<CmpCoolComaNodeType> retrieveNodesStatus(final String schema,
			final String db) throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = "%";
		log.info("Using query " + CmpCoolComaNodeType.QUERY_CMP_NODES + " with " + schema
				+ " " + db);
		final List<CmpCoolComaNodeType> nodelist = (List<CmpCoolComaNodeType>) coolrep
				.findCoolList(CmpCoolComaNodeType.QUERY_CMP_NODES, params);
		return nodelist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.ComaCoolDAO#retrieveTagsStatus(java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final List<CmpCoolComaTagType> retrieveTagsStatus(final String schema,
			final String db) throws CoolIOException {
		final Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = "%";
		params[3] = "%";
		log.info("Using query " + CmpCoolComaTagType.QUERY_CMP_TAGS + " with " + schema
				+ " " + db);
		final List<CmpCoolComaTagType> taglist = (List<CmpCoolComaTagType>) coolrep
				.findCoolList(CmpCoolComaTagType.QUERY_CMP_TAGS, params);
		return taglist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.ComaCoolDAO#retrieveGlobalTagsStatus(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final List<CmpCoolComaGlobalTagType> retrieveGlobalTagsStatus(
			final String schema, final String db, final String gtag)
			throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		log.info("Using query " + CmpCoolComaGlobalTagType.QUERY_CMP_GTAGS + " with "
				+ schema + " " + db);
		final List<CmpCoolComaGlobalTagType> taglist = (List<CmpCoolComaGlobalTagType>) coolrep
				.findCoolList(CmpCoolComaGlobalTagType.QUERY_CMP_GTAGS, params);
		return taglist;
	}

}
