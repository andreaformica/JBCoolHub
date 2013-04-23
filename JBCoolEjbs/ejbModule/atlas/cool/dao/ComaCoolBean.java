/**
 * 
 */
package atlas.cool.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.rest.model.CmpCoolComaGlobalTagType;
import atlas.cool.rest.model.CmpCoolComaNodeType;
import atlas.cool.rest.model.CmpCoolComaTagType;

@Named
@Stateless
/**
 * @author formica
 *
 */
public class ComaCoolBean implements ComaCoolDAO {

	@Inject
	private CoolRepository coolrep;
	
	@Inject
	private Logger log;

	/**
	 * 
	 */
	public ComaCoolBean() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public List<CmpCoolComaNodeType> retrieveNodesStatus(String schema,
			String db) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = new String("%");
		log.info("Using query "+CmpCoolComaNodeType.QUERY_CMP_NODES+" with "+schema+" "+db);
		List<CmpCoolComaNodeType> nodelist = (List<CmpCoolComaNodeType>) coolrep.findCoolList(CmpCoolComaNodeType.QUERY_CMP_NODES,params);
		return nodelist;
	}


	/* (non-Javadoc)
	 * @see atlas.cool.dao.ComaCoolDAO#retrieveTagsStatus(java.lang.String, java.lang.String)
	 */
	@Override
	public List<CmpCoolComaTagType> retrieveTagsStatus(String schema, String db)
			throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = new String("%");
		params[3] = new String("%");
		log.info("Using query "+CmpCoolComaTagType.QUERY_CMP_TAGS+" with "+schema+" "+db);
		List<CmpCoolComaTagType> taglist = (List<CmpCoolComaTagType>) coolrep.findCoolList(CmpCoolComaTagType.QUERY_CMP_TAGS,params);
		return taglist;
	}


	/* (non-Javadoc)
	 * @see atlas.cool.dao.ComaCoolDAO#retrieveGlobalTagsStatus(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<CmpCoolComaGlobalTagType> retrieveGlobalTagsStatus(
			String schema, String db, String gtag) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		log.info("Using query "+CmpCoolComaGlobalTagType.QUERY_CMP_GTAGS+" with "+schema+" "+db);
		List<CmpCoolComaGlobalTagType> taglist = (List<CmpCoolComaGlobalTagType>) 
				coolrep.findCoolList(CmpCoolComaGlobalTagType.QUERY_CMP_GTAGS,params);
		return taglist;
	}

	
}
