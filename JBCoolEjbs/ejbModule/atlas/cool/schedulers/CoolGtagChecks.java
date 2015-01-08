package atlas.cool.schedulers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.ejb3.annotation.TransactionTimeout;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.utils.MailSender;
import atlas.cool.rest.utils.UrlLinkUtils;

/**
 * Session Bean implementation class CoolGtagChecks.
 */
@Stateless
@LocalBean
public class CoolGtagChecks {

	@Inject
	private ComaCbDAO comadao;
	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolUtilsDAO coolutilsdao;
	@Inject
	private MailSender mailsender;

	@Inject
	private Logger log;

	private final List<CoolIovSummary> badcooliovsummary = new ArrayList<CoolIovSummary>();

	private final List<String> destAddrs = new ArrayList<String>();

	/**
	 * Default constructor.
	 */
	public CoolGtagChecks() {
		// TODO Auto-generated constructor stub

		destAddrs.add("andrea.formica1971@gmail.com");
		destAddrs.add("michael.boehler@cern.ch ");
		destAddrs.add("voica@mail.desy.de");
		destAddrs.add("susumu.oda@cern.ch");
	}

	/**
	 * TODO: allow external switch for schedulers
     * Scheduler for tag coverage, disabled by default. Uncomment to enable it.
     */
//	@Schedule(dayOfWeek = "*", hour = "0", persistent = false)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public void checkCoverage() {
		String globaltagname = "COMCOND-BLKPA-RUN1-01";
		final String db = "COMP200";

		final int maxschemas = 999;
		final Date starttime = new Date();
		try {

			final List<ComaCbGtagStates> gtagstates = comadao.findGtagStateAtTime(
					"Next", new Date());
			if (gtagstates != null && gtagstates.size() > 0) {
				globaltagname = gtagstates.get(0).getTagName();
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
			mailsender
					.sendMail(destAddrs.toArray(new String[0]),
							"Global Tag coverage checks " + new Date().toString(),
							buf.toString());

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

		final Map<String, List<CrViewRuninfo>> skippedrunsCacheMap = new HashMap<String, List<CrViewRuninfo>>();

		for (final CoolIovSummary iovsummary : summarylist) {
			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				int iiov = 0;
				for (final Long asince : sincetimes) {
					final IovRange ivr = timeranges.get(asince);
					
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
								final List<CrViewRuninfo> runinhole = coolutilsdao.checkHoles(since,
										until, iovsummary.getIovbase());
								skippedrunsCacheMap.put(cacheKey, runinhole);
								if (runinhole.size() > 0) {
									missingPhysicsRuns = true;
								}
							}
							if (missingPhysicsRuns) {
								hashole = true;
								return hashole;
							}
						} catch (final ComaQueryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					
					if (ivr.getIshole()) {
						final BigDecimal since = new BigDecimal(ivr.getSince());
						final String cacheKey = since.longValue() + "-"
								+ ivr.getUntil();
						Boolean missingPhysicsRuns = false;
						if (skippedrunsCacheMap.containsKey(cacheKey)) {
							final List<CrViewRuninfo> skippedruns = skippedrunsCacheMap
									.get(cacheKey);
							if (skippedruns.size() > 0) {
								missingPhysicsRuns = true;
							}
						} else {
							final List<CrViewRuninfo> skippedruns = coolutilsdao.checkHoles(ivr,
									iovsummary.getIovbase());
							skippedrunsCacheMap.put(cacheKey, skippedruns);
							if (skippedruns.size() > 0) {
								missingPhysicsRuns = true;
							}
						}

						if (missingPhysicsRuns) {
							hashole = true;
							return hashole;
						}
					}
					iiov++;
				}
			}
		}
		return hashole;
	}
}
