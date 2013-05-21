package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.List;

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

public interface CoolDAO {

	public List<NodeType> retrieveNodesFromSchemaAndDb(String schema, String db, String node) throws CoolIOException;
	public List<SchemaNodeTagType> retrieveTagsFromNodesSchemaAndDb(String schema, String db, String node, String tag) throws CoolIOException;
	public List<NodeGtagTagType> retrieveGtagTagsFromSchemaAndDb(String schema, String db, String gtag) throws CoolIOException;
	public List<NodeGtagTagType> retrieveGtagFromSchemaDbNodeTag(String schema, String db, String gtag,String node, String tag) throws CoolIOException;
	public List<NodeGtagTagType> retrieveGtagDoublFldFromSchemaDb(String schema, String db, String gtag) throws CoolIOException;
	public List<GtagType> retrieveGtagsFromSchemaAndDb(String schema, String db, String gtag) throws CoolIOException;
	public List<GtagTagDiffType> retrieveGtagsDiffFromSchemaAndDb(String schema, String db, String gtag1, String gtag2) throws CoolIOException;
	public List<ChannelType> retrieveChannelsFromNodeSchemaAndDb(String schema, String db, String node, String channame) throws CoolIOException;
	public List<IovStatType> retrieveIovStatFromNodeSchemaAndDb(String schema, String db, String node,String tag) throws CoolIOException;
	public List<SchemaType> retrieveSchemasFromNodeSchemaAndDb(String schema, String db, String node) throws CoolIOException;
	public List<IovType> retrieveIovStatPerChannelFromNodeSchemaAndDb(String schema, String db, String node,String tag) throws CoolIOException;
	public List<IovType> retrieveIovSummaryPerChannelFromNodeSchemaAndDb(String schema, String db, String node,String tag) throws CoolIOException;
	public List<IovType> retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(String schema, String db, String node,String tag,BigDecimal since,BigDecimal until) throws CoolIOException;
	public List<IovType> retrieveHolesStatPerChannelFromNodeSchemaAndDb(String schema, String db, String node,String tag) throws CoolIOException;
	public List<IovType> retrieveHolesStatPerChannelFromNodeSchemaAndDbInRange(String schema, String db, String node,String tag,BigDecimal since,BigDecimal until) throws CoolIOException;
	public List<CoolIovType> retrieveIovsFromNodeSchemaAndDbInRangeByChanName(String schema, String db, String node,String tag, String channel, BigDecimal since, BigDecimal until) throws CoolIOException;
	public List<CoolIovType> retrieveIovsFromNodeSchemaAndDbInRangeByChanId(String schema, String db, String node,String tag, Long chanid, BigDecimal since, BigDecimal until) throws CoolIOException;
}
