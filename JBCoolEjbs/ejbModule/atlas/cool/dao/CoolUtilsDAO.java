/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;

import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * @author formica
 * 
 */
public interface CoolUtilsDAO {

	public Map<Long, CoolIovSummary> computeIovSummaryMap(String schema,
			String db, String node, String tag, String iovbase)
			throws CoolIOException;

	public Map<Long, CoolIovSummary> computeIovSummaryRangeMap(String schema,
			String db, String node, String tag, String iovbase,
			BigDecimal since, BigDecimal until) throws CoolIOException;

	public List<NodeType> listNodesInSchema(String schema, String db)
			throws CoolIOException;

	public List<SchemaNodeTagType> listTagsInNodesSchema(String schema,
			String db, String node) throws CoolIOException;

	public List<IovType> getIovStatPerChannel(String schema, String db,
			String fld, String tag) throws CoolIOException;

	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRunRangeAsList(
			String schema, String db, String fld, String tag, String since,
			String until) throws CoolIOException;
	
	public NodeType listIovsInNodesSchemaTagRangeAsList( String schema, String db,
			String fld, String tag,
			 String channel,
			 String since,
			 String until) throws CoolIOException;
	
	public NodeType listPayloadInNodesSchemaTagRangeAsList(String schema, String db,
			String fld, String tag,
			 String channel,
			 BigDecimal since,
			 BigDecimal until) throws CoolIOException;
	
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(String schema, String db,
			 String fld, String tag,
			 BigDecimal since,
			 BigDecimal until) throws CoolIOException;
	
	
}
