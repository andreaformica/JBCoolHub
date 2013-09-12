package atlas.cool.dao.mdb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.dao.CondToolsDAO;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.dao.remote.CondToolsDAORemote;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.utils.MailSender;
import atlas.cool.rest.utils.UrlLinkUtils;

/**
 * Message-Driven Bean implementation class for: GlobalTagCoverageMDB.
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "gtagCoverageQueue") }, mappedName = "gtagCoverageQueue")
@TransactionManagement(value = TransactionManagementType.CONTAINER)
@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
public class GlobalTagCoverageMDB implements MessageListener {

	@Inject
	private ComaCbDAO comadao;
	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolUtilsDAO coolutilsdao;
	@Inject
	private MailSender mailsender;
	@Inject
	private CoolRepositoryDAO coolrep;
	@Inject
	private CondToolsDAO condtools;

	@Inject
	private Logger log;

	private final List<CoolIovSummary> badcooliovsummary = new ArrayList<CoolIovSummary>();

	private List<String> destAddrs = null;

	/**
	 * Default constructor.
	 */
	public GlobalTagCoverageMDB() {

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onMessage(final Message message) {
		try {
			if (message instanceof ObjectMessage) {
				final GtagCoverage jmsmess = (GtagCoverage) ((ObjectMessage) message)
						.getObject();
				final String checktype = (String) (message.getStringProperty("checkType"));
				final String db = (String) (message.getStringProperty("db"));
				if (jmsmess.getDestAddrs() != null) {
					destAddrs = jmsmess.getDestAddrs();
				}
				log.info("Start analyzing coverage for tag " + jmsmess.getGlobaltagname());
				checkCoverage(jmsmess.getGlobaltagname(), checktype, db);
			} else {
				log.log(Level.WARNING, "Cannot treat message of unknown type");
			}
		} catch (final JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param globaltagname
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	protected void checkCoverage(final String globaltagname, final String checkType, final String adb) {
		String db = "COMP200";
		final int maxschemas = 999;
		final Date starttime = new Date();
		try {
			if (adb != null) {
				db = adb;
			}
			if (checkType.equals("store")) {
				log.info("Launch process to store new data associated to "+globaltagname+" from "+db);
				condtools.updateGlobalTagForSchemaDB(globaltagname, "ATLAS_COOL%", db, true);
				return;
			}
			

			List<NodeGtagTagType> nodeingtagList = null;
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb("ATLAS_COOL%", db,
					globaltagname);
			int icount = 0;
			final StringBuffer buf = new StringBuffer();

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
					//synchroSummaryTable(nodeGtagTagType.getGtagName(), summarylist);
					
					buf.append("Analyzed " + schema + " " + db + " "
							+ nodeGtagTagType.getNodeFullpath() + " "
							+ nodeGtagTagType.getTagName() + " summary list of size "
							+ summarylist.size() + "\n");
					log.info("Found collection of iovsummary for tag "
							+ nodeGtagTagType.getTagName() + " of size "
							+ summarylist.size());
					if (hasHoles(summarylist)) {
						log.info("WARN>>> schema " + schema + " node "
								+ nodeGtagTagType.getNodeFullpath() + " tag "
								+ nodeGtagTagType.getTagName() + " has holes...!!!");
						final CoolIovSummary firstofbad = summarylist.iterator().next();
						badcooliovsummary.add(firstofbad);

					}
				} catch (final CoolIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			final Date endtime = new Date();
			buf.append("\n Total time for the summary analysis of global tag "
					+ globaltagname + " was ~ "
					+ (endtime.getTime() - starttime.getTime()) / (1000L * 60)
					+ " minutes \n");
			if (badcooliovsummary.size() > 0) {
				buf.append("\n================== List of Holes overlapping with data* runs =============\n\n");
			}
			for (final CoolIovSummary iovsumm : badcooliovsummary) {
				buf.append("Found holes in " + iovsumm.getSchema() + " "
						+ iovsumm.getNode() + " " + iovsumm.getTag() + " : ");
				buf.append(UrlLinkUtils.createLink(iovsumm.getSchema(), iovsumm.getDb(),
						iovsumm.getNode(), iovsumm.getTag(),
						"0/Inf/time/rangesummary/text/dump \n"));
			}
//			mailsender
//					.sendMail(destAddrs.toArray(new String[0]),
//							"Global Tag coverage checks " + new Date().toString(),
//							buf.toString());

		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ComaQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			badcooliovsummary.clear();
		}
	}

	/**
	 * @param summarylist
	 * @return
	 * @throws ComaQueryException
	 */
	protected boolean hasHoles(final Collection<CoolIovSummary> summarylist)
			throws ComaQueryException {
		boolean hashole = false;

		for (final CoolIovSummary iovsummary : summarylist) {
			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				for (final Long asince : sincetimes) {
					final IovRange ivr = timeranges.get(asince);
					if (ivr.getIshole()) {
						final List<CrViewRuninfo> runs = coolutilsdao.checkHoles(ivr,
								iovsummary.getIovbase());
						if (runs != null && runs.size() > 0) {
							// badcooliovsummary.add(iovsummary);
							hashole = true;
							return hashole;
						}
					}
				}
			}
		}
		return hashole;
	}

	/**
	 * Utility method to synchronize the local summary table with the info gathered from
	 * COOL.
	 * 
	 * @param summarylist
	 */
	protected void synchroSummaryTable(final String globaltag,
			final Collection<CoolIovSummary> summarylist) {
		for (CoolIovSummary iovsummary : summarylist) {
			String schema = iovsummary.getSchema();
			String db = iovsummary.getDb();
			String node = iovsummary.getNode();
			String tag = iovsummary.getTag();
			Long channelid = iovsummary.getChanId();
			BigDecimal chanid = null;
			if (channelid != null) {
				chanid = new BigDecimal(channelid);
			}
			try {
				// check if entry exists in DB
				List<atlas.cool.summary.model.CoolIovSummary> coolsummobjlist = cooldao
						.findIovSummaryList(schema, db, node, tag, chanid);
				if (coolsummobjlist != null && coolsummobjlist.size() > 0) {
					// if yes, update content
					atlas.cool.summary.model.CoolIovSummary summary = coolsummobjlist
							.get(0);
					summary.setCoolChannelName(iovsummary.getChannelName());
					summary.setCoolGlobalTagName(globaltag);
					summary.setCoolSummary(iovsummary.getSummary());
					summary.setCoolMiniovsince(new BigDecimal(iovsummary.getMinsince()));
					summary.setCoolMaxiovsince(new BigDecimal(iovsummary.getMaxsince()));
					summary.setCoolMiniovuntil(new BigDecimal(iovsummary.getMinuntil()));
					summary.setCoolMaxiovuntil(new BigDecimal(iovsummary.getMaxuntil()));
					summary.setCoolTotaliovs(new BigDecimal(iovsummary.getTotalIovs()));
//					log.info("Updating " + summary.toString());
				} else {
					// if not, store entry
					atlas.cool.summary.model.CoolIovSummary summary = new atlas.cool.summary.model.CoolIovSummary();
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
//					log.info("Inserting " + summary.toString());
					coolrep.persist(summary);
				}
			} catch (CoolIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
