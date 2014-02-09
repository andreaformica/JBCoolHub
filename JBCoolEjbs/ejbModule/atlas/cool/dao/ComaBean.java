/**
 * 
 */
package atlas.cool.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * @author formica
 * 
 */
@Named
@Stateless
@Local(ComaDAO.class)
public class ComaBean implements ComaDAO {

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
	public ComaBean() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.ComaDAO#retrieveNodesFromSchemaAndDb(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public final List<NodeType> retrieveNodesFromSchemaAndDb(final String schema,
			final String db, final String node) throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		log.info("Using query " + NodeType.QUERY_COMA_FINDNODES + " with " + schema + " "
				+ db + " " + node);
		final List<NodeType> nodelist = (List<NodeType>) coolrep.findCoolList(
				NodeType.QUERY_COMA_FINDNODES, params);
		// log.info("Retrieved a list of "+nodelist.size()+" nodes");
		return nodelist;
	}

	@Override
	public final List<NodeGtagTagType> retrieveGtagTagsFromSchemaAndDb(
			final String schema, final String db, final String gtag)
			throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		log.info("Using query " + NodeGtagTagType.QUERY_COMA_FINDGTAGS_TAGS_TRACE + " with "
				+ schema + " " + db + " " + gtag);
		final List<NodeGtagTagType> gtaglist = (List<NodeGtagTagType>) coolrep
				.findCoolList(NodeGtagTagType.QUERY_COMA_FINDGTAGS_TAGS_TRACE, params);
		return gtaglist;
	}

	@Override
	public final List<GtagType> retrieveGtagsFromSchemaAndDb(final String schema,
			final String db, final String gtag) throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		log.info("Using query " + GtagType.QUERY_COMA_FINDGTAGS + " with " + schema + " " + db
				+ " " + gtag);
		final List<GtagType> gtaglist = (List<GtagType>) coolrep.findCoolList(
				GtagType.QUERY_COMA_FINDGTAGS, params);
		return gtaglist;
	}

	@Override
	public final List<SchemaNodeTagType> retrieveTagsFromNodesSchemaAndDb(
			final String schema, final String db, final String node, final String tag)
			throws CoolIOException {
		final Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		log.info("Using query " + SchemaNodeTagType.QUERY_COMA_FINDTAGS_IN_NODES + " with "
				+ schema + " " + db + " " + node + " " + tag);
		final List<SchemaNodeTagType> taglist = (List<SchemaNodeTagType>) coolrep
				.findCoolList(SchemaNodeTagType.QUERY_COMA_FINDTAGS_IN_NODES, params);
		return taglist;
	}

}
