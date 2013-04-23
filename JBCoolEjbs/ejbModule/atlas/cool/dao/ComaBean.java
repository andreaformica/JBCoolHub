/**
 * 
 */
package atlas.cool.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.dao.remote.CoolDAORemote;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.GtagTagDiffType;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.IovStatType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.model.SchemaType;

@Named
@Stateless
@Local(ComaDAO.class)
/**
 * @author formica
 *
 */
public class ComaBean implements ComaDAO {

	@Inject
	private CoolRepository coolrep;
	
	@Inject
	private Logger log;

	/**
	 * 
	 */
	public ComaBean() {
		// TODO Auto-generated constructor stub
	}


	/* (non-Javadoc)
	 * @see atlas.cool.dao.ComaDAO#retrieveNodesFromSchemaAndDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<NodeType> retrieveNodesFromSchemaAndDb(String schema,
			String db, String node) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] =db;
		params[2] =node;
		log.info("Using query "+NodeType.QUERY_COMA_FINDNODES+" with "+schema+" "+db+" "+node);
		List<NodeType> nodelist = (List<NodeType>) coolrep.findCoolList(NodeType.QUERY_COMA_FINDNODES,params);
//		log.info("Retrieved a list of "+nodelist.size()+" nodes");
		return nodelist;
	}


	@Override
	public List<NodeGtagTagType> retrieveGtagTagsFromSchemaAndDb(String schema,
			String db, String gtag) throws CoolIOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<GtagType> retrieveGtagsFromSchemaAndDb(String schema,
			String db, String gtag) throws CoolIOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<SchemaNodeTagType> retrieveTagsFromNodesSchemaAndDb(
			String schema, String db, String node, String tag)
			throws CoolIOException {
		// TODO Auto-generated method stub
		return null;
	}

}
