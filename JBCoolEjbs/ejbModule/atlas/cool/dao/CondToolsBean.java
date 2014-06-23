package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.ejb3.annotation.TransactionTimeout;

import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.dao.remote.CondToolsDAORemote;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.LastModTimeType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.summary.model.CondNodeStats;
import atlas.cool.summary.model.CoolCoverage;
import atlas.cool.summary.model.CoolIovRanges;

/**
 * Session Bean implementation class CondToolsBean.
 */
@Named
@Stateless
@Local(CondToolsDAO.class)
@Remote(CondToolsDAORemote.class)
public class CondToolsBean implements CondToolsDAO, CondToolsDAORemote {

	/**
	 * 
	 */
	@Inject
	private CoolRepositoryDAO coolrep;

	/**
	 * 
	 */
	@Inject
	private CoolDAO cooldao;
	/**
	 * 
	 */
	@Inject
	private CoolUtilsDAO coolutilsdao;

	/**
	 * 
	 */
	@Inject
	private Logger log;

	/**
	 * Default constructor.
	 */
	public CondToolsBean() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CondToolsDAO#synchroIovSummary(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void synchroIovSummary(final String globaltag, final String db)
			throws CoolIOException {
		try {
			checkGlobalTagForSchemaDB(globaltag, "ATLAS_COOL%", db, false);
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/**
	 * @param globaltag
	 * @param summarylist
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 120)
	protected void updateTable(final String globaltag,
			final Collection<CoolIovSummary> summarylist) {

		int icount = 0;

		for (final CoolIovSummary iovsummary : summarylist) {

			// Standard loop over iovsummaries...
			final String schema = iovsummary.getSchema();
			final String db = iovsummary.getDb();
			final String node = iovsummary.getNode();
			final String tag = iovsummary.getTag();
			final Long channelid = iovsummary.getChanId();
			BigDecimal chanid = null;
			if (channelid != null) {
				chanid = new BigDecimal(channelid);
			}
			icount++;
			try {
				// check if entry exists in DB
				final List<atlas.cool.summary.model.CoolIovSummary> coolsummobjlist = cooldao
						.findIovSummaryList(schema, db, node, tag, chanid);
				if (coolsummobjlist != null && coolsummobjlist.size() > 0) {
					// if yes, update content
					final atlas.cool.summary.model.CoolIovSummary summary = coolsummobjlist
							.get(0);

					// Verify the total number of iovs...if it is equal skip the
					// update.
					final Long dbtotaliovs = summary.getCoolTotaliovs()
							.longValue();
					final Long newtotal = iovsummary.getTotalIovs();
					if (dbtotaliovs == newtotal) {
						log.fine("SKIP channel: Same number of total iovs for "
								+ iovsummary.getSchema() + " "
								+ iovsummary.getNode() + " "
								+ iovsummary.getChanId());
					} else {
						log.warning("Different number of total iovs for "
								+ iovsummary.getSchema() + " "
								+ iovsummary.getNode() + " "
								+ iovsummary.getChanId());
						// The number of iovs is not the same...update the
						// summary
						summary.setCoolChannelName(iovsummary.getChannelName());
						summary.setCoolGlobalTagName(globaltag);
						summary.setCoolSummary(iovsummary.getSummary());
						summary.setCoolMiniovsince(new BigDecimal(iovsummary
								.getMinsince()));
						summary.setCoolMaxiovsince(new BigDecimal(iovsummary
								.getMaxsince()));
						summary.setCoolMiniovuntil(new BigDecimal(iovsummary
								.getMinuntil()));
						summary.setCoolMaxiovuntil(new BigDecimal(iovsummary
								.getMaxuntil()));
						summary.setCoolTotaliovs(new BigDecimal(iovsummary
								.getTotalIovs()));
						// log.info("Updating " + summary.toString());
						// Merge the summary
						coolrep.merge(summary);
						// log message
						if (icount % 100 == 0
								&& iovsummary.getIovlist() != null) {
							log.info("Updating " + summary.toString()
									+ " with iovranges "
									+ iovsummary.getIovlist().size());
						}
						synchroIovRanges(summary, iovsummary.getIovlist());
					}
				} else {
					// if not, store entry
					final atlas.cool.summary.model.CoolIovSummary summary = new atlas.cool.summary.model.CoolIovSummary();
					summary.setDb(db);
					summary.setSchemaName(schema);
					summary.setCoolNodeFullpath(node);
					summary.setCoolNodeIovbase(iovsummary.getIovbase());
					summary.setCoolTagName(tag);
					summary.setCoolGlobalTagName(globaltag);
					summary.setCoolChannelId(new BigDecimal(iovsummary
							.getChanId()));
					summary.setCoolChannelName(iovsummary.getChannelName());
					summary.setCoolSummary(iovsummary.getSummary());
					summary.setCoolMiniovsince(new BigDecimal(iovsummary
							.getMinsince()));
					summary.setCoolMaxiovsince(new BigDecimal(iovsummary
							.getMaxsince()));
					summary.setCoolMiniovuntil(new BigDecimal(iovsummary
							.getMinuntil()));
					summary.setCoolMaxiovuntil(new BigDecimal(iovsummary
							.getMaxuntil()));
					summary.setCoolTotaliovs(new BigDecimal(iovsummary
							.getTotalIovs()));
					coolrep.persist(summary);
					if (icount % 100 == 0) {
						log.info("Inserting " + summary.toString());
						coolrep.flush();
						coolrep.clear();
					}
					synchroIovRanges(summary, iovsummary.getIovlist());
				}
				coolrep.flush();
			} catch (final CoolIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Synchro a collection of iovranges with the table cool_iov_range.
	 * 
	 * @param iovsumm
	 * @param iovrangelist
	 * @throws CoolIOException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 120)
	protected void synchroIovRanges(
			final atlas.cool.summary.model.CoolIovSummary iovsumm,
			final Collection<IovRange> iovrangelist) throws CoolIOException {

		final List<CoolIovRanges> dbrangeslist = cooldao.findIovRangesList(
				iovsumm.getCoolIovsummaryId(), null);
		if (dbrangeslist != null && dbrangeslist.size() == iovrangelist.size()) {
			log.fine("No difference in list size for iovranges...");
			return;
		}

		for (final IovRange iovRange : iovrangelist) {
			final CoolIovRanges newrange = new CoolIovRanges();
			newrange.setCoolIovbase(iovsumm.getCoolNodeIovbase());
			newrange.setCoolIovrangeIshole(iovRange.getIshole());
			newrange.setCoolIovrangeNiovs(new BigDecimal(iovRange.getNiovs()));
			newrange.setCoolIovrangeSince(new BigDecimal(iovRange.getSince()));
			newrange.setCoolIovrangeUntil(new BigDecimal(iovRange.getUntil()));
			newrange.setCoolIovsinceStr(iovRange.getSinceCoolStr());
			newrange.setCoolIovuntilStr(iovRange.getUntilCoolStr());
			newrange.setCoolIovSummary(iovsumm);
			// final List<CoolIovRanges> dbranges = cooldao.findIovRangesList(
			// iovsumm.getCoolIovsummaryId(), new
			// BigDecimal(iovRange.getSince()));
			/*
			 * if (dbranges != null && dbranges.size() == 1) { //
			 * log.info("Compare range with the DB "); final CoolIovRanges
			 * oldrange = dbranges.get(0); if (oldrange.equals(newrange)) {
			 * log.fine("Iovrange is the same....do not update"); } else {
			 * log.fine("Iovrange is not the same, should update it"); } } else
			 * { // log.info("Cannot find range in DB, persist it");
			 * coolrep.persist(newrange); }
			 */
		}
		// coolrep.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.remote.CondToolsDAORemote#insertCoolIovRanges(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void insertCoolIovRanges(final String schema, final String db,
			final String node, final String tag) {
		try {

			final Collection<CoolIovSummary> summarylist = coolutilsdao
					.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
							node, tag, new BigDecimal(0L), new BigDecimal(
									CoolIov.COOL_MAX_DATE));
			if (summarylist == null) {
				return;
			}
			for (final CoolIovSummary thesumm : summarylist) {
				final Collection<IovRange> iovrangestmp = thesumm.getIovlist();
				log.info("Explore summary from COOL " + thesumm);
				final List<atlas.cool.summary.model.CoolIovSummary> coolsummobjlist = cooldao
						.findIovSummaryList(schema, db, node, tag,
								new BigDecimal(thesumm.getChanId()));
				if (coolsummobjlist != null && coolsummobjlist.size() == 1) {
					log.info("Explore summary stored in CondTools "
							+ coolsummobjlist.get(0));
					final atlas.cool.summary.model.CoolIovSummary iovsumm = coolsummobjlist
							.get(0);
					for (final IovRange iovRange : iovrangestmp) {
						log.info("Iov range found " + iovRange);
						final CoolIovRanges newrange = new CoolIovRanges();
						newrange.setCoolIovbase(iovsumm.getCoolNodeIovbase());
						newrange.setCoolIovrangeIshole(iovRange.getIshole());
						newrange.setCoolIovrangeNiovs(new BigDecimal(iovRange
								.getNiovs()));
						newrange.setCoolIovrangeSince(new BigDecimal(iovRange
								.getSince()));
						newrange.setCoolIovrangeUntil(new BigDecimal(iovRange
								.getUntil()));
						newrange.setCoolIovsinceStr(iovRange.getSinceCoolStr());
						newrange.setCoolIovuntilStr(iovRange.getUntilCoolStr());
						newrange.setCoolIovSummary(iovsumm);
						// log.info("New Iov range added to list " + iovRange);
						coolrep.persist(newrange);
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.remote.CondToolsDAORemote#checkGlobalTagForSchemaDB(java
	 * .lang.String , java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void checkGlobalTagForSchemaDB(final String gtag,
			final String schema, final String db,
			final Boolean ignoreExistingSchemas) throws CoolIOException {
		final int maxschemas = 9999;
		try {
			List<NodeGtagTagType> nodeingtagList = null;
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema,
					db, gtag);
			int icount = 0;

			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				final String coolschema = nodeGtagTagType.getSchemaName();
				if (icount++ > maxschemas) {
					return;
				}
				// This list of objects from Cool_Iov_Summary table
				// should represent what we have already stored, nevertheless
				// we need to check if cool_iov_ranges are present.
				final List<atlas.cool.summary.model.CoolIovSummary> existingSchemaList = cooldao
						.findIovSummaryList(coolschema, db,
								nodeGtagTagType.getNodeFullpath(),
								nodeGtagTagType.getTagName(), null);
				// The following is by channel !!!
				if (existingSchemaList != null && existingSchemaList.size() > 0) {
					// skip schema if iov ranges are present
					// if not, flag it for update
					boolean toupdate = false;
					for (final atlas.cool.summary.model.CoolIovSummary coolIovSummary : existingSchemaList) {
						final Collection<CoolIovRanges> iovranges = coolIovSummary
								.getCoolIovRangeses();
						if (iovranges != null && iovranges.size() > 0) {
							log.info("Skipping schema " + coolschema + " node "
									+ nodeGtagTagType.getNodeFullpath()
									+ " tag " + nodeGtagTagType.getTagName());
							continue;
						} else {
							toupdate = true;
						}
					}
					if (toupdate) {
						log.info("Found schema " + coolschema + " node "
								+ nodeGtagTagType.getNodeFullpath() + " tag "
								+ nodeGtagTagType.getTagName()
								+ " with empty ranges...!");
						updateTableForNodeAndTag(nodeGtagTagType.getGtagName(),
								coolschema, db,
								nodeGtagTagType.getNodeFullpath(),
								nodeGtagTagType.getTagName());
					}
				} else {
					log.info("Insert new summary for " + coolschema + " " + db
							+ " " + nodeGtagTagType.getNodeFullpath() + " "
							+ nodeGtagTagType.getTagName());

					updateTableForNodeAndTag(nodeGtagTagType.getGtagName(),
							coolschema, db, nodeGtagTagType.getNodeFullpath(),
							nodeGtagTagType.getTagName());
				}
			}
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/**
	 * Utility function.
	 * 
	 * @param globaltag
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 120)
	protected void updateTableForNodeAndTag(final String globaltag,
			final String schema, final String db, final String node,
			final String tag) {
		try {
			log.info("Updating db for " + schema + " " + db + " " + node + " "
					+ tag);
			final Collection<CoolIovSummary> summarylist = coolutilsdao
					.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
							node, tag, new BigDecimal(0L), new BigDecimal(
									CoolIov.COOL_MAX_DATE));
			if (summarylist != null && summarylist.size() > 0) {
				log.info("retrieved summarylist of size " + summarylist.size());
				updateTable(globaltag, summarylist);
			} else {
				log.warning("Cannot retrieve list of summary IOVs...store only the cooliov summary as empty");
				final atlas.cool.summary.model.CoolIovSummary summary = new atlas.cool.summary.model.CoolIovSummary();
				summary.setCoolChannelName("unknown");
				summary.setCoolGlobalTagName(globaltag);
				summary.setCoolTagName(tag);
				summary.setCoolChannelId(new BigDecimal(-1));
				summary.setCoolNodeFullpath(node);
				summary.setSchemaName(schema);
				summary.setDb(db);
				summary.setCoolSummary("empty set of iovs");
				summary.setCoolMiniovsince(new BigDecimal(0));
				summary.setCoolMaxiovsince(new BigDecimal(0));
				summary.setCoolMiniovuntil(new BigDecimal(CoolIov.COOL_MAX_DATE));
				summary.setCoolMaxiovuntil(new BigDecimal(CoolIov.COOL_MAX_DATE));
				summary.setCoolTotaliovs(new BigDecimal(0));
				coolrep.persist(summary);
				coolrep.flush();
				log.info("Stored object " + summary);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CondToolsDAO#getNodeStatsForSchemaDb(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<CondNodeStats> getNodeStatsForSchemaDb(final String schema,
			final String db, final String gtag) throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = schema;
		params[1] = db;
		params[2] = "/%";
		log.info("Using query " + CondNodeStats.QUERY_NODESSTATINFO + " with "
				+ schema + " " + db);
		final List<CondNodeStats> statlist = (List<CondNodeStats>) coolrep
				.findCoolList(CondNodeStats.QUERY_NODESSTATINFO, params);
		return statlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CondToolsDAO#updateGlobalTagForSchemaDB(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void updateGlobalTagForSchemaDB(final String gtag,
			final String schema, final String db,
			final Boolean ignoreExistingSchemas) throws CoolIOException {
		// Add bookkeeping of update
		final CoolCoverage coolcovrun = new CoolCoverage(gtag);
		coolcovrun.setDbName(db);
		coolcovrun.setInsTime(new Date());
		coolcovrun.setCovComment("Init");
		checkGlobalTagForSchemaDB(gtag, schema, db, ignoreExistingSchemas);
		try {
			final CoolCoverage coolcov = findGlobalTagCoverage(gtag);
			if (coolcov == null) {
				coolrep.persist(coolcovrun);
				coolrep.commit();
			}
		} catch (final CoolIOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CondToolsDAO#insertCoverageInfo(java.lang.String,
	 * java.lang.String, java.util.Date, java.lang.Integer, java.lang.Integer,
	 * java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
	public void insertCoverageInfo(final String gtag, final String db,
			final Date instime, final Integer nupdschemas,
			final Integer nupdfolders, final String comment)
			throws CoolIOException {
		final CoolCoverage coolcov = new CoolCoverage(gtag, db, instime,
				nupdschemas, nupdfolders, comment);

		try {
			coolrep.persist(coolcov);
			coolrep.flush();
		} catch (final Exception e) {
			throw new CoolIOException(
					"Error in inserting cool coverage logging information :"
							+ e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CondToolsDAO#updateCoverageInfo(java.lang.String,
	 * java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
	public void updateCoverageInfo(final String gtag, String db,
			Integer nupdschemas, Integer nupdfolders, String comment)
			throws CoolIOException {

		final CoolCoverage coolcov = coolrep.findObj(CoolCoverage.class, gtag);
		try {

			if (coolcov == null) {
				if (gtag == null || db == null || nupdschemas == null
						|| nupdfolders == null) {
					log.severe("Cannot insert new coverage summary with null values...");
				}
				insertCoverageInfo(gtag, db, new Date(), nupdschemas,
						nupdfolders, comment);
			} else {
				// update the info
				log.info("Update information for " + gtag + " with comment "
						+ coolcov.getCovComment());
				if (db == null || nupdschemas == null || nupdfolders == null) {
					// get information from COOL_IOV_SUMMARY table
					log.info("Input fields are empty: get information from cool_iov_summary");
					final List<CoolCoverage> covlist = getSummaryCoverage(gtag);
					if (covlist != null && covlist.size() > 0) {
						log.fine("List of coverage of size " + covlist.size());
						final CoolCoverage newcoolcov = covlist.get(0);
						db = newcoolcov.getDbName();
						nupdfolders = newcoolcov.getnUpdatedFolders();
						nupdschemas = newcoolcov.getnUpdatedSchemas();
						comment = " Source:CoolIovSummary ";
						log.info("Filled fields from cool_iov_summary :"
								+ nupdfolders + " " + nupdschemas + " and db "
								+ db);
					}
				}
				log.info("Using input fields: " + nupdfolders + " "
						+ nupdschemas + " " + comment);
				coolcov.setDbName(db);
				coolcov.setnUpdatedFolders(nupdfolders);
				coolcov.setnUpdatedSchemas(nupdschemas);
				coolcov.setCovComment(coolcov.getCovComment() + "\n" + comment);
				coolrep.merge(coolcov);
			}
		} catch (final Exception e) {
			throw new CoolIOException("update coverage got exception "
					+ e.getMessage());
		}
	}

	/**
	 * Retrieve summary of coverage for bookkeeping.
	 * 
	 * @param gtag
	 * @return
	 * @throws Exception
	 */
	protected List<CoolCoverage> getSummaryCoverage(final String gtag)
			throws Exception {
		List<CoolCoverage> covlist = null;
		final Object[] params = new Object[1];
		params[0] = gtag;
		log.info("Using query " + CoolCoverage.QUERY_GETLOG + " with " + gtag);
		covlist = (List<CoolCoverage>) coolrep.findCoolList(
				CoolCoverage.QUERY_GETLOG, params);
		return covlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CondToolsDAO#findGlobalTagCoverage(java.lang.String)
	 */
	@Override
	public CoolCoverage findGlobalTagCoverage(final String gtag)
			throws CoolIOException {
		CoolCoverage coolcov = null;
		coolcov = coolrep.findObj(CoolCoverage.class, gtag);
		return coolcov;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.CondToolsDAO#findLastModTime(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date)
	 */
	@Override
	public List<LastModTimeType> findLastModTime(final String schema,
			final String db, final String node, final String tbl,
			final Date tmodsince) throws CoolIOException {
		List<LastModTimeType> modlist = null;
		final Object[] params = new Object[5];
		params[0] = schema+"%";
		params[1] = db;
		params[2] = "%"+node+"%";
		params[3] = tbl;
		params[4] = tmodsince;
		log.info("Using query " + LastModTimeType.QUERY_MODTABLE + " with "
				+ schema + "," + db + "," + node + "," + tbl + "," + tmodsince);
		modlist = (List<LastModTimeType>) coolrep.findCoolList(
				LastModTimeType.QUERY_MODTABLE, params);
		return modlist;
	}

}
