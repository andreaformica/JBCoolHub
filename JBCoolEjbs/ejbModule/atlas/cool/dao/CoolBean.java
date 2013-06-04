/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.dao.remote.CoolDAORemote;
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

@Named
@Stateless
@Remote(CoolDAORemote.class)
@Local(CoolDAO.class)
/**
 * @author formica
 *
 */
public class CoolBean implements CoolDAO, CoolDAORemote {


	@Inject
	private CoolRepositoryDAO coolrep;
	
	@Inject
	private Logger log;

	/**
	 * 
	 */
	public CoolBean() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#interfaceName()
	 */
	@Override
	public String interfaceName() {
		Class<?> interf[] =  this.getClass().getInterfaces();
		log.info("Number of implementing interfaces "+interf.length);
		String name = "CoolBean:";
		for (int i=0;i<interf.length;i++) {
			String iname = interf[i].getName();
			name += (iname + "/");
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#getInfo(java.lang.String)
	 */
	@Override
	public String getInfo(String name)
    {	
		log.info("\n\n\t Bean's getInfo(String name) called....");
	   	return "[CallerBean] returned Hello "+name;
    }

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveNodesFromSchemaAndDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeType> retrieveNodesFromSchemaAndDb(String schema,
			String db, String node) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] =db;
		params[2] =node;
		log.info("Using query "+NodeType.QUERY_FINDALLNODES+" with "+schema+" "+db+" "+node);
		List<NodeType> nodelist = (List<NodeType>) coolrep.findCoolList(NodeType.QUERY_FINDALLNODES,params);
//		log.info("Retrieved a list of "+nodelist.size()+" nodes");
		return nodelist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveTagsFromNodesSchemaAndDb(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SchemaNodeTagType> retrieveTagsFromNodesSchemaAndDb(
			String schema, String db, String node, String tag)
			throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		log.info("Using query "+SchemaNodeTagType.QUERY_FINDTAGS_IN_NODES+" with "+schema+" "+db+" "+node+" "+tag);
		List<SchemaNodeTagType> taglist = (List<SchemaNodeTagType>) coolrep.findCoolList(SchemaNodeTagType.QUERY_FINDTAGS_IN_NODES,params);
		return taglist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveGtagTagsFromSchemaAndDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeGtagTagType> retrieveGtagTagsFromSchemaAndDb(String schema,
			String db, String gtag) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		log.info("Using query "+NodeGtagTagType.QUERY_FINDGTAGS_TAGS_TRACE+" with "+schema+" "+db+" "+gtag);
		List<NodeGtagTagType> gtaglist = (List<NodeGtagTagType>) coolrep.findCoolList(NodeGtagTagType.QUERY_FINDGTAGS_TAGS_TRACE,params);
		return gtaglist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveGtagFromSchemaDbNodeTag(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeGtagTagType> retrieveGtagFromSchemaDbNodeTag(String schema,
			String db, String gtag, String node, String tag)
			throws CoolIOException {
		Object[] params = new Object[5];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		params[3] = tag;
		params[4] = node;
		log.info("Using query "+NodeGtagTagType.QUERY_FINDGTAGS_FORTAG+" with "+schema+" "+db+" "+gtag+" "+node+" "+tag);
		List<NodeGtagTagType> gtaglist = (List<NodeGtagTagType>) coolrep.findCoolList(NodeGtagTagType.QUERY_FINDGTAGS_FORTAG,params);
		return gtaglist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveGtagDoublFldFromSchemaDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeGtagTagType> retrieveGtagDoublFldFromSchemaDb(
			String schema, String db, String gtag) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		log.info("Using query "+NodeGtagTagType.QUERY_FINDGTAG_DOUBLEFLD+" with "+schema+" "+db+" "+gtag);
		List<NodeGtagTagType> gtaglist = (List<NodeGtagTagType>) coolrep.findCoolList(NodeGtagTagType.QUERY_FINDGTAG_DOUBLEFLD,params);
		return gtaglist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveGtagsFromSchemaAndDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<GtagType> retrieveGtagsFromSchemaAndDb(String schema,
			String db, String gtag) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag;
		log.info("Using query "+GtagType.QUERY_FINDGTAGS+" with "+schema+" "+db+" "+gtag);
		List<GtagType> gtaglist = (List<GtagType>) coolrep.findCoolList(GtagType.QUERY_FINDGTAGS,params);
		return gtaglist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveGtagsDiffFromSchemaAndDb(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<GtagTagDiffType> retrieveGtagsDiffFromSchemaAndDb(String schema,
			String db, String gtag1, String gtag2) throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = gtag1;
		params[3] = gtag2;
		log.info("Using query "+GtagTagDiffType.QUERY_FINDGTAGS_TAGS_DIFF+" with "+schema+" "+db+" "+gtag1+" "+gtag2);
		List<GtagTagDiffType> gtagdifflist = (List<GtagTagDiffType>) coolrep.findCoolList(GtagTagDiffType.QUERY_FINDGTAGS_TAGS_DIFF,params);
		return gtagdifflist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveChannelsFromNodeSchemaAndDb(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ChannelType> retrieveChannelsFromNodeSchemaAndDb(String schema,
			String db, String node, String channame) throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = (channame == null || channame.equals("")) ? "%" : channame;
		log.info("Using query "+ChannelType.QUERY_FINDCHANNELS+" with "+schema+" "+db+" "+node+" "+channame);
		List<ChannelType> channelslist = (List<ChannelType>) coolrep.findCoolList(ChannelType.QUERY_FINDCHANNELS,params);
		return channelslist;
		
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveIovStatFromNodeSchemaAndDb(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<IovStatType> retrieveIovStatFromNodeSchemaAndDb(String schema,
			String db, String node, String tag) throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		
		log.info("Using query "+IovStatType.QUERY_FINDIOVS+" with "+schema+" "+db+" "+node+" "+tag);
		List<IovStatType> iovstatlist = (List<IovStatType>) coolrep.findCoolList(IovStatType.QUERY_FINDIOVS,params);
		return iovstatlist;
		
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveSchemasFromNodeSchemaAndDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SchemaType> retrieveSchemasFromNodeSchemaAndDb(String schema,
			String db, String node) throws CoolIOException {
		Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = (node == null) ? "%" : node;
		log.info("Using query "+SchemaType.QUERY_FINDSCHEMAS+" with "+schema+" "+db+" "+node);
		List<SchemaType> schemalist = (List<SchemaType>) coolrep.findCoolList(SchemaType.QUERY_FINDSCHEMAS,params);
		return schemalist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveIovStatPerChannelFromNodeSchemaAndDb(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<IovType> retrieveIovStatPerChannelFromNodeSchemaAndDb(
			String schema, String db, String node, String tag)
			throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		log.info("Using query "+IovType.QUERY_FINDIOVS+" with "+schema+" "+db+" "+node+" "+tag);
		List<IovType> iovstatlist = (List<IovType>) coolrep.findCoolList(IovType.QUERY_FINDIOVS,params);
		return iovstatlist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveHolesStatPerChannelFromNodeSchemaAndDb(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<IovType> retrieveHolesStatPerChannelFromNodeSchemaAndDb(
			String schema, String db, String node, String tag)
			throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		log.info("Using query "+IovType.QUERY_FINDHOLES+" with "+schema+" "+db+" "+node+" "+tag);
		List<IovType> iovstatlist = (List<IovType>) coolrep.findCoolList(IovType.QUERY_FINDHOLES,params);
		return iovstatlist;
	}
	
	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveIovSummaryPerChannelFromNodeSchemaAndDb(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<IovType> retrieveIovSummaryPerChannelFromNodeSchemaAndDb(
			String schema, String db, String node, String tag)
			throws CoolIOException {
		Object[] params = new Object[4];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		log.info("Using query "+IovType.QUERY_FINDIOVSUMMARY+" with "+schema+" "+db+" "+node+" "+tag);
		List<IovType> iovstatlist = (List<IovType>) coolrep.findCoolList(IovType.QUERY_FINDIOVSUMMARY,params);
		return iovstatlist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public List<IovType> retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(
			String schema, String db, String node, String tag,
			BigDecimal since, BigDecimal until) throws CoolIOException {
		Object[] params = new Object[6];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		params[4] = since;
		params[5] = until;
		log.info("Using query "+IovType.QUERY_FINDIOVSUMMARY_INRANGE+" with "+schema+" "+db+" "+node+" "+tag+" "+since+" "+until);
		List<IovType> iovstatlist = (List<IovType>) coolrep.findCoolList(IovType.QUERY_FINDIOVSUMMARY_INRANGE,params);
		return iovstatlist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveHolesStatPerChannelFromNodeSchemaAndDbInRange(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public List<IovType> retrieveHolesStatPerChannelFromNodeSchemaAndDbInRange(
			String schema, String db, String node, String tag,
			BigDecimal since, BigDecimal until) throws CoolIOException {
		Object[] params = new Object[6];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		params[4] = since;
		params[5] = until;
		log.info("Using query "+IovType.QUERY_FINDHOLES_INRANGE+" with "+schema+" "+db+" "+node+" "+tag+" "+since+" "+until);
		List<IovType> iovstatlist = (List<IovType>) coolrep.findCoolList(IovType.QUERY_FINDHOLES_INRANGE,params);
		return iovstatlist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveIovsFromNodeSchemaAndDbInRangeByChanName(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public List<CoolIovType> retrieveIovsFromNodeSchemaAndDbInRangeByChanName(
			String schema, String db, String node, String tag, String channel,
			BigDecimal since, BigDecimal until) throws CoolIOException {
		Object[] params = new Object[7];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		params[4] = channel;
		params[5] = since;
		params[6] = until;
		log.info("Using query "+CoolIovType.QUERY_FINDIOVS_INRANGE_BYCHAN+" with "+schema+" "+db+" "+node+" "+tag+" "+channel+" "+since+" "+until);
		List<CoolIovType> iovlist = (List<CoolIovType>) coolrep.findCoolList(CoolIovType.QUERY_FINDIOVS_INRANGE_BYCHAN,params);
		// TODO: use this as an example for future need in pagination (2013/06/04)
		//		List<CoolIovType> iovlist = coolrep.findCoolList(CoolIovType.class,CoolIovType.QUERY_FINDIOVS_INRANGE_BYCHAN,params);
		return iovlist;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAO#retrieveIovsFromNodeSchemaAndDbInRangeByChanId(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public List<CoolIovType> retrieveIovsFromNodeSchemaAndDbInRangeByChanId(
			String schema, String db, String node, String tag,
			Long chanid, BigDecimal since, BigDecimal until)
			throws CoolIOException {
		Object[] params = new Object[7];
		params[0] = schema;
		params[1] = db;
		params[2] = node;
		params[3] = tag;
		params[4] = chanid;
		params[5] = since;
		params[6] = until;
		log.info("Using query "+CoolIovType.QUERY_FINDIOVS_INRANGE+" with "+schema+" "+db+" "+node+" "+tag+" "+chanid+" "+since+" "+until);
		List<CoolIovType> iovlist = (List<CoolIovType>) coolrep.findCoolList(CoolIovType.QUERY_FINDIOVS_INRANGE,params);
		return iovlist;
	}

	
}
