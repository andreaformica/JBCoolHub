package atlas.cool.dao;

import java.util.List;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * @author formica
 *
 */
public interface ComaDAO {

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 * @throws CoolIOException
	 */
	List<NodeType> retrieveNodesFromSchemaAndDb(String schema, String db, String node)
			throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 * @throws CoolIOException
	 */
	List<NodeGtagTagType> retrieveGtagTagsFromSchemaAndDb(String schema, String db,
			String gtag) throws CoolIOException;
	/**
	 * @param schema
	 * @param db
	 * @param tag
	 * @return
	 * @throws CoolIOException
	 */
	List<NodeGtagTagType> retrieveTagGtagsFromSchemaAndDb(String schema, String db,
			String tag) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 * @throws CoolIOException
	 */
	List<GtagType> retrieveGtagsFromSchemaAndDb(String schema, String db, String gtag)
			throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @return
	 * @throws CoolIOException
	 */
	List<SchemaNodeTagType> retrieveTagsFromNodesSchemaAndDb(String schema, String db,
			String node, String tag) throws CoolIOException;
}
