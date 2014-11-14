/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.ejb3.annotation.TransactionTimeout;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.dao.ComaRunDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.coma.model.NemoRun;
import atlas.cool.annotations.CoolQueryRepository;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.meta.ParserHeader;
import atlas.cool.payload.model.CoolPayload;
import atlas.cool.payload.model.CoolPayloadTransform;
import atlas.cool.payload.plugin.ClobParser;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.utils.SvgRestUtils;

/**
 * @author formica
 * 
 */
@Named
@Stateless
@Local(CoolUtilsDAO.class)
public class CoolUtilsBean implements CoolUtilsDAO {

	/**
	 * 
	 */
	@Inject
	private CoolDAO cooldao;
	/**
	 * 
	 */
	@Inject
	private ComaCbDAO comadao;
	/**
	 * 
	 */
	@Inject
	private ComaRunDAO comarundao;
	/**
	 * 
	 */
	@Inject
	private CoolPayloadDAO payloaddao;

	/**
	 * 
	 */
	@Inject
	private CoolQueryRepository coolqry;

	/**
	 * 
	 */
	@Inject
	private Logger log;

	/**
	 * 
	 */
	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

	/**
	 * 
	 */
	public CoolUtilsBean() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolDAOUtils#computeIovRangeMap(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SortedMap<Long, CoolIovSummary> computeIovSummaryMap(final String schema,
			final String db, final String node, final String tag, final String iovbase)
			throws CoolIOException {
		try {

			final List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(schema, db, node,
							tag);

			final SortedMap<Long, CoolIovSummary> iovsummary = getSummary(iovperchanList,
					schema, db, node, tag, iovbase);
			return iovsummary;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param iovperchanList
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param iovbase
	 * @return
	 */
	protected SortedMap<Long, CoolIovSummary> getSummary(
			final List<IovType> iovperchanList, final String schema, final String db,
			final String node, final String tag, final String iovbase) {

		final SortedMap<Long, CoolIovSummary> iovsummary = new TreeMap<Long, CoolIovSummary>();

		for (final IovType aniov : iovperchanList) {
			// log.info("Analyze iov from DB : "+aniov.getChannelId()+" "+aniov.getNiovs());
			aniov.setIovBase(iovbase);
			final Double isvalid = aniov.getIovHole().doubleValue();
			Long since = 0L;
			Long until = 0L;
			if (aniov.getIovBase().equals("time")) {
				since = CoolIov.getTime(aniov.getMiniovSince().toBigInteger());
				until = CoolIov.getTime(aniov.getMaxiovUntil().toBigInteger());
			} else {
				since = CoolIov.getRun(aniov.getMiniovSince().toBigInteger());
				until = CoolIov.getRun(aniov.getMaxiovUntil().toBigInteger());
			}

			// niovs += aniov.getNiovs();

			CoolIovSummary iovsumm = null;
			if (iovsummary.containsKey(aniov.getChannelId())) {
				iovsumm = iovsummary.get(aniov.getChannelId());
			} else {
				iovsumm = new CoolIovSummary(aniov.getChannelId());
				iovsumm.setIovbase(aniov.getIovBase());
				iovsumm.setChannelName(aniov.getChannelName());
				iovsumm.setSchema(schema);
				iovsumm.setDb(db);
				iovsumm.setNode(node);
				iovsumm.setTag(tag);
			}
			try {

				iovsumm.appendIov(since, until, aniov.getNiovs(), false);
				// If there is a hole then take its times from other
				// fields and add a line for the previous good iov and
				// the hole
				if (isvalid > 0) {

					since = until; // the since of the hole is the until
									// of the last good
					if (aniov.getIovBase().equals("time")) {
						until = CoolIov.getTime(aniov.getHoleUntil().toBigInteger());
					} else {
						until = CoolIov.getRun(aniov.getHoleUntil().toBigInteger());
					}
					iovsumm.appendIov(since, until, 0L, true);
				}
				iovsummary.put(aniov.getChannelId(), iovsumm);
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return iovsummary;
	}

	/**
	 * @param iovperchanList
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param iovbase
	 * @return
	 */
	protected final SortedMap<Long, CoolIovSummary> getSummaryOrigTime(
			final List<IovType> iovperchanList, final String schema, final String db,
			final String node, final String tag, final String iovbase) {

		final SortedMap<Long, CoolIovSummary> iovsummary = new TreeMap<Long, CoolIovSummary>();

		for (final IovType aniov : iovperchanList) {
			// log.info("Analyze iov from DB : " + aniov.getChannelId() + " "
			// + aniov.getNiovs());
			aniov.setIovBase(iovbase);
			final Double isvalid = aniov.getIovHole().doubleValue();
			Long since = 0L;
			Long until = 0L;
			since = aniov.getMiniovSince().longValueExact();
			until = aniov.getMaxiovUntil().longValueExact();

			// niovs += aniov.getNiovs();

			CoolIovSummary iovsumm = null;
			if (iovsummary.containsKey(aniov.getChannelId())) {
				iovsumm = iovsummary.get(aniov.getChannelId());
			} else {
				iovsumm = new CoolIovSummary(aniov.getChannelId());
				iovsumm.setIovbase(aniov.getIovBase());
				iovsumm.setChannelName(aniov.getChannelName());
				iovsumm.setSchema(schema);
				iovsumm.setDb(db);
				iovsumm.setNode(node);
				iovsumm.setTag(tag);
			}
			try {

				iovsumm.appendIov(since, until, aniov.getNiovs(), false);
				// If there is a hole then take its times from other
				// fields and add a line for the previous good iov and
				// the hole
				if (isvalid > 0) {

					since = until; // the since of the hole is the until
									// of the last good
					until = aniov.getHoleUntil().longValueExact();
					iovsumm.appendIov(since, until, 0L, true);
				}
				iovsummary.put(aniov.getChannelId(), iovsumm);
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return iovsummary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#computeIovSummaryRangeMap(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public SortedMap<Long, CoolIovSummary> computeIovSummaryRangeMap(final String schema,
			final String db, final String node, final String tag, final String iovbase,
			final BigDecimal since, final BigDecimal until) throws CoolIOException {

		try {

			log.info("computeIovSummaryRangeMap: " + schema + " " + db + " " + node + " "
					+ tag + " " + iovbase);
			final List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(schema, db,
							node, tag, since, until);

			final SortedMap<Long, CoolIovSummary> iovsummary = getSummaryOrigTime(
					iovperchanList, schema, db, node, tag, iovbase);
			return iovsummary;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listNodesInSchema(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<NodeType> listNodesInSchema(final String schema, final String db)
			throws CoolIOException {
		log.info("Calling listNodesInSchema..." + schema + " " + db);
		List<NodeType> results = null;
		results = cooldao.retrieveNodesFromSchemaAndDb(schema + "%", db, "%");

		return results;
	}

	
	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#listNodesInSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeType> listNodesInSchema(String schema, String db,
			String node) throws CoolIOException {
		log.info("Calling listNodesInSchema..." + schema + " " + db+" "+node);
		List<NodeType> results = null;
		results = cooldao.retrieveNodesFromSchemaAndDb(schema + "%", db, "%"+node+"%");

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listTagsInNodesSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<SchemaNodeTagType> listTagsInNodesSchema(final String schema,
			final String db, final String node) throws CoolIOException {
		log.info("Calling listTagsInNodeSchema..." + schema + " " + db + " " + node);
		List<SchemaNodeTagType> results = null;

		String lnode = node;
		if (node.equals("all")) {
			lnode = "%";
		} else {
			lnode = "%" + node + "%";
		}
		results = cooldao.retrieveTagsFromNodesSchemaAndDb(schema + "%", db, lnode, null);

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#getIovStatPerChannel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<IovType> getIovStatPerChannel(final String schema, final String db,
			final String fld, final String tag) throws CoolIOException {
		log.info("Calling getIovStatPerChannel..." + schema + " " + db + " " + fld + " "
				+ tag);
		List<IovType> results = null;

		String seltag = "%" + tag + "%";
		if (tag.equals("none")) {
			seltag = null;
		} else if (tag.equals("all")) {
			seltag = "%";
		}
		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		NodeType selnode = null;
		if (nodes != null && nodes.size() > 0) {
			for (final NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		results = cooldao.retrieveIovStatPerChannelFromNodeSchemaAndDb(schema, db, node,
				seltag);
		for (final IovType aniov : results) {
			// log.info("Found iovtype "+aniov.getTagName()+" "+aniov.getChannelId());
			aniov.setIovBase(selnode.getNodeIovBase());
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listIovsInNodesSchemaTagRangeAsList(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listIovsInNodesSchemaTagRangeAsList(final String schema,
			final String db, final String fld, final String tag, final String channel,
			final BigDecimal since, final BigDecimal until) throws CoolIOException {
		log.info("Calling listIovsInNodesSchemaTagRangeAsList..." + schema + " " + db
				+ " folder " + fld + " tag " + tag + " " + channel + " " + since + " "
				+ until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		String chan = "%" + channel + "%";
		if (channel.equals("all")) {
			chan = null;
		}
		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		if (nodes != null && nodes.size() > 0) {
			for (final NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		if (selnode == null) {
			throw new CoolIOException("Cannot find node...");
		}
		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}

		iovlist = cooldao.retrieveIovsFromNodeSchemaAndDbInRangeByChanName(schema, db,
				node, seltag, chan, since, until);
		selnode.setIovList(iovlist);

		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listIovsInNodesSchemaTagRangeAsList(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long,
	 * java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listIovsInNodesSchemaTagRangeAsList(final String schema,
			final String db, final String fld, final String tag, final Long chanid,
			final BigDecimal since, final BigDecimal until) throws CoolIOException {
		log.info("Calling listIovsInNodesSchemaTagRangeAsList..." + schema + " " + db
				+ " folder " + fld + " tag " + tag + " " + chanid + " " + since + " "
				+ until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		if (nodes != null && nodes.size() > 0) {
			for (final NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		if (selnode == null) {
			throw new CoolIOException("Cannot find node...");
		}
		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}

		iovlist = cooldao.retrieveIovsFromNodeSchemaAndDbInRangeByChanId(schema, db,
				node, seltag, chanid, since, until);
		selnode.setIovList(iovlist);

		return selnode;
	}
	
	

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#listIovsLastNumInNodesSchemaTagAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public NodeType listIovsLastNumInNodesSchemaTagAsList(String schema,
			String db, String fld, String tag, Integer num)
			throws CoolIOException {
		log.info("Calling listIovsLastNumInNodesSchemaTagAsList..." + schema + " " + db
				+ " folder " + fld + " tag " + tag + " " + num );
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		if (nodes != null && nodes.size() > 0) {
			for (final NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		if (selnode == null) {
			throw new CoolIOException("Cannot find node...");
		}
		String seltag = tag;
		if (tag.toLowerCase().equals("none")) {
			log.info("Tag is null, so this is a single version folder...");
			seltag = null;
		}
		Integer numiovs = num;
		if (num == null) {
			numiovs=10;
		}

		iovlist = cooldao.retrieveIovsLastNumFromNodeSchemaAndDb(schema, db,
				node, seltag, numiovs);
		if (iovlist != null)
			log.info("Found list of "+iovlist.size()+" iovs...");
		else
			log.warning("No iovs retrieved from query");
		
		selnode.setIovList(iovlist);

		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listPayloadInNodesSchemaTagRangeAsList(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public NodeType listPayloadInNodesSchemaTagRangeAsList(final String schema,
			final String db, final String fld, final String tag, final String channel,
			final BigDecimal since, final BigDecimal until) throws CoolIOException {
		log.info("Calling listPayloadInNodesSchemaTagRangeAsList..." + schema + " " + db
				+ " folder " + fld + " tag " + tag + " " + channel + " " + since + " "
				+ until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		try {
			String chan = "%" + channel + "%";
			if (channel.equals("all")) {
				chan = null;
			}
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
					node);
			if (nodes != null && nodes.size() > 0) {
				for (final NodeType anode : nodes) {
					log.fine("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			if (selnode == null) {
				throw new CoolIOException("Cannot find node...");
			}
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}

			log.info("Retrieving payload " + payloaddao);
			final CoolPayload payload = payloaddao.getPayloadsObj(schema, db, node,
					seltag, since, until, chan);
			log.info("Retrieved payload of n rows = " + payload.getRows());

			final ClobParser parser = coolqry.getParser(selnode.getSchemaName(),
					selnode.getNodeFullpath());

			log.info("Parser checked ...");
			if (parser != null) {
				log.info("Parser has been found... " + parser.getClass().getName());
				payload.setParser(parser);
			}
			CoolPayloadTransform pyldtransf = new CoolPayloadTransform(payload);
			iovlist = pyldtransf.getIovsWithPayload();
			ParserHeader header = pyldtransf.getPyldHeader();
			log.info("Retrieving iovlist ...");
			if (iovlist != null) {
				log.info("Retrieved iovlist of " + iovlist.size());
			}
			if (header != null) {
				log.info("Build header for payload object: "+header);
			}
			selnode.setIovList(iovlist);
			selnode.setParserHeader(header);
		} catch (final Exception e) {
			// payloaddao.remove();
			throw new CoolIOException(e.getMessage());
		}
		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listPayloadInNodesSchemaTagRangeAsList(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long,
	 * java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public NodeType listPayloadInNodesSchemaTagRangeAsList(final String schema,
			final String db, final String fld, final String tag, final Long chanid,
			final BigDecimal since, final BigDecimal until) throws CoolIOException {
		log.info("Calling listPayloadInNodesSchemaTagRangeAsList..." + schema + " " + db
				+ " folder " + fld + " tag " + tag + " " + chanid + " " + since + " "
				+ until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		try {
			final Long chan = chanid;

			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
					node);
			if (nodes != null && nodes.size() > 0) {
				for (final NodeType anode : nodes) {
					log.fine("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			if (selnode == null) {
				throw new CoolIOException("Cannot find node...");
			}
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}
			log.info("Retrieving payload " + payloaddao);
			final CoolPayload payload = payloaddao.getPayloadsObj(schema, db, node,
					seltag, since, until, chan);

			// if (selnode.getNodeFullpath().equals("/RPC/DQMF/ELEMENT_STATUS")
			// && selnode.getSchemaName().equals("ATLAS_COOLOFL_RPC")) {
			// payload.setParser(new RpcClobParser());
			// }
			final ClobParser parser = coolqry.getParser(selnode.getSchemaName(),
					selnode.getNodeFullpath());
			if (parser != null) {
				log.info("Parser has been found... " + parser.getClass().getName());
				payload.setParser(parser);
			}
			CoolPayloadTransform pyldtransf = new CoolPayloadTransform(payload);
			iovlist = pyldtransf.getIovsWithPayload();
			ParserHeader header = pyldtransf.getPyldHeader();
			log.info("Retrieving iovlist ...");
			if (iovlist != null) {
				log.info("Retrieved iovlist of " + iovlist.size());
			}
			if (header != null) {
				log.info("Build header for payload object: "+header);
			}
			selnode.setIovList(iovlist);
			selnode.setParserHeader(header);

		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listIovsSummaryInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			final String schema, final String db, final String fld, final String tag,
			final BigDecimal since, final BigDecimal until) throws CoolIOException {
		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..." + schema + " "
				+ db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;
		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		final List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		NodeType selnode = null;
		if (nodes != null && nodes.size() > 0) {
			log.info("List of nodes retrieved: " + nodes.toString());
			for (final NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType() + " iovbase " + anode.getNodeIovBase());
				selnode = anode;
			}
		}
		if (selnode == null) {
			throw new CoolIOException("Cannot find node...");
		}

		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}

		final Map<Long, CoolIovSummary> iovsummary = computeIovSummaryRangeMap(schema,
				db, node, seltag, selnode.getNodeIovBase(), since, until);

		summarylist = iovsummary.values();

		return summarylist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#dumpIovSummaryAsText(java.util.Collection)
	 */
	@Override
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public String dumpIovSummaryAsText(final Collection<CoolIovSummary> iovsummaryColl) {

		final StringBuffer results = new StringBuffer();
		final Map<String, List<CrViewRuninfo>> skippedrunsCacheMap = new HashMap<String, List<CrViewRuninfo>>();

		// List<NodeGtagTagType> nodeingtagList = null;
		final String colorgoodtagstart = "<span style=\"color:#20D247\">";
		final String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		final String colorseptagstart = "<span style=\"color:#1A91C4\">";
		final String colorbadtagstart = "<span style=\"color:#B43613\">";
		final String colorsingleiovstart = "<span style=\"color:#2E64FE\">";
		final String colortagend = "</span>";

		final int channels = iovsummaryColl.size();
		final Iterator<CoolIovSummary> it = iovsummaryColl.iterator();
		if (it.hasNext()) {
			final CoolIovSummary firstsumm = it.next();
			results.append("<hr><h2>" + colorseptagstart + firstsumm.getSchema() + " > "
					+ " " + firstsumm.getNode() + " ; " + firstsumm.getTag()
					+ colortagend + "</h2>");
		}

		results.append("<h3>Total used channels is " + channels + " </h3>");
		results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

		for (final CoolIovSummary iovsummary : iovsummaryColl) {
			results.append("<p class=\"small\">" + iovsummary.getChanId() + " "
					+ iovsummary.getChannelName() + " - " + iovsummary.getIovbase()
					+ " : ");

			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			final Integer niovranges = timeranges.size();
			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				final long minsince = iovsummary.getMinsince();
				int iiov = 0;
				for (final Long asince : sincetimes) {

					final IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					if (iiov == 0 && ivr.getSince().compareTo(minsince) != 0) {
						colortagstart = colorwarntagstart;
					}
					if (ivr.getNiovs() == 1 && iiov == 0 && niovranges == 1) {
						colortagstart = colorsingleiovstart;
					}

					if (iiov == 0 && ivr.getSince() > 0) {
						// Check against the first valid run number for 2010: 152166
						final BigDecimal since = new BigDecimal(0L);
						final BigDecimal until = new BigDecimal(ivr.getSince());
						final String cacheKey = since.longValue() + "-"
								+ until.longValue();
						log.fine("first iovrange: " + ivr.getSince() + " "
								+ ivr.getUntil() + " check for hole in " + since + " "
								+ until);
						try {
							Boolean missingPhysicsRuns = false;
							if (skippedrunsCacheMap.containsKey(cacheKey)) {
								final List<CrViewRuninfo> runinhole = skippedrunsCacheMap
										.get(cacheKey);
								if (runinhole.size() > 0) {
									missingPhysicsRuns = true;
								}
							} else {
								final List<CrViewRuninfo> runinhole = checkHoles(since,
										until, iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, runinhole);
								if (runinhole.size() > 0) {
									missingPhysicsRuns = true;
								}
							}
							if (missingPhysicsRuns) {
								//colortagstart = colorbadtagstart;
								long timespan = until.longValue() - since.longValue();
								if (iovsummary.getIovbase().equals("time")) {
									timespan = timespan / 1000L;
								}
								String holedump = "[" + timespan + "] ";
								iovDump = colorbadtagstart + "1" + " ["
										+ "0" + "] [" + ivr.getSinceCoolStr()
										+ "] " + holedump + colortagend;

								results.append(" | " + iovDump);
							}
						} catch (final ComaQueryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					String holedump = "";
					if (ivr.getIshole()) {
						colortagstart = colorwarntagstart;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
						}
						holedump = "[" + timespan + "] ";
						try {
							Boolean missingPhysicsRuns = false;
							final BigDecimal since = new BigDecimal(ivr.getSince());
							final String cacheKey = since.longValue() + "-"
									+ ivr.getUntil();
							if (skippedrunsCacheMap.containsKey(cacheKey)) {
								final List<CrViewRuninfo> skippedruns = skippedrunsCacheMap
										.get(cacheKey);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							} else {
								final List<CrViewRuninfo> skippedruns = checkHoles(ivr,
										iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, skippedruns);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							}
							if (missingPhysicsRuns) {
								colortagstart = colorbadtagstart;
							}
						} catch (final ComaQueryException e) {
							e.printStackTrace();
						}
					}
				
					iovDump = colortagstart + ivr.getNiovs() + " ["
							+ ivr.getSinceCoolStr() + "] [" + ivr.getUntilCoolStr()
							+ "] " + holedump + colortagend;

					results.append(" | " + iovDump);
					iiov++;
				}
				results.append("</p>");
			}
		}
		return results.toString();
	}

	@Override
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public String dumpIovSummaryAsText(final Collection<CoolIovSummary> iovsummaryColl,
			final BigDecimal since, final BigDecimal until) {

		final StringBuffer results = new StringBuffer();
		final Map<String, List<CrViewRuninfo>> skippedrunsCacheMap = new HashMap<String, List<CrViewRuninfo>>();

		if (since.longValue() == 0L && until.longValue() >= CoolIov.COOL_MAX_DATE) {
			return dumpIovSummaryAsText(iovsummaryColl);
		}

		// List<NodeGtagTagType> nodeingtagList = null;
		final String colorgoodtagstart = "<span style=\"color:#20D247\">";
		final String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		final String colorseptagstart = "<span style=\"color:#1A91C4\">";
		final String colorbadtagstart = "<span style=\"color:#B43613\">";
		final String colorsingleiovstart = "<span style=\"color:#2E64FE\">";
		final String colortagend = "</span>";

		// results.append("<h1>Iovs statistics.... </h1>");

		final int channels = iovsummaryColl.size();
		final Iterator<CoolIovSummary> it = iovsummaryColl.iterator();
		if (it.hasNext()) {
			final CoolIovSummary firstsumm = it.next();
			results.append("<hr><h2>" + colorseptagstart + firstsumm.getSchema() + " > "
					+ " " + firstsumm.getNode() + " ; " + firstsumm.getTag()
					+ colortagend + "</h2>");
		}
		results.append("<h3>Total of used channels is " + channels + " </h3>");
		results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

		for (final CoolIovSummary iovsummary : iovsummaryColl) {
			results.append("<p class=\"small\">" + iovsummary.getChanId() + " "
					+ iovsummary.getChannelName() + " - " + iovsummary.getIovbase()
					+ " : ");

			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			final Integer niovranges = timeranges.size();
			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				final long minsince = iovsummary.getMinsince();
				int iiov = 0;
				for (final Long asince : sincetimes) {

					final IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					if (iiov == 0 && ivr.getSince().compareTo(minsince) != 0) {
						colortagstart = colorwarntagstart;
					}
					if (ivr.getNiovs() == 1 && iiov == 0 && niovranges == 1) {
						colortagstart = colorsingleiovstart;
					}

					if (iiov == 0 && ivr.getSince() > 0) {
						// Check against the first valid run number for 2010: 152166
						final BigDecimal lsince = since;
						final BigDecimal luntil = new BigDecimal(ivr.getSince());
						final String cacheKey = lsince.longValue() + "-"
								+ luntil.longValue();
						log.fine("first iovrange: " + ivr.getSince() + " "
								+ ivr.getUntil() + " check for hole in " + lsince + " "
								+ luntil);
						try {
							Boolean missingPhysicsRuns = false;
							if (luntil.longValue() > lsince.longValue()) {
							if (skippedrunsCacheMap.containsKey(cacheKey)) {
								final List<CrViewRuninfo> runinhole = skippedrunsCacheMap
										.get(cacheKey);
								if (runinhole.size() > 0) {
									missingPhysicsRuns = true;
								}
							} else {
								final List<CrViewRuninfo> runinhole = checkHoles(lsince,
										luntil, iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, runinhole);
								if (runinhole.size() > 0) {
									missingPhysicsRuns = true;
								}
							}
							}
							if (missingPhysicsRuns) {
								//colortagstart = colorbadtagstart;
								long timespan = luntil.longValue() - lsince.longValue();
								if (iovsummary.getIovbase().equals("time")) {
									timespan = timespan / 1000L;
								}
								String holedump = "[" + timespan + "] ";
								iovDump = colorbadtagstart + "1" + " ["
										+ since + "] [" + ivr.getSinceCoolStr()
										+ "] " + holedump + colortagend;

								results.append(" | " + iovDump);
							}
							log.fine("Hole in IOV0 with color " + colortagstart
									+ " and missing run key " + cacheKey);
						} catch (final ComaQueryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					String holedump = "";
					if (ivr.getIshole()) {
						colortagstart = colorwarntagstart;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
						}
						holedump = "[" + timespan + "] ";
						try {
							Boolean missingPhysicsRuns = false;
							final BigDecimal lsince = new BigDecimal(ivr.getSince());
							final String cacheKey = lsince.longValue() + "-"
									+ ivr.getUntil();
							if (skippedrunsCacheMap.containsKey(cacheKey)) {
								final List<CrViewRuninfo> skippedruns = skippedrunsCacheMap
										.get(cacheKey);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							} else {
								final List<CrViewRuninfo> skippedruns = checkHoles(ivr,
										iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, skippedruns);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							}

							if (missingPhysicsRuns) {
								colortagstart = colorbadtagstart;
							}
						} catch (final ComaQueryException e) {
							e.printStackTrace();
						}
					}
					
					iovDump = colortagstart + ivr.getNiovs() + " ["
							+ ivr.getSinceCoolStr() + "] [" + ivr.getUntilCoolStr()
							+ "] " + holedump + colortagend;

					results.append(" | " + iovDump);
					iiov++;
				}
				results.append("</p>");
			}
		}
		return results.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#dumpIovSummaryAsSvg(java.util.Collection)
	 */
	@Override
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public String dumpIovSummaryAsSvg(final Collection<CoolIovSummary> iovsummaryColl) {

		final StringBuffer results = new StringBuffer();
		final StringBuffer svg = new StringBuffer();

		final Map<String, List<CrViewRuninfo>> skippedrunsCacheMap = new HashMap<String, List<CrViewRuninfo>>();

		final String colorseptagstart = "<span style=\"color:#1A91C4\">";
		final String colortagend = "</span>";
	
		int channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();
			// results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");
			final Iterator<CoolIovSummary> it = iovsummaryColl.iterator();
			if (it.hasNext()) {
				final CoolIovSummary firstsumm = it.next();
				results.append("<hr><h2>" + colorseptagstart + firstsumm.getSchema()
						+ " > " + " " + firstsumm.getNode() + " ; " + firstsumm.getTag()
						+ colortagend + "</h2>");
			}
		} else {
			results.append("<h3>Collection of iovs summaries is null</h3>");
			return results.toString();
		}
		final SvgRestUtils svgutil = new SvgRestUtils();
		svgutil.setSvgabsmin(0L);
		svgutil.setSvgabsmax(CoolIov.COOL_MAX_DATE);
		// Reset IOV for Run1: 2010/01/01 - 2013/03/01
		if (channels < 20) {
			svgutil.setLinewidth(10);
		} else if (channels < 100) {
			svgutil.setLinewidth(6);
		} else if (channels < 300) {
			svgutil.setLinewidth(3);
		} else if (channels < 600) {
			svgutil.setLinewidth(2);
		} else {
			svgutil.setLinewidth(1);
		}
		results.append("<p>Number of channels used " + channels);
		final String svgcanvas = "<svg width=\"" + svgutil.getSvglinewidth()
				+ "px\" height=\""
				+ (channels * svgutil.getLinewidth() + svgutil.getSvgheight())
				+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
		svg.append(svgcanvas);
		Long ichan = 0L;
		for (final CoolIovSummary iovsummary : iovsummaryColl) {

			int iiov = 0;
			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (ichan == 0) {
				results.append(" | Info in ichan 0: niovs=" + iovsummary.getTotalIovs()
						+ " from " + iovsummary.getMinsince() + " / "
						+ iovsummary.getMinuntil() + " to " + iovsummary.getMaxsince()
						+ " / " + iovsummary.getMaxuntil() + "</p><br>");

				svgutil.computeBestRange(iovsummary.getMinsince(),
						iovsummary.getMinuntil(), iovsummary.getMaxsince(),
						iovsummary.getMaxuntil(), iovsummary.getIovbase());
			}
			log.fine("Node " + iovsummary.getNode() + " tag " + iovsummary.getTag()
					+ ": Chan " + iovsummary.getChanId() + " is using svgmin "
					+ svgutil.getSvgabsmin() + " and svgmax " + svgutil.getSvgabsmax()
					+ " from " + iovsummary.getMinsince() + " "
					+ iovsummary.getMinuntil() + " " + iovsummary.getMaxsince());
			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				final int nranges = timeranges.size();
				for (final Long asince : sincetimes) {
					final IovRange ivr = timeranges.get(asince);

					String color = "green";
					if (ivr.getNiovs() == 1 && !ivr.getIshole() && iiov == 0
							&& nranges == 1) {
						color = "blue";
					}

					if (iiov == 0 && ivr.getSince() > 0) {
						// Check against the first valid run number for 2010: 152166
						final BigDecimal since = new BigDecimal(0L);
						final BigDecimal until = new BigDecimal(ivr.getSince());
						final String cacheKey = since.longValue() + "-"
								+ until.longValue();
						try {
							Boolean missingPhysicsRuns = false;
							if (skippedrunsCacheMap.containsKey(cacheKey)) {
								final List<CrViewRuninfo> runinhole = skippedrunsCacheMap
										.get(cacheKey);
								if (runinhole.size() > 0) {
									missingPhysicsRuns = true;
								}
							} else {
								final List<CrViewRuninfo> runinhole = checkHoles(since,
										until, iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, runinhole);
								if (runinhole.size() > 0) {
									missingPhysicsRuns = true;
								}
							}
							if (missingPhysicsRuns) {
								//color = "red";
								svg.append(svgutil.getSvgLine(0L, ivr.getSince(), ichan,
										iovsummary.getIovbase(), true, "red"));
							}
						} catch (final ComaQueryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (ivr.getIshole()) {
						color = "orange";
						try {
							
							final String cacheKey = ivr.getSince() + "-" + ivr.getUntil();
							Boolean missingPhysicsRuns = false;
							if (skippedrunsCacheMap.containsKey(cacheKey)) {
								final List<CrViewRuninfo> skippedruns = skippedrunsCacheMap
										.get(cacheKey);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							} else {
								final List<CrViewRuninfo> skippedruns = checkHoles(ivr,
										iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, skippedruns);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							}
							if (missingPhysicsRuns) {
								color = "red";
							}

						} catch (final ComaQueryException e) {
							e.printStackTrace();
						}
					}

					svg.append(svgutil.getSvgLine(ivr.getSince(), ivr.getUntil(), ichan,
							iovsummary.getIovbase(), ivr.getIshole(), color));
					iiov++;
				}
			}
			ichan++;
		}
		results.append(svg.toString() + "</svg><br>");
		svg.delete(0, svg.length());
		// results.append("</body>");
		return results.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#dumpIovSummaryAsSvg(java.util.Collection)
	 */
	@Override
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public String dumpIovSummaryAsSvg(final Collection<CoolIovSummary> iovsummaryColl,
			final BigDecimal since, final BigDecimal until) {

		final StringBuffer results = new StringBuffer();
		final StringBuffer svg = new StringBuffer();
		final Map<String, List<CrViewRuninfo>> skippedrunsCacheMap = new HashMap<String, List<CrViewRuninfo>>();

		if (since.longValue() == 0L && until.longValue() >= CoolIov.COOL_MAX_DATE) {
			return dumpIovSummaryAsSvg(iovsummaryColl);
		}

		final String colorseptagstart = "<span style=\"color:#1A91C4\">";
		final String colortagend = "</span>";
	
		int channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();

			// results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");
			final Iterator<CoolIovSummary> it = iovsummaryColl.iterator();
			if (it.hasNext()) {
				final CoolIovSummary firstsumm = it.next();
				results.append("<hr><h2>" + colorseptagstart + firstsumm.getSchema()
						+ " > " + " " + firstsumm.getNode() + " ; " + firstsumm.getTag()
						+ colortagend + "</h2>");
			}
		} else {
			results.append("<h3>Collection of iovs summaries is null for this node "
					+ "</h3>");
			return results.toString();
		}
		final SvgRestUtils svgutil = new SvgRestUtils();
		svgutil.setSvgabsmin(since.longValue());
		svgutil.setSvgabsmax(until.longValue());
		if (channels < 20) {
			svgutil.setLinewidth(10);
		} else if (channels < 100) {
			svgutil.setLinewidth(6);
		} else if (channels < 300) {
			svgutil.setLinewidth(3);
		} else if (channels < 600) {
			svgutil.setLinewidth(2);
		} else {
			svgutil.setLinewidth(1);
		}
		results.append("<p>Number of channels used " + channels);
		final String svgcanvas = "<svg width=\"" + svgutil.getSvglinewidth()
				+ "px\" height=\""
				+ (channels * svgutil.getLinewidth() + svgutil.getSvgheight())
				+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
		svg.append(svgcanvas);
		Long ichan = 0L;
		for (final CoolIovSummary iovsummary : iovsummaryColl) {

			int iiov = 0;
			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (ichan == 0) {
				results.append(" | Info in ichan 0: niovs=" + iovsummary.getTotalIovs()
						+ " from " + iovsummary.getMinsince() + " / "
						+ iovsummary.getMinuntil() + " to " + iovsummary.getMaxsince()
						+ " / " + iovsummary.getMaxuntil() + "</p><br>");

			}
			log.fine("Node " + iovsummary.getNode() + " tag " + iovsummary.getTag()
					+ ": Chan " + iovsummary.getChanId() + " is using svgmin "
					+ svgutil.getSvgabsmin() + " and svgmax " + svgutil.getSvgabsmax()
					+ " from iovsummary (minsince,minuntil,maxsince) = "
					+ iovsummary.getMinsince() + " " + iovsummary.getMinuntil() + " "
					+ iovsummary.getMaxsince());

			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				final int nranges = timeranges.size();
				for (final Long asince : sincetimes) {
					final IovRange ivr = timeranges.get(asince);

					String color = "green";
					if (ivr.getNiovs() == 1 && !ivr.getIshole() && iiov == 0
							&& nranges == 1) {
						color = "blue";
					}

					if (iiov == 0 && ivr.getSince() > 0) {
						// final BigDecimal lsince = new
						// BigDecimal(Math.min(since.longValue(), ivr.getSince()));
						// final BigDecimal luntil = new
						// BigDecimal(Math.max(since.longValue(), ivr.getSince()));
						final BigDecimal lsince = since;
						final BigDecimal luntil = new BigDecimal(ivr.getSince());
						final String cacheKey = lsince.longValue() + "-"
								+ luntil.longValue();
						log.fine("first iovrange: " + ivr.getSince() + " "
								+ ivr.getUntil() + " check for hole in " + lsince + " "
								+ luntil);
						try {
							Boolean missingPhysicsRuns = false;
							if (luntil.longValue() > lsince.longValue()) {
								if (skippedrunsCacheMap.containsKey(cacheKey)) {
									final List<CrViewRuninfo> runinhole = skippedrunsCacheMap
											.get(cacheKey);
									if (runinhole.size() > 0) {
										missingPhysicsRuns = true;
									}
								} else {
									final List<CrViewRuninfo> runinhole = checkHoles(
											lsince, luntil, iovsummary.getIovbase());
									skippedrunsCacheMap.put(cacheKey, runinhole);
									if (runinhole.size() > 0) {
										missingPhysicsRuns = true;
									}
								}
								String usedcolor = color;
								if (missingPhysicsRuns) {
									//color = "red";
									usedcolor="red";
									svg.append(svgutil.getSvgLine(since.longValue(), ivr.getSince(), ichan,
											iovsummary.getIovbase(), true, "red"));
									
								}
								log.fine("Hole in IOV0 with color " + usedcolor
										+ " and missing run key " + cacheKey);
							}
						} catch (final ComaQueryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (ivr.getIshole()) {
						color = "orange";
						try {

							final String cacheKey = ivr.getSince() + "-" + ivr.getUntil();
							Boolean missingPhysicsRuns = false;
							if (skippedrunsCacheMap.containsKey(cacheKey)) {
								final List<CrViewRuninfo> skippedruns = skippedrunsCacheMap
										.get(cacheKey);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							} else {
								final List<CrViewRuninfo> skippedruns = checkHoles(ivr,
										iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, skippedruns);
								if (skippedruns.size() > 0) {
									missingPhysicsRuns = true;
								}
							}
							if (missingPhysicsRuns) {
								color = "red";
							}
							log.fine("Hole with color " + color + " and missing run key "
									+ cacheKey);
						} catch (final ComaQueryException e) {
							e.printStackTrace();
						}
					}

					svg.append(svgutil.getSvgLine(ivr.getSince(), ivr.getUntil(), ichan,
							iovsummary.getIovbase(), ivr.getIshole(), color));
					iiov++;
				}
			}
			ichan++;
		}
		results.append(svg.toString() + "</svg><br>");
		svg.delete(0, svg.length());
		// results.append("</body>");
		return results.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#checkHoles(java.util.Collection)
	 */
	@Override
	public String checkHoles(final Collection<CoolIovSummary> iovsummaryColl)
			throws ComaQueryException {
		final StringBuffer results = new StringBuffer();

		// TODO: Put this information somewhere else.
		// This code is a patch for MC checks on IOVs
		final Map<Integer, String> montecarloPatchMap = new HashMap<Integer, String>();
		// Fill the Map using Voica and David information
		montecarloPatchMap.put(152777, "2010 7 TeV for pp period A (very low mu)");
		montecarloPatchMap.put(155697, "2010 7 TeV for pp period C (mu~0.2)");
		montecarloPatchMap.put(154471, "2010 pp 900 GeV");
		montecarloPatchMap.put(162623,
				"2010 pp 7 TeV for period F (36 single bunches, mu~2)");
		montecarloPatchMap.put(167576, "2010 pp 7 TeV (150ns bunch trains)");
		montecarloPatchMap.put(170002, "2010 HI");
		montecarloPatchMap.put(178211, "2011 pp 2.76 TeV");
		montecarloPatchMap.put(180164, "2011 pp 7 TeV (B-D)");
		montecarloPatchMap.put(183003, "2011 pp 7 TeV (E-H)");
		montecarloPatchMap.put(186169, "2011 pp 7 TeV (I-K)");
		montecarloPatchMap.put(189751, "2011 pp 7 TeV (L-M)");
		montecarloPatchMap.put(193662, "2011 HI");
		montecarloPatchMap.put(195847, "2012 pp 8 TeV mc12a");
		montecarloPatchMap.put(195848, "2012 pp 8 TeV mc12b");
		montecarloPatchMap.put(200805, "2012 pp 8 TeV low mu A2");
		montecarloPatchMap.put(206881,
				"2012 pp 8 TeV beta*=90m, mu=0 (C5: 206881 - 206885)");
		montecarloPatchMap.put(210184, "2012 pPb");
		montecarloPatchMap.put(213268,
				"2012 pp 8 TeV beta*=1km, mu=0 (H5: 213264 - 213268)");
		montecarloPatchMap.put(216432,
				"2012 pp 8 TeV 25 ns running (M1: 216399 - 216432)");
		montecarloPatchMap.put(219114, "2013 pPb");
		montecarloPatchMap.put(219305, "2013 pp 2.76 TeV");

		// List<NodeGtagTagType> nodeingtagList = null;
		final String colorgoodtagstart = "<span style=\"color:#20D247\">";
		final String colorbadtagstart = "<span style=\"color:#B43613\">";
		final String colortagend = "</span>";

		if (iovsummaryColl == null) {
			throw new ComaQueryException("Cannot check null collection of summaries...");
		}
		List<CrViewRuninfo> runlist = null;
		final Map<String, List<CrViewRuninfo>> runMap = new HashMap<String, List<CrViewRuninfo>>();
		Boolean ishole = false;
		Boolean coverageerror = true;
		for (final CoolIovSummary iovsummary : iovsummaryColl) {
			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				int iiov = 0;
				for (final Long asince : sincetimes) {
					final IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					String holedump = "";
					// check against the first since for 2010 run number

					if (ivr.getIshole()) {
						ishole = true;
						colortagstart = colorbadtagstart;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
							holedump = "[" + timespan + "] ";
							final Timestamp lsince = new Timestamp(ivr.getSince()
									/ CoolIov.TO_NANOSECONDS);
							final Timestamp luntil = new Timestamp(ivr.getUntil()
									/ CoolIov.TO_NANOSECONDS);
							final String timekey = lsince.toString() + "/"
									+ luntil.toString();
							if (runMap.containsKey(timekey)) {
								runlist = runMap.get(timekey);
							} else {
								runlist = comadao.findRunsInRange(lsince, luntil);
								runMap.put(timekey, runlist);
							}

						} else if (iovsummary.getIovbase().equals("run-lumi")
								|| iovsummary.getIovbase().equals("run-event")) {
							Long runsince = CoolIov.getRun(ivr.getSince());
							final Long rununtil = CoolIov.getRun(ivr.getUntil());
							final Long lbsince = CoolIov.getLumi(ivr.getSince());

							timespan = ivr.getUntil() - ivr.getSince();
							if (timespan < CoolIov.COOL_MAX_LUMIBLOCK) {
								// this means that we have differences only at
								// LB range...
								// We want to ignore differences of 1 LB, when
								// diff is one
								if (lbsince == CoolIov.COOL_MAX_LUMIBLOCK
										&& timespan == 1) {
									// Consider 0 the hole
									timespan = 0;
									runsince = rununtil;
									ishole = false;
									continue;
								}
							}
							if (timespan > CoolIov.COOL_MAX_LUMIBLOCK) {
								timespan = rununtil - runsince;
							}
							holedump = "[" + timespan + "]";
							final BigDecimal lsince = new BigDecimal(runsince);
							final BigDecimal luntil = new BigDecimal(rununtil);
							// Verify holes with run ranges
							final String runkey = lsince.toString() + "/"
									+ luntil.toString();
							if (runMap.containsKey(runkey)) {
								runlist = runMap.get(runkey);
							} else {
								runlist = comadao.findRunsInRange(lsince, luntil);
								runMap.put(runkey, runlist);
							}
						}
						if (iiov == 0) {
							results.append("<p class=\"small\">" + iovsummary.getChanId()
									+ " " + iovsummary.getChannelName() + " - "
									+ iovsummary.getIovbase() + " : ");
						}
						iiov++;

						for (final CrViewRuninfo arun : runlist) {
							if (arun.getRunNumber().intValue() < 152166) {
								continue;
							}
							if (arun.getPPeriod() != null && arun.getPProject() != null) {
								if (arun.getPProject().startsWith("data")) {
									if (!montecarloPatchMap.containsKey(arun
											.getRunNumber().intValue())) {
										continue;
									}
									coverageerror = false;
									iovDump = colortagstart + ivr.getNiovs() + " ["
											+ arun.getRunNumber() + " "
											+ arun.getPProject() + "] " + holedump
											+ colortagend;

									results.append(" | " + iovDump);
								}
							}
						}
					}
				}
				if (ishole) {
					results.append("</p>");
				}
			}
		}
		if (coverageerror) {
			results.append("All relevant runs are covered...");
		}
		return results.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#checkHoles(atlas.cool.rest.model.IovRange,
	 * java.lang.String)
	 */
	@Override
	public List<CrViewRuninfo> checkHoles(final IovRange ivr, final String iovbase)
			throws ComaQueryException {

		final List<CrViewRuninfo> results = new ArrayList<CrViewRuninfo>();

		if (ivr == null) {
			throw new ComaQueryException("Cannot check null iovrange...");
		}
		List<CrViewRuninfo> runlist = null;
		final Map<String, List<CrViewRuninfo>> runMap = new HashMap<String, List<CrViewRuninfo>>();

		if (ivr.getIshole()) {
			long timespan = ivr.getUntil() - ivr.getSince();
			if (iovbase.equals("time")) {
				final Timestamp lsince = new Timestamp(ivr.getSince()
						/ CoolIov.TO_NANOSECONDS);
				final Timestamp luntil = new Timestamp(ivr.getUntil()
						/ CoolIov.TO_NANOSECONDS);
				final String timekey = lsince.toString() + "/" + luntil.toString();
				if (runMap.containsKey(timekey)) {
					runlist = runMap.get(timekey);
				} else {
					log.fine("Retrieve runlist using time from COMA using " + lsince
							+ " " + luntil);
					runlist = comadao.findRunsInRange(lsince, luntil);
					runMap.put(timekey, runlist);
				}
			} else if (iovbase.startsWith("run-")) {
				Long runsince = CoolIov.getRun(ivr.getSince());
				final Long rununtil = CoolIov.getRun(ivr.getUntil());
				final Long lbsince = CoolIov.getLumi(ivr.getSince());
				timespan = ivr.getUntil() - ivr.getSince();
				if (timespan < CoolIov.COOL_MAX_LUMIBLOCK) {
					// this means that we have differences only at
					// LB range...
					// We want to ignore differences of 1 LB, when
					// diff is one
					if (lbsince == CoolIov.COOL_MAX_LUMIBLOCK && timespan == 1) {
						// Consider 0 the hole
						timespan = 0;
						runsince = rununtil;
						return results;
					}
				}
				if (timespan > CoolIov.COOL_MAX_LUMIBLOCK) {
					timespan = rununtil - runsince;
				}
				final BigDecimal lsince = new BigDecimal(runsince);
				final BigDecimal luntil = new BigDecimal(rununtil);
				// Verify holes with run ranges
				final String runkey = lsince.toString() + "/" + luntil.toString();
				if (runMap.containsKey(runkey)) {
					runlist = runMap.get(runkey);
				} else {
					runlist = comadao.findRunsInRange(lsince, luntil);
					log.fine("Retrieve runlist using run from COMA using " + lsince + " "
							+ luntil + ": nruns "
							+ (runlist != null ? runlist.size() : " null list!"));
					runMap.put(runkey, runlist);
				}
			}
			for (final CrViewRuninfo arun : runlist) {
				if (arun.getRunNumber().intValue() < 152166) {
					continue;
				}
				log.fine("Potential matching run " + arun.getRunNumber() + " "
						+ arun.getPProject());
				if (arun.getPPeriod() != null && arun.getPProject() != null) {
					if (arun.getPProject().startsWith("data")) {
						results.add(arun);
					}
				}
			}
		}
		return results;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#checkHoles(java.math.BigDecimal,
	 * java.math.BigDecimal, java.lang.String)
	 */
	@Override
	public List<CrViewRuninfo> checkHoles(final BigDecimal since, final BigDecimal until,
			final String iovbase) throws ComaQueryException {

		final List<CrViewRuninfo> results = new ArrayList<CrViewRuninfo>();

		if (since == null || until == null) {
			throw new ComaQueryException("Cannot check null iovrange...");
		}
		List<CrViewRuninfo> runlist = null;
		final Map<String, List<CrViewRuninfo>> runMap = new HashMap<String, List<CrViewRuninfo>>();
		long timespan = until.longValue() - since.longValue();
		if (iovbase.equals("time")) {
			final Timestamp lsince = new Timestamp(since.longValue()
					/ CoolIov.TO_NANOSECONDS);
			final Timestamp luntil = new Timestamp(until.longValue()
					/ CoolIov.TO_NANOSECONDS);
			final String timekey = lsince.toString() + "/" + luntil.toString();
			if (runMap.containsKey(timekey)) {
				runlist = runMap.get(timekey);
			} else {
				log.info("checkHoles [time]: retrieve runlist from COMA using " + lsince + " " + luntil);
				runlist = comadao.findRunsInRange(lsince, luntil);
				runMap.put(timekey, runlist);
			}
		} else if (iovbase.startsWith("run")) {
			Long runsince = CoolIov.getRun(since.longValue());
			final Long rununtil = CoolIov.getRun(until.longValue());
			final Long lbsince = CoolIov.getLumi(since.longValue());
			if (timespan < CoolIov.COOL_MAX_LUMIBLOCK) {
				// this means that we have differences only at
				// LB range...
				// We want to ignore differences of 1 LB, when
				// diff is one
				if (lbsince == CoolIov.COOL_MAX_LUMIBLOCK && timespan == 1) {
					// Consider 0 the hole
					timespan = 0;
					runsince = rununtil;
					return results;
				}
			}
			if (timespan > CoolIov.COOL_MAX_LUMIBLOCK) {
				timespan = rununtil - runsince;
			}
			final BigDecimal lsince = new BigDecimal(runsince);
			final BigDecimal luntil = new BigDecimal(rununtil);
			// Verify holes with run ranges
			final String runkey = lsince.toString() + "/" + luntil.toString();
			if (runMap.containsKey(runkey)) {
				runlist = runMap.get(runkey);
			} else {
				log.info("checkHoles [run-xxx]: retrieve runlist from COMA using " + lsince + " " + luntil);
				runlist = comadao.findRunsInRange(lsince, luntil);
				runMap.put(runkey, runlist);
			}
		}

		for (final CrViewRuninfo arun : runlist) {
			if (arun.getPPeriod() != null && arun.getPProject() != null) {
				log.fine("Found potential matching with run "
						+ arun.getRunNumber().intValue() + " " + arun.getPProject());
				if (arun.getRunNumber().intValue() < 152166) {
					continue;
				}
				if (arun.getPProject().startsWith("data")) {
					results.add(arun);
				}
			}
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#getTimeRange(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Map<String, Object> getTimeRange(final String since, final String until,
			final String timespan) throws CoolIOException {
		// Time selection
		final Map<String, Object> timerangeMap = new HashMap<String, Object>();
		BigDecimal lsince = null;
		BigDecimal luntil = null;
		String outputformat = "time";
		try {
			if (since.equals("0") && until.equals("Inf")) {
				// Select full range of COOL IOVs
				lsince = new BigDecimal(0L);
				luntil = new BigDecimal(CoolIov.COOL_MAX_DATE);
				outputformat = "fullspan";
			} else {
				if (timespan.equals("time")) {
					// Interpret field as BigDecimal
					lsince = new BigDecimal(since);
					luntil = new BigDecimal(until);
				} else if (timespan.equals("date")) {
					// Interpret fields as dates in the yyyyMMddhhmmss format
					final Date st = df.parse(since);
					final Date ut = df.parse(until);
					lsince = new BigDecimal(st.getTime() * CoolIov.TO_NANOSECONDS);
					luntil = new BigDecimal(ut.getTime() * CoolIov.TO_NANOSECONDS);
				} else if (timespan.equals("datetime")) {
					// Interpret fields as dates in the yyyyMMddhhmmss format
					final Date st = df.parse(since);
					final Date ut = df.parse(until);
					lsince = new BigDecimal(st.getTime());
					luntil = new BigDecimal(ut.getTime());
				} else if (timespan.equals("runlb")) {
					final String[] sinceargs = since.split("-");
					final String[] untilargs = until.split("-");

					String lbstr = null;
					if (sinceargs.length > 0 && !sinceargs[1].isEmpty()) {
						lbstr = sinceargs[1];
					}
					lsince = CoolIov.getCoolRunLumi(sinceargs[0], lbstr);

					lbstr = null;
					if (untilargs.length > 0 && !untilargs[1].isEmpty()) {
						lbstr = untilargs[1];
					}
					luntil = CoolIov.getCoolRunLumi(untilargs[0], lbstr);
					outputformat = "run";
				} else if (timespan.equals("runtime")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						final BigDecimal runstart = new BigDecimal(since);
						final BigDecimal runend = new BigDecimal(until);
						results = comadao.findRunsInRange(runstart, runend);
						if (results.size() > 0) {
							final Timestamp runsince = results.get(0).getStartTime();
							Timestamp rununtil = results.get(0).getEndTime();
							if (results.size() > 1) {
								rununtil = results.get(results.size() - 1).getEndTime();
							}
							lsince = new BigDecimal(runsince.getTime()
									* CoolIov.TO_NANOSECONDS);
							luntil = new BigDecimal(rununtil.getTime()
									* CoolIov.TO_NANOSECONDS);
							timerangeMap.put("runlist", results);
						}
					} catch (final ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (timespan.equals("runlbtime")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						final BigDecimal runlbstart = new BigDecimal(since);
						final BigDecimal runlbend = new BigDecimal(until);
						final Long runstart = CoolIov.getRun(runlbstart.toBigInteger());
						final Long runend = CoolIov.getRun(runlbend.toBigInteger());
						results = comadao.findRunsInRange(new BigDecimal(runstart),
								new BigDecimal(runend));
						if (results.size() > 0) {
							final Timestamp runsince = results.get(0).getStartTime();
							Timestamp rununtil = results.get(0).getEndTime();
							if (results.size() > 1) {
								rununtil = results.get(results.size() - 1).getEndTime();
							}
							lsince = new BigDecimal(runsince.getTime()
									* CoolIov.TO_NANOSECONDS);
							luntil = new BigDecimal(rununtil.getTime()
									* CoolIov.TO_NANOSECONDS);
							timerangeMap.put("runlist", results);
						}
					} catch (final ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (timespan.equals("daterun")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						final Date st = df.parse(since);
						final Date ut = df.parse(until);
						results = comadao.findRunsInRange(new Timestamp(st.getTime()),
								new Timestamp(ut.getTime()));
						if (results.size() > 0) {
							final Long run = results.get(0).getRunNumber().longValue();
							lsince = CoolIov.getCoolRunLumi(run.toString(), "0");
							Long endrun = run + 1L;
							luntil = CoolIov.getCoolRunLumi(endrun.toString(), "0");
							if (results.size() > 1) {
								endrun = results.get(results.size() - 1).getRunNumber()
										.longValue();
								endrun += 1L;
								luntil = CoolIov.getCoolRunLumi(endrun.toString(), "0");
							}
							outputformat = "run";
							timerangeMap.put("runlist", results);
						}
					} catch (final ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (timespan.equals("timerunlb")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						final Date st = new Date(new BigDecimal(since).longValue()
								/ CoolIov.TO_NANOSECONDS);
						final Date ut = new Date(new BigDecimal(until).longValue()
								/ CoolIov.TO_NANOSECONDS);
						results = comadao.findRunsInRange(new Timestamp(st.getTime()),
								new Timestamp(ut.getTime()));
						if (results.size() > 0) {
							final Long run = results.get(0).getRunNumber().longValue();
							lsince = CoolIov.getCoolRunLumi(run.toString(), "0");
							Long endrun = run + 1L;
							luntil = CoolIov.getCoolRunLumi(endrun.toString(), "0");
							if (results.size() > 1) {
								endrun = results.get(results.size() - 1).getRunNumber()
										.longValue();
								endrun += 1L;
								luntil = CoolIov.getCoolRunLumi(endrun.toString(), "0");
							}
							outputformat = "run";
							timerangeMap.put("runlist", results);
						}
					} catch (final ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					throw new CoolIOException("Cannot search using timespan " + timespan);
				}
			}
			if (lsince == null || luntil == null) {
				throw new CoolIOException("Cannot use timerange with null iov times...");
			}
			if (lsince.longValue() > luntil.longValue()) {
				log.log(Level.SEVERE, "Until time preceeds Since time...!!!!");
				throw new CoolIOException("Cannot query DB with this range (since before until)...");
			}
			timerangeMap.put("since", lsince);
			timerangeMap.put("until", luntil);
			timerangeMap.put("iovbase", outputformat);
			log.info("Converted " + since + " to " + lsince + " and " + until + " to "
					+ luntil + " output " + outputformat);
		} catch (final ParseException e) {
			throw new CoolIOException(e.getMessage());
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
		return timerangeMap;
	}

	
	
	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#getNemoTimeRange(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getNemoTimeRange(String since, String until,
			String timespan) throws CoolIOException {
		final Map<String, Object> timerangeMap = new HashMap<String, Object>();

		BigDecimal lsince = null;
		BigDecimal luntil = null;
		Date datesince = null;
		Date dateuntil = null;
		Date runmindate = null;
		Date runmaxdate = null;
		Integer runmin = null;
		Integer runmax = null;
		
		String outputformat = "all";
		timerangeMap.put("coolsince", "null");
		timerangeMap.put("cooluntil", "null");
		timerangeMap.put("coolrunmin", "null");
		timerangeMap.put("coolrunmax", "null");
		timerangeMap.put("since", "null");
		timerangeMap.put("until", "null");
		timerangeMap.put("runmin", "null");
		timerangeMap.put("runmax", "null");
		timerangeMap.put("runmindate", "null");
		timerangeMap.put("runmaxdate", "null");
		timerangeMap.put("sincedate", "null");
		timerangeMap.put("untildate", "null");
		try {
			List<NemoRun> nemolist = null;

			if (since.equals("0")) {
				lsince = new BigDecimal(0L);
				datesince = new Date(0);
				since = df.format(datesince);
				nemolist = comarundao.getNemoRunList(datesince, datesince);
				if (nemolist != null && nemolist.size()>0) {
					runmin = nemolist.get(0).getRun();
					runmindate = nemolist.get(0).getRunSorDate();
				}
			}
			if (until.equals("Inf")) {
				if (timespan.equals("date") || timespan.equals("time")) {
					luntil = new BigDecimal(CoolIov.COOL_MAX_DATE);
					dateuntil = new Date(CoolIov.COOL_MAX_DATE_MILLISECONDS);
					until = df.format(dateuntil);
					nemolist = comarundao.getNemoRunList(dateuntil, dateuntil);
				} else {
					until = "99999999";
				}
				if (nemolist != null && nemolist.size()>0) {
					runmax = nemolist.get(nemolist.size()-1).getRun();
					runmaxdate = nemolist.get(nemolist.size()-1).getRunSorDate();
				}
			}
			
			if (timespan.equals("time")) {
				// Consider input time in cool format: nanoseconds
				lsince = new BigDecimal(since);
				luntil = new BigDecimal(until);
				
				if (lsince.longValue() > luntil.longValue()) {
					log.log(Level.SEVERE, "Until time preceeds Since time...!!!!");
					throw new CoolIOException("Cannot query DB with this range (since before until)...");
				}

				datesince = new Date(new Long(lsince.longValue() / 1000000L));
				dateuntil = new Date(new Long(luntil.longValue() / 1000000L));

				log.info("Searching runs using date "+datesince+" "+dateuntil);
				nemolist = comarundao.getNemoRunList(datesince, dateuntil);
				log.info("Retrieved list of runs "+(nemolist == null ? "none" : nemolist.size()));
				
			} else if (timespan.equals("date")) {
				// Consider input time as a date string in YYYYMMddHHmmss
				datesince = df.parse(since);
				dateuntil = df.parse(until);
				lsince = new BigDecimal(datesince.getTime() * CoolIov.TO_NANOSECONDS);
				luntil = new BigDecimal(dateuntil.getTime() * CoolIov.TO_NANOSECONDS);
				if (lsince.longValue() > luntil.longValue()) {
					log.log(Level.SEVERE, "Until time preceeds Since time...!!!!");
					throw new CoolIOException("Cannot query DB with this range (since before until)...");
				}
				log.info("Searching runs using date "+datesince+" "+dateuntil);
				nemolist = comarundao.getNemoRunList(datesince, dateuntil);
				log.info("Retrieved list of runs "+(nemolist == null ? "none" : nemolist.size()));
			} else if (timespan.equals("run")) {
				// Consider input time as run number
				lsince = null;
				luntil = null;
				datesince = null;
				dateuntil = null;
				runmin = new Integer(since);
				runmax = new Integer(until);
				nemolist = comarundao.getNemoRunList(runmin, runmax);
				log.info("Retrieved nemo run list using "+runmin+" "+runmax+" size "+nemolist.size());
			} else if (timespan.equals("coolrun")) {
				// Consider input time as run number
				lsince = null;
				luntil = null;
				datesince = null;
				dateuntil = null;
				
				runmin = CoolIov.getRun(new BigInteger(since)).intValue();
				runmax = CoolIov.getRun(new BigInteger(until)).intValue();
				nemolist = comarundao.getNemoRunList(runmin, runmax);
				log.info("Retrieved nemo run list using "+runmin+" "+runmax+" size "+nemolist.size());
			}
			
			if (nemolist != null && nemolist.size()>0) {
				runmin = nemolist.get(0).getRun();
				runmindate = nemolist.get(0).getRunSorDate();
				runmax = nemolist.get(nemolist.size()-1).getRun();
				runmaxdate = nemolist.get(nemolist.size()-1).getRunSorDate();
				if (lsince == null || luntil == null) {
					datesince = runmindate;
					dateuntil = runmaxdate;
					lsince = new BigDecimal(datesince.getTime()*CoolIov.TO_NANOSECONDS);
					luntil = new BigDecimal(dateuntil.getTime()*CoolIov.TO_NANOSECONDS);
				}
			}

			
			timerangeMap.put("coolsince", lsince);
			timerangeMap.put("cooluntil", luntil);
			if (datesince != null && dateuntil != null) {
				timerangeMap.put("since", datesince.getTime()/1000L);
				timerangeMap.put("until", dateuntil.getTime()/1000L);
				timerangeMap.put("sincedate", datesince.toString());
				timerangeMap.put("untildate", dateuntil.toString());
			}
			if (runmin != null && runmax != null) {
				timerangeMap.put("runmin", runmin);
				timerangeMap.put("runmax", runmax);
				timerangeMap.put("runmindate", runmindate.toString());
				timerangeMap.put("runmaxdate", runmaxdate.toString());
				timerangeMap.put("coolrunmin", CoolIov.getCoolRun(runmin.toString()));
				timerangeMap.put("coolrunmax", CoolIov.getCoolRun(runmax.toString()));
			}
			timerangeMap.put("iovbase", outputformat);
			log.info("Converted " + since + " to " + lsince + " and " + until + " to "
					+ luntil + " output " + outputformat);
		} catch (final ParseException e) {
			throw new CoolIOException(e.getMessage());
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
		return timerangeMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listIovsDiffInNodesSchemaTagRangeAsList(java.lang.String
	 * , java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listIovsDiffInNodesSchemaTagRangeAsList(final String schema,
			final String db, final String fld, final String tag1, final String tag2,
			final Long chanid, final BigDecimal since, final BigDecimal until)
			throws CoolIOException {
		final NodeType nodefortag1 = listIovsInNodesSchemaTagRangeAsList(schema, db, fld,
				tag1, chanid, since, until);
		final NodeType nodefortag2 = listIovsInNodesSchemaTagRangeAsList(schema, db, fld,
				tag2, chanid, since, until);

		final List<CoolIovType> iovtag1list = nodefortag1.getIovList();
		final List<CoolIovType> iovtag2list = nodefortag2.getIovList();
		final List<CoolIovType> iovtagdifflist = new ArrayList<CoolIovType>();

		int iovdiff = 0;
		for (int iiov = 0; iiov < Math.min(iovtag1list.size(), iovtag2list.size()); iiov++) {
			final CoolIovType iov1 = iovtag1list.get(iiov);
			final CoolIovType iov2 = iovtag2list.get(iiov);
			if (!iov1.equals(iov2)) {
				iovtagdifflist.add(iov1);
				iovtagdifflist.add(iov2);
				iovdiff++;
			}
		}
		log.info("Found " + iovdiff + " different iov in range " + since.longValue()
				+ " " + until.longValue());
		nodefortag1.setIovList(iovtagdifflist);
		return nodefortag1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listIovsDiffInNodesSchemaTagRangeAsList(java.lang.String
	 * , java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listIovsDiffInNodesSchemaTagRangeAsList(final String schema,
			final String db, final String fld, final String tag1, final String tag2,
			final String channel, final BigDecimal since, final BigDecimal until)
			throws CoolIOException {
		final NodeType nodefortag1 = listIovsInNodesSchemaTagRangeAsList(schema, db, fld,
				tag1, channel, since, until);
		final NodeType nodefortag2 = listIovsInNodesSchemaTagRangeAsList(schema, db, fld,
				tag2, channel, since, until);

		final List<CoolIovType> iovtag1list = nodefortag1.getIovList();
		final List<CoolIovType> iovtag2list = nodefortag2.getIovList();
		final List<CoolIovType> iovtagdifflist = new ArrayList<CoolIovType>();

		int iovdiff = 0;
		for (int iiov = 0; iiov < Math.min(iovtag1list.size(), iovtag2list.size()); iiov++) {
			final CoolIovType iov1 = iovtag1list.get(iiov);
			final CoolIovType iov2 = iovtag2list.get(iiov);
			if (!iov1.equals(iov2)) {
				iovtagdifflist.add(iov1);
				iovtagdifflist.add(iov2);
				iovdiff++;
			}
		}
		log.info("Found " + iovdiff + " different iov in range " + since.longValue()
				+ " " + until.longValue());
		nodefortag1.setIovList(iovtagdifflist);
		return nodefortag1;
	}

}
