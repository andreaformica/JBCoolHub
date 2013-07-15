package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.Collection;
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
import atlas.cool.rest.model.NodeGtagTagType;
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 60)
	public void synchroIovSummary(final String globaltag, final String db)
			throws CoolIOException {
		final int maxschemas = 999;
		try {

			List<NodeGtagTagType> nodeingtagList = null;
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb("ATLAS_COOL%", db,
					globaltag);
			int icount = 0;

			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				final String schema = nodeGtagTagType.getSchemaName();
				if (icount++ > maxschemas) {
					return;
				}
				log.info("Found schema " + schema + " and node "
						+ nodeGtagTagType.getNodeFullpath() + " ... search for iovs!");
				try {

					final Collection<CoolIovSummary> summarylist = coolutilsdao
							.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
									nodeGtagTagType.getNodeFullpath(),
									nodeGtagTagType.getTagName(), new BigDecimal(0L),
									new BigDecimal(CoolIov.COOL_MAX_DATE));
					if (summarylist == null) {
						continue;
					}
					updateTable(nodeGtagTagType.getGtagName(), summarylist);
					coolrep.flush();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

	/**
	 * @param globaltag
	 * @param summarylist
	 */
	protected void updateTable(final String globaltag,
			final Collection<CoolIovSummary> summarylist) {

		int icount = 0;
		for (final CoolIovSummary iovsummary : summarylist) {
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
					summary.setCoolChannelName(iovsummary.getChannelName());
					summary.setCoolGlobalTagName(globaltag);
					summary.setCoolSummary(iovsummary.getSummary());
					summary.setCoolMiniovsince(new BigDecimal(iovsummary.getMinsince()));
					summary.setCoolMaxiovsince(new BigDecimal(iovsummary.getMaxsince()));
					summary.setCoolMiniovuntil(new BigDecimal(iovsummary.getMinuntil()));
					summary.setCoolMaxiovuntil(new BigDecimal(iovsummary.getMaxuntil()));
					summary.setCoolTotaliovs(new BigDecimal(iovsummary.getTotalIovs()));
					// log.info("Updating " + summary.toString());
					if (icount % 100 == 0 && iovsummary.getIovlist() != null) {
						log.info("Updating " + summary.toString() + " with iovranges "
								+ iovsummary.getIovlist().size());
					}
					synchroIovRanges(summary, iovsummary.getIovlist());
				} else {
					// if not, store entry
					final atlas.cool.summary.model.CoolIovSummary summary = new atlas.cool.summary.model.CoolIovSummary();
					summary.setDb(db);
					summary.setSchemaName(schema);
					summary.setCoolNodeFullpath(node);
					summary.setCoolNodeIovbase(iovsummary.getIovbase());
					summary.setCoolTagName(tag);
					summary.setCoolGlobalTagName(globaltag);
					summary.setCoolChannelId(new BigDecimal(iovsummary.getChanId()));
					summary.setCoolChannelName(iovsummary.getChannelName());
					summary.setCoolSummary(iovsummary.getSummary());
					summary.setCoolMiniovsince(new BigDecimal(iovsummary.getMinsince()));
					summary.setCoolMaxiovsince(new BigDecimal(iovsummary.getMaxsince()));
					summary.setCoolMiniovuntil(new BigDecimal(iovsummary.getMinuntil()));
					summary.setCoolMaxiovuntil(new BigDecimal(iovsummary.getMaxuntil()));
					summary.setCoolTotaliovs(new BigDecimal(iovsummary.getTotalIovs()));
					// log.info("Inserting " + summary.toString());
					coolrep.persist(summary);
					coolrep.flush();
					synchroIovRanges(summary, iovsummary.getIovlist());
				}
			} catch (final CoolIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.dao.CondToolsDAO#synchroIovRanges(atlas.cool.summary.model.CoolIovSummary
	 * , java.util.Collection)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void synchroIovRanges(final atlas.cool.summary.model.CoolIovSummary iovsumm,
			final Collection<IovRange> iovrangelist) throws CoolIOException {

		// final atlas.cool.summary.model.CoolIovSummary thesumm = coolrep.findObj(
		// atlas.cool.summary.model.CoolIovSummary.class,
		// iovsumm.getCoolIovsummaryId());
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
			final List<CoolIovRanges> dbranges = cooldao.findIovRangesList(
					iovsumm.getCoolIovsummaryId(), new BigDecimal(iovRange.getSince()));
			if (dbranges != null && dbranges.size() == 1) {
				log.info("Compare range with the DB ");
				final CoolIovRanges oldrange = dbranges.get(0);
				if (oldrange.getCoolIovrangeSince().equals(
						newrange.getCoolIovrangeSince())
						&& oldrange.getCoolIovrangeNiovs().equals(
								newrange.getCoolIovrangeNiovs())) {
					log.info("Iovrange is the same....do not update");
				}
			} else {
				// log.info("Cannot find range in DB, persist it");
				coolrep.persist(newrange);
			}
		}
		coolrep.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.dao.remote.CondToolsDAORemote#insertCoolIovRanges(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void insertCoolIovRanges(final String schema, final String db,
			final String node, final String tag) {
		try {

			final Collection<CoolIovSummary> summarylist = coolutilsdao
					.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, node, tag,
							new BigDecimal(0L), new BigDecimal(CoolIov.COOL_MAX_DATE));
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
						newrange.setCoolIovrangeNiovs(new BigDecimal(iovRange.getNiovs()));
						newrange.setCoolIovrangeSince(new BigDecimal(iovRange.getSince()));
						newrange.setCoolIovrangeUntil(new BigDecimal(iovRange.getUntil()));
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
	 * atlas.cool.dao.remote.CondToolsDAORemote#checkGlobalTagForSchemaDB(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 120)
	public void checkGlobalTagForSchemaDB(final String gtag, final String schema,
			final String db) throws CoolIOException {
		final int maxschemas = 9999;
		try {
			List<NodeGtagTagType> nodeingtagList = null;
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb(schema, db, gtag);
			int icount = 0;

			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				final String coolschema = nodeGtagTagType.getSchemaName();
				if (icount++ > maxschemas) {
					return;
				}
				log.info("Found schema " + coolschema + " and node "
						+ nodeGtagTagType.getNodeFullpath() + " ... search for iovs!");
				try {

					final Collection<CoolIovSummary> summarylist = coolutilsdao
							.listIovsSummaryInNodesSchemaTagRangeAsList(coolschema, db,
									nodeGtagTagType.getNodeFullpath(),
									nodeGtagTagType.getTagName(), new BigDecimal(0L),
									new BigDecimal(CoolIov.COOL_MAX_DATE));
					if (summarylist == null) {
						continue;
					}
					updateTable(nodeGtagTagType.getGtagName(), summarylist);
					coolrep.flush();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		} catch (final Exception e) {
			throw new CoolIOException(e.getMessage());
		}
	}

}
