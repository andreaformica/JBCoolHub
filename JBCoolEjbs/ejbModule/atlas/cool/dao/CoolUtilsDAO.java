/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import atlas.coma.exceptions.ComaQueryException;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * @author formica
 * 
 */
public interface CoolUtilsDAO {

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param iovbase
	 * @return
	 * @throws CoolIOException
	 */
	public Map<Long, CoolIovSummary> computeIovSummaryMap(String schema,
			String db, String node, String tag, String iovbase)
			throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param iovbase
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	public Map<Long, CoolIovSummary> computeIovSummaryRangeMap(String schema,
			String db, String node, String tag, String iovbase,
			BigDecimal since, BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @return
	 * @throws CoolIOException
	 */
	public List<NodeType> listNodesInSchema(String schema, String db)
			throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 * @throws CoolIOException
	 */
	public List<SchemaNodeTagType> listTagsInNodesSchema(String schema,
			String db, String node) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @return
	 * @throws CoolIOException
	 */
	public List<IovType> getIovStatPerChannel(String schema, String db,
			String fld, String tag) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param channel
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	public NodeType listIovsInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, String channel,
			BigDecimal since, BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param chanid
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	public NodeType listIovsInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param channel
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	public NodeType listPayloadInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, String channel,
			BigDecimal since, BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param chanid
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	public NodeType listPayloadInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			String schema, String db, String fld, String tag, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * <p>Produces an html output from the list of iov summary</p>
	 * @param iovsummaryColl
	 * @return
	 */
	public String dumpIovSummaryAsText(Collection<CoolIovSummary> iovsummaryColl);

	/**
	 * <p>Produces an html output with svg plots from the list of iov summary</p>
	 * @param iovsummaryColl
	 * @return
	 */
	public String dumpIovSummaryAsSvg(Collection<CoolIovSummary> iovsummaryColl);

	/**
	 * <p>Produces an html output with the coverage check result</p>
	 * <p>Uses COMA tables to gather information on runs</p>
	 * @param iovsummaryColl
	 * @return
	 * @throws ComaQueryException
	 */
	public String checkHoles(Collection<CoolIovSummary> iovsummaryColl)
			throws ComaQueryException;

}
