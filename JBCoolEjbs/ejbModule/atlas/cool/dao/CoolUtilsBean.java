/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.payload.model.CoolPayload;
import atlas.cool.payload.model.CoolPayloadTransform;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.utils.SvgRestUtils;

@Named
@Stateless
@Local(CoolUtilsDAO.class)
/**
 * @author formica
 *
 */
public class CoolUtilsBean implements CoolUtilsDAO {

	@Inject
	private CoolDAO cooldao;
	@Inject
	private ComaCbDAO comadao;
	@Inject
	private CoolPayloadDAO payloaddao;

	@Inject
	private Logger log;
	
	protected SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");


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
	public SortedMap<Long, CoolIovSummary> computeIovSummaryMap(String schema,
			String db, String node, String tag, String iovbase)
			throws CoolIOException {
		try {

			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(schema,
							db, node, tag);

			SortedMap<Long, CoolIovSummary> iovsummary = getSummary(iovperchanList,
					schema, db, node, tag, iovbase);
			return iovsummary;
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected SortedMap<Long, CoolIovSummary> getSummary(
			List<IovType> iovperchanList, String schema, String db,
			String node, String tag, String iovbase) {

		SortedMap<Long, CoolIovSummary> iovsummary = new TreeMap<Long, CoolIovSummary>();

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

	protected SortedMap<Long, CoolIovSummary> getSummaryOrigTime(
			List<IovType> iovperchanList, String schema, String db,
			String node, String tag, String iovbase) {

		SortedMap<Long, CoolIovSummary> iovsummary = new TreeMap<Long, CoolIovSummary>();

		for (IovType aniov : iovperchanList) {
			// log.info("Analyze iov from DB : " + aniov.getChannelId() + " "
			// + aniov.getNiovs());
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
	public SortedMap<Long, CoolIovSummary> computeIovSummaryRangeMap(String schema,
			String db, String node, String tag, String iovbase,
			BigDecimal since, BigDecimal until) throws CoolIOException {

		try {

			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(
							schema, db, node, tag, since, until);

			SortedMap<Long, CoolIovSummary> iovsummary = getSummaryOrigTime(
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
	 * atlas.cool.dao.CoolUtilsDAO#listIovsInNodesSchemaTagRangeAsList(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listIovsInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, String channel,
			BigDecimal since, BigDecimal until) throws CoolIOException {
		log.info("Calling listIovsInNodesSchemaTagRangeAsList..." + schema
				+ " " + db + " folder " + fld + " tag " + tag + " " + channel
				+ " " + since + " " + until);
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

		iovlist = cooldao.retrieveIovsFromNodeSchemaAndDbInRangeByChanName(
				schema, db, node, seltag, chan, since, until);
		selnode.setIovList(iovlist);

		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listIovsInNodesSchemaTagRangeAsList(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listIovsInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException {
		log.info("Calling listIovsInNodesSchemaTagRangeAsList..." + schema
				+ " " + db + " folder " + fld + " tag " + tag + " " + chanid
				+ " " + since + " " + until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

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

		iovlist = cooldao.retrieveIovsFromNodeSchemaAndDbInRangeByChanId(
				schema, db, node, seltag, chanid, since, until);
		selnode.setIovList(iovlist);

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

		try {
			String chan = "%" + channel + "%";
			if (channel.equals("all")) {
				chan = null;
			}
			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
					db, node);
			if (nodes != null && nodes.size() > 0) {
				for (NodeType anode : nodes) {
					log.fine("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}

			log.info("Retrieving payload " + payloaddao);
			CoolPayload payload = payloaddao.getPayloadsObj(schema, db, node,
					seltag, since, until, chan);
			iovlist = new CoolPayloadTransform(payload).getIovsWithPayload();
			if (iovlist != null)
				log.info("Retrieved iovlist of " + iovlist.size());
			selnode.setIovList(iovlist);
		} catch (Exception e) {
			// payloaddao.remove();
			throw new CoolIOException(e.getMessage());
		}
		return selnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CoolUtilsDAO#listPayloadInNodesSchemaTagRangeAsList(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public NodeType listPayloadInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException {
		log.info("Calling listPayloadInNodesSchemaTagRangeAsList..." + schema
				+ " " + db + " folder " + fld + " tag " + tag + " " + chanid
				+ " " + since + " " + until);
		List<CoolIovType> iovlist = null;
		NodeType selnode = null;

		try {
			Long chan = chanid;

			String node = fld;
			if (!fld.startsWith("/")) {
				node = "/" + fld;
			}
			List<NodeType> nodes = cooldao.retrieveNodesFromSchemaAndDb(schema,
					db, node);
			if (nodes != null && nodes.size() > 0) {
				for (NodeType anode : nodes) {
					log.fine("Found " + anode.getNodeFullpath() + " of type "
							+ anode.getNodeIovType());
					selnode = anode;
				}
			}
			String seltag = tag;
			if (tag.equals("none")) {
				seltag = null;
			}
			log.info("Retrieving payload " + payloaddao);
			CoolPayload payload = payloaddao.getPayloadsObj(schema, db, node,
					seltag, since, until, chan);
			iovlist = new CoolPayloadTransform(payload).getIovsWithPayload();
			selnode.setIovList(iovlist);
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
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
			String schema, String db, String fld, String tag, BigDecimal since,
			BigDecimal until) throws CoolIOException {
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

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#dumpIovSummaryAsText(java.util.Collection)
	 */
	public String dumpIovSummaryAsText(Collection<CoolIovSummary> iovsummaryColl) {

		StringBuffer results = new StringBuffer();

		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";

		results.append("<h1>Iovs statistics.... </h1>");

		int channels = iovsummaryColl.size();
		CoolIovSummary firstsumm = iovsummaryColl.iterator().next();
		results.append("<h2>" + colorseptagstart + firstsumm.getSchema()
				+ " > " + " " + firstsumm.getNode() + " ; "
				+ firstsumm.getTag() + colortagend + "</h2>" + "<br>");

		results.append("<h3>Total of used channels is " + channels + " </h3>");
		results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

		for (CoolIovSummary iovsummary : iovsummaryColl) {
			results.append("<p class=\"small\">" + iovsummary.getChanId() + " "
					+ iovsummary.getChannelName() + " - "
					+ iovsummary.getIovbase() + " : ");

			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				long minsince = iovsummary.getMinsince();
				int iiov = 0;
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					if ((iiov == 0)
							&& (ivr.getSince().compareTo(minsince) != 0)) {
						colortagstart = colorwarntagstart;
					}
					String holedump = "";
					if (ivr.getIshole()) {
						colortagstart = colorbadtagstart;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
						}
						holedump = "[" + timespan + "] ";
					}
					iovDump = colortagstart + ivr.getNiovs() + " ["
							+ ivr.getSinceCoolStr() + "] ["
							+ ivr.getUntilCoolStr() + "] " + holedump
							+ colortagend;

					results.append(" | " + iovDump);
					iiov++;
				}
				results.append("</p>");
			}
		}
//		results.append("</body>");
		return results.toString();
	}
	
	public String dumpIovSummaryAsText(Collection<CoolIovSummary> iovsummaryColl, BigDecimal since, BigDecimal until) {

		StringBuffer results = new StringBuffer();

		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorwarntagstart = "<span style=\"color:#D1A22C\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";

		results.append("<h1>Iovs statistics.... </h1>");

		int channels = iovsummaryColl.size();
		CoolIovSummary firstsumm = iovsummaryColl.iterator().next();
		results.append("<h2>" + colorseptagstart + firstsumm.getSchema()
				+ " > " + " " + firstsumm.getNode() + " ; "
				+ firstsumm.getTag() + colortagend + "</h2>" + "<br>");

		results.append("<h3>Total of used channels is " + channels + " </h3>");
		results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");

		for (CoolIovSummary iovsummary : iovsummaryColl) {
			results.append("<p class=\"small\">" + iovsummary.getChanId() + " "
					+ iovsummary.getChannelName() + " - "
					+ iovsummary.getIovbase() + " : ");

			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				long minsince = iovsummary.getMinsince();
				long maxuntil = iovsummary.getMaxuntil();

				if (minsince > since.longValueExact()) {
					colortagstart = colorbadtagstart;
					long timespan = minsince - since.longValue();
					if (iovsummary.getIovbase().equals("time")) {
						timespan = timespan / 1000L;
					}
					String holedump = "[" + timespan + "] ";
					iovDump = colortagstart +  "0 ["
						+ CoolIov.getCoolTimeRunLumiString(since.longValue(), iovsummary.getIovbase()) + "] ["
						+ CoolIov.getCoolTimeRunLumiString(minsince, iovsummary.getIovbase()) + "] " + holedump
						+ colortagend;

					results.append(" | " + iovDump);
				}
				int iiov = 0;
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					if ((iiov == 0)
							&& (ivr.getSince().compareTo(minsince) != 0)) {
						colortagstart = colorwarntagstart;
					}
					String holedump = "";
					if (ivr.getIshole()) {
						colortagstart = colorbadtagstart;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
						}
						holedump = "[" + timespan + "] ";
					}
					iovDump = colortagstart + ivr.getNiovs() + " ["
							+ ivr.getSinceCoolStr() + "] ["
							+ ivr.getUntilCoolStr() + "] " + holedump
							+ colortagend;

					results.append(" | " + iovDump);
					iiov++;
				}
				if (maxuntil < until.longValueExact()) {
					colortagstart = colorbadtagstart;
					long timespan = until.longValue() - maxuntil;
					if (iovsummary.getIovbase().equals("time")) {
						timespan = timespan / 1000L;
					}
					String holedump = "[" + timespan + "] ";
					iovDump = colortagstart +  "0 ["
						+ CoolIov.getCoolTimeRunLumiString(maxuntil, iovsummary.getIovbase()) + "] ["
						+ CoolIov.getCoolTimeRunLumiString(until.longValue(), iovsummary.getIovbase()) + "] " + holedump
						+ colortagend;

					results.append(" | " + iovDump);
				}

				results.append("</p>");
			}
		}
//		results.append("</body>");
		return results.toString();
	}


	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#dumpIovSummaryAsSvg(java.util.Collection)
	 */
	public String dumpIovSummaryAsSvg(Collection<CoolIovSummary> iovsummaryColl) {

		StringBuffer results = new StringBuffer();
		StringBuffer svg = new StringBuffer();

		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colortagend = "</span>";
//		results.append("<head><style>" + "h1 {font-size:25px;} "
//				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
//				+ "hr {color:sienna;}" + "p {font-size:14px;}"
//				+ "p.small {line-height:80%;}" + "</style></head>");

//		results.append("<body>");
		results.append("<h1>Iovs statistics.... </h1>");
		int channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();

			results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");
			Iterator<CoolIovSummary> it = iovsummaryColl.iterator();
			if (it.hasNext()) {
				CoolIovSummary firstsumm = it.next();
				results.append("<h2>" + colorseptagstart
						+ firstsumm.getSchema() + " > " + " "
						+ firstsumm.getNode() + " ; " + firstsumm.getTag()
						+ colortagend + "</h2>" + "<br>");
			}
		}
		SvgRestUtils svgutil = new SvgRestUtils();
		svgutil.setSvgabsmin(0L);
		svgutil.setSvgabsmax(CoolIov.COOL_MAX_DATE);
		if (channels < 20) {
			svgutil.setLinewidth(10);
		} else if (channels < 100) {
			svgutil.setLinewidth(6);
		} else if (channels < 300) {
			svgutil.setLinewidth(3);
		}  else if (channels < 600) {
			svgutil.setLinewidth(2);
		} else {
			svgutil.setLinewidth(1);
		}
		results.append("<p>Number of channels used " + channels);
		String svgcanvas = "<svg width=\"" + svgutil.getSvglinewidth()
				+ "px\" height=\""
				+ (channels * svgutil.getLinewidth() + svgutil.getSvgheight())
				+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
		svg.append(svgcanvas);
		Long ichan = 0L;
		for (CoolIovSummary iovsummary : iovsummaryColl) {

			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (ichan == 0) {
				results.append(" | Info in ichan 0: niovs="
						+ iovsummary.getTotalIovs() + " from "
						+ iovsummary.getMinsince() + " / "
						+ iovsummary.getMinuntil() + " to "
						+ iovsummary.getMaxsince() + " / "
						+ iovsummary.getMaxuntil() + "</p><br>");

				svgutil.computeBestRange(iovsummary.getMinsince(), iovsummary.getMinuntil(), 
						iovsummary.getMaxsince(), iovsummary.getMaxuntil());
			}
			log.fine("Node " + iovsummary.getNode() + " tag "
					+ iovsummary.getTag() + ": Chan " + iovsummary.getChanId()
					+ " is using svgmin " + svgutil.getSvgabsmin()
					+ " and svgmax " + svgutil.getSvgabsmax() + " from "
					+ iovsummary.getMinsince() + " " + iovsummary.getMinuntil()
					+ " " + iovsummary.getMaxsince());
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					svg.append(svgutil.getSvgLine(ivr.getSince(),
							ivr.getUntil(), ichan, iovsummary.getIovbase(),
							ivr.getIshole()));
				}
			}
			ichan++;
		}
		results.append(svg.toString() + "</svg><br>");
		svg.delete(0, svg.length());
//		results.append("</body>");
		return results.toString();
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#dumpIovSummaryAsSvg(java.util.Collection)
	 */
	public String dumpIovSummaryAsSvg(Collection<CoolIovSummary> iovsummaryColl, BigDecimal since, BigDecimal until) {

		StringBuffer results = new StringBuffer();
		StringBuffer svg = new StringBuffer();

		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colortagend = "</span>";
//		results.append("<head><style>" + "h1 {font-size:25px;} "
//				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
//				+ "hr {color:sienna;}" + "p {font-size:14px;}"
//				+ "p.small {line-height:80%;}" + "</style></head>");

//		results.append("<body>");
		results.append("<h1>Iovs statistics.... </h1>");
		int channels = 0;
		if (iovsummaryColl != null) {
			channels = iovsummaryColl.size();

			results.append("<h3>chanId chanName iovbase - niovs [since] [until] [holes in seconds] .... </h3>");
			Iterator<CoolIovSummary> it = iovsummaryColl.iterator();
			if (it.hasNext()) {
				CoolIovSummary firstsumm = it.next();
				results.append("<h2>" + colorseptagstart
						+ firstsumm.getSchema() + " > " + " "
						+ firstsumm.getNode() + " ; " + firstsumm.getTag()
						+ colortagend + "</h2>" + "<br>");
			}
		}
		SvgRestUtils svgutil = new SvgRestUtils();
		svgutil.setSvgabsmin(since.longValue());
		svgutil.setSvgabsmax(until.longValue());
		if (channels < 20) {
			svgutil.setLinewidth(10);
		} else if (channels < 100) {
			svgutil.setLinewidth(6);
		} else if (channels < 300) {
			svgutil.setLinewidth(3);
		}  else if (channels < 600) {
			svgutil.setLinewidth(2);
		} else {
			svgutil.setLinewidth(1);
		}
		results.append("<p>Number of channels used " + channels);
		String svgcanvas = "<svg width=\"" + svgutil.getSvglinewidth()
				+ "px\" height=\""
				+ (channels * svgutil.getLinewidth() + svgutil.getSvgheight())
				+ "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
		svg.append(svgcanvas);
		Long ichan = 0L;
		for (CoolIovSummary iovsummary : iovsummaryColl) {

			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (ichan == 0) {
				results.append(" | Info in ichan 0: niovs="
						+ iovsummary.getTotalIovs() + " from "
						+ iovsummary.getMinsince() + " / "
						+ iovsummary.getMinuntil() + " to "
						+ iovsummary.getMaxsince() + " / "
						+ iovsummary.getMaxuntil() + "</p><br>");

//				svgutil.computeBestRange(iovsummary.getMinsince(), iovsummary.getMinuntil(), 
//						iovsummary.getMaxsince(), iovsummary.getMaxuntil());
			}
			log.fine("Node " + iovsummary.getNode() + " tag "
					+ iovsummary.getTag() + ": Chan " + iovsummary.getChanId()
					+ " is using svgmin " + svgutil.getSvgabsmin()
					+ " and svgmax " + svgutil.getSvgabsmax() + " from "
					+ iovsummary.getMinsince() + " " + iovsummary.getMinuntil()
					+ " " + iovsummary.getMaxsince());
			if (timeranges != null) {
				long minsince = iovsummary.getMinsince();
				long maxuntil = iovsummary.getMaxuntil();

				if (minsince > since.longValueExact()) {
					svg.append(svgutil.getSvgLine(since.longValueExact(),
							minsince, ichan, iovsummary.getIovbase(),
							true));
				}

				Set<Long> sincetimes = timeranges.keySet();
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					svg.append(svgutil.getSvgLine(ivr.getSince(),
							ivr.getUntil(), ichan, iovsummary.getIovbase(),
							ivr.getIshole()));
				}
				if (maxuntil < until.longValueExact()) {
					svg.append(svgutil.getSvgLine(maxuntil,
							until.longValueExact(), ichan, iovsummary.getIovbase(),
							true));
				}

			}
			ichan++;
		}
		results.append(svg.toString() + "</svg><br>");
		svg.delete(0, svg.length());
//		results.append("</body>");
		return results.toString();
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#checkHoles(java.util.Collection)
	 */
	public String checkHoles(Collection<CoolIovSummary> iovsummaryColl)
			throws ComaQueryException {
		StringBuffer results = new StringBuffer();

		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";

		results.append("<h1>Coverage verification.... </h1>");

		if (iovsummaryColl != null) {
			Iterator<CoolIovSummary> it = iovsummaryColl.iterator();
			if (it.hasNext()) {
				CoolIovSummary firstsumm = it.next();
				results.append("<h2>" + colorseptagstart
						+ firstsumm.getSchema() + " > " + " "
						+ firstsumm.getNode() + " ; " + firstsumm.getTag()
						+ colortagend + "</h2>" + "<br>");
			}
		}

		Boolean ishole = false;
		Boolean coverageerror = true;
		for (CoolIovSummary iovsummary : iovsummaryColl) {
			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				int iiov = 0;
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					String holedump = "";
					if (ivr.getIshole()) {
						ishole = true;
						colortagstart = colorbadtagstart;
						List<CrViewRuninfo> runlist = null;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
							holedump = "[" + timespan + "] ";
							Timestamp _since = new Timestamp(ivr.getSince()
									/ CoolIov.TO_NANOSECONDS);
							Timestamp _until = new Timestamp(ivr.getUntil()
									/ CoolIov.TO_NANOSECONDS);
							runlist = comadao.findRunsInRange(_since, _until);

						} else if (iovsummary.getIovbase().equals("run-lumi")) {
							Long runsince = CoolIov.getRun(ivr.getSince());
							Long rununtil = CoolIov.getRun(ivr.getUntil());
							Long lbsince = CoolIov.getLumi(ivr.getSince());

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
							BigDecimal _since = new BigDecimal(runsince);
							BigDecimal _until = new BigDecimal(rununtil);
							// Verify holes with run ranges
							runlist = comadao.findRunsInRange(_since, _until);
						}
						if (iiov == 0) {
							results.append("<p class=\"small\">"
									+ iovsummary.getChanId() + " "
									+ iovsummary.getChannelName() + " - "
									+ iovsummary.getIovbase() + " : ");
						}
						iiov++;

						for (CrViewRuninfo arun : runlist) {
							if (arun.getPPeriod() != null
									&& arun.getPProject() != null) {
								if (arun.getPProject().startsWith("data")) {
									coverageerror = false;
									iovDump = colortagstart + ivr.getNiovs()
											+ " [" + arun.getRunNumber() + " "
											+ arun.getPProject() + "] "
											+ holedump + colortagend;

									results.append(" | " + iovDump);
								}
							}
						}
					}
				}
				if (ishole)
					results.append("</p>");
			}
		}
		if (coverageerror) {
			results.append("All relevant runs are covered...");
		}
		return results.toString();
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#getTimeRange(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Map<String, Object> getTimeRange(String since, String until,
			String timespan) throws CoolIOException {
		// Time selection
		Map<String, Object> timerangeMap = new HashMap<String, Object>();
		BigDecimal _since = null;
		BigDecimal _until = null;
		String outputformat="time";
		try {
			if (since.equals("0") && until.equals("Inf")) {
				// Select full range of COOL IOVs
				_since = new BigDecimal(0L);
				_until = new BigDecimal(CoolIov.COOL_MAX_DATE);
				outputformat="fullspan";
			} else {
				if (timespan.equals("time")) {
					// Interpret field as BigDecimal
					_since = new BigDecimal(since);
					_until = new BigDecimal(until);
				} else if (timespan.equals("date")) {
					// Interpret fields as dates in the yyyyMMddhhmmss format
					Date st = df.parse(since);
					Date ut = df.parse(until);
					_since = new BigDecimal(st.getTime()
							* CoolIov.TO_NANOSECONDS);
					_until = new BigDecimal(ut.getTime()
							* CoolIov.TO_NANOSECONDS);
				} else if (timespan.equals("runlb")) {
					String[] sinceargs = since.split("-");
					String[] untilargs = until.split("-");

					String lbstr = null;
					if (sinceargs.length > 0 && !sinceargs[1].isEmpty()) {
						lbstr = sinceargs[1];
					}
					_since = CoolIov.getCoolRunLumi(sinceargs[0], lbstr);

					lbstr = null;
					if (untilargs.length > 0 && !untilargs[1].isEmpty()) {
						lbstr = untilargs[1];
					}
					_until = CoolIov.getCoolRunLumi(untilargs[0], lbstr);
					outputformat="run-lumi";
				} else if (timespan.equals("runtime")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						BigDecimal runstart = new BigDecimal(since);
						BigDecimal runend = new BigDecimal(until);
						results = comadao.findRunsInRange(runstart, runend);
						if (results.size() > 0) {
							Timestamp runsince = results.get(0).getStartTime();
							Timestamp rununtil = results.get(0).getEndTime();
							if (results.size() > 1)
								rununtil = results.get(results.size() - 1)
										.getEndTime();
							_since = new BigDecimal(runsince.getTime()
									* CoolIov.TO_NANOSECONDS);
							_until = new BigDecimal(rununtil.getTime()
									* CoolIov.TO_NANOSECONDS);
						}
					} catch (ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (timespan.equals("runlbtime")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						BigDecimal runlbstart = new BigDecimal(since);
						BigDecimal runlbend = new BigDecimal(until);
						Long runstart = CoolIov.getRun(runlbstart.toBigInteger());
						Long runend = CoolIov.getRun(runlbend.toBigInteger());
						results = comadao.findRunsInRange(new BigDecimal(runstart), new BigDecimal(runend));
						if (results.size() > 0) {
							Timestamp runsince = results.get(0).getStartTime();
							Timestamp rununtil = results.get(0).getEndTime();
							if (results.size() > 1)
								rununtil = results.get(results.size() - 1)
										.getEndTime();
							_since = new BigDecimal(runsince.getTime()
									* CoolIov.TO_NANOSECONDS);
							_until = new BigDecimal(rununtil.getTime()
									* CoolIov.TO_NANOSECONDS);
						}
					} catch (ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (timespan.equals("daterun")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						Date st = df.parse(since);
						Date ut = df.parse(until);
						results = comadao.findRunsInRange(
								new Timestamp(st.getTime()),
								new Timestamp(ut.getTime()));
						if (results.size() > 0) {
							Long run = results.get(0).getRunNumber()
									.longValue();
							_since = CoolIov
									.getCoolRunLumi(run.toString(), "0");
							Long endrun = run + 1L;
							_until = CoolIov.getCoolRunLumi(endrun.toString(),
									"0");
							if (results.size() > 1) {
								endrun = results.get(results.size() - 1)
										.getRunNumber().longValue();
								endrun += 1L;
								_until = CoolIov.getCoolRunLumi(
										endrun.toString(), "0");
							}
							outputformat="run-lumi";
						}
					} catch (ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (timespan.equals("timerunlb")) {
					// Convert run request into time range given by start of
					// since run
					// and end of until run
					List<CrViewRuninfo> results = null;
					try {
						Date st = new Date(new BigDecimal(since).longValue()/CoolIov.TO_NANOSECONDS);
						Date ut = new Date(new BigDecimal(until).longValue()/CoolIov.TO_NANOSECONDS);
						results = comadao.findRunsInRange(
								new Timestamp(st.getTime()),
								new Timestamp(ut.getTime()));
						if (results.size() > 0) {
							Long run = results.get(0).getRunNumber()
									.longValue();
							_since = CoolIov
									.getCoolRunLumi(run.toString(), "0");
							Long endrun = run + 1L;
							_until = CoolIov.getCoolRunLumi(endrun.toString(),
									"0");
							if (results.size() > 1) {
								endrun = results.get(results.size() - 1)
										.getRunNumber().longValue();
								endrun += 1L;
								_until = CoolIov.getCoolRunLumi(
										endrun.toString(), "0");
							}
							outputformat="run-lumi";
						}
					} catch (ComaQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					throw new CoolIOException("Cannot search using timespan "
							+ timespan);
				}
			}
			if (_since.longValue()>_until.longValue()) {
				log.log(Level.SEVERE, "Until time preceeds Since time...!!!!");
				throw new CoolIOException("Cannot query DB with this range...");
			}
			timerangeMap.put("since", _since);
			timerangeMap.put("until", _until);
			timerangeMap.put("iovbase", outputformat);
			log.info("Converted "+since+" to "+_since+" and "+until+" to "+_until+" output "+outputformat);
		} catch (ParseException e) {
			throw new CoolIOException(e.getMessage());
		} catch (Exception e) {
			throw new CoolIOException(e.getMessage());
		}
		return timerangeMap;
	}

}
