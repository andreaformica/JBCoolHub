/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.meta.CoolIov;
import atlas.cool.meta.CoolPayload;
import atlas.cool.meta.CoolPayloadTransform;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

@Named
@Stateless
@Local(CoolUtilsDAO.class)
/**
 * @author formica
 *
 */
public class CoolUtilsBean implements CoolUtilsDAO {

	@Inject
	private CoolRepository coolrep;

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolPayloadDAO payloaddao;

	@Inject
	private Logger log;

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
	public Map<Long, CoolIovSummary> computeIovSummaryMap(String schema,
			String db, String node, String tag, String iovbase)
			throws CoolIOException {
		try {

			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(schema,
							db, node, tag);

			Map<Long, CoolIovSummary> iovsummary = getSummary(iovperchanList,
					schema, db, node, tag, iovbase);
			return iovsummary;
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected Map<Long, CoolIovSummary> getSummary(
			List<IovType> iovperchanList, String schema, String db,
			String node, String tag, String iovbase) {

		Map<Long, CoolIovSummary> iovsummary = new HashMap<Long, CoolIovSummary>();

		for (IovType aniov : iovperchanList) {
			// log.info("Analyze iov from DB : "+aniov.getChannelId()+" "+aniov.getNiovs());
			aniov.setIovBase(iovbase);
			Double isvalid = aniov.getIovHole().doubleValue();
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
						until = CoolIov.getTime(aniov.getHoleUntil()
								.toBigInteger());
					} else {
						until = CoolIov.getRun(aniov.getHoleUntil()
								.toBigInteger());
					}
					iovsumm.appendIov(since, until, 0L, true);
				}
				iovsummary.put(aniov.getChannelId(), iovsumm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return iovsummary;
	}

	protected Map<Long, CoolIovSummary> getSummaryOrigTime(
			List<IovType> iovperchanList, String schema, String db,
			String node, String tag, String iovbase) {

		Map<Long, CoolIovSummary> iovsummary = new HashMap<Long, CoolIovSummary>();

		for (IovType aniov : iovperchanList) {
			log.info("Analyze iov from DB : " + aniov.getChannelId() + " "
					+ aniov.getNiovs());
			aniov.setIovBase(iovbase);
			Double isvalid = aniov.getIovHole().doubleValue();
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return iovsummary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#computeIovSummaryRangeMap(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public Map<Long, CoolIovSummary> computeIovSummaryRangeMap(String schema,
			String db, String node, String tag, String iovbase,
			BigDecimal since, BigDecimal until) throws CoolIOException {

		try {

			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(
							schema, db, node, tag, since, until);

			Map<Long, CoolIovSummary> iovsummary = getSummaryOrigTime(
					iovperchanList, schema, db, node, tag, iovbase);
			return iovsummary;
		} catch (CoolIOException e) {
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
	public List<NodeType> listNodesInSchema(String schema, String db)
			throws CoolIOException {
		log.info("Calling listNodesInSchema..." + schema + " " + db);
		List<NodeType> results = null;
		results = cooldao.retrieveNodesFromSchemaAndDb(schema + "%", db, "%");
		if (results == null) {
			// create a fake entry
			NodeType nt = new NodeType();
			nt.setNodeId(1L);
			nt.setNodeFullpath("this is a fake node");
			nt.setNodeTinstime(new Timestamp(new Date().getTime()));
			List<NodeType> _fakes = new ArrayList<NodeType>();
			_fakes.add(nt);
			results = _fakes;
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#listTagsInNodesSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<SchemaNodeTagType> listTagsInNodesSchema(String schema,
			String db, String node) throws CoolIOException {
		log.info("Calling listTagsInNodeSchema..." + schema + " " + db + " "
				+ node);
		List<SchemaNodeTagType> results = null;

		if (node.equals("all")) {
			node = "%";
		} else {
			node = "%" + node + "%";
		}
		results = cooldao.retrieveTagsFromNodesSchemaAndDb(schema + "%", db,
				node, null);

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CoolUtilsDAO#getIovStatPerChannel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<IovType> getIovStatPerChannel(String schema, String db,
			String fld, String tag) throws CoolIOException {
		log.info("Calling getIovStatPerChannel..." + schema + " " + db + " "
				+ fld + " " + tag);
		List<IovType> results = null;

		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}
		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		NodeType selnode = null;
		if (nodes != null && nodes.size() > 0) {
			for (NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		results = cooldao.retrieveIovStatPerChannelFromNodeSchemaAndDb(schema,
				db, node, seltag);
		for (IovType aniov : results) {
			aniov.setIovBase(selnode.getNodeIovBase());
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listIovsSummaryInNodesSchemaTagRunRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRunRangeAsList(
			String schema, String db, String fld, String tag, String since,
			String until) throws CoolIOException {
		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..."
				+ schema + " " + db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;

		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		NodeType selnode = null;
		if (nodes != null && nodes.size() > 0) {
			for (NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}

		Map<Long, CoolIovSummary> iovsummary = computeIovSummaryRangeMap(
				schema, db, node, seltag, selnode.getNodeIovBase(),
				CoolIov.getCoolRun(since), CoolIov.getCoolRun(until));

		summarylist = iovsummary.values();

		return summarylist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listIovsInNodesSchemaTagRangeAsList(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public NodeType listIovsInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, String channel, String since,
			String until) throws CoolIOException {
		log.info("Calling listIovsInNodesSchemaTagRangeAsList..." + schema
				+ " " + db + " folder " + fld + " tag " + tag + " " + channel
				+ " " + since + " " + until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		String chan = "%" + channel + "%";
		if (channel.equals("all")) {
			chan = "%";
		}
		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		if (nodes != null && nodes.size() > 0) {
			for (NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}

		if (since.contains("-")) {
			String[] sinceargs = since.split("-");
			String[] untilargs = until.split("-");
			String lbstr = null;
			if (sinceargs.length > 0 && !sinceargs[1].isEmpty()) {
				lbstr = sinceargs[1];
			}
			BigDecimal _since = CoolIov.getCoolRunLumi(sinceargs[0], lbstr);
			lbstr = null;
			if (untilargs.length > 0 && !untilargs[1].isEmpty()) {
				lbstr = untilargs[1];
			}
			BigDecimal _until = CoolIov.getCoolRunLumi(untilargs[0],
					sinceargs[1]);
			iovlist = cooldao.retrieveIovsFromNodeSchemaAndDbInRangeByChanName(
					schema, db, node, seltag, chan, _since, _until);
			selnode.setIovList(iovlist);
		} else {
			return null;
		}

		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listPayloadInNodesSchemaTagRangeAsList(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listPayloadInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, String channel,
			BigDecimal since, BigDecimal until) throws CoolIOException {
		log.info("Calling listPayloadInNodesSchemaTagRangeAsList..." + schema
				+ " " + db + " folder " + fld + " tag " + tag + " " + channel
				+ " " + since + " " + until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		Integer chan = 0;
		if (channel.equals("all")) {
			chan = null;
		} else {
			chan = new Integer(channel);
		}
		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		if (nodes != null && nodes.size() > 0) {
			for (NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}

		CoolPayload payload = payloaddao.getPayloadsObj(schema, db, node,
				seltag, since, until, chan);
		iovlist = new CoolPayloadTransform(payload).getIovsWithPayload();
		selnode.setIovList(iovlist);

		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listIovsSummaryInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			String schema, String db, String fld, String tag, 
			BigDecimal since, BigDecimal until) throws CoolIOException {
		log.info("Calling listIovsSummaryInNodesSchemaTagRangeAsList..."
				+ schema + " " + db + " folder " + fld + " tag " + tag);
		Collection<CoolIovSummary> summarylist = null;
		String node = fld;
		if (!fld.startsWith("/")) {
			node = "/" + fld;
		}
		List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema, db,
				node);
		NodeType selnode = null;
		if (nodes != null && nodes.size() > 0) {
			for (NodeType anode : nodes) {
				log.info("Found " + anode.getNodeFullpath() + " of type "
						+ anode.getNodeIovType());
				selnode = anode;
			}
		}
		String seltag = tag;
		if (tag.equals("none")) {
			seltag = null;
		}

		Map<Long, CoolIovSummary> iovsummary = computeIovSummaryRangeMap(
				schema, db, node, seltag, selnode.getNodeIovBase(), since,
				until);

		summarylist = iovsummary.values();

		return summarylist;
	}

}
