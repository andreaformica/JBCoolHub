package atlas.cool.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.GtagTagDiffType;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.IovStatType;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.model.SchemaType;

/**
 * @author formica
 * 
 */
public interface CoolDAO {

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @return List of matching nodes.
	 * @throws CoolIOException
	 *             Cool Exception.
	 */
	List<NodeType> retrieveNodesFromSchemaAndDb(String schema, String db, String node)
			throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag search string.
	 * @return List of Tags for given schema.
	 * @throws CoolIOException
	 */
	List<SchemaNodeTagType> retrieveTagsFromNodesSchemaAndDb(String schema, String db,
			String node, String tag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param gtag
	 *            The global tag.
	 * @return
	 * @throws CoolIOException
	 */
	List<NodeGtagTagType> retrieveGtagTagsFromSchemaAndDb(String schema, String db,
			String gtag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param gtag
	 *            The global tag.
	 * @param node
	 *            The node.
	 * @param tag
	 *            The tag associated.
	 * @return List of tags associated to global tag.
	 * @throws CoolIOException
	 */
	List<NodeGtagTagType> retrieveGtagFromSchemaDbNodeTag(String schema, String db,
			String gtag, String node, String tag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param gtag
	 *            The global tag.
	 * @return List of tags associated to global tag.
	 * @throws CoolIOException
	 */
	List<NodeGtagTagType> retrieveGtagDoublFldFromSchemaDb(String schema, String db,
			String gtag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param gtag
	 *            The global tag.
	 * @return List of global tags.
	 * @throws CoolIOException
	 */
	List<GtagType> retrieveGtagsFromSchemaAndDb(String schema, String db, String gtag)
			throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param gtag1
	 *            The first global tag to compare.
	 * @param gtag2
	 *            The second global tag to compare.
	 * @return List of differences.
	 * @throws CoolIOException
	 */
	List<GtagTagDiffType> retrieveGtagsDiffFromSchemaAndDb(String schema, String db,
			String gtag1, String gtag2) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param channame
	 *            The channel name.
	 * @return List of channels.
	 * @throws CoolIOException
	 */
	List<ChannelType> retrieveChannelsFromNodeSchemaAndDb(String schema, String db,
			String node, String channame) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @return List of iovs.
	 * @throws CoolIOException
	 */
	List<IovStatType> retrieveIovStatFromNodeSchemaAndDb(String schema, String db,
			String node, String tag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @return List of schemas.
	 * @throws CoolIOException
	 */
	List<SchemaType> retrieveSchemasFromNodeSchemaAndDb(String schema, String db,
			String node) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @return List of iovs.
	 * @throws CoolIOException
	 */
	List<IovType> retrieveIovStatPerChannelFromNodeSchemaAndDb(String schema, String db,
			String node, String tag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @return List of iovs.
	 * @throws CoolIOException
	 */
	List<IovType> retrieveIovSummaryPerChannelFromNodeSchemaAndDb(String schema,
			String db, String node, String tag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @param since
	 *            The since time.
	 * @param until
	 *            The until time.
	 * @return
	 * @throws CoolIOException
	 */
	List<IovType> retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(String schema,
			String db, String node, String tag, BigDecimal since, BigDecimal until)
			throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @return
	 * @throws CoolIOException
	 */
	List<IovType> retrieveHolesStatPerChannelFromNodeSchemaAndDb(String schema,
			String db, String node, String tag) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @param since
	 *            The since time.
	 * @param until
	 *            The until time.
	 * @return
	 * @throws CoolIOException
	 */
	List<IovType> retrieveHolesStatPerChannelFromNodeSchemaAndDbInRange(String schema,
			String db, String node, String tag, BigDecimal since, BigDecimal until)
			throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @param channel
	 *            The channel.
	 * @param since
	 *            The since time.
	 * @param until
	 *            The until time.
	 * @return
	 * @throws CoolIOException
	 */
	List<CoolIovType> retrieveIovsFromNodeSchemaAndDbInRangeByChanName(String schema,
			String db, String node, String tag, String channel, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @param num
	 *            The number of iovs to retrieve per channel.
	 * @return
	 * @throws CoolIOException
	 */
	List<CoolIovType> retrieveIovsLastNumFromNodeSchemaAndDb(String schema,
			String db, String node, String tag, Integer num) throws CoolIOException;


	/**
	 * @param schema
	 *            The DB schema name.
	 * @param db
	 *            The DB instance.
	 * @param node
	 *            The COOL node.
	 * @param tag
	 *            The tag.
	 * @param chanid
	 *            The channel ID.
	 * @param since
	 *            The since time.
	 * @param until
	 *            The until time.
	 * @return
	 * @throws CoolIOException
	 */
	List<CoolIovType> retrieveIovsFromNodeSchemaAndDbInRangeByChanId(String schema,
			String db, String node, String tag, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * 			The schema name.
	 * @param db
	 * 			The db instance.
	 * @param node
	 * 			The node full path.
	 * @param objid
	 * 			The id of the entry flagged as last entry.
	 * @param lmd
	 * 			The time of last modification.
	 * @return
	 * @throws CoolIOException
	 * TODO: Add number to select object ID from the iov table.
	 */
	List<CoolIovType> retrieveLastModIovsFromNodeSchemaAndDb(String schema,
			String db, String node, BigDecimal seqid, Timestamp lmd) throws CoolIOException;
	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param chanid
	 * @return
	 * @throws CoolIOException
	 */
	List<atlas.cool.summary.model.CoolIovSummary> findIovSummaryList(String schema,
			String db, String node, String tag, BigDecimal chanid) throws CoolIOException;
	
	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param chanid
	 * @return
	 * @throws CoolIOException
	 */
	List<atlas.cool.summary.model.CoolIovSummary> findIovSummaryList(String schema,
			String db) throws CoolIOException;

	
	/**
	 * @param iovid
	 * @param since
	 * @return
	 * @throws CoolIOException
	 */
	List<atlas.cool.summary.model.CoolIovRanges> findIovRangesList(BigDecimal iovid, BigDecimal since) throws CoolIOException;

	/**
	 * Retrieves tags associated to global tags considering also intermediate branch tags.
	 * 
	 * @param schema
	 * @param db
	 * @param gtag
	 * @return
	 * @throws CoolIOException
	 */
	List<NodeGtagTagType> retrieveGtagBranchTagsFromSchemaAndDb(String schema,
			String db, String gtag)  throws CoolIOException;

}
